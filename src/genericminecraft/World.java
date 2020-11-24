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
    
    private final int CHUNKS_X = 4;
    private final int CHUNKS_Z = 4;
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
        int pX = (int)player.getPosInBlockSpace().x;
        int pY = (int)player.getPosInBlockSpace().y;
        int pZ = (int)player.getPosInBlockSpace().z;
        
        float pW = Player.PLAYER_SIZE_XZ/4f;
        float pH = Player.PLAYER_SIZE_Y/4f;
        
        if(player.getVelocity().x > 0)
        {
            if( getBlock((int)(pX+pW), (int)(pY-pH), (int)pZ) != null)
            {
                if(isPlayerCollidingWithBlock((int)(pX+pW), (int)(pY-pH), (int)pZ))
                {
                    player.getVelocity().x = 0;
                }
            }
            if( getBlock((int)(pX+pW), (int)(pY+pH), (int)pZ) != null)
            {
                if(isPlayerCollidingWithBlock((int)(pX+pW), (int)(pY+pH), (int)pZ))
                {
                    player.getVelocity().x = 0;
                }
            }
        }
        
        if(player.getVelocity().x < 0)
        {
            if( getBlock((int)(pX-pW), (int)(pY-pH), (int)pZ) != null)
            {
                if(isPlayerCollidingWithBlock((int)(pX-pW), (int)(pY-pH), (int)pZ))
                {
                    player.getVelocity().x = 0;
                }
            }
            if( getBlock((int)(pX-pW), (int)(pY+pH), (int)pZ) != null)
            {
                if(isPlayerCollidingWithBlock((int)(pX-pW), (int)(pY+pH), (int)pZ))
                {
                    player.getVelocity().x = 0;
                }
            }
        }
        
        if(player.getVelocity().z > 0)
        {
            if( getBlock((int)pX, (int)(pY-pH), (int)(pZ+pW)) != null)
            {
                if(isPlayerCollidingWithBlock((int)pX, (int)(pY-pH), (int)(pZ+pW)))
                {
                    player.getVelocity().z = 0;
                }
            }
            if( getBlock((int)pX, (int)(pY+pH), (int)(pZ+pW)) != null)
            {
                if(isPlayerCollidingWithBlock((int)pX, (int)(pY+pH), (int)(pZ+pW)))
                {
                    player.getVelocity().z = 0;
                }
            }
        }
        
        if(player.getVelocity().z < 0)
        {
            if( getBlock((int)pX, (int)(pY-pH), (int)(pZ-pW)) != null)
            {
                if(isPlayerCollidingWithBlock((int)pX, (int)(pY-pH), (int)(pZ-pW)))
                {
                    player.getVelocity().z = 0;
                }
            }
            if( getBlock((int)pX, (int)(pY+pH), (int)(pZ-pW)) != null)
            {
                if(isPlayerCollidingWithBlock((int)pX, (int)(pY+pH), (int)(pZ-pW)))
                {
                    player.getVelocity().z = 0;
                }
            }
        }
        
        if(player.getVelocity().y > 0)
        {
            if( getBlock((int)pX, (int)(pY+pH*2), (int)pZ) != null )
            {
                if(isPlayerCollidingWithBlock((int)pX, (int)(pY+pH*2), (int)pZ))
                    player.getVelocity().y = 0;
            }
        }
        if(player.getVelocity().y < 0)
        {
            if( getBlock((int)pX, (int)(pY-pH*2), (int)pZ) != null )
            {
                if(isPlayerCollidingWithBlock((int)pX, (int)(pY-pH*2), (int)pZ))
                    player.getVelocity().y = 0;
            }
        }
    }
    
    public void removeBlockAt(int x, int y, int z)
    {
        getBlock(x, y, z).setBlockType(Block.BlockType.AIR);
        getChunk(x, z).rebuildMesh();
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
    
    public Chunk getChunk(int x, int z)
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
        
        if(getBlock(x, y, z).getType() != Block.BlockType.AIR && getBlock(x, y, z).getType() != Block.BlockType.WATER)
        {
            if(
                Math.abs(blockWorldY - player.getPos().y) < cubeSize + Player.PLAYER_SIZE_Y &&
                Math.abs(blockWorldX - player.getPos().x) < cubeSize + Player.PLAYER_SIZE_XZ &&
                Math.abs(blockWorldZ - player.getPos().z) < cubeSize + Player.PLAYER_SIZE_XZ
                )
            {
            return true;
            }
        }
        
        return false;
    }
}
