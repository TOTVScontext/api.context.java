package br.com.fiap.Model.Dto;

public record NewsRequest(
        String title,
        String subtitle,
        String content,
        String redirection) {
}
