package genericminecraft;

import java.util.HashMap;
import java.util.Random;

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
    
    private final int COLLISION_CHECK_DISTANCE = 2;
    private final int CHUNKS_X = 4;
    private final int CHUNKS_Z = 2;
    static final int SEED = new Random().nextInt();
    static final Random R = new Random(SEED);
    static final SimplexNoise HEIGHT_NOISE  = new SimplexNoise(150, 0.4, R.nextInt());
    static final SimplexNoise HUMIDITY_NOISE = new SimplexNoise(300, 0.6, R.nextInt());
    private Player player;
    
    public World(Player player)
    {
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
                        
                        if ((int)player.getPosInBlockSpace().x == x && (int)player.getPosInBlockSpace().z == z)
                        {
                            if (player.getVelocity().y > 0)
                            { 
                                if ( player.getPos().y < y*Chunk.CUBE_LENGTH )
                                {
                                    player.getVelocity().y = 0;
                                    player.getPos().y = y*Chunk.CUBE_LENGTH - Player.SIZE_Y/2 - Chunk.CUBE_LENGTH/2;
                                }
                            }
                            else if (player.getVelocity().y < 0)
                            {
                                if ( player.getPos().y > y*Chunk.CUBE_LENGTH)
                                {
                                    player.getVelocity().y = 0;
                                    player.getPos().y = y*Chunk.CUBE_LENGTH + Player.SIZE_Y/2 + Chunk.CUBE_LENGTH/2;
                                }
                            }
                            break;
                        }
                        if (player.getVelocity().x > 0)
                        {   
                            if ( player.getPos().x < x*Chunk.CUBE_LENGTH )
                            {
                                player.getPos().x = x*Chunk.CUBE_LENGTH - Player.SIZE_XZ/2 - Chunk.CUBE_LENGTH/2;
                                player.getVelocity().x = 0;
                            }
                        }
                        else if (player.getVelocity().x < 0)
                        {
                            if ( player.getPos().x > x*Chunk.CUBE_LENGTH )
                            {
                                player.getPos().x =  x*Chunk.CUBE_LENGTH + Player.SIZE_XZ/2 + Chunk.CUBE_LENGTH/2;
                                player.getVelocity().x = 0;
                            }
                            
                        }
                        
                        if (player.getVelocity().z > 0)
                        {   
                            if ( player.getPos().z < z*Chunk.CUBE_LENGTH )
                            {
                                player.getPos().z = z*Chunk.CUBE_LENGTH - Player.SIZE_XZ/2 - Chunk.CUBE_LENGTH/2;
                                player.getVelocity().z = 0;
                            }
                        }
                        else if (player.getVelocity().z < 0)
                        {
                            if ( player.getPos().z > z*Chunk.CUBE_LENGTH )
                            {
                                player.getPos().z = z*Chunk.CUBE_LENGTH + Player.SIZE_XZ/2 + Chunk.CUBE_LENGTH/2;
                                player.getVelocity().z = 0;
                            }
                            
                        }
                        
                        
                    }
                }
            }
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
        
        if (getBlock(x, y, z) == null)
            return false;
        
        if(getBlock(x, y, z).getType() != Block.BlockType.AIR && getBlock(x, y, z).getType() != Block.BlockType.WATER)
        {
            if(
                Math.abs(blockWorldY - player.getPos().y) < cubeSize + Player.SIZE_Y &&
                Math.abs(blockWorldX - player.getPos().x) < cubeSize + Player.SIZE_XZ &&
                Math.abs(blockWorldZ - player.getPos().z) < cubeSize + Player.SIZE_XZ
                )
            {
            return true;
            }
        }
        
        return false;
    }
    public boolean willPlayerCollideWithBlock(int x, int y, int z)
    {   
        // Block Collision Box Data
        float cubeSize = Chunk.CUBE_LENGTH;
        float blockWorldX = x*cubeSize;
        float blockWorldY = y*cubeSize;
        float blockWorldZ = z*cubeSize;
        
        if (getBlock(x, y, z) == null)
            return false;
        
        if(getBlock(x, y, z).getType() != Block.BlockType.AIR && getBlock(x, y, z).getType() != Block.BlockType.WATER)
        {
            if(
                Math.abs(blockWorldY - player.getPos().y + player.getVelocity().y) < cubeSize + Player.SIZE_Y &&
                Math.abs(blockWorldX - player.getPos().x + player.getVelocity().x) < cubeSize + Player.SIZE_XZ &&
                Math.abs(blockWorldZ - player.getPos().z + player.getVelocity().z) < cubeSize + Player.SIZE_XZ
                )
            {
            return true;
            }
        }
        
        return false;
    }
}
