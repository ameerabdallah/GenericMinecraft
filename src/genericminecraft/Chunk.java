package genericminecraft;

import java.nio.FloatBuffer;
import java.util.Random;
import org.lwjgl.BufferUtils;
import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL15.*;
import org.newdawn.slick.opengl.Texture;
import org.newdawn.slick.opengl.TextureLoader;
import org.newdawn.slick.util.ResourceLoader;

/***************************************************************
* @file: Chunk.java
* @author: Ameer Abdallah
* @class: CS 4450 - Computer Graphics
* 
* @assignment: Checkpoint 2
* @date last modified: 10/26/2020
*
* @purpose: 
* Manages the rendering of a chunk object and the blocks that the chunk consists
* of. Manages the vertex buffer objects for vertex, color and texture data.
****************************************************************/ 
public class Chunk 
{
    static final int CHUNK_SIZE = 30;
    static final int CHUNK_SIZE_Y = 70;
    static final int CUBE_LENGTH = 2;
    // This is the multiplier for the height map generated with noise
    static final int HEIGHT_VARIATION = 60; 
    
    // 3D Array of blocks that are inside the chunk
    private Block[][][] blocks;
    
    // VBO Handles
    private int VBOVertexHandle;
    private int VBOColorHandle;
    private int VBOTextureHandle;
    
    private Texture texture;
    
    private int chunkX, chunkZ;
    private Random r;
    
    // Simplex noise for height map
    private SimplexNoise sNoise;
    private SimplexNoise humidityNoise;
    
    // height map for terrain generation
    private int[][] heights;
    private int[][][] caves;
    private double[][] humidity;
    
    // where the chunk is relative to the blocks from other chunks
    private int bOffsetX;
    private int bOffsetZ;
    
    
    // method: Chunk()
    // purpose: Generate chunk and set up rebuildMesh at the end
    public Chunk(int chunkX, int chunkZ, SimplexNoise sNoise, SimplexNoise humidityNoise)
    {
        boolean fillWater = false;
        this.chunkX = chunkX;
        this.chunkZ = chunkZ;
     
        bOffsetX = chunkX*CHUNK_SIZE;
        bOffsetZ = chunkZ*CHUNK_SIZE;
        
        // open and generate texture
        
        try
        {
            texture = TextureLoader.getTexture("PNG",
                    ResourceLoader.getResourceAsStream("terrain.png"));
            
        }
        catch(Exception e)
        {
            e.printStackTrace();
        }
        texture.bind();
        texture.setTextureFilter(GL_NEAREST);
        
        this.sNoise = sNoise;
        
        r = new Random();
        
        this.humidityNoise = humidityNoise;
        blocks = new Block[CHUNK_SIZE][CHUNK_SIZE_Y][CHUNK_SIZE];
        heights = new int[CHUNK_SIZE][CHUNK_SIZE];
        humidity = new double[CHUNK_SIZE][CHUNK_SIZE];
        
        
        for(int x = 0; x < CHUNK_SIZE; x++)
        {
            for(int z = 0; z < CHUNK_SIZE; z++)
            {
                double noise = (sNoise.getNoise(x + bOffsetX, z + bOffsetZ) + 1)/2;
                heights[x][z] = CHUNK_SIZE_Y - (int) (noise * HEIGHT_VARIATION);
                noise = (humidityNoise.getNoise(x+bOffsetX, z+bOffsetZ) + 1) / 2 ;
                humidity[x][z] = noise;
            }
        }
        
        
        for(int x = 0; x < CHUNK_SIZE; x++)
        {
            for(int z = 0; z < CHUNK_SIZE; z++)
            {
                for(int y = 0; y  <CHUNK_SIZE_Y; y++)
                {
                    
                    if(y >= heights[x][z])
                    {
                        if(fillWater && y < 37)
                            blocks[x][y][z] = new Block(Block.BlockType.WATER);
                        else
                        blocks[x][y][z] = new Block(Block.BlockType.AIR);
                    }
                    else if(y == heights[x][z]-1)
                    {
                        if(y < 37)
                        {
                            fillWater = true;
                            blocks[x][y][z] = new Block(Block.BlockType.WATER);
                        }
                        else
                        {
                            if ( humidity[x][z] < 0.3f )
                                blocks[x][y][z] = new Block(Block.BlockType.SAND);
                            else
                                blocks[x][y][z] = new Block(Block.BlockType.GRASS);
                        }
                    }
                    else if(y > 30)
                        blocks[x][y][z] = new Block(Block.BlockType.DIRT);
                    else if(y > 0)
                        blocks[x][y][z] = new Block(Block.BlockType.STONE);
                    else 
                        blocks[x][y][z] = new Block(Block.BlockType.BEDROCK);
                }
            }
            
                fillWater = false;
        }
        
        VBOColorHandle = glGenBuffers();
        VBOVertexHandle = glGenBuffers();
        VBOTextureHandle = glGenTextures();
        rebuildMesh();
    }
    
