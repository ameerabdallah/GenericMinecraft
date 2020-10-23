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
public class Block {
    
    private float blockLength;
    private float x, y, z;
    private BlockType blockType;
    
    // method: Block
    // construct a Block object
    public Block(float x, float y, float z, BlockType blockType)
    {
        this.blockType = blockType;
        blockLength = 1;
        this.x = x*2*blockLength;
        this.y = y*2*blockLength;
        this.z = z*2*blockLength;
    }
    
    // method: drawBlock
    // display block to the display
    public void drawBlock()
    {
        glBegin(GL_QUADS);
            // Top
            glColor3f(0.0f,0.0f,1.0f);
            glVertex3f(x+blockLength, y+blockLength, z-blockLength);
            glVertex3f(x-blockLength, y+blockLength, z-blockLength);
            glVertex3f(x-blockLength, y+blockLength, z+blockLength);
            glVertex3f(x+blockLength, y+blockLength, z+blockLength);
            // Bottom
            glColor3f(0.0f, 1.0f, 0.0f);
            glVertex3f(x+blockLength, y-blockLength, z+blockLength);
            glVertex3f(x-blockLength, y-blockLength, z+blockLength);
            glVertex3f(x-blockLength, y-blockLength, z-blockLength);
            glVertex3f(x+blockLength, y-blockLength, z-blockLength);
            // Front
            glColor3f(1.0f, 0.0f, 0.0f);
            glVertex3f(x+blockLength, y+blockLength, z+blockLength);
            glVertex3f(x-blockLength, y+blockLength, z+blockLength);
            glVertex3f(x-blockLength, y-blockLength, z+blockLength);
            glVertex3f(x+blockLength, y-blockLength, z+blockLength);
            // Back
            glColor3f(1.0f, 1.0f, 0.0f);
            glVertex3f(x+blockLength, y-blockLength, z-blockLength);
            glVertex3f(x-blockLength, y-blockLength, z-blockLength);
            glVertex3f(x-blockLength, y+blockLength, z-blockLength);
            glVertex3f(x+blockLength, y+blockLength, z-blockLength);
            // Left
            glColor3f(0.0f, 1.0f, 1.0f);
            glVertex3f(x-blockLength, y+blockLength, z+blockLength);
            glVertex3f(x-blockLength, y+blockLength, z-blockLength);
            glVertex3f(x-blockLength, y-blockLength, z-blockLength);
            glVertex3f(x-blockLength, y-blockLength, z+blockLength);
            // Right
            glColor3f(1.0f, 1.0f, 1.0f);
            glVertex3f(x+blockLength, y+blockLength, z-blockLength);
            glVertex3f(x+blockLength, y+blockLength, z+blockLength);
            glVertex3f(x+blockLength, y-blockLength, z+blockLength);
            glVertex3f(x+blockLength, y-blockLength, z-blockLength);
        glEnd();
    }
    
}
