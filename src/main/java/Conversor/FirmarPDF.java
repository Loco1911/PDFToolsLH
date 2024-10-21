/*
package Conversor;

import com.itextpdf.kernel.colors.ColorConstants;
import com.itextpdf.kernel.geom.Rectangle;
import com.itextpdf.kernel.pdf.*;
import com.itextpdf.kernel.pdf.canvas.PdfCanvas;
import com.itextpdf.signatures.*;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.security.GeneralSecurityException;
import java.security.KeyStore;
import java.security.PrivateKey;
import java.security.cert.Certificate;

public class FirmarPDF {
    public void Firmar(PdfReader reader, PdfWriter writer) {
    }

    // Metodo para agregar 4 líneas negras para las firmas en la última página
    public static void agregarLineasFirma(String pdfPath, String outputPdfPath) throws IOException {
        PdfDocument pdfDocument = new PdfDocument(new PdfReader(pdfPath), new PdfWriter(outputPdfPath));

        int totalPaginas = pdfDocument.getNumberOfPages();
        PdfPage ultimaPagina = pdfDocument.getPage(totalPaginas);
        Rectangle pageSize = ultimaPagina.getPageSize();

        PdfCanvas canvas = new PdfCanvas(ultimaPagina);

        float yStart = 100; // Posición inicial de las líneas desde abajo
        float lineHeight = 20; // Altura entre las líneas
        float margin = 50; // Margen desde los lados

        // Dibujar 4 líneas para firmas
        for (int i = 0; i < 4; i++) {
            float yPosition = yStart + (i * lineHeight);
            canvas.setStrokeColor(ColorConstants.BLACK)
                    .setLineWidth(1)
                    .moveTo(margin, yPosition)
                    .lineTo(pageSize.getWidth() - margin, yPosition)
                    .stroke();
        }

        pdfDocument.close();
    }

    // Metodo para firmar el PDF con un certificado PFX
    public static void firmarPdfConCertificado(String pdfPath, String pfxPath, String password, String outputPdfPath)
            throws GeneralSecurityException, IOException {

        // Cargar el archivo PFX y obtener las credenciales
        KeyStore keyStore = KeyStore.getInstance("PKCS12");
        try (FileInputStream fis = new FileInputStream(pfxPath)) {
            keyStore.load(fis, password.toCharArray());
        }

        String alias = keyStore.aliases().nextElement();
        PrivateKey privateKey = (PrivateKey) keyStore.getKey(alias, password.toCharArray());
        Certificate[] chain = keyStore.getCertificateChain(alias);

        // Leer el PDF
        PdfDocument pdfDoc = new PdfDocument(new PdfReader(pdfPath), new PdfWriter(outputPdfPath));
        PdfSigner signer = new PdfSigner(pdfDoc.getReader(), new FileOutputStream(outputPdfPath), new StampingProperties());

        // Crear el proveedor de firma con la clave privada y el algoritmo de hash SHA256
        IExternalSignature pks = new PrivateKeySignature(privateKey, DigestAlgorithms.SHA256, "BC");
        IExternalDigest digest = new BouncyCastleDigest();

        // Firmar el documento
        signer.signDetached(digest, pks, chain, null, null, null, 0, PdfSigner.CryptoStandard.CMS);
    }


}
*/
