package Firmar;

import Utils.ColoredLogger;
import com.itextpdf.forms.PdfAcroForm;
import com.itextpdf.forms.fields.PdfFormField;
import com.itextpdf.forms.fields.PdfSignatureFormField;
import com.itextpdf.kernel.font.PdfFontFactory;
import com.itextpdf.kernel.geom.PageSize;
import com.itextpdf.kernel.geom.Rectangle;
import com.itextpdf.kernel.pdf.*;
import com.itextpdf.kernel.pdf.canvas.PdfCanvas;
import com.itextpdf.kernel.pdf.xobject.PdfFormXObject;
import com.itextpdf.signatures.*;
import org.bouncycastle.jce.provider.BouncyCastleProvider;

import java.io.*;
import java.security.KeyStore;
import java.security.PrivateKey;
import java.security.Security;
import java.security.cert.Certificate;
import java.security.cert.X509Certificate;
import java.util.Enumeration;
import java.util.logging.Level;
import java.util.logging.Logger;

public class SignatureHelper {
    private static final Logger logger = Logger.getLogger(SignatureHelper.class.getName());
    private PrivateKey privateKey;
    private Certificate[] certChain;
    private String signatureAlgorithm = DigestAlgorithms.SHA256; // Definir por defecto

    static {
        ColoredLogger.setup();
    }

    public void loadKeyStore(String keyStorePath, String keyStorePassword) throws Exception {
        Security.addProvider(new BouncyCastleProvider());

        KeyStore keyStore = KeyStore.getInstance("PKCS12");
        try (FileInputStream fis = new FileInputStream(keyStorePath)) {
            keyStore.load(fis, keyStorePassword.toCharArray());
            logger.log(Level.INFO, "Keystore cargado correctamente");

            Enumeration<String> aliases = keyStore.aliases();
            if (aliases.hasMoreElements()) {
                String alias = aliases.nextElement();
                logger.log(Level.INFO, "Alias encontrado: " + alias);

                privateKey = (PrivateKey) keyStore.getKey(alias, keyStorePassword.toCharArray());
                if (privateKey != null) {
                    logger.log(Level.INFO, "Clave privada encontrada para alias: " + alias);
                } else {
                    logger.log(Level.SEVERE, "No se encontró clave privada para alias: " + alias);
                    throw new IllegalStateException("No se encontró clave privada para alias: " + alias);
                }

                certChain = keyStore.getCertificateChain(alias);
                if (certChain != null) {
                    logger.log(Level.INFO, "Cadena de certificados encontrada para alias: " + alias);
                } else {
                    logger.log(Level.SEVERE, "No se encontró cadena de certificados para alias: " + alias);
                    throw new IllegalStateException("No se encontró cadena de certificados para alias: " + alias);
                }
            } else {
                logger.log(Level.SEVERE, "No se encontraron alias en el keystore.");
                throw new IllegalStateException("No se encontraron alias en el keystore.");
            }
        }
    }

    public void signPdf(String src, String dest, String reason, String location, String contact) throws Exception {
        // Validación de entradas
        if (src == null || dest == null || reason == null || location == null || contact == null) {
            throw new IllegalArgumentException("Todos los parámetros deben ser no nulos.");
        }

        PdfReader reader = new PdfReader(src);
             PdfWriter writer = new PdfWriter(dest);
             PdfSigner signer = new PdfSigner(reader, writer, new StampingProperties().useAppendMode());

            // Obtener el formulario y el número de firmas previas
            PdfAcroForm acroForm = PdfAcroForm.getAcroForm(signer.getDocument(), true);
            int firmasPrevias = acroForm.getAllFormFields().size();

            // Definir el rectángulo de la firma
            Rectangle signatureRect = getSignatureRectangle(firmasPrevias);

            // Crear la apariencia de la firma utilizando un PdfFormXObject
            PdfFormXObject n2 = new PdfFormXObject(signatureRect);
            PdfCanvas canvas = new PdfCanvas(n2, signer.getDocument());

            // Aquí puedes personalizar la apariencia de la firma
            X509Certificate cert = (X509Certificate) certChain[0];
            String subject = cert.getSubjectX500Principal().getName();
            drawTextInRectangle(canvas, subject, signatureRect);

            // Crear el campo de firma utilizando makeFormField
            PdfSignatureFormField sigField = (PdfSignatureFormField) PdfFormField.makeFormField(n2.getPdfObject(), signer.getDocument());
            sigField.setFieldName("sig_" + (firmasPrevias + 1));
            acroForm.addField(sigField);

            // Configurar detalles de la firma
            signer.setFieldName("sig_" + (firmasPrevias + 1));
            signer.setReason(reason);
            signer.setContact(contact);
            signer.setLocation(location);
            signer.setCertificationLevel(PdfSigner.NOT_CERTIFIED);

            // Firmar PDF con certificado
            IExternalSignature pks = new PrivateKeySignature(privateKey, signatureAlgorithm, "BC");
            IExternalDigest digest = new BouncyCastleDigest();
            signer.signDetached(digest, pks, certChain, null, null, null, 0, PdfSigner.CryptoStandard.CMS);

            logger.log(Level.INFO, "Firma realizada con éxito en: " + dest);

    }

