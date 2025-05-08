package escuelaing.edu.co.bakend_gl.config;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.servlet.NoHandlerFoundException;

import java.time.LocalDateTime;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.stream.Collectors;

import lombok.extern.slf4j.Slf4j;

/**
 * Manejador global de excepciones
 * Proporciona respuestas estandarizadas para diferentes tipos de errores
 */
@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    /**
     * Maneja excepciones no controladas
     */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<Object> handleGlobalException(Exception ex, WebRequest request) {
        log.error("Error no controlado: ", ex);
        return createErrorResponse(
                HttpStatus.INTERNAL_SERVER_ERROR, 
                "Ha ocurrido un error inesperado", 
                ex.getMessage(), 
                request.getDescription(false));
    }

    /**
     * Maneja excepciones de validación (datos inválidos)
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Object> handleValidationExceptions(MethodArgumentNotValidException ex, WebRequest request) {
        Map<String, String> errores = ex.getBindingResult()
                .getFieldErrors()
                .stream()
                .collect(Collectors.toMap(
                    error -> error.getField(), 
                    error -> error.getDefaultMessage() == null ? "Error de validación" : error.getDefaultMessage()
                ));
        
        return createErrorResponse(
                HttpStatus.BAD_REQUEST, 
                "Error de validación", 
                errores, 
                request.getDescription(false));
    }

    /**
     * Maneja excepciones de parámetros faltantes
     */
    @ExceptionHandler(MissingServletRequestParameterException.class)
    public ResponseEntity<Object> handleMissingParams(MissingServletRequestParameterException ex, WebRequest request) {
        return createErrorResponse(
                HttpStatus.BAD_REQUEST, 
                "Parámetro requerido no encontrado", 
                "El parámetro '" + ex.getParameterName() + "' es obligatorio", 
                request.getDescription(false));
    }

    /**
     * Maneja cuando no se encuentra un endpoint
     */
    @ExceptionHandler(NoHandlerFoundException.class)
    public ResponseEntity<Object> handleNoHandlerFound(NoHandlerFoundException ex, WebRequest request) {
        return createErrorResponse(
                HttpStatus.NOT_FOUND, 
                "Recurso no encontrado", 
                "No se encontró el recurso: " + ex.getRequestURL(), 
                request.getDescription(false));
    }

    /**
     * Método para crear una respuesta de error estándar
     */
    private ResponseEntity<Object> createErrorResponse(HttpStatus status, String mensaje, Object detalle, String ruta) {
        Map<String, Object> body = new LinkedHashMap<>();
        body.put("timestamp", LocalDateTime.now());
        body.put("status", status.value());
        body.put("error", status.getReasonPhrase());
        body.put("message", mensaje);
        body.put("details", detalle);
        body.put("path", ruta);
        
        return new ResponseEntity<>(body, status);
    }
} 