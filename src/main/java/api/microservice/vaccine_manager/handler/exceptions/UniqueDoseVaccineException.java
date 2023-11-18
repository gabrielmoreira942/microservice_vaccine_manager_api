package api.microservice.vaccine_manager.handler.exceptions;

public class UniqueDoseVaccineException extends Exception {
    public UniqueDoseVaccineException(String firstName, String lastName, String vaccineName, String vaccineDate) {
        super("Não foi possível registrar sua solicitação pois o paciente " + firstName + " " + lastName + "recebeu uma dose ÚNICA de " + vaccineName + "no dia " + vaccineDate);
    }
}
