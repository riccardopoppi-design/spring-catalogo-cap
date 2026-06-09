package main.java.com.riccardopoppi.catalogo_cap.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class APIResponse<T> {
    private String status;   // Conterrà: success, fail, o error
    private T data;          // I dati veri dei cappelli o la mappa con gli errori del form
    private String message;  // Il messaggio di testo usato solo se c'è un guasto del server

    public static <T> APIResponse<T> success(T data) {
        return new APIResponse<>("success", data, null);
    }

    public static <T> APIResponse<T> fail(T errors) {
        return new APIResponse<>("fail", errors, null);
    }

    public static <T> APIResponse<T> error(String message) {
        return new APIResponse<>("error", null, message);
    }
}
