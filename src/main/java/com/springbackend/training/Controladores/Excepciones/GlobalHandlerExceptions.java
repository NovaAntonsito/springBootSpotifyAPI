package com.springbackend.training.Controladores.Excepciones;


import com.springbackend.training.Controladores.Excepciones.Class.ErrorResponse;
import org.jetbrains.annotations.NotNull;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import se.michaelthelin.spotify.exceptions.SpotifyWebApiException;
import se.michaelthelin.spotify.exceptions.detailed.UnauthorizedException;

@ControllerAdvice
public class GlobalHandlerExceptions {
    
    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<ErrorResponse> handleRuntimeException(RuntimeException e) {
        if(e.getMessage().contains("Invalid access token") || e.getMessage().contains("Token not found")){
            ErrorResponse er = new ErrorResponse(
                    HttpStatus.UNAUTHORIZED.value(),
                    "Acceso no autorizado",
                    e.getMessage()
            );
            return new ResponseEntity<>(er, HttpStatus.UNAUTHORIZED);
        }
        return new ResponseEntity<>(new ErrorResponse(HttpStatus.INTERNAL_SERVER_ERROR.value(), "Error unknown", e.getMessage()), HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @ExceptionHandler(MissingServletRequestParameterException.class)
    public ResponseEntity<ErrorResponse> handleMissingServletRequestParameterException(@NotNull MissingServletRequestParameterException e) {
        if(e.getMessage().contains("Required request parameter")){
            ErrorResponse er = new ErrorResponse(
                    HttpStatus.BAD_REQUEST.value(),
                    "Missing parameter",
                    e.getMessage()
            );
            return new ResponseEntity<>(er, HttpStatus.CONFLICT);
        }
        return new ResponseEntity<>(new ErrorResponse(HttpStatus.INTERNAL_SERVER_ERROR.value(), "Error unknown", e.getMessage()), HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @ExceptionHandler(UnauthorizedException.class)
    public ResponseEntity<ErrorResponse> handleSpotifyWebApiException(@NotNull UnauthorizedException e) {
        if(e.getMessage().contains("The access token expired")){
            ErrorResponse er = new ErrorResponse(
                    HttpStatus.UNAUTHORIZED.value(),
                    "Unauthorized",
                    e.getMessage()
            );
            return new ResponseEntity<>(er, HttpStatus.UNAUTHORIZED);
        }
        return new ResponseEntity<>(new ErrorResponse(HttpStatus.INTERNAL_SERVER_ERROR.value(), "Error unknown", e.getMessage()), HttpStatus.INTERNAL_SERVER_ERROR);
    }

}
