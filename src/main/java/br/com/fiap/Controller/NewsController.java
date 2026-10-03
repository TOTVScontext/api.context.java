package br.com.fiap.Controller;

import br.com.fiap.Excecao.AcaoDesconhecidaException;
import br.com.fiap.Excecao.MetodoNaoPermitidoException;
import br.com.fiap.Model.Dto.News;
import br.com.fiap.Model.Dto.NewsPage;
import br.com.fiap.Model.Dto.NewsRequest;
import br.com.fiap.Model.Service.NewsService;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.Set;

@RestController
@RequestMapping("/api/news")
public class NewsController {

    private static final Set<String> ACOES_CONHECIDAS = Set.of("list", "get", "create", "update", "delete");

    private final NewsService newsService;

    public NewsController(NewsService newsService) {
        this.newsService = newsService;
    }

    @GetMapping(params = "action=list")
    public NewsPage listar(@RequestParam(name = "page", defaultValue = "1") int pagina,
                           @RequestParam(name = "page_size", defaultValue = "20") int tamanhoPagina) {
        return newsService.listar(pagina, tamanhoPagina);
    }

    @GetMapping(params = "action=get")
    public News buscarPorId(@RequestParam(name = "id", required = false) Long id) {
        return newsService.buscarPorId(id);
    }

    @PostMapping(params = "action=create")
    @ResponseStatus(HttpStatus.CREATED)
    public News criar(@RequestBody NewsRequest request) {
        return newsService.criar(request);
    }

    @PutMapping(params = "action=update")
    public News atualizar(@RequestParam(name = "id", required = false) Long id,
                          @RequestBody NewsRequest request) {
        return newsService.atualizar(id, request);
    }

    @DeleteMapping(params = "action=delete")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void excluir(@RequestParam(name = "id", required = false) Long id) {
        newsService.excluir(id);
    }

    @RequestMapping
    public void acaoNaoSuportada(@RequestParam(name = "action", required = false) String acao) {
        if (acao != null && ACOES_CONHECIDAS.contains(acao)) {
            throw new MetodoNaoPermitidoException();
        }
        throw new AcaoDesconhecidaException(acao);
    }
}
