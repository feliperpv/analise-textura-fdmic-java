package analisetextura;

import extratorcaracteristicas.FDMIC;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import org.opencv.core.Core;
import org.opencv.core.Mat;
import org.opencv.imgcodecs.Imgcodecs;
import weka.Weka;

/**
 *
 * @author Felipe
 */
public class AnaliseTextura {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) throws IOException{
        //Loading the OpenCV core library  
        System.loadLibrary(Core.NATIVE_LIBRARY_NAME);

        //Outex
        //File diretorio_img = new File("C:\\Users\\Cliente\\Desktop\\TCC\\Outex\\Base");
        //Vistex
        File diretorio_img = new File("C:\\Users\\Cliente\\Desktop\\TCC\\Vistex\\Base");

        File[] imagens = diretorio_img.listFiles();
        int TAM_DIR = imagens.length;
        FDMIC fdmic = new FDMIC();
        TAM_DIR = 35; //teste

        double matrix[][] = new double[TAM_DIR][66];

        //Gera matriz com dataset        
        for (int i = 0; i < TAM_DIR; i++) {
            Mat imagem = Imgcodecs.imread(diretorio_img.getAbsolutePath() + "\\" + imagens[i].getName());
            double[] VRetorno = fdmic.fdmicMain(imagem);

            for (int j = 0; j < 66; j++) {
                matrix[i][j] = VRetorno[j];
            }
        }

        //Lê arquivo das classes e gera uma lista com as classes
        List<String> listaClasses = new ArrayList<>();
        int cont = 0;
        try {
            BufferedReader classe_txt
                    = new BufferedReader(new FileReader("C:\\Users\\Cliente\\Desktop\\TCC\\Vistex\\classe.txt"));

            String c = classe_txt.readLine();
            while (c != null && cont < TAM_DIR) {
                listaClasses.add(c);
                c = classe_txt.readLine();
                cont++;
            }

            classe_txt.close();

        } catch (IOException e) {
            System.out.print("Erro ao abrir o arquvio classe.txt");
        }

        //Gera arquivo arff
        Weka weka = new Weka();
        weka.geraArquivoArff(matrix, listaClasses);

        System.out.println("FIM");
    }

}
