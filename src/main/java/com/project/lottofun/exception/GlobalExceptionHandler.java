package com.project.lottofun.exception;

import com.project.lottofun.model.dto.ApiResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class GlobalExceptionHandler {
/**
 * @ExceptionHandler metodu doğrudan çağrılmaz — Spring, bir @Controller veya @RestController içinde çalışırken oluşan exception’ları otomatik olarak yakalar ve @ControllerAdvice sınıfındaki ilgili @ExceptionHandler ile eşleştirir.
 *
 * Yani senin örneğinde:
 *
 * java
 * Kopyala
 * Düzenle
 * throw new DrawExecutionException("Failed to execute draw", e);
 * satırı çalıştığında:
 *
 * Bu exception bir controller endpoint’inde çağrılan executeDrawIfDue() fonksiyonu içindeyse,
 *
 * Ve bu exception dışarıya doğru fırlatılıyorsa (örneğin bir @PostMapping metodundan),
 *
 * Spring otomatik olarak GlobalExceptionHandler içindeki şu handler’a düşer:
 * */
    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ApiResponse<Object>> handleIllegalArgumentException(IllegalArgumentException ex) {
        ApiResponse<Object> response = new ApiResponse<>(false, ex.getMessage(), null);
        return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
    }
}
