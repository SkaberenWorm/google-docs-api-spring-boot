package cl.skaberen.google.docs.example.google_docs_example.controllers;

import java.io.IOException;

import javax.servlet.http.HttpServletResponse;

import com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeRequestUrl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import cl.skaberen.google.docs.example.google_docs_example.Utils.Global;
import cl.skaberen.google.docs.example.google_docs_example.services.IGoogleDocsService;
import cl.skaberen.google.docs.example.google_docs_example.services.impl.GoogleConnectionService;

@RequestMapping("/api/google")
@RestController
public class GoogleRestController {

    @Autowired
    IGoogleDocsService googleService;

    @Autowired
    private GoogleConnectionService connection;

    @GetMapping("/ask")
    public void ask(HttpServletResponse response) throws IOException {

        // Step 1: Authorize --> ask for auth code
        String url = new GoogleAuthorizationCodeRequestUrl(connection.getClientSecrets(), connection.getRedirectUrl(),
                Global.SCOPES)
                        // .setApprovalPrompt("auto")
                        .setApprovalPrompt("force").build();

        System.out.println("Go to the following link in your browser: ");
        System.out.println(url);

        response.sendRedirect(url);
    }

    @GetMapping("/oauth2callback")
    public void callback(@RequestParam("code") String code, HttpServletResponse response) throws IOException {
        if (connection.exchangeCode(code)) {
            response.sendRedirect(connection.getSourceUrl());
            // response.sendRedirect("/api/google/validated/createDoc");
        } else {
            response.sendRedirect("/api/google/error");
        }
    }

    @GetMapping("/error")
    public ResponseEntity<String> error() {
        return new ResponseEntity<String>("ERROR", HttpStatus.OK);
    }

    @GetMapping("/validated/get-title")
    public ResponseEntity<String> getTitleValidated() {
        return new ResponseEntity<String>(this.googleService.getTitleDoc(connection, Global.DOCUMENT_ID),
                HttpStatus.OK);
    }

    @GetMapping("validate/get-title")
    public ResponseEntity<String> getTitleValidate() {
        return new ResponseEntity<String>(this.googleService.getTitleDoc(connection, Global.DOCUMENT_ID),
                HttpStatus.OK);
    }

    @GetMapping("/validated/createDoc")
    public ResponseEntity<String> getCreateDocValidated() {
        this.googleService.createDoc(connection);
        return new ResponseEntity<String>("--", HttpStatus.OK);
    }

    @GetMapping("validate/createDoc")
    public ResponseEntity<String> getCreateDocValidat() {
        this.googleService.createDoc(connection);
        return new ResponseEntity<String>("--", HttpStatus.OK);
    }

}
