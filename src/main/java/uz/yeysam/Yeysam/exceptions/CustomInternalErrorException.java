package uz.yeysam.Yeysam.exceptions;

public class CustomInternalErrorException extends RuntimeException{

    public CustomInternalErrorException(String message) {
        super(message);
    }
}
