package uz.yeysam.Yeysam.exceptions;

public class VerificationFailedException extends RuntimeException {
    public VerificationFailedException(String message) {
        super(message);
    }
}