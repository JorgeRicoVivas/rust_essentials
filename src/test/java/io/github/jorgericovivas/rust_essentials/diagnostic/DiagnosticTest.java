package io.github.jorgericovivas.rust_essentials.diagnostic;

class DiagnosticTest {
    
    @org.junit.jupiter.api.Test
    void showDiagnostic() {
Diagnostic diagnostic = new Diagnostic()
        .withConcept("This is an error.\nThis message tells what the error means.")
        .withNote("This is a note message.\nThis message tells why the error happened.")
        .withHelp("This is a help message.\nThis message tells information to help solve in solving the problem.");
System.out.println(diagnostic);
    }
    
    @org.junit.jupiter.api.Test
    void showDiagnosticExceptionContents() {
        var diagnosedException = new DiagnosedException(
                new Diagnostic()
                        .withConcept("An error\nHi")
                        .withNote("A note\nhi")
                        .withHelp("A help message\nhi"));
        System.out.println(diagnosedException.diagnostic());
    }
}