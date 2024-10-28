package org.Main;

import Firmar.PdfSignerService;
import Firmar.SignatureHelper;

import java.util.logging.Level;
import java.util.logging.Logger;

public class Main {
    private static final Logger logger = Logger.getLogger(Main.class.getName());

    public static void main(String[] args) {



        String pdfInputPath = "C:\\Users\\PC1293\\Documents\\Pasantias LH\\contrato_de_prueba_paginas_llenas.pdf";
        String pdfOutputPath = "C:\\Users\\PC1293\\Documents\\Pasantias LH\\contrato_de_prueba_paginas_llenas_signed_new_2.pdf";
        String pdfOutputLines = "C:\\Users\\PC1293\\Documents\\Pasantias LH\\contrato_de_prueba_paginas_llenas_signed_lines_2.pdf";
        String pdfOutputPath2 ="C:\\Users\\PC1293\\Documents\\Pasantias LH\\contrato_de_prueba_paginas_llenas_final.pdf";


        String keyStorePath1 = "C:\\Users\\PC1293\\Documents\\Pasantias LH\\certificate.pfx";
        String keyStorePassword1 = "Modernizacion1234%";

        String keyStorePath2 = "C:\\Users\\PC1293\\Documents\\Pasantias LH\\certificate3.pfx";
        String keyStorePassword2 = "Pamela1234";
        String pdfOutputPath3 ="C:\\Users\\PC1293\\Documents\\Pasantias LH\\contrato_de_prueba_paginas_llenas_final_2.pdf";

        String keyStorePath3 = "C:\\Users\\PC1293\\Documents\\Pasantias LH\\certificate2.pfx";
        String keyStorePassword3 = "Octavio1234";
        String pdfOutputPath4 ="C:\\Users\\PC1293\\Documents\\Pasantias LH\\contrato_de_prueba_paginas_llenas_final_3.pdf";

        String keyStorePath4 = "C:\\Users\\PC1293\\Documents\\Pasantias LH\\certificate4.pfx";
        String keyStorePassword4 = "Valentina1234";
        String pdfOutputPath5 ="C:\\Users\\PC1293\\Documents\\Pasantias LH\\contrato_de_prueba_paginas_llenas_final_4.pdf";

        String keyStorePath5 = "C:\\Users\\PC1293\\Documents\\Pasantias LH\\certificate5.pfx";
        String keyStorePassword5 = "Marcelo1234";
        String pdfOutputPath6 ="C:\\Users\\PC1293\\Documents\\Pasantias LH\\contrato_de_prueba_paginas_llenas_final_5.pdf";

        try {
            PdfSignerService signer = new PdfSignerService();
            signer.agregarLineas(pdfInputPath, pdfOutputLines);
            signer.signPdfNextPosition(pdfOutputLines, pdfOutputPath, keyStorePath1, keyStorePassword1);
            signer.signPdfNextPosition(pdfOutputPath, pdfOutputPath2, keyStorePath2, keyStorePassword2);
            signer.signPdfNextPosition(pdfOutputPath2, pdfOutputPath4, keyStorePath3, keyStorePassword3);
            signer.signPdfNextPosition(pdfOutputPath4, pdfOutputPath5, keyStorePath4, keyStorePassword4);
            signer.signPdfNextPosition(pdfOutputPath5, pdfOutputPath6, keyStorePath5, keyStorePassword5);

            //PdfSignerService signer2 = new PdfSignerService();
            //signer2.signPdfNextPosition(pdfOutputPath, pdfOutputPath, keyStorePath2, keyStorePassword2);

            /*SignatureHelper helper = new SignatureHelper();
            SignatureHelper.agregarLineas(pdfInputPath, pdfOutputPath);
            helper.loadKeyStore(keyStorePath1, keyStorePassword1);
            helper.signPdf(pdfOutputPath, pdfOutputPath2,  "Firmado electrónicamente por", "Modernización Institucional", "Renzo Sparta");

            helper.loadKeyStore(keyStorePath2, keyStorePassword2);
            helper.signPdf(pdfOutputPath2, pdfOutputPath3, "Firmado electrónicamente por", "Modernización Institucional", "Octavio Díaz");*/

            logger.log(Level.INFO, "PDF firmado correctamente.");
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Ocurrió un error: " + e, e);
        }
    }
}