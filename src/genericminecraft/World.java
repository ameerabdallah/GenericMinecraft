package genericminecraft;

import java.util.HashMap;
import java.util.Random;
import static org.lwjgl.opengl.GL11.GL_NEAREST;
import static org.lwjgl.opengl.GL11.GL_TEXTURE_2D;
import static org.lwjgl.opengl.GL11.glBindTexture;
import static org.lwjgl.opengl.GL11.glDisable;
import static org.lwjgl.opengl.GL11.glEnable;
import static org.lwjgl.opengl.GL13.GL_TEXTURE1;
import static org.lwjgl.opengl.GL13.GL_TEXTURE2;
import static org.lwjgl.opengl.GL13.glActiveTexture;
import org.newdawn.slick.opengl.Texture;
import org.newdawn.slick.opengl.TextureLoader;
import org.newdawn.slick.util.ResourceLoader;

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
public class World {
    
    private HashMap < String, Chunk > chunks = new HashMap < String, Chunk >();
    
    public static int TEXTURE_ID;
    
    private final int COLLISION_CHECK_DISTANCE = 2;
    private final int CHUNKS_X = 4;
    private final int CHUNKS_Z = 4;
    private final float MOVEMENT_EPSILON = 0.00001f;
    static final int SEED = new Random().nextInt();
    static final Random R = new Random(SEED);
    static final SimplexNoise HEIGHT_NOISE  = new SimplexNoise(150, 0.4, R.nextInt());
    static final SimplexNoise HUMIDITY_NOISE = new SimplexNoise(300, 0.6, R.nextInt());
    private Player player;
    
    private Texture texture1, texture2;
    
    
    public World(Player player)
    {
        initTextures();
        TEXTURE_ID = texture1.getTextureID();
        loadChunks();
        this.player = player;
    }
    
    private void loadChunks()
    {
        for(int i = 0; i < CHUNKS_X; i++)
        {
            for(int j = 0; j < CHUNKS_Z; j++)
            {
                chunks.put(i + " " + j, new Chunk(i, j));
            }
        }
        
        for(int i = 0; i < CHUNKS_X; i++)
        {
            for(int j = 0; j < CHUNKS_Z; j++)
            {
                chunks.get(i + " " + j).initializeSurroundingBlockValues();
                chunks.get(i + " " + j).rebuildMesh();
            }
        }
    }
    
    public void render()
    {
        for(int i = 0; i < CHUNKS_X; i++)
        {
            for(int j = 0; j < CHUNKS_Z; j++)
            {
                chunks.get(i + " " + j).render();
            }
        }
    }
    
    public void handleCollisions()
    {
        for(int x = (int)player.getPosInBlockSpace().x-COLLISION_CHECK_DISTANCE; x < (int)player.getPosInBlockSpace().x+COLLISION_CHECK_DISTANCE; x++)
        {
            for(int z = (int)player.getPosInBlockSpace().z-COLLISION_CHECK_DISTANCE; z < (int)player.getPosInBlockSpace().z+COLLISION_CHECK_DISTANCE; z++)
            {
                for(int y = (int)player.getPosInBlockSpace().y-COLLISION_CHECK_DISTANCE; y < (int)player.getPosInBlockSpace().y+COLLISION_CHECK_DISTANCE; y++)
                {
                    if(isPlayerCollidingWithBlock(x, y, z))
                    {
                        if (player.getVelocity().y > 0)
                        { 
                            if ( player.getPos().y < y*Chunk.CUBE_LENGTH )
                            {
                                player.getVelocity().y = 0;
                                player.getPos().y = y*Chunk.CUBE_LENGTH - Player.SIZE_Y/2 - Chunk.CUBE_LENGTH/2;
                                break;
                            }
                        }
                        else if (player.getVelocity().y < 0)
                        {
                            if ( player.getPos().y > y*Chunk.CUBE_LENGTH)
                            {
                                player.getVelocity().y = 0;
                                player.getPos().y = y*Chunk.CUBE_LENGTH + Player.SIZE_Y/2 + Chunk.CUBE_LENGTH/2;
                                player.setGrounded(true);
                                break;
                            }
                        }
                        
                        
                        if (player.getVelocity().x > 0)
                        {   
                            if ( player.getPos().x < x*Chunk.CUBE_LENGTH)
                            {
                                player.getPos().x = x*Chunk.CUBE_LENGTH - Player.SIZE_XZ/2 - Chunk.CUBE_LENGTH/2;
                                player.getVelocity().x = 0;
                                break;
                            }
                        }
                        else if (player.getVelocity().x < 0)
                        {
                            if ( player.getPos().x > x*Chunk.CUBE_LENGTH)
                            {
                                player.getPos().x =  x*Chunk.CUBE_LENGTH + Player.SIZE_XZ/2 + Chunk.CUBE_LENGTH/2;
                                player.getVelocity().x = 0;
                                break;
                            }
                            
                        }
                        
                        if (player.getVelocity().z > 0)
                        {   
                            if ( player.getPos().z < z*Chunk.CUBE_LENGTH)
                            {
                                player.getPos().z = z*Chunk.CUBE_LENGTH - Player.SIZE_XZ/2 - Chunk.CUBE_LENGTH/2;
                                player.getVelocity().z = 0;
                                break;
                            }
                        }
                        else if (player.getVelocity().z < 0)
                        {
                            if ( player.getPos().z > z*Chunk.CUBE_LENGTH)
                            {
                                player.getPos().z = z*Chunk.CUBE_LENGTH + Player.SIZE_XZ/2 + Chunk.CUBE_LENGTH/2;
                                player.getVelocity().z = 0;
                                break;
                            }
                            
                        }
                        
                        
                    }
                }
            }
        }
    }
    
