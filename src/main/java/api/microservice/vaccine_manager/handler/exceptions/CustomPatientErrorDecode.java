package api.microservice.vaccine_manager.handler.exceptions;

import feign.FeignException;
import feign.Response;
import feign.codec.ErrorDecoder;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

public class CustomPatientErrorDecode implements ErrorDecoder {

    @Override
    public Exception decode(String methodKey, Response response) {
        if (response.status() == HttpStatus.NOT_FOUND.value()) {
            return new NotFoundException("Paciente não encontrado");
        }

        return FeignException.errorStatus(methodKey, response);
    }
}