package uz.yeysam.Yeysam.exceptions;

public class CustomInvalidFileException extends RuntimeException {
    public CustomInvalidFileException(String message) {
        super(message);
    }
}