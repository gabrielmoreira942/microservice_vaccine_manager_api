package api.microservice.vaccine_manager.handler.exceptions;

public class UnprocessableEntityException extends Exception {

    public UnprocessableEntityException(String message) {
        super(message);
    }
}
