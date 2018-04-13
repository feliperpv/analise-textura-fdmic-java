package analisetextura;

import extratorcaracteristicas.FDMIC;
import org.opencv.core.Core;

/**
 *
 * @author Felipe
 */
public class AnaliseTextura {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        //Loading the OpenCV core library  
        System.loadLibrary(Core.NATIVE_LIBRARY_NAME);
        
        FDMIC fdmic = new FDMIC();
        fdmic.fdmicMain();
    }
    
}
