package uz.yeysam.Yeysam.exceptions;

public class ResendLimitExceededException extends RuntimeException {
    public ResendLimitExceededException(String message) {
        super(message);
    }
}