    // method: render()
    // purpose: render meshes
    public void render()
    {
        glPushMatrix();
            glBindBuffer(GL_ARRAY_BUFFER, VBOVertexHandle);
            glVertexPointer(3, GL_FLOAT, 0, 0L);
            glBindBuffer(GL_ARRAY_BUFFER, VBOColorHandle);
            glColorPointer(3, GL_FLOAT, 0, 0L);
            glBindBuffer(GL_ARRAY_BUFFER, VBOTextureHandle);
            glBindTexture(GL_TEXTURE_2D, 1);
            glTexCoordPointer(2, GL_FLOAT, 0, 0L);
            glDrawArrays(GL_QUADS, 0,
                        CHUNK_SIZE*CHUNK_SIZE_Y*CHUNK_SIZE*24);
        glPopMatrix();
    }
    // method: rebuildMesh()
    // purpose: rebuild the mesh based on the current position of blocks in the chunk
    public void rebuildMesh()
    {
        VBOTextureHandle = glGenBuffers();
        VBOColorHandle = glGenBuffers();
        VBOVertexHandle = glGenBuffers();
        
        FloatBuffer vertexPositionData = 
                BufferUtils.createFloatBuffer((CHUNK_SIZE*(CHUNK_SIZE_Y)*
                        CHUNK_SIZE)*6*4*3);
        
        FloatBuffer vertexColorData = 
                BufferUtils.createFloatBuffer((CHUNK_SIZE*(CHUNK_SIZE_Y)*
                        CHUNK_SIZE)*6*4*3);
        
        FloatBuffer vertexTextureData = 
                BufferUtils.createFloatBuffer((CHUNK_SIZE*(CHUNK_SIZE_Y)*CHUNK_SIZE)*6*4*2
                );
        
        for(int x = 0; x < CHUNK_SIZE; x++)
        {
            for(int z = 0; z < CHUNK_SIZE; z++)
            {
                for(int y = 0; y < CHUNK_SIZE_Y; y++)
                {
                    
                    if(blocks[x][y][z].getType() != Block.BlockType.AIR)
                    {
                        
                        // vertex data
                        vertexPositionData.put(createCube(x, y, z));

                        // color data
                        vertexColorData.put
                        (
                            createCubeVertexColor(blocks[x][y][z])
                        );

                        // texture data
                        vertexTextureData.put
                        (
                                createTextureCube
                                (
                                    0f, 0f, blocks[x][y][z]
                                )
                        );
                    }
                }
            }
        }
        
        vertexTextureData.flip();
        vertexColorData.flip();
        vertexPositionData.flip();
        
        glBindBuffer(GL_ARRAY_BUFFER, VBOTextureHandle);
        glBufferData(GL_ARRAY_BUFFER, vertexTextureData,
                GL_STATIC_DRAW);
        glBindBuffer(GL_ARRAY_BUFFER, 0);
        glBindBuffer(GL_ARRAY_BUFFER,
                VBOVertexHandle);
        glBufferData(GL_ARRAY_BUFFER,
                vertexPositionData,
                GL_STATIC_DRAW);
        glBindBuffer(GL_ARRAY_BUFFER, 0);
        glBindBuffer(GL_ARRAY_BUFFER,
                VBOColorHandle);
        glBufferData(GL_ARRAY_BUFFER,
                vertexColorData,
                GL_STATIC_DRAW);
        glBindBuffer(GL_ARRAY_BUFFER, 0);
    }
    
