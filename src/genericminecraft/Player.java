/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package genericminecraft;

import org.lwjgl.util.vector.Vector3f;

/**
 *
 * @author Ameer Abdallah <Ameer Abdallah>
 */
public class Player 
{
    public static final float PLAYER_SIZE_Y = Chunk.CUBE_LENGTH*2;
    public static final float PLAYER_SIZE_XZ = Chunk.CUBE_LENGTH/2;
    private final float GRAVITY = .01f;
    private final float TERMINAL_VELOCITY = 10f;
    private final float ACCELERATION_XZ = 0.005f;
    private final float JUMP_ACCELERATION = 0.07f;
    
    private boolean flying;
    
    private Vector3f velocity;
    private Vector3f pos;
    private Vector3f posInBlockSpace;
    
    private Camera camera;
    
    public Player(float x, float y, float z)
    {
        flying = true;
        
        camera = new Camera(0, 0, 0);
        velocity = new Vector3f(0, 0, 0);
        pos = new Vector3f(x, y, z);
        posInBlockSpace = new Vector3f(x, y, z);
        
        updatePosition();
    }
    
    public void updateVelocityJump(float dt)
    {
        pos.y += 0.5f;
        velocity.y += JUMP_ACCELERATION*dt;
    }
    
    public void updateVelocityFlying(float dt)
    {
        velocity.y += ACCELERATION_XZ*dt;
    }
    
    public void updateVelocityDropping(float dt)
    {
        velocity.y -= ACCELERATION_XZ*dt;
    }
    
    public void updateVelocityForward(float dt)
    {
        velocity.x += ACCELERATION_XZ * (float)Math.sin(Math.toRadians(camera.getYaw()))*dt;
        velocity.z -= ACCELERATION_XZ * (float)Math.cos(Math.toRadians(camera.getYaw()))*dt;
    }
    
    public void updateVelocityBackward(float dt)
    {
        velocity.x -= ACCELERATION_XZ * (float)Math.sin(Math.toRadians(camera.getYaw()))*dt;
        velocity.z += ACCELERATION_XZ * (float)Math.cos(Math.toRadians(camera.getYaw()))*dt;
    }
    
    public void updateVelocityLeft(float dt)
    {
        velocity.x += ACCELERATION_XZ * (float)Math.sin(Math.toRadians(camera.getYaw()-90))*dt;
        velocity.z -= ACCELERATION_XZ * (float)Math.cos(Math.toRadians(camera.getYaw()-90))*dt;
    }
    
    public void updateVelocityRight(float dt)
    {
        velocity.x += ACCELERATION_XZ * (float)Math.sin(Math.toRadians(camera.getYaw()+90))*dt;
        velocity.z -= ACCELERATION_XZ * (float)Math.cos(Math.toRadians(camera.getYaw()+90))*dt;
    }
    
    public void updateGravity(float dt)
    {
        velocity.y -= GRAVITY*dt;
        if(Math.abs(velocity.y) > TERMINAL_VELOCITY)
            velocity.y = -TERMINAL_VELOCITY;
    }
    
    public void updatePosition()
    {
        pos.x += velocity.x;
        pos.y += velocity.y;
        pos.z += velocity.z;
        
        // Adjust camera to move with player appropriately
        camera.getPos().x = pos.x;
        camera.getPos().y = pos.y+(0.25f)*PLAYER_SIZE_Y;
        camera.getPos().z = pos.z;
        
        posToBlockSpace();
    }
    
    public Camera getCamera()
    {
        return camera;
    }
    
    public Vector3f getPos()
    {
        return pos;
    }
    
    public boolean isFlying()
    {
        return flying;
    }
    
    public void setFlying(boolean flying)
    {
        this.flying = flying;
    }
    
    public void updateVelocity(float dt)
    {
        if(!flying && Math.round(velocity.y*10)/10 <= 0)
            updateGravity(dt);
        
        velocity.scale(.85f);
    }
    
    
    public Vector3f getVelocity()
    {
        return velocity;
    }
    
    public Vector3f getPosInBlockSpace()
    {
        return posInBlockSpace;
    }
    
    private void posToBlockSpace()
    {
        posInBlockSpace.x = pos.x/Chunk.CUBE_LENGTH;
        posInBlockSpace.y = pos.y/Chunk.CUBE_LENGTH;
        posInBlockSpace.z = pos.z/Chunk.CUBE_LENGTH;
    }
}
