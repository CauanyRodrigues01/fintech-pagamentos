package com.fintech.pagamentos.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

import java.time.LocalDateTime;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.stream.Collectors;

@ControllerAdvice
public class GlobalExceptionHandler {

    // Tratamento para erros de validação (@Valid)
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleValidationExeceptions(
            MethodArgumentNotValidException ex, WebRequest request) {

        List<String> errors = ex.getBindingResult()
                .getFieldErrors()
                .stream()
                .map(error -> error.getField() + ": " + error.getDefaultMessage())
                .collect(Collectors.toList());

        ErrorResponse errorResponse = new ErrorResponse(
            LocalDateTime.now(),
            HttpStatus.BAD_REQUEST.value(),
            "Validation Error",
            "Erro(s) de validação nos dados fornecidos.",
            request.getDescription(false).replace("uri=",""),
            errors
        );

        return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);  // Retorna 400 Bad Request

    }

    // Tratamento para recursos não encontrados (usado com Optional.orElseThrow(RuntimeException))
    @ExceptionHandler({NoSuchElementException.class, RuntimeException.class})
    public ResponseEntity<ErrorResponse> handleNotFoundException(
            RuntimeException ex, WebRequest request ) {

        // Se a mensagem da RuntimeException for específica de "não encontrado"
        HttpStatus status = HttpStatus.INTERNAL_SERVER_ERROR;
        String errorMessage = "Ocorreu um erro interno.";

        if (ex instanceof NoSuchElementException || ex.getMessage().contains("Não encontrado")) {
            status = HttpStatus.NOT_FOUND;
            errorMessage = ex.getMessage();
        } else {
            // Para outras RuntimeExceptions que não sejam de "não encontrado"
            errorMessage = "Ocorreu um erro inesperado no servidor.";
            ex.printStackTrace();
        }

        ErrorResponse errorResponse = new ErrorResponse(
                LocalDateTime.now(),
                status.value(),
                status.getReasonPhrase(),
                errorMessage,
                request.getDescription(false).replace("uri=", ""),
                null
        );

        return new ResponseEntity<>(errorResponse, status);

    }

    // Tratamento para erro de tipo na URL (ex: passar string para UUID)
    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ResponseEntity<ErrorResponse> handleTypeMismatch(
            MethodArgumentTypeMismatchException ex,
            WebRequest request) {

        String error = String.format("Parâmetro '%s' com valor '%s' não pôde ser convertido para o tipo esperado '&%s'.",
                ex.getName(), ex.getValue(), ex.getRequiredType().getSimpleName());

        ErrorResponse errorResponse = new ErrorResponse(
                LocalDateTime.now(),
                HttpStatus.BAD_REQUEST.value(),
                "Type Mismatch Error",
                error,
                request.getDescription(false).replace("uri=", ""),
                null
        );

        return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);

    }

    // Tratamento genérico para qualquer outra exceção não capturada
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleGlobalException(
            Exception ex, WebRequest request) {
        ErrorResponse errorResponse = new ErrorResponse(
                LocalDateTime.now(),
                HttpStatus.INTERNAL_SERVER_ERROR.value(),
                "Internal Server Error",
                "Ocorreu um errro inesperado no servidor",
                request.getDescription(false).replace("uri=", ""),
                null
        );

        ex.printStackTrace();

        return new ResponseEntity<>(errorResponse, HttpStatus.INTERNAL_SERVER_ERROR);
    }

}
