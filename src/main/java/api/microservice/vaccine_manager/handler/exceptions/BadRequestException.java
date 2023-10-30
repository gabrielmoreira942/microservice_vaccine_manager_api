package api.microservice.vaccine_manager.handler.exceptions;

public class BadRequestException extends Exception {

    public BadRequestException(String message) {
        super(message);
    }
}
