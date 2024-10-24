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
import com.itextpdf.kernel.geom.Rectangle;
import com.itextpdf.kernel.pdf.PdfDocument;
import org.bouncycastle.jce.provider.BouncyCastleProvider;

import java.io.FileInputStream;
import java.security.KeyStore;
import java.security.PrivateKey;
import java.security.Security;
import java.security.cert.Certificate;
import java.security.cert.X509Certificate;
import java.util.Enumeration;
import java.util.Map;
import java.util.logging.Logger;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class PdfSignerService {

    private static final Logger logger = Logger.getLogger(PdfSignerService.class.getName());

    private final String src;
    private final String dest;
    private final String pathToPfx;
    private final String password;

    public PdfSignerService(String src, String dest, String pathToPfx, String password) {
        this.src = src;
        this.dest = dest;
        this.pathToPfx = pathToPfx;
        this.password = password;
    }

    @SuppressWarnings("Duplicates")
    public void signPdf() throws Exception {
        logger.info("Iniciando el proceso de firma del PDF...");

        // Leer el certificado PFX
        Security.addProvider(new BouncyCastleProvider());
        KeyStore ks = KeyStore.getInstance("PKCS12");
        ks.load(new FileInputStream(pathToPfx), password.toCharArray());

        // Obtener la clave privada y el certificado
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

        // Obtener el issuer del certificado
        X509Certificate cert = (X509Certificate) ks.getCertificate(alias);
        String issuer = cert.getIssuerX500Principal().getName();
        logger.info("Issuer del certificado obtenido: " + issuer);

        // Extraer y formatear los campos específicos del issuer
        Map<String, String> fields = Stream.of(issuer.split(","))
                .map(String::trim)
                .filter(s -> s.matches("(CN|OU|O|L|ST|C)=(.*)"))
                .collect(Collectors.toMap(
                        s -> s.split("=")[0],
                        s -> s.split("=")[1]
                ));

        String formattedIssuer = "Firmado electrónicamente por\n" + String.join("\n",
                fields.getOrDefault("CN", ""),
                fields.getOrDefault("OU", ""),
                fields.getOrDefault("O", ""),
                fields.getOrDefault("L", ""),
                fields.getOrDefault("ST", ""),
                fields.getOrDefault("C", "")
        );

        // Crear el lector y escritor de PDF
        PdfReader reader = new PdfReader(src);
        PdfWriter writer = new PdfWriter(dest);
        StampingProperties stampingProperties = new StampingProperties();
        PdfSigner signer = new PdfSigner(reader, writer, stampingProperties);

        // Obtener la altura de la última página
        PdfDocument pdfDoc = signer.getDocument();
        int lastPageNumber = pdfDoc.getNumberOfPages();
        Rectangle pageSize = pdfDoc.getPage(lastPageNumber).getPageSize();

        // Crear la apariencia de la firma
        PdfSignatureAppearance appearance = signer.getSignatureAppearance();

        // Establecer la firma al final de la última página
        signer.setPageNumber(lastPageNumber);
        float x = 36;
        float y = 36; // Coordenadas X e Y para posicionar la firma al final de la página
        float width = 200;
        float height = 100;
        signer.setPageRect(new Rectangle(x, y, width, height)); // Posición y tamaño de la firma en la página

        // Configurar los atributos de apariencia de la firma
        appearance.setLayer2Text(formattedIssuer); // Mostrar el texto formateado
        signer.setFieldName("sig");

        // Configurar detalles de la firma
        signer.setReason("");
        signer.setContact("");
        signer.setLocation("");

        // Firmar el documento
        IExternalSignature pks = new PrivateKeySignature(pk, DigestAlgorithms.SHA256, "BC");
        BouncyCastleDigest digest = new BouncyCastleDigest();
        signer.signDetached(digest, pks, chain, null, null, null, 0, PdfSigner.CryptoStandard.CMS);

        logger.info("Proceso de firma del PDF finalizado.");
    }
}