    private void drawTextInRectangle(PdfCanvas canvas, String text, Rectangle rect) {
        float margin = 5;
        canvas.beginText();
        try {
            canvas.setFontAndSize(PdfFontFactory.createFont(), 10);
        } catch (IOException e) {
            logger.log(Level.SEVERE, "Error al establecer la fuente: ", e);
        }

        // Coloca el texto en el rectángulo de la firma
        float textXPosition = rect.getLeft() + margin;
        float textYPosition = rect.getBottom() + margin;
        canvas.moveText(textXPosition, textYPosition);
        canvas.showText(text);
        canvas.endText();
        canvas.release();
    }

    private Rectangle getSignatureRectangle(int firmasPrevias) {
        float yPositionPrimeraLinea = 200;
        float espacioMinimo = 2; // Espacio mínimo entre la firma y la línea
        float desplazarDerecha = 36; // Desplazamiento base hacia la derecha
        float espacioEntreLineas = 20;
        float espacioEntreFilas = 70; // Altura del rectángulo de la firma
        float lineWidth = 200;
        float pageWidth = PageSize.A4.getWidth(); // Ancho de la página A4

        return switch (firmasPrevias) {
            case 0 ->
                    new Rectangle(desplazarDerecha + 65, yPositionPrimeraLinea + espacioMinimo, lineWidth, 60); // Primera firma, primera línea
            case 1 ->
                    new Rectangle(desplazarDerecha + 250, yPositionPrimeraLinea + espacioMinimo, lineWidth, 60); // Segunda firma, primera fila, segunda columna
            case 2 ->
                    new Rectangle(desplazarDerecha + 65, yPositionPrimeraLinea - espacioEntreFilas + espacioMinimo, lineWidth, 60); // Tercera firma, segunda fila, primera columna
            case 3 ->
                    new Rectangle(desplazarDerecha + 250, yPositionPrimeraLinea - espacioEntreFilas + espacioMinimo, lineWidth, 60); // Cuarta firma, segunda fila, segunda columna
            case 4 ->
                    new Rectangle((pageWidth - lineWidth) / 2, yPositionPrimeraLinea - 2 * espacioEntreFilas + espacioMinimo, lineWidth, 60); // Quinta firma, tercera fila, primera columna
            default ->
                    new Rectangle(desplazarDerecha, yPositionPrimeraLinea + espacioMinimo, lineWidth, 60); // Default
        };
    }

    public static void agregarLineas(String pdfPath, String outputPdfPath) {
        try (PdfReader reader = new PdfReader(pdfPath);
             PdfWriter writer = new PdfWriter(outputPdfPath);
             PdfDocument pdfDoc = new PdfDocument(reader, writer)) {

            PdfPage lastPage = pdfDoc.getPage(pdfDoc.getNumberOfPages());
            PdfCanvas canvas = new PdfCanvas(lastPage);

            float yPosition = 200; // Posición Y de la primera fila de líneas
            float espacioEntreLineas = 20; // Espacio entre líneas
            float lineWidth = 200; // Ancho de cada línea igual al ancho de la firma

            // Espacio entre filas de líneas
            float espacioEntreFilas = 70; // Altura del rectángulo de la firma

            // Primera fila con 2 líneas centradas
            int numeroDeLineasPrimeraFila = 2;
            float totalWidthPrimeraFila = (lineWidth * numeroDeLineasPrimeraFila) + (espacioEntreLineas * (numeroDeLineasPrimeraFila - 1));
            float xStartPrimeraFila = (lastPage.getPageSize().getWidth() - totalWidthPrimeraFila) / 2;

            for (int i = 0; i < numeroDeLineasPrimeraFila; i++) {
                float xPosition = xStartPrimeraFila + i * (lineWidth + espacioEntreLineas);
                canvas.moveTo(xPosition, yPosition);
                canvas.lineTo(xPosition + lineWidth, yPosition);
                canvas.setStrokeColorRgb(0, 0, 0);
                canvas.stroke();
            }

            // Segunda fila con 2 líneas centradas
            int numeroDeLineasSegundaFila = 2;
            float totalWidthSegundaFila = (lineWidth * numeroDeLineasSegundaFila) + (espacioEntreLineas * (numeroDeLineasSegundaFila - 1));
            float xStartSegundaFila = (lastPage.getPageSize().getWidth() - totalWidthSegundaFila) / 2;

            float yPositionSegundaFila = yPosition - espacioEntreFilas;
            for (int i = 0; i < numeroDeLineasSegundaFila; i++) {
                float xPosition = xStartSegundaFila + i * (lineWidth + espacioEntreLineas);
                canvas.moveTo(xPosition, yPositionSegundaFila);
                canvas.lineTo(xPosition + lineWidth, yPositionSegundaFila);
                canvas.setStrokeColorRgb(0, 0, 0);
                canvas.stroke();
            }

            // Tercera fila con 1 línea centrada
            int numeroDeLineasTerceraFila = 1;
            float xStartTerceraFila = (lastPage.getPageSize().getWidth() - lineWidth) / 2;

            float yPositionTerceraFila = yPositionSegundaFila - espacioEntreFilas;
            for (int i = 0; i < numeroDeLineasTerceraFila; i++) {
                canvas.moveTo(xStartTerceraFila, yPositionTerceraFila);
                canvas.lineTo(xStartTerceraFila + lineWidth, yPositionTerceraFila);
                canvas.setStrokeColorRgb(0, 0, 0);
                canvas.stroke();
            }

            logger.log(Level.INFO, "Líneas agregadas exitosamente al PDF.");
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Error al agregar líneas al PDF", e);
        }
    }
}