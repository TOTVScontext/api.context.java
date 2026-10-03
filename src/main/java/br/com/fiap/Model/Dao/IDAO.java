package br.com.fiap.Model.Dao;

import java.util.List;
import java.util.Optional;

public interface IDAO<T> {

    Long inserir(T objeto);

    boolean alterar(T objeto);

    boolean excluir(Long id);

    Optional<T> listarUm(Long id);

    List<T> listarTodos(long offset, int limite);

    long contar();
}
