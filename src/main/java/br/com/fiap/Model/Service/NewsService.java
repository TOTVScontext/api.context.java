package br.com.fiap.Model.Service;

import br.com.fiap.Excecao.PersistenciaException;
import br.com.fiap.Excecao.RecursoNaoEncontradoException;
import br.com.fiap.Excecao.RequisicaoInvalidaException;
import br.com.fiap.Model.Dao.ConnectionFactory;
import br.com.fiap.Model.Dao.NewsDao;
import br.com.fiap.Model.Dto.News;
import br.com.fiap.Model.Dto.NewsPage;
import br.com.fiap.Model.Dto.NewsRequest;
import org.springframework.stereotype.Service;

import java.net.URI;
import java.net.URISyntaxException;
import java.sql.Connection;
import java.util.function.Function;

@Service
public class NewsService {

    private static final int TAMANHO_PAGINA_MAXIMO = 50;
    private static final int TITULO_TAMANHO_MAXIMO = 255;
    private static final int SUBTITULO_TAMANHO_MAXIMO = 255;
    private static final int REDIRECIONAMENTO_TAMANHO_MAXIMO = 2048;
    private static final String MSG_NAO_ENCONTRADA = "Notícia não encontrada.";

    public NewsPage listar(int pagina, int tamanhoPagina) {
        int paginaValida = Math.max(1, pagina);
        int tamanhoValido = Math.min(TAMANHO_PAGINA_MAXIMO, Math.max(1, tamanhoPagina));
        long offset = (long) (paginaValida - 1) * tamanhoValido;

        return executar(dao -> {
            NewsPage resultado = new NewsPage(
                    dao.listarTodos(offset, tamanhoValido),
                    dao.contar(),
                    paginaValida,
                    tamanhoValido);
            return resultado;
        });
    }

    public News buscarPorId(Long id) {
        validarId(id);
        return executar(dao -> dao.listarUm(id)
                .orElseThrow(() -> new RecursoNaoEncontradoException(MSG_NAO_ENCONTRADA)));
    }

    public News criar(NewsRequest request) {
        News news = montarNews(request);
        return executar(dao -> {
            Long idGerado = dao.inserir(news);
            return dao.listarUm(idGerado)
                    .orElseThrow(() -> new PersistenciaException("Registro criado não localizado."));
        });
    }

    public News atualizar(Long id, NewsRequest request) {
        validarId(id);
        News news = montarNews(request);
        news.setId(id);

        return executar(dao -> {
            if (!dao.alterar(news)) {
                throw new RecursoNaoEncontradoException(MSG_NAO_ENCONTRADA);
            }
            return dao.listarUm(id)
                    .orElseThrow(() -> new RecursoNaoEncontradoException(MSG_NAO_ENCONTRADA));
        });
    }

    public void excluir(Long id) {
        validarId(id);
        executar(dao -> {
            if (!dao.excluir(id)) {
                throw new RecursoNaoEncontradoException(MSG_NAO_ENCONTRADA);
            }
            return null;
        });
    }

    private <R> R executar(Function<NewsDao, R> operacao) {
        Connection con = ConnectionFactory.abrirConexao();
        try {
            return operacao.apply(new NewsDao(con));
        } finally {
            ConnectionFactory.fecharConexao(con);
        }
    }

    private void validarId(Long id) {
        if (id == null) {
            throw new RequisicaoInvalidaException("Parâmetro \"id\" é obrigatório.");
        }
    }

    private News montarNews(NewsRequest request) {
        if (request == null) {
            throw new RequisicaoInvalidaException("Corpo da requisição é obrigatório.");
        }

        String titulo = normalizar(request.title());
        if (titulo == null) {
            throw new RequisicaoInvalidaException("Campo \"title\" é obrigatório.");
        }
        validarTamanho(titulo, "title", TITULO_TAMANHO_MAXIMO);

        String conteudo = normalizar(request.content());
        if (conteudo == null) {
            throw new RequisicaoInvalidaException("Campo \"content\" é obrigatório.");
        }

        String subtitulo = normalizar(request.subtitle());
        if (subtitulo != null) {
            validarTamanho(subtitulo, "subtitle", SUBTITULO_TAMANHO_MAXIMO);
        }

        String redirecionamento = normalizar(request.redirection());
        if (redirecionamento != null) {
            validarTamanho(redirecionamento, "redirection", REDIRECIONAMENTO_TAMANHO_MAXIMO);
            validarUrl(redirecionamento);
        }

        News news = new News();
        news.setTitle(titulo);
        news.setSubtitle(subtitulo);
        news.setContent(conteudo);
        news.setRedirection(redirecionamento);
        return news;
    }

    private String normalizar(String valor) {
        if (valor == null) {
            return null;
        }
        String aparado = valor.trim();
        return aparado.isEmpty() ? null : aparado;
    }

    private void validarTamanho(String valor, String campo, int tamanhoMaximo) {
        if (valor.length() > tamanhoMaximo) {
            throw new RequisicaoInvalidaException(
                    "Campo \"" + campo + "\" deve ter no máximo " + tamanhoMaximo + " caracteres.");
        }
    }

    private void validarUrl(String valor) {
        boolean valida;
        try {
            URI uri = new URI(valor);
            String esquema = uri.getScheme();
            valida = ("http".equalsIgnoreCase(esquema) || "https".equalsIgnoreCase(esquema))
                    && uri.getHost() != null;
        } catch (URISyntaxException e) {
            valida = false;
        }

        if (!valida) {
            throw new RequisicaoInvalidaException("Campo \"redirection\" deve ser uma URL http(s) válida.");
        }
    }
}
