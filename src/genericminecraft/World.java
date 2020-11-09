/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package genericminecraft;

import java.util.Random;

/**
 *
 * @author Ameer Abdallah <Ameer Abdallah>
 */
public class World {
    
    private Chunk chunks[][];
    
    static final int SEED = new Random().nextInt();
    static final Random R = new Random(SEED);
    static final SimplexNoise HEIGHT_NOISE  = new SimplexNoise(150, 0.4, R.nextInt());
    static final SimplexNoise HUMIDITY_NOISE = new SimplexNoise(300, 0.6, R.nextInt());
    
    public World()
    {
        loadChunks();
    }
    
    private void loadChunks()
    {
        chunks = new Chunk[4][4];
        
        for(int i = 0; i < chunks.length; i++)
        {
            for(int j = 0; j < chunks[i].length; j++)
            {
                chunks[i][j] = new Chunk(i, j);
            }
        }
        
        for(int i = 0; i < chunks.length; i++)
        {
            for(int j = 0; j < chunks[i].length; j++)
            {
                chunks[i][j].initializeSurroundingBlockValues();
                chunks[i][j].rebuildMesh();
            }
        }
    }
    
    public void render()
    {
        for(int i = 0; i < chunks.length; i++)
        {
            for(int j = 0; j < chunks[i].length; j++)
            {
                chunks[i][j].render();
            }
        }
    }
    
}
