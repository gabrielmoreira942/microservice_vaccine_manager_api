package api.microservice.vaccine_manager.handler;

import api.microservice.vaccine_manager.handler.exceptions.BadRequestException;
import api.microservice.vaccine_manager.handler.exceptions.InvalidVaccineDateException;
import api.microservice.vaccine_manager.handler.exceptions.NotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.util.HashMap;
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

        return new ResponseEntity<>(body, HttpStatus.BAD_REQUEST);
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

    @ExceptionHandler(Exception.class)
    public ResponseEntity<Object> handleAllExceptions(Exception ex) {
        if (ex instanceof MethodArgumentNotValidException ||
                ex instanceof BadRequestException ||
                ex instanceof InvalidVaccineDateException ||
                ex instanceof NotFoundException) {
            return null;
        }

        Map<String, Object> body = new HashMap<>();
        body.put("mensagem", "Ocorreu um erro na aplicação. Nossa equipe de TI já foi notificada e em" +
                " breve nossos serviços estarão reestabelecidos. Para maiores informações entre em" +
                " contato pelo nosso WhatsApp 71 99999-9999. Lamentamos o ocorrido!");

        return new ResponseEntity<>(body, HttpStatus.INTERNAL_SERVER_ERROR);
    }
}
