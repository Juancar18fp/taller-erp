package com.jcfp.tallererp.util;

public class DatosEmpresaException extends RuntimeException {
    public DatosEmpresaException(String message) {
        super(message);
    }

    public DatosEmpresaException(String message, Throwable cause) {
        super(message, cause);
    }
}
