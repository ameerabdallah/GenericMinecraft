package genericminecraft;

import java.io.IOException;
import java.nio.FloatBuffer;
import java.util.Random;
import org.lwjgl.BufferUtils;
import org.lwjgl.LWJGLException;
import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.Display;
import org.lwjgl.opengl.DisplayMode;
import static org.lwjgl.opengl.GL11.GL_AMBIENT;
import static org.lwjgl.opengl.GL11.GL_COLOR_ARRAY;
import static org.lwjgl.opengl.GL11.GL_COLOR_BUFFER_BIT;
import static org.lwjgl.opengl.GL11.GL_COLOR_MATERIAL;
import static org.lwjgl.opengl.GL11.GL_CULL_FACE;
import static org.lwjgl.opengl.GL11.GL_DEPTH_BUFFER_BIT;
import static org.lwjgl.opengl.GL11.GL_DEPTH_TEST;
import static org.lwjgl.opengl.GL11.GL_DIFFUSE;
import static org.lwjgl.opengl.GL11.GL_FILL;
import static org.lwjgl.opengl.GL11.GL_FRONT;
import static org.lwjgl.opengl.GL11.GL_FRONT_AND_BACK;
import static org.lwjgl.opengl.GL11.GL_LIGHT0;
import static org.lwjgl.opengl.GL11.GL_LIGHTING;
import static org.lwjgl.opengl.GL11.GL_LINE;
import static org.lwjgl.opengl.GL11.GL_LINES;
import static org.lwjgl.opengl.GL11.glClearColor;
import static org.lwjgl.opengl.GL11.GL_MODELVIEW;
import static org.lwjgl.opengl.GL11.GL_NICEST;
import static org.lwjgl.opengl.GL11.GL_PERSPECTIVE_CORRECTION_HINT;
import static org.lwjgl.opengl.GL11.GL_PROJECTION;
import static org.lwjgl.opengl.GL11.GL_QUADS;
import static org.lwjgl.opengl.GL11.GL_SPECULAR;
import static org.lwjgl.opengl.GL11.GL_TEXTURE_2D;
import static org.lwjgl.opengl.GL11.GL_TEXTURE_COORD_ARRAY;
import static org.lwjgl.opengl.GL11.GL_VERTEX_ARRAY;
import static org.lwjgl.opengl.GL11.glBegin;
import static org.lwjgl.opengl.GL11.glClear;
import static org.lwjgl.opengl.GL11.glColor3f;
import static org.lwjgl.opengl.GL11.glCullFace;
import static org.lwjgl.opengl.GL11.glDisable;
import static org.lwjgl.opengl.GL11.glEnable;
import static org.lwjgl.opengl.GL11.glEnableClientState;
import static org.lwjgl.opengl.GL11.glEnd;
import static org.lwjgl.opengl.GL11.glFlush;
import static org.lwjgl.opengl.GL11.glHint;
import static org.lwjgl.opengl.GL11.glLight;
import static org.lwjgl.opengl.GL11.glLoadIdentity;
import static org.lwjgl.opengl.GL11.glMatrixMode;
import static org.lwjgl.opengl.GL11.glPolygonMode;
import static org.lwjgl.opengl.GL11.glPushMatrix;
import static org.lwjgl.opengl.GL11.glVertex3f;
import static org.lwjgl.util.glu.GLU.gluPerspective;
import org.lwjgl.util.vector.Vector3f;

