/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package genericminecraft;

import org.lwjgl.LWJGLException;
import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.DisplayMode;
import org.lwjgl.opengl.Display;
import static org.lwjgl.opengl.GL11.GL_MODELVIEW;
import static org.lwjgl.opengl.GL11.GL_PROJECTION;
import static org.lwjgl.opengl.GL11.glLoadIdentity;
import static org.lwjgl.opengl.GL11.glMatrixMode;
import static org.lwjgl.opengl.GL11.glPushMatrix;
import static org.lwjgl.opengl.GL11.glViewport;
import static org.lwjgl.util.glu.GLU.gluOrtho2D;

/**
 *
 * @author ameer
 */
public class LWJGLMain {
    
    public static final int 
            W_WIDTH = 640,
            W_HEIGHT = 480;
    
    public LWJGLMain()
    {
        
    }
    
    // method: create
    // Call this to create the display, keyboard and mouse
    public void create() throws LWJGLException
    {
        // Display
        Display.setDisplayMode(new DisplayMode(W_WIDTH, W_HEIGHT));
        Display.setFullscreen(false);
        Display.setResizable(false);
        Display.setTitle("Generic Minecraft");
        Display.create();
        
        // Keyboard
        Keyboard.create();
        
        // Mouse
        Mouse.create();
        
        // OpenGL Code
        initGL();
        resizeGL();
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
    public void processKeyboard()
    {
        
    }
    
    // method: processMouse
    // make changes to the program based on mouse input
    public void processMouse()
    {
        
    }
    
    public void run()
    {
        while(!Display.isCloseRequested() && !Keyboard.isKeyDown(Keyboard.KEY_ESCAPE))
        {
            if(Display.isVisible())
            {
                Mouse.setGrabbed(true);
                processKeyboard();
                processMouse();
                update();
                render();
            }
            else if(Display.isDirty())
            {
                Mouse.setGrabbed(false);
                render();
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
    
    public void render()
    {
        
    }
    
    public void update()
    {
        
    }
    
    public void initGL()
    {
        
    }
    
    public void resizeGL()
    {
        glViewport(0, 0, W_WIDTH, W_HEIGHT);
        
        glMatrixMode(GL_PROJECTION);
        glLoadIdentity();
        gluOrtho2D(0, 0, W_WIDTH, W_WIDTH);
        glPushMatrix();
        
        glMatrixMode(GL_MODELVIEW);
        glLoadIdentity();
        glPushMatrix();
        
    }
    
}
