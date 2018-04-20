/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package weka;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;

/**
 *
 * @author Felipe
 */
public class Weka {
    
    public void geraArquivoArff(double matrix[][], List<String> listaClasses) throws IOException {

        String arff = "@relation atributos\n\n";
        String classe = "@attribute classe {";
        String anterior = "-1";
        String dataset = "";
        String attribute = "";

        boolean first = true;

        for (int i = 0; i < listaClasses.size(); i++) {

            if (!listaClasses.get(i).equalsIgnoreCase(anterior)) {
                classe += listaClasses.get(i) + ",";
            }
            anterior = listaClasses.get(i);

            for (int j = 0; j < 66; j++) {
                if (first) {
                    attribute += "@attribute " + j + " real\n";
                }

                dataset += matrix[i][j] + ",";
            }
            dataset += listaClasses.get(i) + "\n";
            first = false;
        }

        classe = classe.substring(0, classe.length() - 1);

        arff += attribute;
        arff += classe + "}\n\n";
        arff += "@data\n";
        arff += dataset;

        File arquivo = new File("atributos.arff");
        FileOutputStream f = new FileOutputStream(arquivo);
        f.write(arff.getBytes());
        f.close();
    }
    
}
