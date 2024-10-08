package org.Main;

import Conversor.ConversorAPdf;
import Conversor.MergePdf;

public class Main {
    public static void main(String[] args) {

        ConversorAPdf conversor = new ConversorAPdf();

        //conversor.convertir();
        conversor.combinar();
        //MergePdf mergePdf = new MergePdf();
        //mergePdf.combinar();
    }
}