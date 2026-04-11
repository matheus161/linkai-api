package br.com.matheus161.linkai_api.controllers;

import br.com.matheus161.linkai_api.dto.CreateLinkRequestDto;
import br.com.matheus161.linkai_api.dto.CreateLinkResponseDto;
import br.com.matheus161.linkai_api.infra.security.CurrentUserId;
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
    public ResponseEntity<CreateLinkResponseDto> create(@RequestBody @Valid CreateLinkRequestDto body,
                                                        @CurrentUserId String userId) {
        CreateLinkResponseDto response = service.create(body, userId);
        return ResponseEntity.ok(response);
    }
}
