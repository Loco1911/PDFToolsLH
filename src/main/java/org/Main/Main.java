package org.Main;

import Conversor.ConversorAPdf;

public class Main {
    public static void main(String[] args) {

        ConversorAPdf conversor = new ConversorAPdf();

        //conversor.convertir("C:\\Users\\PC1293\\Documents\\Pasantias LH\\input.html", "C:\\Users\\PC1293\\Documents\\Pasantias LH\\ouput_test.pdf");
        String[] archivos = {
             "C:\\Users\\PC1293\\Documents\\Pasantias LH\\output - copia.pdf", "C:\\Users\\PC1293\\Documents\\Pasantias LH\\output - copia (2).pdf", "C:\\Users\\PC1293\\Documents\\Pasantias LH\\output - copia (3).pdf"
        };
        conversor.combinar(archivos, "C:\\Users\\PC1293\\Documents\\Pasantias LH\\output_combinado.pdf");
    }
}