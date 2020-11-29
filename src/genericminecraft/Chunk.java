package genericminecraft;

import java.nio.FloatBuffer;
import java.util.Random;
import org.lwjgl.BufferUtils;
import org.lwjgl.Sys;
import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL15.*;

/***************************************************************
* @file: Chunk.java
* @author: Ameer Abdallah
* @class: CS 4450 - Computer Graphics
* 
* @assignment: Checkpoint 3
* @date last modified: 10/30/2020
*
* @purpose: 
* Manages the rendering of a chunk object and the blocks that the chunk consists
* of. Manages the vertex buffer objects for vertex, color and texture data.
****************************************************************/ 
public class Chunk 
{
    static final int CHUNK_SIZE = 32;
    static final int CHUNK_SIZE_Y = 70;
    static final int CUBE_LENGTH = 2;
    // This is the multiplier for the height map generated with noise
    static final int HEIGHT_VARIATION = 60; 
    public boolean tCheck;
    
    // 3D Array of blocks that are inside the chunk
    private Block[][][] blocks;
    
    // looking at chunks from a top down view, the z axis goes up and x goes right
    private Chunk forward, backward, left, right;
    
    // VBO Handles
    private int VBOVertexHandle;
    private int VBOColorHandle;
    private int VBOTextureHandle;
    
    public final int CHUNK_X, CHUNK_Z;
    private Random r;
    
    // height map for terrain generation
    private double[][] heights;
    private double[][] humidity;
    
    // where the chunk is relative to the blocks from other chunks
    private int bOffsetX;
    private int bOffsetZ;
    
    
    // method: Chunk()
    // purpose: Generate chunk and set up rebuildMesh at the end
    public Chunk(int chunkX, int chunkZ)
    {
        boolean fillWater = false;
        this.CHUNK_X = chunkX;
        this.CHUNK_Z = chunkZ;
     
        bOffsetX = chunkX*CHUNK_SIZE;
        bOffsetZ = chunkZ*CHUNK_SIZE;
        
        r = new Random();
        
        blocks = new Block[CHUNK_SIZE][CHUNK_SIZE_Y][CHUNK_SIZE];
        heights = new double[CHUNK_SIZE][CHUNK_SIZE];
        humidity = new double[CHUNK_SIZE][CHUNK_SIZE];
        
        
        for(int x = 0; x < CHUNK_SIZE; x++)
        {
            for(int z = 0; z < CHUNK_SIZE; z++)
            {
                double noise = (World.HEIGHT_NOISE.getNoise(x + bOffsetX, z + bOffsetZ) + 1)/2;
                heights[x][z] = CHUNK_SIZE_Y - (int) (noise * HEIGHT_VARIATION);
                noise = (World.HUMIDITY_NOISE.getNoise(x+bOffsetX, z+bOffsetZ) + 1) / 2 ;
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
                    else if(y > heights[x][z]-5)
                        blocks[x][y][z] = new Block(Block.BlockType.DIRT);
                    else if(y <= 1)
                        blocks[x][y][z] = new Block(Block.BlockType.BEDROCK);
                    else 
                        blocks[x][y][z] = new Block(Block.BlockType.STONE);
                }
                fillWater = false;
            }
        }
        
        forward = null;
        backward = null;
        left = null;
        right = null;
        
        VBOColorHandle = glGenBuffers();
        VBOVertexHandle = glGenBuffers();
        VBOTextureHandle = glGenTextures();
        
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
            glBindTexture(GL_TEXTURE_2D, World.TEXTURE_ID);
            glTexCoordPointer(2, GL_FLOAT, 0, 0L);
            
