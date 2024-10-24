package org.Main;

import Firmar.PdfSignerService;
import Firmar.SignatureHelper;

import java.util.logging.Level;
import java.util.logging.Logger;

public class Main {
    private static final Logger logger = Logger.getLogger(Main.class.getName());

    public static void main(String[] args) {


        String keyStorePath1 = "C:\\Users\\PC1293\\Documents\\Pasantias LH\\certificate.pfx";
        String keyStorePassword1 = "Modernizacion1234%";
        String pdfInputPath = "C:\\Users\\PC1293\\Documents\\Pasantias LH\\contrato_de_prueba_paginas_llenas.pdf";
        String pdfOutputPath = "C:\\Users\\PC1293\\Documents\\Pasantias LH\\contrato_de_prueba_paginas_llenas_signed_new_2.pdf";
        String pdfOutputPath2 ="C:\\Users\\PC1293\\Documents\\Pasantias LH\\contrato_de_prueba_paginas_llenas_final.pdf";
        String signatureText = "Texto de Firma";

        String keyStorePath2 = "C:\\Users\\PC1293\\Documents\\Pasantias LH\\certificate2.pfx";
        String keyStorePassword2 = "Octavio1234";
        String pdfOutputPath3 ="C:\\Users\\PC1293\\Documents\\Pasantias LH\\contrato_de_prueba_paginas_llenas_final_2.pdf";

        try {

            PdfSignerService signer = new PdfSignerService(pdfInputPath, pdfOutputPath, keyStorePath1, keyStorePassword1);
            signer.signPdf();
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