package Firmar;

import com.itextpdf.forms.form.element.SignatureFieldAppearance;
import com.itextpdf.io.image.ImageData;
import com.itextpdf.io.image.ImageDataFactory;
import com.itextpdf.kernel.geom.Rectangle;
import com.itextpdf.kernel.pdf.*;
import com.itextpdf.kernel.pdf.canvas.PdfCanvas;
import com.itextpdf.signatures.*;
import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.*;
import java.security.KeyStore;
import java.security.PrivateKey;
import java.security.Security;
import java.security.cert.Certificate;
import java.util.logging.Level;
import java.util.logging.Logger;

public class SignerHelper {
    private static final Logger logger = Logger.getLogger(SignerHelper.class.getName());

    private static Rectangle fourthRectPosition;

    public static void agregarLineas(String pdfPath, String outputPdfPath) {
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

                if (i == 3) {
                    fourthRectPosition = new Rectangle(xPosition, yPosition + espacioEntreLineas, lineWidth, -50);
                }
            }

            System.out.println("Líneas agregadas exitosamente al PDF.");
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Error al agregar líneas al PDF", e);
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

            try (PdfReader reader = new PdfReader(pdfPath);
                 PdfWriter writer = new PdfWriter(outputPdfPath)) {

                // Crear PdfSigner
                PdfSigner pdfSigner = new PdfSigner(reader, writer, new StampingProperties());

                // Obtener el número total de páginas
                int totalPages = pdfSigner.getDocument().getNumberOfPages();

                // Usar la posición del cuarto rectángulo a partir del método agregarLineas
                if (fourthRectPosition == null) {
                    throw new IllegalStateException("La posición del cuarto rectángulo no fue determinada.");
                }
                Rectangle rect = new Rectangle(fourthRectPosition.getX(),
                        fourthRectPosition.getY() + fourthRectPosition.getHeight(),
                        fourthRectPosition.getWidth(),
                        100);
                pdfSigner.setLocation(" ").setReason("").setPageRect(rect).setPageNumber(totalPages).setContact(" ");

                // Cargar la imagen de la firma
                BufferedImage image = ImageIO.read(new File(imagePath));
                ImageData imageData = ImageDataFactory.create(image, null);

                // Usar SignatureFieldAppearance para establecer el texto de la firma
                SignatureFieldAppearance signatureFieldAppearance = new SignatureFieldAppearance("firma");
                signatureFieldAppearance.setContent(imageData);
                pdfSigner.setSignatureAppearance(signatureFieldAppearance);

                // Firmar el PDF
                Security.addProvider(new org.bouncycastle.jce.provider.BouncyCastleProvider());
                IExternalSignature pks = new PrivateKeySignature(privateKey, DigestAlgorithms.SHA256, "BC");
                IExternalDigest digest = new BouncyCastleDigest();
                pdfSigner.signDetached(digest, pks, chain, null, null, null, 0, PdfSigner.CryptoStandard.CMS);

                System.out.println("PDF firmado exitosamente.");
            }
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Error al firmar el PDF", e);
        }
    }
}