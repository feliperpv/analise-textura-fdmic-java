/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package analisetextura;

import extratorcaracteristicas.FDMIC;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.List;
import org.opencv.core.Mat;
import org.opencv.imgcodecs.Imgcodecs;

/**
 *
 * @author Felipe
 */
public class Utils {

    public void geraDataSet(File diretorio, double[][] matrix, int TAM_DIR, int TAM_ATRIB) {

        FDMIC fdmic = new FDMIC();
        File[] imagens = diretorio.listFiles();

        for (int i = 0; i < TAM_DIR; i++) {
            Mat imagem = Imgcodecs.imread(diretorio.getAbsolutePath() + "\\" + imagens[i].getName());
            double[] VRetorno = fdmic.fdmicMain(imagem);

            for (int j = 0; j < TAM_ATRIB; j++) {
                matrix[i][j] = VRetorno[j];
            }
        }
    }

    public void geraListaClasses(List<String> listaClasses, int TAM_DIR) {

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
    }
}
