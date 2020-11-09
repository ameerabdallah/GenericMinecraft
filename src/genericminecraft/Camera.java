/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package genericminecraft;

import java.nio.FloatBuffer;
import org.lwjgl.BufferUtils;
import static org.lwjgl.opengl.GL11.GL_LIGHT0;
import static org.lwjgl.opengl.GL11.GL_POSITION;
import static org.lwjgl.opengl.GL11.glLight;
import static org.lwjgl.opengl.GL11.glPopMatrix;
import static org.lwjgl.opengl.GL11.glPushMatrix;
import static org.lwjgl.opengl.GL11.glRotatef;
import static org.lwjgl.opengl.GL11.glTranslatef;
import org.lwjgl.util.vector.Vector3f;

/***************************************************************
* @file: Camera.java
* @author: Ameer Abdallah
* @class: CS 4450 - Computer Graphics
* 
* @assignment: Checkpoint 2
* @date last modified: 10/12/2020
*
* @purpose: 
* Helps transform world space to screen space
*
****************************************************************/ 
public class Camera {
    private Vector3f pos;
    private Vector3f lPos;
    private float yaw;
    private float pitch;
    
    
    
    // method: Camera
    // construct a Camera object
    public Camera(float x, float y, float z)
    {
        pos = new Vector3f(x, y, z);
        lPos = new Vector3f(x, y, z);
        lPos.x = (4*Chunk.CUBE_LENGTH*Chunk.CHUNK_SIZE)/2f;
        lPos.y = 150.0f;
        lPos.z = (4*Chunk.CUBE_LENGTH*Chunk.CHUNK_SIZE)/2f;
        
        yaw = 180f;
        pitch = 0.0f;
        
    }
    
    // method: yaw
    // increment the local variable yaw by a value 'n'
    public void yaw(float n)
    {
        yaw += n;
    }
    
    // method: pitch
    // increment the local variable pitch by a value 'n' while clamping it
    // -89 degress < pitch < 89 degrees
    public void pitch(float n)
    {
        pitch -= n;
        if(pitch > 89.0f) pitch = 89.0f;
        else if(pitch < -89.0f) pitch  = -89.0f;
    }
    
    // method: walkForward
    // move forward
    public void walkForward(float distance)
    {
        float dx = distance * (float)Math.sin(Math.toRadians(yaw));
        float dz = distance * (float)Math.cos(Math.toRadians(yaw));
        pos.x -= dx;
        pos.z += dz;
    }
    
    // method: walkBackward
    // move backward
    public void walkBackward(float distance)
    {
        float dx = distance * (float)Math.sin(Math.toRadians(yaw+180));
        float dz = distance * (float)Math.cos(Math.toRadians(yaw+180));
        pos.x -= dx;
        pos.z += dz;
    }
    
    // method: strafeLeft
    // move left
    public void strafeLeft(float distance)
    {
        float dx = distance * (float)Math.sin(Math.toRadians(yaw-90));
        float dz = distance * (float)Math.cos(Math.toRadians(yaw-90));
        pos.x -= dx;
        pos.z += dz;
    }
    
    // method: strafeRight
    // move right
    public void strafeRight(float distance)
    {
        float dx = distance * (float)Math.sin(Math.toRadians(yaw+90));
        float dz = distance * (float)Math.cos(Math.toRadians(yaw+90));
        pos.x -= dx;
        pos.z += dz;
    }
    
    // method: moveUp
    // move up along y-axis
    public void moveUp(float distance)
    {
        pos.y -= distance;
    }
    
    // method: moveDown
    // move down along y-axis
    public void moveDown(float distance)
    {
        pos.y += distance;
    }
    
    // method: lookThrough
    // transform vertices to give camera perspective
    public void lookThrough()
    {
        glRotatef(pitch, 1.0f, 0.0f, 0.0f);
        glRotatef(yaw, 0.0f, 1.0f, 0.0f);
        glTranslatef(pos.x, pos.y, pos.z);
        
        FloatBuffer lightPosition = BufferUtils.createFloatBuffer(4);
        lightPosition.put(new float[]{lPos.x, lPos.y, lPos.z, 1.0f}).flip();
        glLight(GL_LIGHT0, GL_POSITION, lightPosition);
    }
    
    public Vector3f getPos()
    {
        return pos;
    }
}
