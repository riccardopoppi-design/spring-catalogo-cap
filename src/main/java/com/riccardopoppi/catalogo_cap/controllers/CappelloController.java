package com.riccardopoppi.catalogo_cap.controllers;

import main.java.com.riccardopoppi.catalogo_cap.dto.APIResponse;
import main.java.com.riccardopoppi.catalogo_cap.dto.CappelloDTO;
import main.java.com.riccardopoppi.catalogo_cap.domain.Cappello;
import main.java.com.riccardopoppi.catalogo_cap.repositories.CappelloRepository;

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
@CrossOrigin(origins = "*") // Permette al frontend Vue di connettersi senza blocchi CORS
public class CappelloController {

    @Autowired
    private CappelloRepository cappelloRepository;

    @Autowired
    private ModelMapper modelMapper;

    // 1. LISTA COMPLETA dei cappelli (Convertiti in DTO)
    @GetMapping
    public ResponseEntity<APIResponse<List<CappelloDTO>>> getAll() {
        List<Cappello> cappelli = cappelloRepository.findAll();
        List<CappelloDTO> dtos = cappelli.stream()
                .map(c -> modelMapper.map(c, CappelloDTO.class))
                .collect(Collectors.toList());
        return ResponseEntity.ok(APIResponse.success(dtos));
    }

    // 2. DETTAGLIO di un singolo cappello per ID
    @GetMapping("/{id}")
    public ResponseEntity<APIResponse<?>> getById(@PathVariable UUID id) {
        return cappelloRepository.findById(id)
                .map(c -> {
                    CappelloDTO dto = modelMapper.map(c, CappelloDTO.class);
                    return ResponseEntity.ok((APIResponse<?>) APIResponse.success(dto));
                })
                .orElse(ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(APIResponse.error("Cappello non trovato con l'ID fornito: " + id)));
    }

    // 3. CREAZIONE di un nuovo cappello (Con Validazione automatica)
    @PostMapping
    public ResponseEntity<APIResponse<?>> create(@Valid @RequestBody CappelloDTO dto, BindingResult result) {
        if (result.hasErrors()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(APIResponse.fail(getErrorsMap(result)));
        }
        Cappello entity = modelMapper.map(dto, Cappello.class);
        Cappello saved = cappelloRepository.save(entity);
        return ResponseEntity.status(HttpStatus.CREATED).body(APIResponse.success(modelMapper.map(saved, CappelloDTO.class)));
    }

    // 4. AGGIORNAMENTO di un cappello esistente
    @PutMapping("/{id}")
    public ResponseEntity<APIResponse<?>> update(@PathVariable UUID id, @Valid @RequestBody CappelloDTO dto, BindingResult result) {
        if (result.hasErrors()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(APIResponse.fail(getErrorsMap(result)));
        }
        if (!cappelloRepository.existsById(id)) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(APIResponse.error("Impossibile aggiornare. Cappello inesistente."));
        }
        Cappello entity = modelMapper.map(dto, Cappello.class);
        entity.setId(id); // Forza l'ID corretto sull'entità da aggiornare
        Cappello updated = cappelloRepository.save(entity);
        return ResponseEntity.ok(APIResponse.success(modelMapper.map(updated, CappelloDTO.class)));
    }

    // 5. ELIMINAZIONE di un cappello
    @DeleteMapping("/{id}")
    public ResponseEntity<APIResponse<?>> delete(@PathVariable UUID id) {
        if (!cappelloRepository.existsById(id)) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(APIResponse.error("Impossibile eliminare. Cappello inesistente."));
        }
        cappelloRepository.deleteById(id);
        return ResponseEntity.ok(APIResponse.success("Cappello rimosso con successo dal catalogo"));
    }

    // Funzione di supporto per mappare gli errori di validazione nel formato richiesto (campo -> messaggio)
    private Map<String, String> getErrorsMap(BindingResult result) {
        Map<String, String> errors = new HashMap<>();
        for (FieldError error : result.getFieldErrors()) {
            errors.put(error.getField(), error.getDefaultMessage());
        }
        return errors;
    }
}