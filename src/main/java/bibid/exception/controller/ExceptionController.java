package bibid.exception.controller;


import bibid.exception.errorCode.ErrorCode;
import bibid.exception.errorCode.MakeSignatureException;
import bibid.exception.errorCode.ObjectStorageException;
import bibid.exception.response.ErrorResponse;
import com.google.gson.Gson;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.net.URISyntaxException;
import java.util.NoSuchElementException;
import java.util.zip.DataFormatException;

@RestControllerAdvice
public class ExceptionController {

    @ExceptionHandler(NullPointerException.class)
    public ResponseEntity<String> nullPointerExceptionHandler(NullPointerException e) {
        System.out.println("에러났음 : "+  e);
        ErrorResponse response = new ErrorResponse(ErrorCode.NULL_POINT);
        return setResponse(response, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(NoSuchElementException.class)
    public ResponseEntity<String> noSuchElementExceptionHandler(NoSuchElementException e) {
        System.out.println("에러났음 : "+  e);
        ErrorResponse response = new ErrorResponse(ErrorCode.NO_SUCH_ELEMENT);
        return setResponse(response, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(ClassCastException.class)
    public ResponseEntity<String> classCastExceptionHandler(ClassCastException e) {
        System.out.println("에러났음 : "+  e);
        ErrorResponse response = new ErrorResponse(ErrorCode.CLASS_CAST);
        return setResponse(response, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(StackOverflowError.class)
    public ResponseEntity<String> stackOverFlowErrorHandler(StackOverflowError e) {
        System.out.println("에러났음 : "+  e);
        ErrorResponse response = new ErrorResponse(ErrorCode.STACK_OVER_FLOW);
        return setResponse(response, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(DataFormatException.class)
    public ResponseEntity<String> dataFormatExceptionHandler(DataFormatException e) {
        System.out.println("에러났음 : "+  e);
        ErrorResponse response = new ErrorResponse(ErrorCode.DATA_FORMAT);
        return setResponse(response, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(MakeSignatureException.class)
    public ResponseEntity<String> makeSignatureExceptionHandler(MakeSignatureException e) {
        ErrorResponse response = new ErrorResponse(ErrorCode.MAKE_SIGNATURE);
        return setResponse(response, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(ObjectStorageException.class)
    public ResponseEntity<String> objectStorageExceptionHandler(ObjectStorageException e) {
        ErrorResponse response = new ErrorResponse(ErrorCode.OBJECT_STORAGE);
        return setResponse(response, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(URISyntaxException.class)
    public ResponseEntity<String> URISyntaxExceptionHandler(URISyntaxException e) {
        ErrorResponse response = new ErrorResponse(ErrorCode.URL_SYNTAX);
        return setResponse(response, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<String> runtimeExceptionHandler(RuntimeException e) {
        System.out.println("에러났음 : "+  e);
        ErrorResponse response = new ErrorResponse(9997, e.getMessage());
        return setResponse(response, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(IllegalStateException.class)
    public ResponseEntity<String> illegalStateExceptionHandler(IllegalStateException e) {
        System.out.println("에러났음 : "+  e);
        ErrorResponse response = new ErrorResponse(9998, e.getMessage());
        return setResponse(response, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<String> exceptionHandler(Exception e) {
        System.out.println("에러났음 : "+  e);
        ErrorResponse response = new ErrorResponse(ErrorCode.EXCEPTION);
        return setResponse(response, HttpStatus.BAD_REQUEST);
    }


    private ResponseEntity<String> setResponse(ErrorResponse errorResponse, HttpStatus status) {
        String responseJson = createResponseJson(errorResponse);
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        System.out.println("Exception Controller Advice: " + errorResponse);
        return new ResponseEntity<>(responseJson, headers, status);
    }

    private String createResponseJson(ErrorResponse errorResponse) {
        return new Gson().toJson(errorResponse);
    }
}

