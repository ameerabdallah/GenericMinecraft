/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package genericminecraft;


/**
 *
 * @author ameer
 */
public class GenericMinecraft {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        // TODO code application logic here
        LWJGLMain main = null;
        try
        {
            main = new LWJGLMain();
            main.create();
            main.run();
        }
        catch(Exception ex)
        {
            System.out.println("An error has occured: " + ex.toString() + "\n");
            ex.printStackTrace();
        }
        finally
        {
            if(main != null)
            {
                main.destroy();
            }
        }
        
    }
    
}
