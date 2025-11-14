package br.com.matheus161.linkai_api.dto;

import jakarta.validation.constraints.NotNull;

public record LoginRequestDto (
        @NotNull(message = "O e-mail é obrigatório")
        String email,

        @NotNull(message = "A senha é obrigatória")
        String password
) {}
