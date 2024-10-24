package Firmar;

import com.itextpdf.kernel.pdf.PdfReader;
import com.itextpdf.signatures.PdfSigner;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.kernel.pdf.StampingProperties;
import com.itextpdf.signatures.BouncyCastleDigest;
import com.itextpdf.signatures.DigestAlgorithms;
import com.itextpdf.signatures.IExternalSignature;
import com.itextpdf.signatures.PrivateKeySignature;
import com.itextpdf.signatures.PdfSignatureAppearance;
import org.bouncycastle.jce.provider.BouncyCastleProvider;

import java.io.FileInputStream;
import java.security.KeyStore;
import java.security.PrivateKey;
import java.security.Security;
import java.security.cert.Certificate;
import java.util.Enumeration;
import java.util.logging.Level;
import java.util.logging.Logger;

public class PdfSignerService {

    private static final Logger logger = Logger.getLogger(PdfSignerService.class.getName());

    private String src;
    private String dest;
    private String pathToPfx;
    private String password;

    public PdfSignerService(String src, String dest, String pathToPfx, String password) {
        this.src = src;
        this.dest = dest;
        this.pathToPfx = pathToPfx;
        this.password = password;
    }

    public void signPdf() throws Exception {
        logger.info("Iniciando el proceso de firma del PDF...");

        // Leer el certificado PFX
        logger.info("Cargando el archivo de certificado PFX...");
        Security.addProvider(new BouncyCastleProvider());
        KeyStore ks = KeyStore.getInstance("PKCS12");
        ks.load(new FileInputStream(pathToPfx), password.toCharArray());
        logger.info("Certificado PFX cargado correctamente.");

        // Obtener la clave privada y el certificado
        logger.info("Extrayendo la clave privada y el certificado del archivo PFX...");
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
        logger.info("Clave privada y certificado extraídos correctamente.");

        // Obtener el subject del certificado
        String subject = ks.getCertificate(alias).toString();
        logger.info("Subject del certificado obtenido: " + subject);

        // Crear el lector y escritor de PDF
        logger.info("Creando el lector y escritor de PDF...");
        PdfReader reader = new PdfReader(src);
        PdfWriter writer = new PdfWriter(dest);
        StampingProperties stampingProperties = new StampingProperties();
        PdfSigner signer = new PdfSigner(reader, writer, stampingProperties);
        logger.info("Lector y escritor de PDF creados correctamente.");

        // Crear la apariencia de la firma
        PdfSignatureAppearance appearance = signer.getSignatureAppearance();

        // Establecer la firma en la última página
        int lastPageNumber = signer.getDocument().getNumberOfPages();
        appearance.setPageNumber(lastPageNumber); // Solo en la última página
        appearance.setPageRect(new com.itextpdf.kernel.geom.Rectangle(36, 648, 200, 100)); // Posición y tamaño de la firma en la página
        logger.info("Apariencia de la firma configurada para la última página del PDF.");

        appearance.setReason("");  // No se mostrará la razón
        appearance.setLocation("");  // No se mostrará la localización
        appearance.setLayer2Text(subject);  // Mostrar solo el subject
        signer.setFieldName("sig");

        // Firmar el documento
        logger.info("Iniciando el proceso de firma...");
        // Asegurarse que BouncyCastle esté registrado como proveedor

        IExternalSignature pks = new PrivateKeySignature(pk, DigestAlgorithms.SHA256, "BC");
        BouncyCastleDigest digest = new BouncyCastleDigest();
        signer.signDetached(digest, pks, chain, null, null, null, 0, PdfSigner.CryptoStandard.CMS);
        logger.info("Firma completada correctamente.");

        logger.info("Proceso de firma del PDF finalizado.");
    }
}