    // method: createCubeVertexColor()
    // Create the colors for each vertex of the cube
    private float[] createCubeVertexColor(Block block)
    {
        float   rTop, gTop, bTop,
                rBot, gBot, bBot,
                rFront, gFront, bFront,
                rBack, gBack, bBack,
                rLeft, gLeft, bLeft,
                rRight, gRight, bRight;
        
        switch(block.getType())
        {
            case AIR:
                return new float[] {};
            case GRASS:
                rTop =      .34f;
                gTop =      .78f;
                bTop =      .236f;
                rBot =      1;
                gBot =      1;
                bBot =      1;
                rFront =    1;
                gFront =    1;
                bFront =    1;
                rBack =     1;
                gBack =     1;
                bBack =     1;
                rLeft =     1;
                gLeft =     1;
                bLeft =     1;
                rRight =    1;
                gRight =    1;
                bRight =    1;
                break;
            default:
                rTop =      1;
                gTop =      1;
                bTop =      1;
                rBot =      1;
                gBot =      1;
                bBot =      1;
                rFront =    1;
                gFront =    1;
                bFront =    1;
                rBack =     1;
                gBack =     1;
                bBack =     1;
                rLeft =     1;
                gLeft =     1;
                bLeft =     1;
                rRight =    1;
                gRight =    1;
                bRight =    1;
                break;
        }
        // return the color values for each vertex of the cube
         return new float[] 
         {
             rTop, gTop, bTop, rTop, gTop, bTop, rTop, gTop, bTop, rTop, gTop, bTop,
             rBot, gBot, bBot, rBot, gBot, bBot, rBot, gBot, bBot, rBot, gBot, bBot,
             rFront, gFront, bFront, rFront, gFront, bFront, rFront, gFront, bFront, rFront, gFront, bFront,
             rBack, gBack, bBack, rBack, gBack, bBack, rBack, gBack, bBack, rBack, gBack, bBack,
             rLeft, gLeft, bLeft, rLeft, gLeft, bLeft, rLeft, gLeft, bLeft, rLeft, gLeft, bLeft,
             rRight, gRight, bRight, rRight, gRight, bRight, rRight, gRight, bRight, rRight, gRight, bRight,
         };
    }
    
    
    // method: createCube()
    // create the vertices relative to the center of the cube, the position is given
    // through the parameters and is the position of the center of the cube
    private float[] createCube(float x, float y, float z)
    {
        int iX = (int)x, iY = (int)y, iZ = (int)z;
        int offset = CUBE_LENGTH / 2;
        
        switch(blocks[iX][iY][iZ].getType())
        {
            case AIR:
                return new float[] {};
            default:
                break;
        }
            
        x = (x+bOffsetX)*CUBE_LENGTH;
        y = y*CUBE_LENGTH;
        z = (z+bOffsetZ)*CUBE_LENGTH;
        
        // Create Cube vertices relative to the center of the cube
        return new float[]
        {
            // TOP QUAD
            x+offset, y+offset, z+offset,
            x-offset, y+offset, z+offset,
            x-offset, y+offset, z-offset,
            x+offset, y+offset, z-offset,
            // BOTTOM QUAD
            x+offset, y-offset, z-offset,
            x-offset, y-offset, z-offset,
            x-offset, y-offset, z+offset,
            x+offset, y-offset, z+offset,
            // FRONT QUAD
            x+offset, y+offset, z-offset,
            x-offset, y+offset, z-offset,
            x-offset, y-offset, z-offset,
            x+offset, y-offset, z-offset,
            // BACK QUAD
            x+offset, y-offset, z+offset,
            x-offset, y-offset, z+offset,
            x-offset, y+offset, z+offset,
            x+offset, y+offset, z+offset,
            // LEFT QUAD
            x-offset, y+offset, z-offset, 
            x-offset, y+offset, z+offset,
            x-offset, y-offset, z+offset,
            x-offset, y-offset, z-offset,
            // RIGHT QUAD
            x+offset, y+offset, z+offset,
            x+offset, y+offset, z-offset,
            x+offset, y-offset, z-offset,
            x+offset, y-offset, z+offset
        };
    }

