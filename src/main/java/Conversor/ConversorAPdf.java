package Conversor;

import com.itextpdf.html2pdf.ConverterProperties;
import com.itextpdf.html2pdf.HtmlConverter;
import com.itextpdf.html2pdf.css.*;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfReader;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.kernel.utils.PdfMerger;

import javax.swing.*;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;

public class ConversorAPdf {
    public void combinar(){
        // Crear un JFileChooser para seleccionar los archivos PDF
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setDialogTitle("Selecciona archivos PDF");
        fileChooser.setFileFilter(new javax.swing.filechooser.FileNameExtensionFilter("Archivos PDF", "pdf"));
        fileChooser.setMultiSelectionEnabled(true); // Permitir selección múltiple

        int userSelection = fileChooser.showOpenDialog(null);
        if (userSelection != JFileChooser.APPROVE_OPTION) {
            JOptionPane.showMessageDialog(null,"No se seleccionó ningún archivo.");
            return; // Salir si no se selecciona
        }

        File[] selectedFiles = fileChooser.getSelectedFiles(); // Obtener archivos seleccionados

        // Crear un JFileChooser para guardar el PDF combinado
        JFileChooser saveChooser = new JFileChooser();
        saveChooser.setDialogTitle("Guardar archivo PDF combinado");
        saveChooser.setSelectedFile(new File("documentos_combinados.pdf")); // Nombre predeterminado

        int saveSelection = saveChooser.showSaveDialog(null);
        if (saveSelection != JFileChooser.APPROVE_OPTION) {
            JOptionPane.showMessageDialog(null,"No se seleccionó ningún archivo para guardar.");
            return; // Salir si no se selecciona
        }

        File outputFile = saveChooser.getSelectedFile(); // Obtener el archivo de salida

        // Asegúrate de que el archivo tenga la extensión .pdf
        if (!outputFile.getName().endsWith(".pdf")) {
            outputFile = new File(outputFile.getAbsolutePath() + ".pdf");
        }

        try {
            // Crear un PdfWriter para el PDF de salida
            PdfWriter writer = new PdfWriter(outputFile);
            // Crear un PdfDocument para el PDF de salida
            PdfDocument pdfDocument = new PdfDocument(writer);
            // Crear un PdfMerger para combinar los PDFs
            PdfMerger merger = new PdfMerger(pdfDocument);

            // Iterar sobre los archivos seleccionados
            for (File pdfFile : selectedFiles) {
                PdfDocument pdfToMerge = new PdfDocument(new PdfReader(pdfFile));
                merger.merge(pdfToMerge, 1, pdfToMerge.getNumberOfPages()); // Combina todas las páginas
                pdfToMerge.close(); // Cierra el documento que se está fusionando
            }

            pdfDocument.close(); // Cierra el documento de salida

            JOptionPane.showMessageDialog(null,"PDFs combinados exitosamente en: " + outputFile.getAbsolutePath());
        } catch (IOException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(null, "Error al combinar los PDFs: " + e.getMessage());
        }
    }
    public void convertir() {
        // Selección de archivo HTML en el sistema
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setDialogTitle("Selecciona un archivo HTML");
        fileChooser.setFileFilter(new javax.swing.filechooser.FileNameExtensionFilter("Archivos HTML", "html"));

        int userSelection = fileChooser.showOpenDialog(null);
        if (userSelection != JFileChooser.APPROVE_OPTION) {
            System.out.println("No se seleccionó ningún archivo");
            return; // Salir si no se selecciona
        }

        JFileChooser fileChooser2 = new JFileChooser();
        fileChooser2.setDialogTitle("Selecciona un archivo CSS");
        fileChooser2.setFileFilter(new javax.swing.filechooser.FileNameExtensionFilter("Archivos CSS", "css"));

        int userSelection2 = fileChooser2.showOpenDialog(null);
        if (userSelection2 != JFileChooser.APPROVE_OPTION) {
            JOptionPane.showMessageDialog(null,"No se seleccionó ningún archivo, continuando sin css");
            File htmlFile = fileChooser.getSelectedFile();

            // Selección de ruta de guardado del PDF
            JFileChooser saveChooser = new JFileChooser();
            saveChooser.setDialogTitle("Guardar archivo PDF");
            saveChooser.setSelectedFile(new File("output.pdf"));

            userSelection = saveChooser.showSaveDialog(null);
            if (userSelection != JFileChooser.APPROVE_OPTION) {
                System.out.println("No se seleccionó ningún archivo para guardar");
                return; // Salir si no se selecciona
            }

            File pdfFile = saveChooser.getSelectedFile();

            // Conversión del HTML a PDF
            try {
                String htmlContent = Files.readString(htmlFile.toPath());

                // Usar try-with-resources para asegurarse de que se cierra el FileOutputStream
                try (FileOutputStream outputStream = new FileOutputStream(pdfFile)) {

                    HtmlConverter.convertToPdf(htmlContent, outputStream);
                    JOptionPane.showMessageDialog(null, "Conversión completada. PDF guardado en: " + pdfFile.getAbsolutePath());
                }
            } catch (IOException e) {
                JOptionPane.showMessageDialog(null, "Error al leer el archivo HTML o al guardar el PDF: " + e.getMessage());
            } catch (Exception e) {
                JOptionPane.showMessageDialog(null, "Error durante la conversión: " + e.getMessage());
            }
        } else {
            File htmlFile = fileChooser.getSelectedFile();
            File cssFile = fileChooser2.getSelectedFile();



            // Selección de ruta de guardado del PDF
            JFileChooser saveChooser = new JFileChooser();
            saveChooser.setDialogTitle("Guardar archivo PDF");
            saveChooser.setSelectedFile(new File("output.pdf"));

            userSelection = saveChooser.showSaveDialog(null);
            if (userSelection != JFileChooser.APPROVE_OPTION) {
                System.out.println("No se seleccionó ningún archivo para guardar");
                return; // Salir si no se selecciona
            }

            File pdfFile = saveChooser.getSelectedFile();


            // Conversión del HTML a PDF
            try {
                String htmlContent = Files.readString(htmlFile.toPath());

                // Usar try-with-resources para asegurarse de que se cierra el FileOutputStream
                try (FileOutputStream outputStream = new FileOutputStream(pdfFile)) {
                    ConverterProperties converterProperties = new ConverterProperties();
                    converterProperties.setBaseUri(cssFile.toURI().toString());
                    HtmlConverter.convertToPdf(htmlContent, outputStream, converterProperties);
                    JOptionPane.showMessageDialog(null, "Conversión completada. PDF guardado en: " + pdfFile.getAbsolutePath());
                }
            } catch (IOException e) {
                JOptionPane.showMessageDialog(null, "Error al leer el archivo HTML o al guardar el PDF: " + e.getMessage());
            } catch (Exception e) {
                JOptionPane.showMessageDialog(null, "Error durante la conversión: " + e.getMessage());
            }
        }

    }
}
