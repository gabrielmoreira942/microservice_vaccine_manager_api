package api.microservice.vaccine_manager.handler;

import api.microservice.vaccine_manager.handler.exceptions.*;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

@ControllerAdvice
public class CustomExceptionHandler {

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Object> handleMethodArgumentNotValid(MethodArgumentNotValidException ex) {
        Map<String, Object> body = new HashMap<>();

        Map<String, String> errors = new HashMap<>();
        ex.getBindingResult().getFieldErrors().forEach(
                fieldError -> errors.put(fieldError.getField(), fieldError.getDefaultMessage())
        );

        body.put("mensagem", errors);

        return new ResponseEntity<>(body, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(InvalidVaccineDateException.class)
    public ResponseEntity<Object> handleInvalidVaccineDate(InvalidVaccineDateException ex) {
        Map<String, Object> body = new HashMap<>();

        body.put("mensagem", ex.getMessage());

        return new ResponseEntity<>(body, HttpStatus.UNPROCESSABLE_ENTITY);
    }

    @ExceptionHandler(ExpiredVaccineException.class)
    public ResponseEntity<Object> handleExpiredVaccine(ExpiredVaccineException ex) {
        Map<String, Object> body = new HashMap<>();

        body.put("mensagem", ex.getMessage());

        return new ResponseEntity<>(body, HttpStatus.UNPROCESSABLE_ENTITY);
    }

    @ExceptionHandler(NotFoundException.class)
    public ResponseEntity<Object> handleResourceNotFound(NotFoundException ex) {
        Map<String, Object> body = new HashMap<>();

        body.put("mensagem", ex.getMessage());

        return new ResponseEntity<>(body, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(BadRequestException.class)
    public ResponseEntity<Object> handleRegisterBadRequest(BadRequestException ex) {
        Map<String, Object> body = new HashMap<>();

        body.put("mensagem", ex.getMessage());

        return new ResponseEntity<>(body, HttpStatus.BAD_REQUEST);
    }


    @ExceptionHandler(AmountOfVacinationException.class)
    public ResponseEntity<Object> handleAmountOfVaccination(AmountOfVacinationException ex) {
        Map<String, Object> body = new HashMap<>();

        body.put("mensagem", ex.getMessage());

        return new ResponseEntity<>(body, HttpStatus.UNPROCESSABLE_ENTITY);
    }

    @ExceptionHandler(UnequalVaccineManufacturerException.class)
    public ResponseEntity<Object> handleUnequalVaccineManufacturerException(UnequalVaccineManufacturerException ex) {
        Map<String, Object> body = new HashMap<>();

        body.put("mensagem", ex.getMessage());

        return new ResponseEntity<>(body, HttpStatus.UNPROCESSABLE_ENTITY);
    }

    @ExceptionHandler(UniqueDoseVaccineException.class)
    public ResponseEntity<Object> handleUniqueDoseVaccineException(UniqueDoseVaccineException ex) {
        Map<String, Object> body = new HashMap<>();

        body.put("mensagem", ex.getMessage());

        return new ResponseEntity<>(body, HttpStatus.UNPROCESSABLE_ENTITY);
    }

    @ExceptionHandler(UnprocessableEntityException.class)
    public ResponseEntity<Object> handleUnprocessableEntityException(UnprocessableEntityException ex) {
        Map<String, Object> body = new HashMap<>();

        body.put("mensagem", ex.getMessage());

        return new ResponseEntity<>(body, HttpStatus.UNPROCESSABLE_ENTITY);
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<Object> handleHttpMessageNotReadable(HttpMessageNotReadableException ex) {
        Throwable rootCause = ex.getRootCause();
        if (rootCause instanceof DateTimeParseException) {
            Map<String, Object> body = new LinkedHashMap<>();
            body.put("mensagem", "Formato de data inválido. Use o formato 'yyyy-MM-dd'");

            return new ResponseEntity<>(body, HttpStatus.BAD_REQUEST);
        }
        Map<String, Object> body = new HashMap<>();
        body.put("mensagem", "Você passou algum dado inválido no corpo");
        return new ResponseEntity<>(body, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<Object> handleAllExceptions(Exception ex) {
        if (
            ex instanceof MethodArgumentNotValidException ||
            ex instanceof BadRequestException ||
            ex instanceof InvalidVaccineDateException ||
            ex instanceof NotFoundException ||
            ex instanceof UniqueDoseVaccineException ||
            ex instanceof UnequalVaccineManufacturerException ||
            ex instanceof AmountOfVacinationException ||
            ex instanceof UnprocessableEntityException ||
            ex instanceof ExpiredVaccineException ||
            ex instanceof HttpMessageNotReadableException
        ) {
            return null;
        }

        Map<String, Object> body = new HashMap<>();
        body.put("mensagem", "Ocorreu um erro na aplicação. Nossa equipe de TI já foi notificada e em" +
                " breve nossos serviços estarão reestabelecidos. Para maiores informações entre em" +
                " contato pelo nosso WhatsApp 71 99999-9999. Lamentamos o ocorrido!");

        return new ResponseEntity<>(body, HttpStatus.INTERNAL_SERVER_ERROR);
    }
}
