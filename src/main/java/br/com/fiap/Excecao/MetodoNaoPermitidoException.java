package br.com.fiap.Excecao;

public class MetodoNaoPermitidoException extends RuntimeException {

    public MetodoNaoPermitidoException() {
        super("Método não permitido.");
    }
}
