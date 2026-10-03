package br.com.fiap.Model.Dao;

import br.com.fiap.Excecao.PersistenciaException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public final class ConnectionFactory {

    private static final Logger LOGGER = LoggerFactory.getLogger(ConnectionFactory.class);

    private static final String DRIVER_JDBC = "oracle.jdbc.driver.OracleDriver";
    private static final String URL_PADRAO = "jdbc:oracle:thin:@oracle.fiap.com.br:1521:ORCL";

    private ConnectionFactory() {
    }

    public static Connection abrirConexao() {
        try {
            Class.forName(DRIVER_JDBC);
            return DriverManager.getConnection(
                    valor("DB_URL", URL_PADRAO),
                    obrigatorio("DB_USER"),
                    obrigatorio("DB_PASSWORD"));
        } catch (ClassNotFoundException e) {
            throw new PersistenciaException("Driver JDBC do Oracle não encontrado.", e);
        } catch (SQLException e) {
            throw new PersistenciaException("Falha ao conectar ao banco de dados Oracle: " + e.getMessage(), e);
        }
    }

    public static void fecharConexao(Connection con) {
        if (con == null) {
            return;
        }
        try {
            con.close();
        } catch (SQLException e) {
            LOGGER.warn("Falha ao fechar a conexão: {}", e.getMessage());
        }
    }

    private static String valor(String variavel, String padrao) {
        String valor = System.getenv(variavel);
        return valor == null || valor.isBlank() ? padrao : valor;
    }

    private static String obrigatorio(String variavel) {
        String valor = System.getenv(variavel);
        if (valor == null || valor.isBlank()) {
            throw new PersistenciaException("Variável de ambiente obrigatória não definida: " + variavel);
        }
        return valor;
    }
}
