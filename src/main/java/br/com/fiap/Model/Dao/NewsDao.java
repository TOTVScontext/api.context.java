package br.com.fiap.Model.Dao;

import br.com.fiap.Excecao.PersistenciaException;
import br.com.fiap.Model.Dto.News;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class NewsDao implements IDAO<News> {

    private static final String COLUNAS = "id, title, subtitle, content, redirection, created_at";

    private final Connection con;

    public NewsDao(Connection con) {
        this.con = con;
    }

    public Connection getCon() {
        return con;
    }

    @Override
    public Long inserir(News news) {
        String sql = "insert into news(title, subtitle, content, redirection, created_at) "
                + "values(?, ?, ?, ?, systimestamp)";
        try (PreparedStatement ps = getCon().prepareStatement(sql, new String[]{"ID"})) {
            ps.setString(1, news.getTitle());
            ps.setString(2, news.getSubtitle());
            ps.setString(3, news.getContent());
            ps.setString(4, news.getRedirection());
            ps.executeUpdate();

            try (ResultSet rs = ps.getGeneratedKeys()) {
                if (rs.next()) {
                    return rs.getLong(1);
                }
                throw new PersistenciaException("Identificador gerado não retornado na inserção.");
            }
        } catch (SQLException e) {
            throw new PersistenciaException("Erro ao inserir notícia: " + e.getMessage(), e);
        }
    }

    @Override
    public boolean alterar(News news) {
        String sql = "update news set title = ?, subtitle = ?, content = ?, redirection = ? where id = ?";
        try (PreparedStatement ps = getCon().prepareStatement(sql)) {
            ps.setString(1, news.getTitle());
            ps.setString(2, news.getSubtitle());
            ps.setString(3, news.getContent());
            ps.setString(4, news.getRedirection());
            ps.setLong(5, news.getId());
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            throw new PersistenciaException("Erro ao alterar notícia: " + e.getMessage(), e);
        }
    }

    @Override
    public boolean excluir(Long id) {
        String sql = "delete from news where id = ?";
        try (PreparedStatement ps = getCon().prepareStatement(sql)) {
            ps.setLong(1, id);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            throw new PersistenciaException("Erro ao excluir notícia: " + e.getMessage(), e);
        }
    }

    @Override
    public Optional<News> listarUm(Long id) {
        String sql = "select " + COLUNAS + " from news where id = ?";
        try (PreparedStatement ps = getCon().prepareStatement(sql)) {
            ps.setLong(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() ? Optional.of(mapear(rs)) : Optional.empty();
            }
        } catch (SQLException e) {
            throw new PersistenciaException("Erro ao buscar notícia: " + e.getMessage(), e);
        }
    }

    @Override
    public List<News> listarTodos(long offset, int limite) {
        String sql = "select * from (select t.*, rownum rn from "
                + "(select " + COLUNAS + " from news order by created_at desc, id desc) t "
                + "where rownum <= ?) where rn > ?";
        try (PreparedStatement ps = getCon().prepareStatement(sql)) {
            ps.setLong(1, offset + limite);
            ps.setLong(2, offset);
            try (ResultSet rs = ps.executeQuery()) {
                List<News> lista = new ArrayList<>();
                while (rs.next()) {
                    lista.add(mapear(rs));
                }
                return lista;
            }
        } catch (SQLException e) {
            throw new PersistenciaException("Erro ao listar notícias: " + e.getMessage(), e);
        }
    }

    @Override
    public long contar() {
        String sql = "select count(*) from news";
        try (PreparedStatement ps = getCon().prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            return rs.next() ? rs.getLong(1) : 0L;
        } catch (SQLException e) {
            throw new PersistenciaException("Erro ao contar notícias: " + e.getMessage(), e);
        }
    }

    private News mapear(ResultSet rs) throws SQLException {
        News news = new News();
        news.setId(rs.getLong("id"));
        news.setTitle(rs.getString("title"));
        news.setSubtitle(rs.getString("subtitle"));
        news.setContent(rs.getString("content"));
        news.setRedirection(rs.getString("redirection"));
        Timestamp createdAt = rs.getTimestamp("created_at");
        news.setCreatedAt(createdAt != null ? createdAt.toInstant() : null);
        return news;
    }
}
