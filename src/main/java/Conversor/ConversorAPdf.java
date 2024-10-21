package Conversor;

//import com.itextpdf.html2pdf.ConverterProperties;
import com.itextpdf.html2pdf.HtmlConverter;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfReader;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.kernel.utils.PdfMerger;

import javax.swing.*;
import java.io.File;
import java.io.FileNotFoundException;
//import java.io.FileOutputStream;
import java.io.IOException;
import java.util.logging.Logger;
//import java.nio.file.Files;

public class ConversorAPdf {
    private static final Logger logger = Logger.getLogger(ConversorAPdf.class.getName());

    public void combinar(String[] archivos, String pdfOutput){


        try {
            // Convertir el arreglo de String a un arreglo de File
            File[] archivosFile = new File[archivos.length];
            for (int i = 0; i < archivos.length; i++) {
                archivosFile[i] = new File(archivos[i]);
            }
            // Crear un PdfWriter para el PDF de salida
            PdfWriter writer = new PdfWriter(pdfOutput);
            // Crear un PdfDocument para el PDF de salida
            PdfDocument pdfDocument = new PdfDocument(writer);
            // Crear un PdfMerger para combinar los PDF
            PdfMerger merger = new PdfMerger(pdfDocument);

            // Iterar sobre los archivos seleccionados
            for (File pdfFile : archivosFile) {
                PdfDocument pdfToMerge = new PdfDocument(new PdfReader(pdfFile));
                merger.merge(pdfToMerge, 1, pdfToMerge.getNumberOfPages()); // Combina todas las páginas
                pdfToMerge.close(); // Cierra el documento que se está fusionando
            }

            pdfDocument.close(); // Cierra el documento de salida

            System.out.println("PDFs combinados exitosamente en: " + pdfOutput);
            /*throw new RuntimeException("Algo salió mal!");*/
        } catch (IOException e) {
            logger.severe("Error durante la conversión:");
            logger.severe(e.toString());
            JOptionPane.showMessageDialog(null, "Error al combinar los PDFs: " + e.getMessage());
        }
    }
    public void convertir(String htmlInput, String pdfOutput) {

        // Conversión del HTML a PDF
        try {
            File filePdfOutput = new File(pdfOutput);
            /*filePdfOutput.getParentFile().mkdirs();*/

            PdfWriter writer = new PdfWriter(pdfOutput);
            PdfDocument pdfDocument = new PdfDocument(writer);
            pdfDocument.close();
            try {

                HtmlConverter.convertToPdf(new File(htmlInput), filePdfOutput);
                System.out.println("Conversión completada. PDF guardado en: " + filePdfOutput.getAbsolutePath());
            } catch (FileNotFoundException e) {
                System.out.println("Error al leer el archivo HTML o al guardar el PDF: + e.getMessage()");

            }
            /*throw new RuntimeException("Algo saliò mal!");*/

        } catch (Exception e) {

            logger.severe("Error durante la conversión:");
            logger.severe(e.toString());
            /*System.out.println("Error durante la conversión: " + e.getMessage());*/

    }
        }

    }
