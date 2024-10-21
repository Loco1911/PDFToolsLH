/*
package Conversor;

import com.itextpdf.kernel.geom.Rectangle;
import com.itextpdf.kernel.pdf.PdfReader;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.signatures.PdfSignatureAppearance;
import com.itextpdf.signatures.PdfSigner;

import java.io.FileInputStream;
import java.security.KeyStore;
import java.security.PrivateKey;
import java.security.cert.Certificate;

public class FirmarPDF2 {
    public void firmarPDF(String pdfPath, String pfxPath) throws Exception {
        // Carga del certificado
        KeyStore ks = KeyStore.getInstance("PKCS12");
        ks.load(new FileInputStream(pfxPath), "CLAVE_PRIVADA_CERTIFICADO".toCharArray());
        String alias = (String) ks.aliases().nextElement();
        PrivateKey key = (PrivateKey) ks.getKey(alias, "CLAVE_PRIVADA_CERTIFICADO".toCharArray());
        Certificate[] chain = ks.getCertificateChain(alias);


        // Creación del firmante
        PdfReader reader = new PdfReader(pdfPath);
        PdfWriter writer = new PdfWriter(pdfPath + "_firmado.pdf");
        PdfSigner signer = new PdfSigner(reader, writer);

        // Configuración de la apariencia de la firma
        PdfSignatureAppearance appearance = signer.getSignatureAppearance();
        appearance.setReason("Firma digital");
        appearance.setLocation("Mi ubicación");
        appearance.setVisibleSignature(new Rectangle(100, 700, 300, 750), 1, null);
        appearance.setDigestAlgorithm(DigestAlgorithm.SHA256);
        appearance.setSubfilter(Subfilter.ETSI_CADES);

        // Creación de la firma externa
        ExternalSignature externalSignature = new ExternalSignatureContainer(key, chain);

        // Cierre y firma del documento
        signer.signDetached(externalSignature);
    }
}
*/