    // method: getTextureCube()
    // purpose: maps texture to cube based on block.getType()
    private float[] createTextureCube(float x, float y, Block block) 
    {
        float topX, topY;
        float botX, botY;
        float frontX, frontY;
        float backX, backY;
        float leftX, leftY;
        float rightX, rightY;
        
        float offset = 1f/16f;
        
        switch(block.getType())
        {
            case AIR:
                return new float[]{};
            case WATER:
                topX =      13;
                topY =      12;
                botX =      13;
                botY =      12;
                frontX =    13;
                frontY =    12;
                backX =     13;
                backY =     12;
                leftX =     13;
                leftY =     12;
                rightX =    13;
                rightY =    12; 
                break;
            case BEDROCK:
                topX =      1;
                topY =      1;
                botX =      1;
                botY =      1;
                frontX =    1;
                frontY =    1;
                backX =     1;
                backY =     1;
                leftX =     1;
                leftY =     1;
                rightX =    1;
                rightY =    1; 
                break;
            case STONE:
                topX =      1;
                topY =      0;
                botX =      1;
                botY =      0;
                frontX =    1;
                frontY =    0;
                backX =     1;
                backY =     0;
                leftX =     1;
                leftY =     0;
                rightX =    1;
                rightY =    0; 
                break;
            case SAND:
                topX =      2;
                topY =      1;
                botX =      2;
                botY =      1;
                frontX =    2;
                frontY =    1;
                backX =     2;
                backY =     1;
                leftX =     2;
                leftY =     1;
                rightX =    2;
                rightY =    1; 
                break;
            case GRAVEL:
                topX =      3;
                topY =      1;
                botX =      3;
                botY =      1;
                frontX =    3;
                frontY =    1;
                backX =     3;
                backY =     1;
                leftX =     3;
                leftY =     1;
                rightX =    3;
                rightY =    1; 
                break;
            case GRASS:
                topX =      0;
                topY =      0;
                botX =      2;
                botY =      0;
                frontX =    3;
                frontY =    0;
                backX =     3;
                backY =     0;
                leftX =     3;
                leftY =     0;
                rightX =    3;
                rightY =    0; 
                break;
            case DIRT:
                topX =      2;
                topY =      0;
                botX =      2;
                botY =      0;
                frontX =    2;
                frontY =    0;
                backX =     2;
                backY =     0;
                leftX =     2;
                leftY =     0;
                rightX =    2;
                rightY =    0; 
                break;
            case WOOD_LOG:
                topX =      5;
                topY =      1;
                botX =      5;
                botY =      1;
                frontX =    4;
                frontY =    1;
                backX =     4;
                backY =     1;
                leftX =     4;
                leftY =     1;
                rightX =    4;
                rightY =    1;
                break;
            default: // this maps to the missing texture
                topX =      3 ;
                topY =      13;
                botX =      3 ;
                botY =      13;
                frontX =    3 ;
                frontY =    13;
                backX =     3 ;
                backY =     13;
                leftX =     3 ;
                leftY =     13;
                rightX =    3 ;
                rightY =    13; 
                break;
        }
        
         return new float[] {
            // TOP
            x + offset*(topX+1), y + offset*(topY+1),
            x + offset*topX, y + offset*(topY+1),
            x + offset*topX, y + offset*topY,
            x + offset*(topX+1), y + offset*topY,
            // BOT QUAD
            x + offset*(botX+1), y + offset*(botY+1),
            x + offset*botX, y + offset*(botY+1),
            x + offset*botX, y + offset*botY,
            x + offset*(botX+1), y + offset*botY,
            // FRONT QUAD
            x + offset*frontX, y + offset*frontY,
            x + offset*(frontX+1), y + offset*frontY,
            x + offset*(frontX+1), y + offset*(frontY+1),
            x + offset*frontX, y + offset*(frontY+1),
            // BACK QUAD
            x + offset*(backX+1), y + offset*(backY+1),
            x + offset*backX, y + offset*(backY+1),
            x + offset*backX, y + offset*backY,
            x + offset*(backX+1), y + offset*backY,
            // LEFT QUAD
            x + offset*leftX, y + offset*leftY,
            x + offset*(leftX+1), y + offset*leftY,
            x + offset*(leftX+1), y + offset*(leftY+1),
            x + offset*leftX, y + offset*(leftY+1),
            // RIGHT QUAD
            x + offset*rightX, y + offset*rightY,
            x + offset*(rightX+1), y + offset*rightY,
            x + offset*(rightX+1), y + offset*(rightY+1),
            x + offset*rightX, y + offset*(rightY+1)};       
    }
}
