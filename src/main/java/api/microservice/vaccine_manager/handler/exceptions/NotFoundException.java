package api.microservice.vaccine_manager.handler.exceptions;

public class NotFoundException extends Exception {

    public NotFoundException(String message){
        super(message);
    }

}
