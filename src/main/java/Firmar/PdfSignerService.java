package Firmar;

import com.itextpdf.forms.PdfAcroForm;
import com.itextpdf.forms.fields.PdfFormField;
import com.itextpdf.kernel.font.PdfFont;
import com.itextpdf.kernel.font.PdfFontFactory;
import com.itextpdf.kernel.pdf.*;
import com.itextpdf.kernel.pdf.canvas.PdfCanvas;
import com.itextpdf.signatures.PdfSigner;
import com.itextpdf.signatures.BouncyCastleDigest;
import com.itextpdf.signatures.DigestAlgorithms;
import com.itextpdf.signatures.IExternalSignature;
import com.itextpdf.signatures.PrivateKeySignature;
import com.itextpdf.signatures.PdfSignatureAppearance;
import com.itextpdf.kernel.geom.Rectangle;
import com.itextpdf.io.font.constants.StandardFonts;
import org.bouncycastle.jce.provider.BouncyCastleProvider;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.security.KeyStore;
import java.security.PrivateKey;
import java.security.Security;
import java.security.cert.Certificate;
import java.security.cert.X509Certificate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Enumeration;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static java.nio.charset.StandardCharsets.UTF_8;

/**
 * Servicio para firmar documentos PDF con soporte para caracteres especiales y acentos.
 */
public class PdfSignerService {
    private static final Logger logger = Logger.getLogger(PdfSignerService.class.getName());
    private static final String FORMATO_FECHA = "dd/MM/yyyy HH:mm:ss";
    private static final String NOMBRE_CAMPO_FIRMA = "sig";
    private static final float FIRMA_ANCHO = 200;
    private static final float FIRMA_ALTO = 100;
    private static final float ESPACIO_ENTRE_FILAS = 70; // Altura entre filas
    private static final float ESPACIO_ENTRE_LINEAS = 20; // Espacio entre líneas
    private static final float FIRMA_X_INICIAL = 90; // Posición X inicial
    private static final float FIRMA_Y_INICIAL = 200; // Posición Y inicial

//    private final String src;
//    private final String dest;
//    private final String pathToPfx;
//    private final String password;
//    private final String lines;

    public PdfSignerService() {
//        this.src = src;
//        this.dest = dest;
//        this.pathToPfx = pathToPfx;
//        this.password = password;
//        this.lines = lines;
    }

    /*public void signPdf() throws Exception {
        logger.info("Iniciando el proceso de firma del PDF...");

        Security.addProvider(new BouncyCastleProvider());
        KeyStore ks = KeyStore.getInstance("PKCS12");
        ks.load(new FileInputStream(pathToPfx), password.toCharArray());

        String alias = "";
        Enumeration<String> aliases = ks.aliases();
        while (aliases.hasMoreElements()) {
            alias = aliases.nextElement();
            if (ks.isKeyEntry(alias)) {
                break;
            }
        }

        PrivateKey pk = (PrivateKey) ks.getKey(alias, password.toCharArray());
        Certificate[] chain = ks.getCertificateChain(alias);

        X509Certificate cert = (X509Certificate) ks.getCertificate(alias);
        String issuer = cert.getIssuerX500Principal().getName();
        logger.info("Emisor del certificado obtenido: " + issuer);

        Map<String, String> fields = Stream.of(issuer.split(","))
                .map(String::trim)
                .filter(s -> s.matches("(CN|OU|O|L|ST|C)=(.*)"))
                .collect(Collectors.toMap(
                        s -> s.split("=")[0],
                        s -> s.split("=")[1]
                ));

        LocalDateTime now = LocalDateTime.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern(FORMATO_FECHA);
        String fechaFirma = now.format(formatter);

        // Texto de la firma con acentos
        String formattedIssuer = new String(String.format("Firmado electrónicamente por:%n%s%n%s%n%s%n%s, %s, %s%n%s",
                fields.getOrDefault("CN", ""),
                fields.getOrDefault("OU", ""),
                fields.getOrDefault("O", ""),
                fields.getOrDefault("L", ""),
                fields.getOrDefault("ST", ""),
                fields.getOrDefault("C", ""),
                fechaFirma).getBytes(StandardCharsets.UTF_8), StandardCharsets.UTF_8);

        try (PdfReader reader = new PdfReader(lines);
             PdfWriter writer = new PdfWriter(dest)) {

            StampingProperties stampingProperties = new StampingProperties();
            PdfSigner signer = new PdfSigner(reader, writer, stampingProperties);

            PdfDocument pdfDoc = signer.getDocument();
            int lastPageNumber = pdfDoc.getNumberOfPages();

            // Configurar la apariencia de la firma con fuente que soporte acentos
            PdfSignatureAppearance appearance = signer.getSignatureAppearance();
            PdfFont font = PdfFontFactory.createFont(StandardFonts.HELVETICA, StandardCharsets.UTF_8.name());
            appearance.setLayer2Font(font);

            // Establecer la posición de la firma
            signer.setPageNumber(lastPageNumber);
            signer.setPageRect(new Rectangle(FIRMA_X, FIRMA_Y, FIRMA_ANCHO, FIRMA_ALTO));

            // Configurar el texto de la firma
            appearance.setLayer2Text(formattedIssuer);
            signer.setFieldName(NOMBRE_CAMPO_FIRMA);

            // Metadatos de la firma
            signer.setReason("Firma digital");
            signer.setLocation("");
            signer.setContact("");

            // Realizar la firma digital
            IExternalSignature pks = new PrivateKeySignature(pk, DigestAlgorithms.SHA256, "BC");
            BouncyCastleDigest digest = new BouncyCastleDigest();
            signer.signDetached(digest, pks, chain, null, null, null, 0, PdfSigner.CryptoStandard.CMS);

            logger.info("Proceso de firma del PDF finalizado exitosamente.");
        }
    }*/


