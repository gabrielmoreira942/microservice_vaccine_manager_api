package api.microservice.vaccine_manager.handler.exceptions;

public class BadRequestException extends Throwable {

    public BadRequestException(String message) {
        super(message);
    }
}
