package genericminecraft;

import java.nio.FloatBuffer;
import java.util.Random;
import org.lwjgl.BufferUtils;
import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL15.*;

/**
 *
 * @author ameer
 */
public class Chunk 
{
    static final int CHUNK_SIZE = 30;
    static final int CUBE_LENGTH = 2;
    private Block[][][] blocks;
    private int VBOVertexHandle;
    private int VBOColorHandle;
    private int startX, startZ;
    private Random r;
    
    public Chunk(int startX, int startZ)
    {
        r = new Random();
        blocks = new Block[CHUNK_SIZE][CHUNK_SIZE][CHUNK_SIZE];
        for(int x = 0; x < CHUNK_SIZE; x++)
        {
            for(int y = 0; y < CHUNK_SIZE; y++)
            {
                for(int z = 0; z < CHUNK_SIZE; z++)
                {
                    if(r.nextFloat()>0.7f)
                        blocks[x][y][z] = new Block(Block.BlockType.GRASS);
                    else if(r.nextFloat() > 0.4f)
                        blocks[x][y][z] = new Block(Block.BlockType.DIRT);
                    else if(r.nextFloat() > 0.2f)
                        blocks[x][y][z] = new Block(Block.BlockType.WATER);
                    else
                        blocks[x][y][z] = new Block(Block.BlockType.STONE);
                }
            }
        }
        System.out.println("Finished Randomizing Chunk");
        VBOColorHandle = glGenBuffers();
        VBOVertexHandle = glGenBuffers();
        this.startX = startX;
        this.startZ = startZ;
        rebuildMesh();
    }
    
    public void render()
    {
        glPushMatrix();
            glBindBuffer(GL_ARRAY_BUFFER, VBOVertexHandle);
            glVertexPointer(3, GL_FLOAT, 0, 0L);
            glBindBuffer(GL_ARRAY_BUFFER, VBOColorHandle);
            glColorPointer(3, GL_FLOAT, 0, 0L);
            glDrawArrays(GL_QUADS, 0,
                        CHUNK_SIZE*CHUNK_SIZE*CHUNK_SIZE*24);
        glPopMatrix();
    }
    
    public void rebuildMesh()
    {
        VBOColorHandle = glGenBuffers();
        VBOVertexHandle = glGenBuffers();
        
        FloatBuffer vertexPositionData = 
                BufferUtils.createFloatBuffer(
                (CHUNK_SIZE*CHUNK_SIZE*
                        CHUNK_SIZE)*6*4*3);
        
        FloatBuffer vertexColorData = 
                BufferUtils.createFloatBuffer(
                (CHUNK_SIZE*CHUNK_SIZE*
                        CHUNK_SIZE)*6*4*3);
        
        for(float x = 0; x < CHUNK_SIZE; x++)
        {
            for(float z = 0; z < CHUNK_SIZE; z++)
            {
                for(float y = 0; y < CHUNK_SIZE; y++)
                {
                    vertexPositionData.put
                    (
                    createCube
                    (
                            (float)(startX + x*CUBE_LENGTH),
                            (float)(y*CUBE_LENGTH),
                            (float)(startZ + z*CUBE_LENGTH))
                    );
                    vertexColorData.put(
                    createCubeVertexColor
                    (
                    getCubeColor(
                    blocks[(int)x][(int)y][(int)z])
                    ));
                }
            }
        }
        
        vertexColorData.flip();
        vertexPositionData.flip();
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
    
    private float[] getCubeColor(Block block)
    {
        switch(block.GetID())
        {
            case 1: 
                return new float[]{ 0, 1, 0};
            case 2:
                return new float[]{ 1, 0.5f, 0};
            case 3:
                return new float[]{ 0.0f, 0.0f, 1f};
        }
        return new float[] { 1, 1, 1 };
    }
    
    private float[] createCubeVertexColor(float[] cubeColorArray)
    {
        float[] cubeColors = new float[cubeColorArray.length*4*6];
        
        for(int i = 0; i < cubeColors.length; i++)
        {
            cubeColors[i] = cubeColorArray[i%cubeColorArray.length];
        }
        
        return cubeColors;
    }
    
    private float[] createCube(float x, float y, float z)
    {
        int offset = CUBE_LENGTH / 2;
        
        // vertices are relative to the center of the cube
        
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
}
