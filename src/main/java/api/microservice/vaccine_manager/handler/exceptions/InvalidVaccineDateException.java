package api.microservice.vaccine_manager.handler.exceptions;

public class InvalidVaccineDateException extends Throwable{
    public InvalidVaccineDateException() {
        super("Data para vacinação inválida ou superior à data de validade.");
    }
}
