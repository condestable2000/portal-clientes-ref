package com.imagina.demo.exception;

public class EmailInvalidoException extends RuntimeException {

    private final String campo;

    public EmailInvalidoException(String campo, String motivo) {
        super(motivo);
        this.campo = campo;
    }

    public String getCampo() {
        return campo;
    }
}
