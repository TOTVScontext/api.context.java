package br.com.fiap.Excecao;

public class AcaoDesconhecidaException extends RuntimeException {

    public AcaoDesconhecidaException(String acao) {
        super("Ação desconhecida: \"" + acao + "\".");
    }
}
