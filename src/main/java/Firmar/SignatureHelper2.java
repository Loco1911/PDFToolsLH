package Firmar;

import com.itextpdf.kernel.geom.Rectangle;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfReader;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.kernel.pdf.StampingProperties;
import com.itextpdf.signatures.*;
import org.bouncycastle.jce.provider.BouncyCastleProvider;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.security.KeyStore;
import java.security.PrivateKey;
import java.security.Security;
import java.security.cert.Certificate;
import java.util.Enumeration;
import java.util.logging.Level;
import java.util.logging.Logger;

public class SignatureHelper2 {
    private static final Logger logger = Logger.getLogger(SignatureHelper2.class.getName());

    private PrivateKey privateKey;
    private Certificate[] certChain;
    private String signatureAlgorithm;

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

                // Detectar el algoritmo de firma
                signatureAlgorithm = detectSignatureAlgorithm(privateKey);
                logger.log(Level.INFO, "Algoritmo de firma detectado: " + signatureAlgorithm);
            } else {
                logger.log(Level.SEVERE, "No se encontraron alias en el keystore.");
                throw new IllegalStateException("No se encontraron alias en el keystore.");
            }
        }
    }

    private String detectSignatureAlgorithm(PrivateKey privateKey) {
        String algorithm = privateKey.getAlgorithm();

        switch (algorithm) {
            case "RSA":
                return "SHA256withRSA"; // Podrían ser otros algoritmos como SHA1withRSA, etc.
            case "DSA":
                return "SHA256withDSA"; // Podrían ser otros algoritmos como SHA1withDSA, etc.
            case "EC":
                return "SHA256withECDSA"; // Podrían ser otros algoritmos como SHA1withECDSA, etc.
            default:
                return "SHA256withRSA";
        }
    }

    public void signPdf(String src, String dest, String text) throws Exception {
        // Verificación adicional para signatureAlgorithm
        if (signatureAlgorithm == null) {
            logger.log(Level.SEVERE, "Algoritmo de firma no está definido.");
//            throw new IllegalStateException("Algoritmo de firma no está definido.");
        }
        //String signature = "SHA256withRSA";

        PdfDocument pdfDoc = new PdfDocument(new PdfReader(src), new PdfWriter(dest));
        PdfSigner signer = new PdfSigner(new PdfReader(src), new FileOutputStream(dest), new StampingProperties().useAppendMode());

        int pageHeight = (int) signer.getDocument().getDefaultPageSize().getHeight();
        Rectangle signatureRect = new Rectangle(36, pageHeight - 100, 200, 60);

        PdfSignatureAppearance appearance = signer.getSignatureAppearance()
                .setReason("Firma Digital")
                .setLocation("Localización")
                .setPageRect(signatureRect)
                .setPageNumber(1);

        signer.setFieldName("sig_1");

        IExternalSignature pks = new PrivateKeySignature(privateKey, signatureAlgorithm, "BC");
        IExternalDigest digest = new BouncyCastleDigest();
        signer.signDetached(digest, pks, certChain, null, null, null, 0, PdfSigner.CryptoStandard.CMS);

        logger.log(Level.INFO, "Firma realizada con éxito en: " + dest);
    }
}