    private void rebuildChunkMeshes()
    {
        for(int i = 0; i < CHUNKS_X; i++)
        {
            for(int j = 0; j < CHUNKS_Z; j++)
            {
                chunks.get(i + " " + j).rebuildMesh();
            }
        }
    }
    
    public Texture getTexture1()
    {
        return texture1;
    }
    public Texture getTexture2()
    {
        return texture2;
    }
    
    private void initTextures()
    {
        texture1 = setTexture("terrain.png");
        texture1.setTextureFilter(GL_NEAREST);
        texture2 = setTexture("terrain2.png");
        texture2.setTextureFilter(GL_NEAREST);
    }
    
    private Texture setTexture(String fileName)
    {
        try
        {
            return TextureLoader.getTexture("PNG",
                    ResourceLoader.getResourceAsStream(fileName));
        }
        catch(Exception e)
        {
            e.printStackTrace();
        }
        
        return null;
    }
    
    public void changeTexture()
    {
        if(TEXTURE_ID == texture1.getTextureID())
        {
            TEXTURE_ID = texture2.getTextureID();
        }
        else
        {
            TEXTURE_ID = texture1.getTextureID();
        }
    }
    
    public void removeBlockAt(int x, int y, int z)
    {
        getBlock(x, y, z).setBlockType(Block.BlockType.AIR);
        getChunkContainingBlock(x, z).rebuildMesh();
    }
    
    // get block from block space
    public Block getBlock(int x, int y, int z)
    {
        int chunkX = x / Chunk.CHUNK_SIZE;
        int chunkZ = z / Chunk.CHUNK_SIZE;
        
        int blockX = x % Chunk.CHUNK_SIZE;
        int blockY = y;
        int blockZ = z % Chunk.CHUNK_SIZE;
        
        if(x < 0)
        {
            chunkX--;
        }
        if(z < 0)
        {
            chunkZ--;
        }
        
        
        if(chunks.get(chunkX + " " + chunkZ) == null || y < 0  || y > Chunk.CHUNK_SIZE_Y-1)
            return null;
        
        
        return chunks.get(chunkX + " " + chunkZ).getBlock(blockX, blockY, blockZ);
    }
    
    public Chunk getChunkContainingBlock(int x, int z)
    {
        int chunkX = x / Chunk.CHUNK_SIZE;
        int chunkZ = z / Chunk.CHUNK_SIZE;
        
        if(x < 0)
        {
            chunkX--;
        }
        if(z < 0)
        {
            chunkZ--;
        }
        
        return chunks.get(chunkX + " " + chunkZ);
    }
    
    public boolean isPlayerCollidingWithBlock(int x, int y, int z)
    {   
        // Block Collision Box Data
        float cubeSize = Chunk.CUBE_LENGTH;
        float blockWorldX = x*cubeSize;
        float blockWorldY = y*cubeSize;
        float blockWorldZ = z*cubeSize;
        
        // 
        float pMinX = player.getPos().x-Player.SIZE_XZ/2;
        float pMinY = player.getPos().y-Player.SIZE_Y/2;
        float pMinZ = player.getPos().z-Player.SIZE_XZ/2;
        float pMaxX = player.getPos().x+Player.SIZE_XZ/2;
        float pMaxY = player.getPos().y+Player.SIZE_Y/2;
        float pMaxZ = player.getPos().z+Player.SIZE_XZ/2;
        
        
        float bMinX = blockWorldX-Chunk.CUBE_LENGTH/2;
        float bMinY = blockWorldY-Chunk.CUBE_LENGTH/2;
        float bMinZ = blockWorldZ-Chunk.CUBE_LENGTH/2;
        float bMaxX = blockWorldX+Chunk.CUBE_LENGTH/2;
        float bMaxY = blockWorldY+Chunk.CUBE_LENGTH/2;
        float bMaxZ = blockWorldZ+Chunk.CUBE_LENGTH/2;
        
        
        if (getBlock(x, y, z) == null)
            return false;
        
        if(getBlock(x, y, z).getType() != Block.BlockType.AIR && getBlock(x, y, z).getType() != Block.BlockType.WATER)
        {
            if(
                pMinX <= bMaxX - MOVEMENT_EPSILON && pMaxX >= bMinX + MOVEMENT_EPSILON&&
                pMinY <= bMaxY - MOVEMENT_EPSILON && pMaxY >= bMinY + MOVEMENT_EPSILON&&
                pMinZ <= bMaxZ - MOVEMENT_EPSILON && pMaxZ >= bMinZ + MOVEMENT_EPSILON
                )
            {
            return true;
            }
        }
        
        return false;
    }
    
}
