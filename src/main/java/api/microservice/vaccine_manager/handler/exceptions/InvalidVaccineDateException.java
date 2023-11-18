package api.microservice.vaccine_manager.handler.exceptions;

public class InvalidVaccineDateException extends Exception {
    public InvalidVaccineDateException(String message) {
        super(message);
    }
}
