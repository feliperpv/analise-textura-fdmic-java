package analisetextura;

import extratorcaracteristicas.FDMIC;
import java.io.File;
import org.opencv.core.Core;
import org.opencv.core.Mat;
import org.opencv.imgcodecs.Imgcodecs;

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

        //Outex
        //File diretorio_img = new File("C:\\Users\\Cliente\\Desktop\\TCC\\Outex\\Base");
        //Vistex
        File diretorio_img = new File("C:\\Users\\Cliente\\Desktop\\TCC\\Vistex\\Base");
        //Teste
        //File diretorio_img = new File("C:\\Users\\Cliente\\Desktop\\TCC\\teste\\Base");

        File[] imagens = diretorio_img.listFiles();
        int TAM_DIR = imagens.length;
        FDMIC fdmic = new FDMIC();
        TAM_DIR = 10;

        double matrix[][] = new double[TAM_DIR][66];

        for (int i = 0; i < TAM_DIR; i++) {
            Mat imagem = Imgcodecs.imread(diretorio_img.getAbsolutePath() + "\\" + imagens[i].getName());
            double[] VRetorno = fdmic.fdmicMain(imagem);

            for (int j = 0; j < 66; j++) {
                matrix[i][j] = VRetorno[j];
                System.out.print(matrix[i][j] + " ");
            }
            System.out.println("");
        }
        
        //Gerar
        File diretorio_classe = new File("C:\\Users\\Cliente\\Desktop\\TCC\\Vistex");
        //diretorio_classe
        
    }

}
