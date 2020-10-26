/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package genericminecraft;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author ameer
 */
public class LWJGLMainTest {
    
    public LWJGLMainTest() {
    }
    
    @BeforeClass
    public static void setUpClass() {
    }
    
    @AfterClass
    public static void tearDownClass() {
    }
    
    @Before
    public void setUp() {
    }
    
    @After
    public void tearDown() {
    }

    /**
     * Test of createChunks method, of class LWJGLMain.
     */
    @Test
    public void testCreateChunks() {
        System.out.println("createChunks");
        LWJGLMain instance = new LWJGLMain();
        instance.createChunks();
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of create method, of class LWJGLMain.
     */
    @Test
    public void testCreate() throws Exception {
        System.out.println("create");
        LWJGLMain instance = new LWJGLMain();
        instance.create();
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of destroy method, of class LWJGLMain.
     */
    @Test
    public void testDestroy() {
        System.out.println("destroy");
        LWJGLMain instance = new LWJGLMain();
        instance.destroy();
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of run method, of class LWJGLMain.
     */
    @Test
    public void testRun() {
        System.out.println("run");
        LWJGLMain instance = new LWJGLMain();
        instance.run();
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }
    
}
