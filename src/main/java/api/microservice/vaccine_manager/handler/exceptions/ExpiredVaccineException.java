package api.microservice.vaccine_manager.handler.exceptions;

public class ExpiredVaccineException extends Exception {
    public ExpiredVaccineException(String message) {
        super(message);
    }
}