/***************************************************************
* @file: LWJGLMain.java
* @author: Ameer Abdallah
* @class: CS 4450 - Computer Graphics
* 
* @assignment: Checkpoint 3
* @date last modified: 11/08/2020
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
    
    private FloatBuffer diffuseWhiteLight;
    private FloatBuffer specularWhiteLight;
    private FloatBuffer ambientWhiteLight;
    
    // player information
    // movement sensativity
    private float mouseSensitivity;
    private float moveSpeed;
    private int iteration;
    private int seed;
    
    // display information
    private DisplayMode displayMode;
    
    // timing information
    private long lastFrame;
    private long dt;
    
    // camera information used to transform from world space to screen space
    private Player player;
    private Random r;
    
    // World
    private World world;
    public Chunk textureChanger;
    // method: LWJGLMain
    // initialize information
    public LWJGLMain() throws IOException
    {
        lastFrame = getTime();
        
        mouseSensitivity = 0.005f;
        moveSpeed = 0.07f;
        iteration = 0;
        seed = new Random().nextInt();
        r = new Random(seed);
        
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
        player = new Player(10f, 90f, -10f);
        
        world = new World(player);
        
        System.out.println(Chunk.timer);
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
        InputHelper.update();
        
        if (InputHelper.isKeyDown(Keyboard.KEY_F1)) 
        {
            glPolygonMode(GL_FRONT_AND_BACK, GL_LINE);
            glDisable(GL_TEXTURE_2D);
        }
        if (InputHelper.isKeyDown(Keyboard.KEY_F2)) 
        {
            glPolygonMode(GL_FRONT_AND_BACK, GL_FILL);
            glEnable(GL_TEXTURE_2D);
        }
        if (InputHelper.isKeyReleased(Keyboard.KEY_F3))
        {
            player.toggleFirstPerson();
        }
        if (InputHelper.isKeyReleased(Keyboard.KEY_F4))
        {
            world.changeTexture();
        }
        if(InputHelper.isKeyDown(Keyboard.KEY_D)) 
        {
            player.updateVelocityRight(dt);
        }
        if(InputHelper.isKeyDown(Keyboard.KEY_A)) 
        {
            player.updateVelocityLeft(dt);
        }
        if(InputHelper.isKeyReleased(Keyboard.KEY_F))
        {
            player.setFlying(player.isFlying() ? false : true);
            player.getVelocity().y = 0;
        }
        if(InputHelper.isKeyDown(Keyboard.KEY_SPACE)) 
        {
            if (player.isFlying())
                player.updateVelocityFlying(dt);
            else if (player.isGrounded())
            {
                player.setGrounded(false);
                player.updateVelocityJump(dt);
            }
        }
        if(InputHelper.isKeyDown(Keyboard.KEY_LSHIFT)) 
        {
            if(player.isFlying())
                player.updateVelocityDropping(dt);
        }
        if(InputHelper.isKeyDown(Keyboard.KEY_W)) 
        {
            player.updateVelocityForward(dt);
        }
        if(InputHelper.isKeyDown(Keyboard.KEY_S)) 
        {
            player.updateVelocityBackward(dt);
        }
    }
    
    // method: processMouse
    // make changes to the program based on mouse input
    private void processMouse()
    {
        float dx = Mouse.getX() - (W_WIDTH/2);
        float dy = Mouse.getY() - (W_HEIGHT/2);
        
        player.getCamera().yaw(dx * mouseSensitivity * (float)dt);
        player.getCamera().pitch(dy * mouseSensitivity * (float)dt);
        
        Mouse.setCursorPosition(W_WIDTH/2, W_HEIGHT/2);
        
        if(Mouse.isButtonDown(0))
        {
            for(float i = 0; i < 500*Chunk.CUBE_LENGTH; i+=.1)
            {
                Vector3f raycast = new Vector3f(player.getCamera().getLook());
                
                raycast.scale(i);
                raycast.x += player.getCamera().getPos().x/Chunk.CUBE_LENGTH;
                raycast.y += player.getCamera().getPos().y/Chunk.CUBE_LENGTH;
                raycast.z += player.getCamera().getPos().z/Chunk.CUBE_LENGTH;
                
                if (world.getBlock((int)raycast.x, (int)raycast.y, (int)raycast.z) != null)
                {
                    if(world.getBlock((int)raycast.x, (int)raycast.y, (int)raycast.z).getType() != Block.BlockType.AIR &&
                       world.getBlock((int)raycast.x, (int)raycast.y, (int)raycast.z).getType() != Block.BlockType.WATER)
                    {
                        world.removeBlockAt((int)raycast.x, (int)raycast.y, (int)raycast.z);
                        break;
                    }
                }
            }
        }
        
    }
    
    // method: run
    // runs the game loop
    public void run()
    {
        while(!Display.isCloseRequested() && !Keyboard.isKeyDown(Keyboard.KEY_ESCAPE))
        {
            dt = getDeltaTime();
            if(Display.isVisible())
            {
                Mouse.setGrabbed(true);
                processMouse();
                processKeyboard();
                update();
                render();
            }
            else 
            {
                Mouse.setGrabbed(false);
                if(Display.isDirty())
                {
                    update();
                    render();
                }
            }
            
            if(iteration%100 == 0)
            {
//                System.out.println(dt+" ms per loop");
//                System.out.printf(
//                        "Velocity = (%.2f, %.2f, %.2f)\n",
//                        player.getVelocity().x,
//                        player.getVelocity().y,
//                        player.getVelocity().z
//                );
//                System.out.printf(
//                        "Position = (%.2f, %.2f, %.2f)\n",
//                        player.getPos().x,
//                        player.getPos().y,
//                        player.getPos().z
//                );
            }
            iteration++;
            Display.update();
            
            Display.sync(60);
            
        }
    }
    
    // method: render()
    // render the chunks and perform matrix operations
    private void render()
    {
        glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);
        
        glMatrixMode(GL_MODELVIEW);
        glLoadIdentity();
        
        player.getCamera().lookThrough();
        
        world.render(); 
        
        
        if(!player.isFirstPerson())
        {
            glDisable(GL_CULL_FACE);
            glPolygonMode(GL_FRONT_AND_BACK, GL_LINE);
            glDisable(GL_TEXTURE_2D);
            glColor3f(1, 0, 0);
            glBegin(GL_QUADS);
                // bottom
                glVertex3f(player.getPos().x-Player.SIZE_XZ/2, player.getPos().y-Player.SIZE_Y/2, player.getPos().z-Player.SIZE_XZ/2);
                glVertex3f(player.getPos().x+Player.SIZE_XZ/2, player.getPos().y-Player.SIZE_Y/2, player.getPos().z-Player.SIZE_XZ/2);
                glVertex3f(player.getPos().x+Player.SIZE_XZ/2, player.getPos().y-Player.SIZE_Y/2, player.getPos().z+Player.SIZE_XZ/2);
                glVertex3f(player.getPos().x-Player.SIZE_XZ/2, player.getPos().y-Player.SIZE_Y/2, player.getPos().z+Player.SIZE_XZ/2);
                
                // top
                glVertex3f(player.getPos().x-Player.SIZE_XZ/2, player.getPos().y+Player.SIZE_Y/2, player.getPos().z-Player.SIZE_XZ/2);
                glVertex3f(player.getPos().x+Player.SIZE_XZ/2, player.getPos().y+Player.SIZE_Y/2, player.getPos().z-Player.SIZE_XZ/2);
                glVertex3f(player.getPos().x+Player.SIZE_XZ/2, player.getPos().y+Player.SIZE_Y/2, player.getPos().z+Player.SIZE_XZ/2);
                glVertex3f(player.getPos().x-Player.SIZE_XZ/2, player.getPos().y+Player.SIZE_Y/2, player.getPos().z+Player.SIZE_XZ/2);
                
                // front
                glVertex3f(player.getPos().x+Player.SIZE_XZ/2, player.getPos().y+Player.SIZE_Y/2, player.getPos().z+Player.SIZE_XZ/2);
                glVertex3f(player.getPos().x+Player.SIZE_XZ/2, player.getPos().y+Player.SIZE_Y/2, player.getPos().z-Player.SIZE_XZ/2);
                glVertex3f(player.getPos().x+Player.SIZE_XZ/2, player.getPos().y-Player.SIZE_Y/2, player.getPos().z-Player.SIZE_XZ/2);
                glVertex3f(player.getPos().x+Player.SIZE_XZ/2, player.getPos().y-Player.SIZE_Y/2, player.getPos().z+Player.SIZE_XZ/2);
                
                // back
                glVertex3f(player.getPos().x-Player.SIZE_XZ/2, player.getPos().y+Player.SIZE_Y/2, player.getPos().z+Player.SIZE_XZ/2);
                glVertex3f(player.getPos().x-Player.SIZE_XZ/2, player.getPos().y+Player.SIZE_Y/2, player.getPos().z-Player.SIZE_XZ/2);
                glVertex3f(player.getPos().x-Player.SIZE_XZ/2, player.getPos().y-Player.SIZE_Y/2, player.getPos().z-Player.SIZE_XZ/2);
                glVertex3f(player.getPos().x-Player.SIZE_XZ/2, player.getPos().y-Player.SIZE_Y/2, player.getPos().z+Player.SIZE_XZ/2);
                
                // left
                glVertex3f(player.getPos().x+Player.SIZE_XZ/2, player.getPos().y+Player.SIZE_Y/2, player.getPos().z+Player.SIZE_XZ/2);
                glVertex3f(player.getPos().x-Player.SIZE_XZ/2, player.getPos().y+Player.SIZE_Y/2, player.getPos().z+Player.SIZE_XZ/2);
                glVertex3f(player.getPos().x-Player.SIZE_XZ/2, player.getPos().y-Player.SIZE_Y/2, player.getPos().z+Player.SIZE_XZ/2);
                glVertex3f(player.getPos().x+Player.SIZE_XZ/2, player.getPos().y-Player.SIZE_Y/2, player.getPos().z+Player.SIZE_XZ/2);
                
                // right
                glVertex3f(player.getPos().x+Player.SIZE_XZ/2, player.getPos().y+Player.SIZE_Y/2, player.getPos().z-Player.SIZE_XZ/2);
                glVertex3f(player.getPos().x-Player.SIZE_XZ/2, player.getPos().y+Player.SIZE_Y/2, player.getPos().z-Player.SIZE_XZ/2);
                glVertex3f(player.getPos().x-Player.SIZE_XZ/2, player.getPos().y-Player.SIZE_Y/2, player.getPos().z-Player.SIZE_XZ/2);
                glVertex3f(player.getPos().x+Player.SIZE_XZ/2, player.getPos().y-Player.SIZE_Y/2, player.getPos().z-Player.SIZE_XZ/2);
            glEnd();
            
            glColor3f(0, 1, 1);
            glBegin(GL_LINES);
                glVertex3f(player.getPos().x, player.getPos().y, player.getPos().z);
                glVertex3f(player.getCamera().getLook().x+player.getCamera().getPos().x, player.getCamera().getLook().y+player.getCamera().getPos().y, player.getCamera().getLook().z+player.getCamera().getPos().z);
            glEnd();
            
            glPolygonMode(GL_FRONT_AND_BACK, GL_FILL);
            glEnable(GL_TEXTURE_2D);
            glEnable(GL_CULL_FACE);
        }
        
        
        glFlush();
    }
    
    private void update()
    {
        player.updateVelocity(dt);  
        world.handleCollisions(); 
        player.updatePosition(); 

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
    
    private void initLightArrays()
    {
        diffuseWhiteLight   = BufferUtils.createFloatBuffer(4);
        specularWhiteLight  = BufferUtils.createFloatBuffer(4);
        ambientWhiteLight   = BufferUtils.createFloatBuffer(4);
        
        diffuseWhiteLight.put(  new float[]{1.0f, 1.0f, 1.0f, 0.0f} ).flip();
        specularWhiteLight.put( new float[]{0.5f, 0.5f, 0.5f, 0.0f} ).flip();
        ambientWhiteLight.put(  new float[]{.25f, .25f, .25f, 0.0f} ).flip();
    }
    
    // method: initGL
    // initialize openGL
    private void initGL()
    {
        glClearColor(0.5f, 0.95f, 1.0f, 1.0f);
        
        glMatrixMode(GL_PROJECTION);
        glLoadIdentity();
        gluPerspective(90f, (float)displayMode.getWidth()/(float)displayMode.getHeight(),
                0.1f, 500.0f);
        glPushMatrix();
        
        glMatrixMode(GL_MODELVIEW);
        
        glEnableClientState(GL_VERTEX_ARRAY);
        glEnableClientState(GL_COLOR_ARRAY);
        glEnableClientState(GL_TEXTURE_COORD_ARRAY);
        glEnable(GL_DEPTH_TEST);
        glEnable(GL_TEXTURE_2D);
        glEnable(GL_CULL_FACE);
        glEnable(GL_COLOR_MATERIAL);
        glCullFace(GL_FRONT);
        
        glHint(GL_PERSPECTIVE_CORRECTION_HINT, GL_NICEST);
        glLoadIdentity();
        glPushMatrix();
        
        initLightArrays();
        glLight(GL_LIGHT0, GL_SPECULAR, specularWhiteLight);
        glLight(GL_LIGHT0, GL_DIFFUSE, diffuseWhiteLight);
        glLight(GL_LIGHT0, GL_AMBIENT, ambientWhiteLight);
        
        glEnable(GL_LIGHTING);
        glEnable(GL_LIGHT0);
    }
    
}
