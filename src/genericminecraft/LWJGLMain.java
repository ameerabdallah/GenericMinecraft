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
        Mouse.setGrabbed(true);
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
        
    }
    
}
