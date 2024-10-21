package Firmar;

import com.itextpdf.io.exceptions.IOException;
import com.itextpdf.io.image.ImageData;
import com.itextpdf.io.image.ImageDataFactory;
import com.itextpdf.kernel.colors.ColorConstants;
import com.itextpdf.kernel.geom.Rectangle;
import com.itextpdf.kernel.pdf.*;
import com.itextpdf.kernel.pdf.canvas.PdfCanvas;
import com.itextpdf.signatures.*;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.*;
import java.security.GeneralSecurityException;
import java.security.KeyStore;
import java.security.PrivateKey;
import java.security.Security;
import java.security.cert.Certificate;

public class SignerHelper {

    public static void agregarLineas(String pdfPath, String outputPdfPath) throws FileNotFoundException {
        try (PdfReader reader = new PdfReader(pdfPath);
             PdfWriter writer = new PdfWriter(outputPdfPath);
             PdfDocument pdfDoc = new PdfDocument(reader, writer)) {

            PdfPage lastPage = pdfDoc.getPage(pdfDoc.getNumberOfPages());
            PdfCanvas canvas = new PdfCanvas(lastPage);

            float yPosition = 50; // Posición Y donde se dibujan las líneas
            float espacioEntreLineas = 20; // Espacio entre líneas
            int numeroDeLineas = 4; // Número total de líneas
            float lineWidth = 100; // Ancho de cada línea

            float totalWidth = (lineWidth * numeroDeLineas) + (espacioEntreLineas * (numeroDeLineas - 1));
            float xStart = (lastPage.getPageSize().getWidth() - totalWidth) / 2;

            for (int i = 0; i < numeroDeLineas; i++) {
                float xPosition = xStart + i * (lineWidth + espacioEntreLineas);
                canvas.moveTo(xPosition, yPosition);
                canvas.lineTo(xPosition + lineWidth, yPosition);
                canvas.setStrokeColorRgb(0, 0, 0);
                canvas.stroke();
            }

            System.out.println("Líneas agregadas exitosamente al PDF.");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void firmarPdfConCertificado(String pdfPath, String pfxPath, String password, String outputPdfPath, String imagePath) {
        try {
            KeyStore keyStore = KeyStore.getInstance("PKCS12");
            try (FileInputStream fis = new FileInputStream(pfxPath)) {
                keyStore.load(fis, password.toCharArray());
            }

            String alias = keyStore.aliases().nextElement();
            PrivateKey privateKey = (PrivateKey) keyStore.getKey(alias, password.toCharArray());
            Certificate[] chain = keyStore.getCertificateChain(alias);

            // Crear PdfReader y PdfWriter por separado
            try (PdfReader reader = new PdfReader(pdfPath);
                 PdfWriter writer = new PdfWriter(outputPdfPath);
                 PdfDocument pdfDocument = new PdfDocument(reader, writer)) {

                // Crear PdfSigner
                StampingProperties stampingProperties = new StampingProperties();
                PdfSigner signer = new PdfSigner(reader, writer, stampingProperties);

                // Obtener el número total de páginas
                int totalPages = pdfDocument.getNumberOfPages();

                // Definir el área de la firma en la última página
                Rectangle rect = new Rectangle(36, 480, 200, 100);
                PdfSignatureAppearance appearance = signer.getSignatureAppearance();
                appearance.setReuseAppearance(false)
                        .setPageRect(rect)
                        .setPageNumber(totalPages)
                        .setImageScale(-1); // Ajustar la escala de la imagen

                // Establecer parámetros que no se mostrarán en el PDF
                appearance.setLocation(" ");
                appearance.setReason(" ");
                appearance.setContact(" ");

                // Cargar la imagen de la firma
                BufferedImage image = ImageIO.read(new File(imagePath));
                ImageData imageData = ImageDataFactory.create(image, null);
                appearance.setImage(imageData);
                appearance .setLayer2Text("Firma digital");

                // Firmar el PDF
                Security.addProvider(new org.bouncycastle.jce.provider.BouncyCastleProvider());
                IExternalSignature pks = new PrivateKeySignature(privateKey, DigestAlgorithms.SHA256, "BC");
                IExternalDigest digest = new BouncyCastleDigest();
                //signer.setExternalDigest(new BouncyCastleDigest(), new Provider("BC"));
                signer.signDetached(digest, pks, chain, null, null, null, 0, PdfSigner.CryptoStandard.CMS);

                System.out.println("PDF firmado exitosamente.");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}