package br.com.matheus161.linkai_api.services;

import br.com.matheus161.linkai_api.dto.LoginRequestDto;
import br.com.matheus161.linkai_api.dto.LoginResponseDto;
import br.com.matheus161.linkai_api.dto.RegisterRequestDto;
import br.com.matheus161.linkai_api.dto.RegisterResponseDto;

public interface IAuthService {
    public LoginResponseDto login(LoginRequestDto body);
    public RegisterResponseDto register(RegisterRequestDto body);
}
