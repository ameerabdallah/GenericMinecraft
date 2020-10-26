package genericminecraft;

import java.nio.FloatBuffer;
import java.util.Random;
import org.lwjgl.BufferUtils;
import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL15.*;
import org.newdawn.slick.opengl.Texture;
import org.newdawn.slick.opengl.TextureLoader;
import org.newdawn.slick.util.ResourceLoader;

/**
 *
 * @author ameer
 */
public class Chunk 
{
    static final int CHUNK_SIZE = 30;
    static final int CHUNK_SIZE_Y = 70;
    static final int CUBE_LENGTH = 2;
    static final int HEIGHT_VARIATION = 60;
    private Block[][][] blocks;
    private int VBOVertexHandle;
    private int VBOColorHandle;
    private int VBOTextureHandle;
    private Texture texture;
    private int startX, startZ;
    private Random r;
    private SimplexNoise sNoise;
    private int[][] heights;
    private SimplexNoise humidityNoise;
    private double[][] humidity;
    private int bOffsetX;
    private int bOffsetZ;
    
    public Chunk(int startX, int startZ, SimplexNoise sNoise, SimplexNoise humidityNoise)
    {
        this.startX = startX;
        this.startZ = startZ;
     
        bOffsetX = startX*CHUNK_SIZE;
        bOffsetZ = startZ*CHUNK_SIZE;
        
        // open texture
        try
        {
            texture = TextureLoader.getTexture("PNG",
                    ResourceLoader.getResourceAsStream("terrain.png"));
        }
        catch(Exception e)
        {
            e.printStackTrace();
        }
        texture.hasAlpha();
        
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
                for(int y = 0; y  < heights[x][z]; y++)
                {
                    if(y == heights[x][z]-1)
                    {
                        if(y < 37)
                            blocks[x][y][z] = new Block(Block.BlockType.WATER);
                        else
                        {
                            if (humidity[x][z] < 0.3f)
                                blocks[x][y][z] = new Block(Block.BlockType.SAND);
                            else
                                blocks[x][y][z] = new Block(Block.BlockType.GRASS);
                        }
                    }
                    else if(y > 20)
                        blocks[x][y][z] = new Block(Block.BlockType.DIRT);
                    else if(y > 0)
                        blocks[x][y][z] = new Block(Block.BlockType.STONE);
                    else
                        blocks[x][y][z] = new Block(Block.BlockType.BEDROCK);
                        
                }
            }
        }
        
        VBOColorHandle = glGenBuffers();
        VBOVertexHandle = glGenBuffers();
        VBOTextureHandle = glGenBuffers();
        rebuildMesh();
    }
    
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
                for(int y = 0; y < heights[x][z]; y++)
                {
                    // vertex data
                    vertexPositionData.put
                    (
                    createCube
                    (
                            (float)(bOffsetX*CUBE_LENGTH + x*CUBE_LENGTH),
                            (float)(y*CUBE_LENGTH),
                            (float)(bOffsetZ*CUBE_LENGTH + z*CUBE_LENGTH)
                    )
                    );
                    
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
    
    private float[] createCube(float x, float y, float z)
    {
        int offset = CUBE_LENGTH / 2;
        
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

    private float[] createTextureCube(float x, float y, Block block) 
    {
        float topX, topY;
        float botX, botY;
        float frontX, frontY;
        float backX, backY;
        float leftX, leftY;
        float rightX, rightY;
        
        float offset = (1024f/16)/1024;
                
        switch(block.getType())
        {
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
            default:
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
