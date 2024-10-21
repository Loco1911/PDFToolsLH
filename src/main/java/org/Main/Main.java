package org.Main;

import Conversor.ConversorAPdf;

import Firmar.SignerHelper;
import Firmar.SignerHelper.*;

import static Firmar.SignerHelper.agregarLineas;
import static Firmar.SignerHelper.firmarPdfConCertificado;


public class Main {
    public static void main(String[] args) {




        //conversor.convertir("C:\\Users\\PC1293\\Documents\\Pasantias LH\\input.html", "C:\\Users\\PC1293\\Documents\\Pasantias LH\\ouput_test.pdf");
        //String[] archivos = {
        //      "C:\\Users\\PC1293\\Documents\\Pasantias LH\\output - copia.pdf", "C:\\Users\\PC1293\\Documents\\Pasantias LH\\output - copia (2).pdf", "C:\\Users\\PC1293\\Documents\\Pasantias LH\\output - copia (3).pdf"
        //};
        //conversor.combinar(archivos, "C:\\Users\\PC1293\\Documents\\Pasantias LH\\output_combinado.pdf");

        /*try {
            // Ruta del archivo PDF original
            String pdfPath = "C:\\Users\\PC1293\\Documents\\Pasantias LH\\output.pdf";

            // 1. Agregar las líneas de firma al PDF
            String outputPdfWithLines = "ruta/a/tu/archivo_lineas.pdf";
            agregarLineasFirma(pdfPath, outputPdfWithLines);

            // 2. Firmar el PDF con el certificado PFX
            String pfxPath = "ruta/a/tu/certificado.pfx";
            String password = "contraseña";
            String outputPdfSigned = "ruta/a/tu/archivo_firmado.pdf";
            firmarPdfConCertificado(outputPdfWithLines, pfxPath, password, outputPdfSigned);

            System.out.println("PDF modificado y firmado correctamente.");

        } catch (Exception e) {
            e.printStackTrace();
        }
    }*/
        try {
            // Ruta del archivo PDF original
            String pdfPath = "C:\\Users\\PC1293\\Documents\\Pasantias LH\\output_combinado.pdf";
            String imagePath = "C:\\Users\\PC1293\\Documents\\Pasantias LH\\firma.jpeg";

            // 1. Agregar las líneas de firma al PDF
            String outputPdfWithLines = "C:\\Users\\PC1293\\Documents\\Pasantias LH\\output_new.pdf";
            agregarLineas(pdfPath, outputPdfWithLines);

            // 2. Firmar el PDF con el certificado PFX
            String pfxPath = "C:\\Users\\PC1293\\Documents\\Pasantias LH\\certificate.pfx";
            String password = "Modernizacion1234%";
            String outputPdfSigned = "C:\\Users\\PC1293\\Documents\\Pasantias LH\\output_signed.pdf";
            firmarPdfConCertificado(outputPdfWithLines, pfxPath, password, outputPdfSigned, imagePath);

            System.out.println("PDF modificado y firmado correctamente.");

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}