package br.com.matheus161.linkai_api.services;

import br.com.matheus161.linkai_api.dto.CreateLinkRequestDto;
import br.com.matheus161.linkai_api.dto.CreateLinkResponseDto;

public interface ILinkService {
    public CreateLinkResponseDto create(CreateLinkRequestDto body);
}
