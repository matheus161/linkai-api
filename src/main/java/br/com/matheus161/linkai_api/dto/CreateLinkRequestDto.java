package br.com.matheus161.linkai_api.dto;


import jakarta.validation.constraints.NotNull;


public record CreateLinkRequestDto(
        @NotNull(message = "O nome é obrigatório")
        String title,

        @NotNull(message = "A descrição é obrigatório")
        String description,

        @NotNull(message = "O link é obrigatório")
        String original_link
) {}