            glDrawArrays(GL_QUADS, 0,
                        CHUNK_SIZE*CHUNK_SIZE_Y*CHUNK_SIZE*24);
        glPopMatrix();
    }
    
    public void initializeSurroundingBlockValues()
    {
        // intialize surrounding blocks
        for(int x = 0; x < CHUNK_SIZE; x++)
        {
            for(int z = 0; z < CHUNK_SIZE; z++)
            {
                for(int y = 0; y  < CHUNK_SIZE_Y; y++)
                {
                    // set top block
                    if(y+1 == CHUNK_SIZE_Y)
                        blocks[x][y][z].setTop(null);
                    else
                        blocks[x][y][z].setTop(blocks[x][y+1][z]);
                    
                    // set bot block
                    if(y-1 < 0)
                        blocks[x][y][z].setBot(null);
                    else
                        blocks[x][y][z].setBot(blocks[x][y-1][z]);
                    
                    // set back block
                    if(z+1 == CHUNK_SIZE)
                    {
                        if(backward == null)
                            blocks[x][y][z].setBack(null);
                        else
                            blocks[x][y][z].setBack(backward.getBlock(x, y, 0));
                    }
                    else
                        blocks[x][y][z].setBack(blocks[x][y][z+1]);
                    
                    // set front block
                    if(z-1 < 0)
                    {
                        if(forward == null)
                            blocks[x][y][z].setFront(null);
                        else
                            blocks[x][y][z].setFront(forward.getBlock(x, y, CHUNK_SIZE-1));
                    }
                    else
                        blocks[x][y][z].setFront(blocks[x][y][z-1]);
                    
                    // set right block
                    if(x+1 == CHUNK_SIZE)
                    {
                        if(right == null)
                            blocks[x][y][z].setRight(null);
                        else
                            blocks[x][y][z].setRight(right.getBlock(0, y, z));
                    }
                    else
                        blocks[x][y][z].setRight(blocks[x+1][y][z]);
                    
                    // set left block
                    if(x-1 < 0)
                    {
                        if(left == null)
                            blocks[x][y][z].setLeft(null);
                        else
                            blocks[x][y][z].setLeft(left.getBlock(CHUNK_SIZE-1, y, z));
                    }
                    else
                        blocks[x][y][z].setLeft(blocks[x-1][y][z]);
                }
            }
        }
        
    }
    static long timer = 0;
    // method: rebuildMesh()
    // purpose: rebuild the mesh based on the current position of blocks in the chunk
    public void rebuildMesh()
    {
        long time = Sys.getTime();
        
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
                BufferUtils.createFloatBuffer((CHUNK_SIZE*(CHUNK_SIZE_Y)*
                        CHUNK_SIZE)*6*4*2);
        
        for(int x = 0; x < CHUNK_SIZE; x++)
        {
            for(int z = 0; z < CHUNK_SIZE; z++)
            {
                for(int y = 0; y < CHUNK_SIZE_Y; y++)
                {
                    createCubeData
                    (
                    x, y, z,
                    vertexPositionData,
                    vertexColorData,
                    vertexTextureData
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
        
        
        timer +=  Sys.getTime() - time;
    }
    
    // method: createCubeData
    private void createCubeData(int x, int y, int z, FloatBuffer vertexPositionData, FloatBuffer vertexColorData, FloatBuffer vertexTextureData)
    {
        Block block  = blocks[x][y][z];
        float[] posData = new float[]{};
        float[] textureData = new float[]{};
        float[] colorData = new float[]{};
        
        int posOffset = CUBE_LENGTH / 2;
        float offset = 1f/16f;
        
        float topX, topY;
        float botX, botY;
        float frontX, frontY;
        float backX, backY;
        float leftX, leftY;
        float rightX, rightY;
        
        float   rTop, gTop, bTop,
                rBot, gBot, bBot,
                rFront, gFront, bFront,
                rBack, gBack, bBack,
                rLeft, gLeft, bLeft,
                rRight, gRight, bRight;
        
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
        
        x = (x+bOffsetX)*CUBE_LENGTH;
        y = y*CUBE_LENGTH;
        z = (z+bOffsetZ)*CUBE_LENGTH;
        
        switch(block.getType())
        {
            case AIR:
                return;
            case GRASS:
                // Color Data
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
                // Texture Data
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
            case WATER:
                // Texture Data
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
                // Texture Data
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
                // Texture Data
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
                // Texture Data
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
                // Texture Data
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
            case DIRT:
                // Texture Data
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
                // Texture Data
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
                // Texture Data
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
        
        if(block.drawTop())
        {
            colorData = Util.append(colorData, new float[]{rTop, gTop, bTop, rTop, gTop, bTop, rTop, gTop, bTop, rTop, gTop, bTop});
            
            posData = Util.append(posData, new float[]{
            x+posOffset, y+posOffset, z+posOffset,
            x-posOffset, y+posOffset, z+posOffset,
            x-posOffset, y+posOffset, z-posOffset,
            x+posOffset, y+posOffset, z-posOffset,});
            
            textureData = Util.append(textureData, new float[]{
            x + offset*(topX+1), y + offset*(topY+1),
            x + offset*topX, y + offset*(topY+1),
            x + offset*topX, y + offset*topY,
            x + offset*(topX+1), y + offset*topY,});
        }
        if(block.drawBot())
        {
            colorData = Util.append(colorData, new float[]{rBot, gBot, bBot, rBot, gBot, bBot, rBot, gBot, bBot, rBot, gBot, bBot});
            
            posData = Util.append(posData, new float[]{
            x+posOffset, y-posOffset, z-posOffset,
            x-posOffset, y-posOffset, z-posOffset,
            x-posOffset, y-posOffset, z+posOffset,
            x+posOffset, y-posOffset, z+posOffset,});
            
            textureData = Util.append(textureData, new float[]{
            x + offset*(botX+1), y + offset*(botY+1),
            x + offset*botX, y + offset*(botY+1),
            x + offset*botX, y + offset*botY,
            x + offset*(botX+1), y + offset*botY,});
        }
        if(block.drawFront())
        {
            colorData = Util.append(colorData, new float[]{rFront, gFront, bFront, rFront, gFront, bFront, rFront, gFront, bFront, rFront, gFront, bFront});
            
            posData = Util.append(posData, new float[]{
            x+posOffset, y+posOffset, z-posOffset,
            x-posOffset, y+posOffset, z-posOffset,
            x-posOffset, y-posOffset, z-posOffset,
            x+posOffset, y-posOffset, z-posOffset,});
            
            textureData = Util.append(textureData, new float[]{
            x + offset*frontX, y + offset*frontY,
            x + offset*(frontX+1), y + offset*frontY,
            x + offset*(frontX+1), y + offset*(frontY+1),
            x + offset*frontX, y + offset*(frontY+1),});
        }
        if(block.drawBack())
        { 
            colorData = Util.append(colorData, new float[]{rBack, gBack, bBack, rBack, gBack, bBack, rBack, gBack, bBack, rBack, gBack, bBack});
            
            posData = Util.append(posData, new float[]{
            x+posOffset, y-posOffset, z+posOffset,
            x-posOffset, y-posOffset, z+posOffset,
            x-posOffset, y+posOffset, z+posOffset,
            x+posOffset, y+posOffset, z+posOffset,});
            
            textureData = Util.append(textureData, new float[]{
            x + offset*(backX+1), y + offset*(backY+1),
            x + offset*backX, y + offset*(backY+1),
            x + offset*backX, y + offset*backY,
            x + offset*(backX+1), y + offset*backY,});
        }
        if(block.drawLeft())
        {
            colorData = Util.append(colorData, new float[]{rLeft, gLeft, bLeft, rLeft, gLeft, bLeft, rLeft, gLeft, bLeft, rLeft, gLeft, bLeft});
            
            posData = Util.append(posData, new float[]{
            x-posOffset, y+posOffset, z-posOffset, 
            x-posOffset, y+posOffset, z+posOffset,
            x-posOffset, y-posOffset, z+posOffset,
            x-posOffset, y-posOffset, z-posOffset,});
            
            textureData = Util.append(textureData, new float[]{
            x + offset*leftX, y + offset*leftY,
            x + offset*(leftX+1), y + offset*leftY,
            x + offset*(leftX+1), y + offset*(leftY+1),
            x + offset*leftX, y + offset*(leftY+1),});
        }
        if(block.drawRight())
        {
            colorData = Util.append(colorData, new float[]{rRight, gRight, bRight, rRight, gRight, bRight, rRight, gRight, bRight, rRight, gRight, bRight});
            
            posData = Util.append(posData, new float[]{
            x+posOffset, y+posOffset, z+posOffset,
            x+posOffset, y+posOffset, z-posOffset,
            x+posOffset, y-posOffset, z-posOffset,
            x+posOffset, y-posOffset, z+posOffset});
            
            textureData = Util.append(textureData, new float[]{
            x + offset*rightX, y + offset*rightY,
            x + offset*(rightX+1), y + offset*rightY,
            x + offset*(rightX+1), y + offset*(rightY+1),
            x + offset*rightX, y + offset*(rightY+1)});
        }
        
        vertexPositionData.put(posData);
        vertexColorData.put(colorData);
        vertexTextureData.put(textureData);
        
    }
    
    // gets block from chunk space
    public Block getBlock(int x, int y, int z)
    {
        return blocks[x][y][z];
    }

    public Chunk getForward() {
        return forward;
    }

    public void setForward(Chunk forward) {
        this.forward = forward;
    }

    public Chunk getBackward() {
        return backward;
    }

    public void setBackward(Chunk backward) {
        this.backward = backward;
    }

    public Chunk getLeft() {
        return left;
    }

    public void setLeft(Chunk left) {
        this.left = left;
    }

    public Chunk getRight() {
        return right;
    }

    public void setRight(Chunk right) {
        this.right = right;
    }
    
    @Override
    public boolean equals(Object object)
    {
        if(!(object instanceof Chunk))
            return false;
        
        Chunk chunk = (Chunk) object;
        
        return chunk.CHUNK_X == CHUNK_X && chunk.CHUNK_Z == CHUNK_Z;
    }
    
}
