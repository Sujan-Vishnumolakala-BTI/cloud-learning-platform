package com.learningplatform.enroll_service.controller;

import com.learningplatform.enroll_service.dto.CertificateResponse;
import com.learningplatform.enroll_service.entity.Certificate;
import com.learningplatform.enroll_service.repository.CertificateRepository;
import com.learningplatform.enroll_service.service.CertificatePdfService;
import com.learningplatform.enroll_service.service.CertificateService;

import org.springframework.http.ContentDisposition;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;

import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/certificates")
public class CertificateController {

    private final CertificateService certificateService;
    private final CertificatePdfService pdfService;
    private final CertificateRepository certificateRepository;

    public CertificateController(
            CertificateService certificateService,
            CertificatePdfService pdfService,
            CertificateRepository certificateRepository) {

        this.certificateService =
                certificateService;

        this.pdfService =
                pdfService;

        this.certificateRepository =
                certificateRepository;
    }

    /*
     * CREATE / GET MY CERTIFICATE
     */
    @PostMapping("/courses/{courseId}")
    public ResponseEntity<CertificateResponse>
    generateCertificate(
            @PathVariable Long courseId) {

        return ResponseEntity.ok(
                certificateService
                        .generateCertificate(
                                courseId));
    }

    /*
     * GET CERTIFICATE DETAILS
     */
    @GetMapping("/courses/{courseId}")
    public ResponseEntity<CertificateResponse>
    getMyCertificate(
            @PathVariable Long courseId) {

        return ResponseEntity.ok(
                certificateService
                        .getMyCertificate(
                                courseId));
    }

    /*
     * DOWNLOAD CERTIFICATE PDF
     */
    @GetMapping("/courses/{courseId}/download")
    public ResponseEntity<byte[]> downloadCertificate(
            @PathVariable Long courseId) {

        CertificateResponse response =
                certificateService
                        .getMyCertificate(
                                courseId);

        Certificate certificate =
                certificateRepository
                        .findById(
                                response.getId())
                        .orElseThrow(() ->
                                new RuntimeException(
                                        "Certificate not found"));

        byte[] pdf =
                pdfService.generateCertificate(
                        certificate);

        String filename =
                "CloudPath-Certificate-" +
                        certificate.getCourseTitle()
                                .replaceAll(
                                        "[^a-zA-Z0-9-_]",
                                        "-") +
                        ".pdf";

        HttpHeaders headers =
                new HttpHeaders();

        headers.setContentType(
                MediaType.APPLICATION_PDF);

        headers.setContentDisposition(
                ContentDisposition
                        .attachment()
                        .filename(filename)
                        .build());

        return ResponseEntity
                .ok()
                .headers(headers)
                .body(pdf);
    }
}