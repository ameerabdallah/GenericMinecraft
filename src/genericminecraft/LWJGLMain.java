/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package genericminecraft;

import java.io.IOException;
import java.util.Random;
import org.lwjgl.LWJGLException;
import org.lwjgl.Sys;
import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.Display;
import org.lwjgl.opengl.DisplayMode;
import static org.lwjgl.opengl.GL11.GL_BLEND;
import static org.lwjgl.opengl.GL11.GL_COLOR_ARRAY;
import static org.lwjgl.opengl.GL11.GL_COLOR_BUFFER_BIT;
import static org.lwjgl.opengl.GL11.GL_DEPTH_BUFFER_BIT;
import static org.lwjgl.opengl.GL11.GL_DEPTH_TEST;
import static org.lwjgl.opengl.GL11.glClearColor;
import static org.lwjgl.opengl.GL11.GL_MODELVIEW;
import static org.lwjgl.opengl.GL11.GL_NICEST;
import static org.lwjgl.opengl.GL11.GL_PERSPECTIVE_CORRECTION_HINT;
import static org.lwjgl.opengl.GL11.GL_PROJECTION;
import static org.lwjgl.opengl.GL11.GL_TEXTURE_2D;
import static org.lwjgl.opengl.GL11.GL_TEXTURE_COORD_ARRAY;
import static org.lwjgl.opengl.GL11.GL_VERTEX_ARRAY;
import static org.lwjgl.opengl.GL11.glClear;
import static org.lwjgl.opengl.GL11.glEnable;
import static org.lwjgl.opengl.GL11.glEnableClientState;
import static org.lwjgl.opengl.GL11.glHint;
import static org.lwjgl.opengl.GL11.glLoadIdentity;
import static org.lwjgl.opengl.GL11.glMatrixMode;
import static org.lwjgl.opengl.GL11.glPushMatrix;
import static org.lwjgl.util.glu.GLU.gluPerspective;

/***************************************************************
* @file: LWJGLMain.java
* @author: Ameer Abdallah
* @class: CS 4450 - Computer Graphics
* 
* @assignment: Checkpoint 1
* @date last modified: 10/12/2020
*
* @purpose: 
* Display a cube with different colors on each side. using the w a s d keys to move
* and the mouse to look around you can move around the cube
*
****************************************************************/ 
public class LWJGLMain {
    
    // window information
    public static final int 
            W_WIDTH = 1920,
            W_HEIGHT = 1080;
    
    // movement sensativity
    private float mouseSensitivity;
    private float moveSpeed;
    
    // display information
    private DisplayMode displayMode;
    
    // timing information
    private float lastFrame;
    private float deltaTime;
    
    // camera information used to transform from world space to screen space
    private Camera camera;
    
    private Chunk chunks[][];
    
    
    // method: LWJGLMain
    // initialize information
    public LWJGLMain() throws IOException
    {
        lastFrame = getTime();
        
        mouseSensitivity = 0.001f;
        moveSpeed = 0.05f;
        
    }
    
    public void createChunks()
    {
        chunks = new Chunk[5][20];
        for(int x = 0; x < chunks.length; x++)
            for(int z = 0; z < chunks[x].length; z++)
                chunks[x][z] = new Chunk(x, z, new SimplexNoise(70, .1, 50));
    }
    
    // method: create
    // Call this to create the display, keyboard and mouse
    public void create() throws LWJGLException
    {
        // Display
        Display.setDisplayMode(new DisplayMode(W_WIDTH, W_HEIGHT));
        Display.setFullscreen(false);
        Display.setTitle("Generic Minecraft");
        
        // referenced from lecture slides on creating display '3D Viewing Lecture'
        DisplayMode d[] = Display.getAvailableDisplayModes();
        
        for(int i = 0; i < d.length; i++)
        {
            if(d[i].getWidth() == W_WIDTH
                    && d[i].getHeight() == W_HEIGHT
                    && d[i].getBitsPerPixel() == 32)
            {
                displayMode = d[i];
                break;
            }
        }
        Display.setDisplayMode(displayMode);
        Display.create();
        
        // Keyboard
        Keyboard.create();
        
        // Mouse
        Mouse.create();
        Mouse.setCursorPosition(W_WIDTH/2, W_HEIGHT/2);
        
        // OpenGL Code
        initGL();
        
        // create camera
        camera = new Camera(0f, 0f, -40f);
    }
    
