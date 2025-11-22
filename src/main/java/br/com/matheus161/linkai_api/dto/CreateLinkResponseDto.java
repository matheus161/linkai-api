package br.com.matheus161.linkai_api.dto;

public record CreateLinkResponseDto(String title, String description, String original_link, String redirect_id) {
}
