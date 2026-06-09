package com.riccardopoppi.catalogo_cap.dto;

import java.util.UUID;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class CappelloDTO {
    private UUID id;

    @NotBlank(message = "Il codice del cappello è obbligatorio")
    private String codice;

    @NotBlank(message = "Il nome del modello è obbligatorio")
    private String nome;

    @NotBlank(message = "La marca è obbligatoria")
    private String marca;

    @NotBlank(message = "La taglia è obbligatoria")
    private String taglia;

    @Min(value = 1900, message = "L'anno non può essere inferiore al 1900")
    private int anno;

    @Min(value = 0, message = "Il prezzo non può essere inferiore a zero")
    private double prezzo;
    
    private String immagine;
}