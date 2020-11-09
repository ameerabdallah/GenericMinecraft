/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package genericminecraft;

/**
 *
 * @author Ameer Abdallah <Ameer Abdallah>
 */
public class Util {
    
    public static float[] append(float[] a1, float[] a2) 
    {
    float[] ret = new float[a1.length + a2.length];
    System.arraycopy(a1, 0, ret, 0, a1.length);
    System.arraycopy(a2, 0, ret, a1.length, a2.length);
    return ret;
  }
}
