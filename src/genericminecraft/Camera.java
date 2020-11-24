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
import static org.lwjgl.opengl.GL11.glRotatef;
import static org.lwjgl.opengl.GL11.glTranslatef;
import org.lwjgl.util.vector.Vector3f;

/***************************************************************
* @file: Camera.java
* @author: Ameer Abdallah
* @class: CS 4450 - Computer Graphics
* 
* @assignment: Checkpoint 3
* @date last modified: 11/08/2020
*
* @purpose: 
* Helps transform world space to screen space
*
****************************************************************/ 
public class Camera {
    private Vector3f pos;
    private Vector3f lPos;
    private Vector3f look;
    private float yaw;
    private float pitch;
    
    static final float PLAYER_HEIGHT = 2*Chunk.CUBE_LENGTH;
    static final float PLAYER_LENGTH = 2/Chunk.CUBE_LENGTH;
    
    
    
    // method: Camera
    // construct a Camera object
    public Camera(float x, float y, float z)
    {
        pos = new Vector3f(x, y, z);
        lPos = new Vector3f(x, y, z);
        lPos.x = (4*Chunk.CUBE_LENGTH*Chunk.CHUNK_SIZE)/2f;
        lPos.y = 150.0f;
        lPos.z = (4*Chunk.CUBE_LENGTH*Chunk.CHUNK_SIZE)/2f;
        
        yaw = 180.0f;
        pitch = 0.0f;
        float lookX, lookY, lookZ;
        lookX = (float)(Math.cos(Math.toRadians(pitch))*Math.sin(Math.toRadians(yaw)));
        lookY = (float)Math.sin(Math.toRadians(pitch));        
        lookZ = -(float)(Math.cos(Math.toRadians(pitch))*Math.cos(Math.toRadians(yaw)));

        look = new Vector3f(lookX, lookY, lookZ);
    }
    
    // method: yaw
    // increment the local variable yaw by a value 'n'
    public void yaw(float n)
    {
        yaw += n;
        float lookX, lookY, lookZ;
        lookX = (float)(Math.cos(Math.toRadians(pitch))*Math.sin(Math.toRadians(yaw)));
        lookY = (float)Math.sin(Math.toRadians(pitch));        
        lookZ = -(float)(Math.cos(Math.toRadians(pitch))*Math.cos(Math.toRadians(yaw)));

        look = new Vector3f(lookX, lookY, lookZ);
    }
    
    // method: pitch
    // increment the local variable pitch by a value 'n' while clamping it
    // -89 degress < pitch < 89 degrees
    public void pitch(float n)
    {
        pitch += n;
        if(pitch > 89.0f) pitch = 89.0f;
        else if(pitch < -89.0f) pitch  = -89.0f;
        
        float lookX, lookY, lookZ;
        lookX = (float)(Math.cos(Math.toRadians(pitch))*Math.sin(Math.toRadians(yaw)));
        lookY = (float)Math.sin(Math.toRadians(pitch));        
        lookZ = -(float)(Math.cos(Math.toRadians(pitch))*Math.cos(Math.toRadians(yaw)));

        look = new Vector3f(lookX, lookY, lookZ);
    }
    
    
    // method: lookThrough
    // transform vertices to give camera perspective
    public void lookThrough()
    {
        glRotatef(-pitch, 1.0f, 0.0f, 0.0f);
        glRotatef(yaw, 0.0f, 1.0f, 0.0f);
        glTranslatef(-pos.x, -pos.y, -pos.z);
        FloatBuffer lightPosition = BufferUtils.createFloatBuffer(4);
        lightPosition.put(new float[]{lPos.x, lPos.y, lPos.z, 1.0f}).flip();
        glLight(GL_LIGHT0, GL_POSITION, lightPosition);
    }
    
    public void setPos(float x, float y, float z)
    {
        this.pos = new Vector3f(x, y, z);
    }
    
    public Vector3f getPos()
    {
        return pos;
    }
    
    public Vector3f getLook()
    {
        return look;
    }
    
    public float getYaw()
    {
        return yaw;
    }
    
    public float getPitch()
    {
        return pitch;
    }
}
