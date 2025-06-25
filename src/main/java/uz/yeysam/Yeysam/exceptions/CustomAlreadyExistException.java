package uz.yeysam.Yeysam.exceptions;

public class CustomAlreadyExistException extends RuntimeException {
    public CustomAlreadyExistException(String message) {
        super(message);
    }
}