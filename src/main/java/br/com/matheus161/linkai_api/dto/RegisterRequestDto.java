package br.com.matheus161.linkai_api.dto;

import br.com.matheus161.linkai_api.domain.validation.ValidPassword;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.NotBlank;

public record RegisterRequestDto (
        @NotNull(message = "O nome é obrigatório")
        String name,

        @Email(message = "E-mail inválido")
        @NotBlank(message = "O e-mail é obrigatório")
        String email,

        @NotBlank(message = "A senha é obrigatória")
        @ValidPassword
        String password
) {}
