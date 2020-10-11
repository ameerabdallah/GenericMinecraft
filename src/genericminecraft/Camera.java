/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package genericminecraft;

import static org.lwjgl.opengl.GL11.glRotatef;
import static org.lwjgl.opengl.GL11.glTranslatef;
import org.lwjgl.util.vector.Vector3f;

/**
 *
 * @author ameer
 */
public class Camera {
    private Vector3f pos;
    private Vector3f iPos;
    private float yaw;
    private float pitch;
    
    public Camera(float x, float y, float z)
    {
        pos = new Vector3f(x, y, z);
        iPos = new Vector3f(x, y, z);
        iPos.x = 0.0f;
        iPos.y = 15f;
        iPos.z = 0.0f;
        
        yaw = 0.0f;
        pitch = 0.0f;
    }
    
    public void yaw(float n)
    {
        yaw += n;
    }
    
    public void pitch(float n)
    {
        if(pitch > 89.0f) pitch = 89.0f;
        if(pitch < -89.0f) pitch  = -89.0f;
        pitch -= n;
    }
    
    public void walkForward(float distance)
    {
        float dx = distance * (float)Math.sin(Math.toRadians(yaw));
        float dz = distance * (float)Math.cos(Math.toRadians(yaw));
        pos.x -= dx;
        pos.z += dz;
    }
    
    public void walkBackward(float distance)
    {
        float dx = distance * (float)Math.sin(Math.toRadians(yaw+180));
        float dz = distance * (float)Math.cos(Math.toRadians(yaw+180));
        pos.x -= dx;
        pos.z += dz;
    }
    
    public void strafeLeft(float distance)
    {
        float dx = distance * (float)Math.sin(Math.toRadians(yaw-90));
        float dz = distance * (float)Math.cos(Math.toRadians(yaw-90));
        pos.x -= dx;
        pos.z += dz;
    }
    
    public void strafeRight(float distance)
    {
        float dx = distance * (float)Math.sin(Math.toRadians(yaw+90));
        float dz = distance * (float)Math.cos(Math.toRadians(yaw+90));
        pos.x -= dx;
        pos.z += dz;
    }
    
    public void moveUp(float distance)
    {
        pos.y -= distance;
    }
    
    public void moveDown(float distance)
    {
        pos.y += distance;
    }
    
    public void lookThrough()
    {
        glRotatef(pitch, 1.0f, 0.0f, 0.0f);
        glRotatef(yaw, 0.0f, 1.0f, 0.0f);
        
        glTranslatef(pos.x, pos.y, pos.z);
    }
}
