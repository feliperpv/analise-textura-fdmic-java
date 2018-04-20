package analisetextura;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import org.opencv.core.Core;
import weka.Weka;

/**
 *
 * @author Felipe
 */
public class AnaliseTextura {

    public static void main(String[] args) throws IOException{
        //Loading the OpenCV core library  
        System.loadLibrary(Core.NATIVE_LIBRARY_NAME);

        //Outex
        //File diretorio_img = new File("C:\\Users\\Cliente\\Desktop\\TCC\\Outex\\Base");
        //Vistex
        File diretorio_img = new File("C:\\Users\\Cliente\\Desktop\\TCC\\Vistex\\Base");
        
        int TAM_DIR = diretorio_img.listFiles().length;
        int TAM_ATRIB = 66;
        TAM_DIR = 35; //teste
        
        Utils utils = new Utils();
        //Gera dataset
        double matrix[][] = new double[TAM_DIR][TAM_ATRIB];
        utils.geraDataSet(diretorio_img, matrix, TAM_DIR, TAM_ATRIB);

        //Lê arquivo das classes e gera uma lista com as classes
        List<String> listaClasses = new ArrayList<>();
        utils.geraListaClasses(listaClasses, TAM_DIR);

        //Gera arquivo arff
        Weka weka = new Weka();
        weka.geraArquivoArff(matrix, listaClasses);

        System.out.println("FIM");
    }

}
