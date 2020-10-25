/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package genericminecraft;

import static org.lwjgl.opengl.GL11.GL_QUADS;
import static org.lwjgl.opengl.GL11.glBegin;
import static org.lwjgl.opengl.GL11.glColor3f;
import static org.lwjgl.opengl.GL11.glEnd;
import static org.lwjgl.opengl.GL11.glVertex3f;

/***************************************************************
* @file: Block.java
* @author: Ameer Abdallah
* @class: CS 4450 - Computer Graphics
* 
* @assignment: Checkpoint 1
* @date last modified: 10/12/2020
*
* @purpose: 
* Holds block information
****************************************************************/ 
public class Block 
{
    private boolean isActive; // to draw or not to draw
    private BlockType blockType; // whatever kind of block it is
    
    // method: Block
    // construct a Block object
    public Block(BlockType blockType)
    {
        this.blockType = blockType;
        isActive = true;
    }
    
    public enum BlockType 
    {
        GRASS(0),
        DIRT(1),
        SAND(2),
        STONE(3),
        BEDROCK(4),
        WATER(5),
        GRAVEL(6),
        WOOD_LOG(7);

        private int BlockID;

        BlockType(int i)
        {
            BlockID = i;
        }

        public int GetID()
        {
            return BlockID;
        }
        public void SetID(int i)
        {
            BlockID = i;
        }
    }
    
    public boolean isActive()
    {
        return isActive;
    }
    
    public void setActive(boolean activeStatus)
    {
        this.isActive = activeStatus;
    }
    
    public int GetID()
    {
        return blockType.GetID();
    }
    
    public BlockType getType()
    {
        return blockType;
    }
}
