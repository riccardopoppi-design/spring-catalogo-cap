package com.riccardopoppi.catalogo_cap.controllers;

import com.riccardopoppi.catalogo_cap.dto.APIResponse;
import com.riccardopoppi.catalogo_cap.dto.CappelloDTO;
import com.riccardopoppi.catalogo_cap.domain.Cappello;
import com.riccardopoppi.catalogo_cap.repositories.CappelloRepository;

import jakarta.validation.Valid;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/cappelli")
@CrossOrigin(origins = "*")
public class CappelloController {

    @Autowired
    private CappelloRepository cappelloRepository;

    @Autowired
    private ModelMapper modelMapper;

    @GetMapping
    public ResponseEntity<APIResponse<List<CappelloDTO>>> getAll() {
        List<Cappello> cappelli = cappelloRepository.findAll();
        List<CappelloDTO> dtos = cappelli.stream()
                .map(c -> modelMapper.map(c, CappelloDTO.class))
                .collect(Collectors.toList());
        return ResponseEntity.ok(APIResponse.success(dtos));
    }

@GetMapping("/{id}")
    public ResponseEntity<?> getById(@PathVariable UUID id) {
        return cappelloRepository.findById(id)
                .map(c -> {
                    CappelloDTO dto = modelMapper.map(c, CappelloDTO.class);
                    return ResponseEntity.ok(APIResponse.success(dto));
                })
                .orElse(ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(APIResponse.error("Cappello non trovato con l'ID fornito: " + id)));
    }

    @PostMapping
    public ResponseEntity<APIResponse<?>> create(@Valid @RequestBody CappelloDTO dto, BindingResult result) {
        if (result.hasErrors()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(APIResponse.fail(getErrorsMap(result)));
        }
        Cappello entity = modelMapper.map(dto, Cappello.class);
        Cappello saved = cappelloRepository.save(entity);
        return ResponseEntity.status(HttpStatus.CREATED).body(APIResponse.success(modelMapper.map(saved, CappelloDTO.class)));
    }

    @PutMapping("/{id}")
    public ResponseEntity<APIResponse<?>> update(@PathVariable UUID id, @Valid @RequestBody CappelloDTO dto, BindingResult result) {
        if (result.hasErrors()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(APIResponse.fail(getErrorsMap(result)));
        }
        if (!cappelloRepository.existsById(id)) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(APIResponse.error("Impossibile aggiornare. Cappello inesistente."));
        }
        Cappello entity = modelMapper.map(dto, Cappello.class);
        entity.setId(id);
        Cappello updated = cappelloRepository.save(entity);
        return ResponseEntity.ok(APIResponse.success(modelMapper.map(updated, CappelloDTO.class)));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<APIResponse<?>> delete(@PathVariable UUID id) {
        if (!cappelloRepository.existsById(id)) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(APIResponse.error("Impossibile eliminare. Cappello inesistente."));
        }
        cappelloRepository.deleteById(id);
        return ResponseEntity.ok(APIResponse.success("Cappello rimosso con successo dal catalogo"));
    }

    private Map<String, String> getErrorsMap(BindingResult result) {
        Map<String, String> errors = new HashMap<>();
        for (FieldError error : result.getFieldErrors()) {
            errors.put(error.getField(), error.getDefaultMessage());
        }
        return errors;
    }
}