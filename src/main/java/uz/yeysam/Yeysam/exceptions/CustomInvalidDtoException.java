package uz.yeysam.Yeysam.exceptions;

public class CustomInvalidDtoException extends RuntimeException {
    public CustomInvalidDtoException(String message) {
        super(message);
    }
}