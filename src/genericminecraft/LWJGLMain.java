package genericminecraft;

import java.io.IOException;
import java.util.Random;
import org.lwjgl.LWJGLException;
import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.Display;
import org.lwjgl.opengl.DisplayMode;
import static org.lwjgl.opengl.GL11.GL_COLOR_ARRAY;
import static org.lwjgl.opengl.GL11.GL_COLOR_BUFFER_BIT;
import static org.lwjgl.opengl.GL11.GL_CULL_FACE;
import static org.lwjgl.opengl.GL11.GL_DEPTH_BUFFER_BIT;
import static org.lwjgl.opengl.GL11.GL_DEPTH_TEST;
import static org.lwjgl.opengl.GL11.GL_FRONT;
import static org.lwjgl.opengl.GL11.glClearColor;
import static org.lwjgl.opengl.GL11.GL_MODELVIEW;
import static org.lwjgl.opengl.GL11.GL_NICEST;
import static org.lwjgl.opengl.GL11.GL_PERSPECTIVE_CORRECTION_HINT;
import static org.lwjgl.opengl.GL11.GL_PROJECTION;
import static org.lwjgl.opengl.GL11.GL_TEXTURE_2D;
import static org.lwjgl.opengl.GL11.GL_TEXTURE_COORD_ARRAY;
import static org.lwjgl.opengl.GL11.GL_VERTEX_ARRAY;
import static org.lwjgl.opengl.GL11.glClear;
import static org.lwjgl.opengl.GL11.glCullFace;
import static org.lwjgl.opengl.GL11.glEnable;
import static org.lwjgl.opengl.GL11.glEnableClientState;
import static org.lwjgl.opengl.GL11.glFlush;
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
* @assignment: Checkpoint 2
* @date last modified: 10/25/2020
*
* @purpose: 
* Create a randomly generated world using simplex noise while utilizing multiple
* chunks.
****************************************************************/ 
public class LWJGLMain {
    
    // window information
    public static final int 
            W_WIDTH = 1920,
            W_HEIGHT = 1080;
    
    // movement sensativity
    private float mouseSensitivity;
    private float moveSpeed;
    private int iteration;
    
    // display information
    private DisplayMode displayMode;
    
    // timing information
    private long lastFrame;
    private long deltaTime;
    
    // camera information used to transform from world space to screen space
    private Camera camera;
    
    // chunks
    private Chunk chunks[][];
    
    
    
    // method: LWJGLMain
    // initialize information
    public LWJGLMain() throws IOException
    {
        lastFrame = getTime();
        
        mouseSensitivity = 0.005f;
        moveSpeed = 0.07f;
        iteration = 0;
        
    }
    
    // method: createChunks()
    // fill the array 'chunks' with chunks relative to each other based on indices
    public void createChunks()
    {
        SimplexNoise sNoise = new SimplexNoise(50, .2, new Random().nextInt());
        SimplexNoise humidityNoise = new SimplexNoise(150, 0.1, new Random().nextInt());
        SimplexNoise caveNoise = new SimplexNoise(5, 0.3, new Random().nextInt());
        
        chunks = new Chunk[4][4];
        for(int x = 0; x < chunks.length; x++)
            for(int z = 0; z < chunks[0].length; z++)
                chunks[x][z] = new Chunk(x, z, sNoise, humidityNoise);
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
        camera = new Camera(50f, -90f, -30f);
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
        float v = moveSpeed*(float)deltaTime;
        
        
        if(Keyboard.isKeyDown(Keyboard.KEY_D)) 
        {
            camera.strafeRight(v);
        }
        if(Keyboard.isKeyDown(Keyboard.KEY_A)) 
        {
            camera.strafeLeft(v);
        }
        if(Keyboard.isKeyDown(Keyboard.KEY_SPACE)) 
        {
            camera.moveUp(v);
        }
        if(Keyboard.isKeyDown(Keyboard.KEY_LSHIFT)) 
        {
            camera.moveDown(v);
        }
        if(Keyboard.isKeyDown(Keyboard.KEY_W)) 
        {
            camera.walkForward(v);
        }
        if(Keyboard.isKeyDown(Keyboard.KEY_S)) 
        {
            camera.walkBackward(v);
        }
    }
    
    // method: processMouse
    // make changes to the program based on mouse input
    private void processMouse()
    {
        float dx = Mouse.getX() - (W_WIDTH/2);
        float dy = Mouse.getY() - (W_HEIGHT/2);
        
        camera.yaw(dx * mouseSensitivity * (float)deltaTime);
        camera.pitch(dy * mouseSensitivity * (float)deltaTime);
        
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
            
            Display.update();
            Display.sync(60);
            
            if(iteration%100 == 0)
                System.out.println(deltaTime+" ms per iteration");
            iteration++;
        }
    }
    
    // method: render()
    // render the chunks and perform matrix operations
    private void render()
    {
        glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);
        
        glMatrixMode(GL_MODELVIEW);
        glLoadIdentity();
        
        camera.lookThrough();
        for(int x = 0; x < chunks.length; x++)
            for(int z = 0; z < chunks[x].length; z++ )
                chunks[x][z].render();
        
        glFlush();
    }
    
    // method: getDeltaTime
    // returns the time elapsed since the last time getDeltaTime() was called
    private long getDeltaTime()
    {
        long deltaTime = System.nanoTime() - lastFrame;
        
        lastFrame = getTime();
        
        return deltaTime/1000000;
    }
    
    // method: getTime
    // returns system time
    private long getTime()
    {
        return System.nanoTime();
    }
    
    // method: initGL
    // initialize openGL
    private void initGL()
    {
        glClearColor(0.5f, 0.95f, 1.0f, 1.0f);
        
        glMatrixMode(GL_PROJECTION);
        glLoadIdentity();
        gluPerspective(100.f, (float)displayMode.getWidth()/(float)displayMode.getHeight(),
                0.1f, 300.0f);
        glPushMatrix();
        
        glMatrixMode(GL_MODELVIEW);
        
        glEnableClientState(GL_VERTEX_ARRAY);
        glEnableClientState(GL_COLOR_ARRAY);
        glEnableClientState(GL_TEXTURE_COORD_ARRAY);
        glEnable(GL_DEPTH_TEST);
        glEnable(GL_TEXTURE_2D);
        glEnable(GL_CULL_FACE);
        glCullFace(GL_FRONT);
        
        glHint(GL_PERSPECTIVE_CORRECTION_HINT, GL_NICEST);
        glLoadIdentity();
        glPushMatrix();
    }
    
    
}