    // method: destroy
    // this should be called before the program is terminated
    public void destroy()
    {
        Display.destroy();
        Keyboard.destroy();
        Mouse.destroy();
    }
    
    // method: processKeyboard
    // make changes to the program based on keyboard input
    private void processKeyboard()
    {
        float v = moveSpeed*deltaTime;
        
        if(Keyboard.isKeyDown(Keyboard.KEY_D)) camera.strafeRight(v);
        if(Keyboard.isKeyDown(Keyboard.KEY_A)) camera.strafeLeft(v);
        if(Keyboard.isKeyDown(Keyboard.KEY_SPACE)) camera.moveUp(v);
        if(Keyboard.isKeyDown(Keyboard.KEY_LSHIFT)) camera.moveDown(v);
        if(Keyboard.isKeyDown(Keyboard.KEY_W)) camera.walkForward(v);
        if(Keyboard.isKeyDown(Keyboard.KEY_S)) camera.walkBackward(v);
    }
    
    // method: processMouse
    // make changes to the program based on mouse input
    private void processMouse()
    {
        float dx = Mouse.getX()- (W_WIDTH/2);
        float dy = Mouse.getY() - (W_HEIGHT/2);
        
        camera.yaw(dx * mouseSensitivity * deltaTime);
        camera.pitch(dy * mouseSensitivity * deltaTime);
        
        Mouse.setCursorPosition(W_WIDTH/2, W_HEIGHT/2);
    }
    
    // method: run
    // runs the game loop
    public void run()
    {
        while(!Display.isCloseRequested() && !Keyboard.isKeyDown(Keyboard.KEY_ESCAPE))
        {
            deltaTime = getDeltaTime();
            if(Display.isVisible())
            {
                Mouse.setGrabbed(true);
                processKeyboard();
                processMouse();
                render();
            }
            else 
            {
                Mouse.setGrabbed(false);
                if(Display.isDirty())
                {
                    render();
                }
            }
            try
            {
                Thread.sleep(100);
            }
            catch(InterruptedException ex)
            {
                
            }
            
            Display.update();
            Display.sync(60);
        }
    }
    
    private void render()
    {
        glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);
        
        glMatrixMode(GL_MODELVIEW);
        glLoadIdentity();
        
        camera.lookThrough();
        for(int x = 0; x < chunks.length; x++)
            for(int z = 0; z < chunks[x].length; z++ )
                chunks[x][z].render();
    }
    
    // method: getDeltaTime
    // returns the time elapsed since the last time getDeltaTime() was called
    private float getDeltaTime()
    {
        double currentTime = getTime();
        double deltaTime = currentTime - lastFrame;
        lastFrame = getTime();
        return (float)deltaTime;
    }
    
    // method: getTime
    // returns system time
    private long getTime()
    {
        return (Sys.getTime() * 1000) / Sys.getTimerResolution();
    }
    
    // method: initGL
    // initialize openGL
    private void initGL()
    {
        glClearColor(0.0f, 0.0f, 0.0f, 0.0f);
        
        glMatrixMode(GL_PROJECTION);
        glLoadIdentity();
        gluPerspective(100.f, (float)displayMode.getWidth()/(float)displayMode.getHeight(),
                0.1f, 300.0f);
        glPushMatrix();
        
        glMatrixMode(GL_MODELVIEW);
        
        glEnableClientState(GL_VERTEX_ARRAY);
        glEnableClientState(GL_COLOR_ARRAY);
        glEnable(GL_DEPTH_TEST);
        glEnable(GL_TEXTURE_2D);
        glEnableClientState(GL_TEXTURE_COORD_ARRAY);
        glEnable(GL_BLEND);
        
        glHint(GL_PERSPECTIVE_CORRECTION_HINT, GL_NICEST);
        glLoadIdentity();
        glPushMatrix();
    }
    
    
}
