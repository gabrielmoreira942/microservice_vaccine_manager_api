package api.microservice.vaccine_manager.handler.exceptions;

public class UnequalVaccineManufacturerException extends Exception {
    public UnequalVaccineManufacturerException(String namePatient, String surnamePatient, String nameOldVaccine) {
        super("A primeira dose aplicada no paciente " + namePatient + " " + surnamePatient + " foi " + nameOldVaccine + ". Todas as doses devem ser aplicadas com o mesmo medicamento!");
    }
}
