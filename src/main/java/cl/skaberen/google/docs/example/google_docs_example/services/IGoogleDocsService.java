package cl.skaberen.google.docs.example.google_docs_example.services;

import cl.skaberen.google.docs.example.google_docs_example.services.impl.GoogleConnectionService;

public interface IGoogleDocsService {
    String getTitleDoc(GoogleConnectionService connection);

    public void createDoc(GoogleConnectionService connection);
}