    public void agregarLineas(String src, String dest) {
        try (PdfReader reader = new PdfReader(src);
             PdfWriter writer = new PdfWriter(dest);
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

    //Investigar Foramtos p12
    public void signPdfNextPosition(String src, String dest, String pfx, String pfxPassword) throws Exception {
        logger.info("Iniciando el proceso de firma del PDF en la última página...");

        // Inverstigar BouncyCastle
        Security.addProvider(new BouncyCastleProvider());
        KeyStore ks = KeyStore.getInstance("PKCS12");
        ks.load(new FileInputStream(pfx), pfxPassword.toCharArray());

        //Investigar con más de un alias
        String alias = "";
        Enumeration<String> aliases = ks.aliases();
        while (aliases.hasMoreElements()) {
            alias = aliases.nextElement();
            if (ks.isKeyEntry(alias)) {
                break;
            }
        }

        PrivateKey pk = (PrivateKey) ks.getKey(alias, pfxPassword.toCharArray());
        // Investigar Charset
        Certificate[] chain = ks.getCertificateChain(alias);

        X509Certificate cert = (X509Certificate) ks.getCertificate(alias);
        String issuer = cert.getIssuerX500Principal().getName(), StandardCharsets.UTF_8;
        logger.info("Emisor del certificado obtenido: " + issuer);

        Map<String, String> fields = Stream.of(issuer.split(","))
                .map(String::trim)
                .filter(s -> s.matches("(CN|OU|O|L|ST|C)=(.*)"))
                .collect(Collectors.toMap(
                        s -> s.split("=")[0],
                        s -> s.split("=")[1]
                ));

        LocalDateTime now = LocalDateTime.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern(FORMATO_FECHA);
        String fechaFirma = now.format(formatter);
// No tocar
        // Investigar Charset
        String formattedIssuer = new String(String.format("Firmado electrónicamente por:%n%s%n%s%n%s%n%s, %s, %s%n%s",
                fields.getOrDefault("CN", ""),
                fields.getOrDefault("OU", ""),
                fields.getOrDefault("O", ""),
                fields.getOrDefault("L", ""),
                fields.getOrDefault("ST", ""),
                fields.getOrDefault("C", ""),
                fechaFirma).getBytes(StandardCharsets.UTF_8), StandardCharsets.UTF_8);

        try (PdfReader reader = new PdfReader(src);
             PdfWriter writer = new PdfWriter(dest)) {

            StampingProperties stampingProperties = new StampingProperties();
            PdfSigner signer = new PdfSigner(reader, writer, stampingProperties);

            PdfDocument pdfDoc = signer.getDocument();
            int lastPageNumber = pdfDoc.getNumberOfPages();

            // Obtener el conteo de firmas existentes
            PdfAcroForm acroForm = PdfAcroForm.getAcroForm(signer.getDocument(), true);
            int signatureCount = acroForm.getAllFormFields().size();




//            int signatureCount = getSignatureCount(pdfDoc);
            logger.info("Número de firmas existentes en el documento: " + signatureCount);


            // Determinar la posición de la firma
            float xPosition = FIRMA_X_INICIAL;
            float yPosition = FIRMA_Y_INICIAL;

            // Ajustar la posición según el número de firmas
            if (signatureCount == 0) {
                // Posición inicial
                signer.setFieldName("sig_1");
                logger.info("Colocando la primera firma en la posición inicial.");
            } else if (signatureCount == 1) {
                signer.setFieldName("sig_2");
                xPosition += FIRMA_ANCHO + ESPACIO_ENTRE_LINEAS;
                logger.info("Colocando la segunda firma a la derecha de la primera.");
            } else if (signatureCount == 2) {
                signer.setFieldName("sig_3");
                yPosition -= ESPACIO_ENTRE_FILAS;
                // Reiniciar X para nueva fila
                logger.info("Colocando la tercera firma en la segunda fila.");
            } else if (signatureCount == 3) {
                signer.setFieldName("sig_4");
                yPosition -= ESPACIO_ENTRE_FILAS;
                xPosition += FIRMA_ANCHO + ESPACIO_ENTRE_LINEAS;
                logger.info("Colocando la cuarta firma a la derecha de la tercera.");
            } else if (signatureCount == 4) {
                signer.setFieldName("sig_5");
                yPosition -= ESPACIO_ENTRE_FILAS * 2;
                xPosition += 90 + ESPACIO_ENTRE_LINEAS;
                // Reiniciar X para nueva fila
                logger.info("Colocando la quinta firma en la tercera fila.");
            }

            signer.setPageNumber(lastPageNumber);
            Rectangle rect = new Rectangle(xPosition, yPosition, FIRMA_ANCHO, FIRMA_ALTO);
            signer.setPageRect(rect);

            String sign_name = signer.getFieldName();
            logger.info("Nombre de firma: " + sign_name);

            // Configurar el texto de la firma
            PdfSignatureAppearance appearance = signer.getSignatureAppearance();
            PdfFont font = PdfFontFactory.createFont(StandardFonts.HELVETICA, StandardCharsets.UTF_8.name());
            appearance.setLayer2Font(font);
            appearance.setLayer2Text(formattedIssuer);
//            signer.setFieldName(NOMBRE_CAMPO_FIRMA);

            // Metadatos de la firma
            signer.setReason("");
            signer.setLocation("");
            signer.setContact("");

            // Realizar la firma digital
            // Investigar otros algoritmon
            IExternalSignature pks = new PrivateKeySignature(pk, DigestAlgorithms.SHA256, "BC");
            BouncyCastleDigest digest = new BouncyCastleDigest();
            signer.signDetached(digest, pks, chain, null, null, null, 0, PdfSigner.CryptoStandard.CMS);

            logger.info("Proceso de firma del PDF finalizado exitosamente.");
        }
    }

//    private int getSignatureCount(PdfDocument pdfDoc) {
//        int signatureCount = 0;
//
//        // Obtener el AcroForm del documento
//        PdfAcroForm acroForm = PdfAcroForm.getAcroForm(pdfDoc, false);
//        if (acroForm != null) {
//            // Iterar a través de todos los campos del formulario
//            for (Map.Entry<String, PdfFormField> entry : acroForm.getAllFormFields().entrySet()) {
//                PdfFormField field = entry.getValue();
//                // Comprobar si el campo es un campo de firma
//                if (field.getFieldName() != null && field.getFieldName().toString().toLowerCase().contains("sig")) {
//                    signatureCount++;
//                }
//            }
//        }
//        logger.info("Total de campos de firma encontrados: " + signatureCount);
//        return signatureCount;
//    }
}