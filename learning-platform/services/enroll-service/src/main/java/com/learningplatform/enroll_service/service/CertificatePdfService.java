package com.learningplatform.enroll_service.service;

import com.learningplatform.enroll_service.entity.Certificate;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.common.PDRectangle;
import org.apache.pdfbox.pdmodel.font.PDType1Font;
// import org.apache.pdfbox.pdmodel.font.PDType1Font;
import org.apache.pdfbox.pdmodel.font.Standard14Fonts;

import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

@Service
public class CertificatePdfService {

    public byte[] generateCertificate(
            Certificate certificate) {

        try {

            PDRectangle landscape =
                    new PDRectangle(
                            PDRectangle.A4.getHeight(),
                            PDRectangle.A4.getWidth());

            try (PDDocument document =
                         new PDDocument()) {

                PDPage page =
                        new PDPage(landscape);

                document.addPage(page);

                try (PDPageContentStream content =
                             new PDPageContentStream(
                                     document,
                                     page)) {

                    float width =
                            page.getMediaBox()
                                    .getWidth();

                    float height =
                            page.getMediaBox()
                                    .getHeight();

                    /*
                     * Border
                     */
                    content.setLineWidth(3);

                    content.addRect(
                            30,
                            30,
                            width - 60,
                            height - 60);

                    content.stroke();

                    /*
                     * CLOUDPATH
                     */
                    drawCentered(
                            content,
                            "CLOUDPATH",
                            width / 2,
                            height - 100,
                            26,
                            new PDType1Font(Standard14Fonts.FontName.HELVETICA_BOLD));

                    /*
                     * Certificate
                     */
                    drawCentered(
                            content,
                            "CERTIFICATE OF COMPLETION",
                            width / 2,
                            height - 160,
                            30,
                            new PDType1Font(Standard14Fonts.FontName.HELVETICA_BOLD));

                    /*
                     * Certification text
                     */
                    drawCentered(
                            content,
                            "This certificate is proudly presented to",
                            width / 2,
                            height - 220,
                            16,
                            new PDType1Font(Standard14Fonts.FontName.HELVETICA));

                    /*
                     * Student
                     */
                    drawCentered(
                            content,
                            certificate.getStudentName(),
                            width / 2,
                            height - 275,
                            28,
                            new PDType1Font(Standard14Fonts.FontName.HELVETICA_BOLD));

                    /*
                     * Course
                     */
                    drawCentered(
                            content,
                            "for successfully completing",
                            width / 2,
                            height - 325,
                            15,
                            new PDType1Font(Standard14Fonts.FontName.HELVETICA));

                    drawCentered(
                            content,
                            certificate.getCourseTitle(),
                            width / 2,
                            height - 370,
                            24,
                            new PDType1Font(Standard14Fonts.FontName.HELVETICA_BOLD));

                    /*
                     * Certificate ID
                     */
                    drawCentered(
                            content,
                            "Certificate ID: " +
                                    certificate
                                            .getCertificateNumber(),
                            width / 2,
                            100,
                            12,
                            new PDType1Font(Standard14Fonts.FontName.HELVETICA));

                    /*
                     * Issued date
                     */
                    drawCentered(
                            content,
                            "Issued: " +
                                    certificate
                                            .getIssuedAt()
                                            .toLocalDate(),
                            width / 2,
                            75,
                            12,
                            new PDType1Font(Standard14Fonts.FontName.HELVETICA));
                }

                ByteArrayOutputStream output =
                        new ByteArrayOutputStream();

                document.save(output);

                return output.toByteArray();
            }

        } catch (IOException exception) {

            throw new RuntimeException(
                    "Unable to generate certificate PDF",
                    exception);
        }
    }

    private void drawCentered(
            PDPageContentStream content,
            String text,
            float centerX,
            float y,
            float fontSize,
            PDType1Font font)
            throws IOException {

        float textWidth =
                font.getStringWidth(text)
                        / 1000
                        * fontSize;

        float x =
                centerX -
                        (textWidth / 2);

        content.beginText();

        content.setFont(
                font,
                fontSize);

        content.newLineAtOffset(
                x,
                y);

        content.showText(text);

        content.endText();
    }
}