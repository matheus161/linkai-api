package br.com.matheus161.linkai_api.controllers;

import br.com.matheus161.linkai_api.dto.CreateLinkRequestDto;
import br.com.matheus161.linkai_api.dto.CreateLinkResponseDto;
import br.com.matheus161.linkai_api.services.ILinkService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/link")
@RequiredArgsConstructor
public class LinkController {
    private final ILinkService service;


    @PostMapping
    public ResponseEntity<CreateLinkResponseDto> create(@RequestBody @Valid CreateLinkRequestDto body) {
        CreateLinkResponseDto response = service.create(body);
        return ResponseEntity.ok(response);
    }
}
