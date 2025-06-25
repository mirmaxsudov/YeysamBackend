package uz.yeysam.Yeysam.exceptions;

import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.MalformedJwtException;
import lombok.NonNull;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.validation.FieldError;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;
import uz.yeysam.Yeysam.model.response.ApiErrorResponse;

import javax.validation.ConstraintViolationException;
import java.security.SignatureException;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestControllerAdvice
public class GlobalExceptionHandler extends ResponseEntityExceptionHandler {

    @Override
    protected ResponseEntity<Object> handleMethodArgumentNotValid(MethodArgumentNotValidException ex,
                                                                  @NonNull HttpHeaders headers,
                                                                  @NonNull HttpStatusCode status,
                                                                  @NonNull WebRequest request) {
        Map<String, String> validationErrors = new HashMap<>();
        List<ObjectError> validationErrorList = ex.getBindingResult().getAllErrors();

        validationErrorList.forEach((error) -> {
            String fieldName = ((FieldError) error).getField();
            String message = error.getDefaultMessage();
            validationErrors.put(fieldName, message);
        });
        return new ResponseEntity<>(validationErrors, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<Object> handleDataIntegrityViolation(DataIntegrityViolationException ex) {
        // Get the root cause to inspect for specific messages
        Throwable rootCause = ExceptionUtils.getRootCause(ex);
        String errorMessage = "Data integrity violation";
        HttpStatus status = HttpStatus.BAD_REQUEST;

        if (rootCause != null && rootCause.getMessage().contains("already exists")) {
            errorMessage = "Unique constraint violation: " + rootCause.getMessage();
            status = HttpStatus.CONFLICT; // 409 Conflict
        }
        return new ResponseEntity<>(Map.of("error", errorMessage), status);
    }

    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<ApiErrorResponse> handleConstraintViolation(ConstraintViolationException ex) {
        String message = ex.getConstraintViolations().stream()
                .map(violation -> violation.getPropertyPath() + ": " + violation.getMessage())
                .collect(Collectors.joining(", "));
        return new ResponseEntity<>(new ApiErrorResponse("Constraint violation: " + message, HttpStatus.BAD_REQUEST, LocalDateTime.now(), 400), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(NullPointerException.class)
    public ResponseEntity<ApiErrorResponse> handleNullPointerException(NullPointerException ex) {
        return new ResponseEntity<>(new ApiErrorResponse("Null value encountered: " + ex.getMessage(), HttpStatus.BAD_REQUEST, LocalDateTime.now(), 400), HttpStatus.BAD_REQUEST);
    }


    @ResponseStatus(HttpStatus.NOT_FOUND)
    @ExceptionHandler(CustomNotFoundException.class)
    public ApiErrorResponse handleNotFoundException(CustomNotFoundException exception) {
        return new ApiErrorResponse(exception.getMessage(), HttpStatus.NOT_FOUND, LocalDateTime.now(), 404);
    }

    @ResponseStatus(HttpStatus.CONFLICT)
    @ExceptionHandler(CustomConflictException.class)
    public ApiErrorResponse handleAlreadyExistException(CustomConflictException exception) {
        return new ApiErrorResponse(exception.getMessage(), HttpStatus.CONFLICT, LocalDateTime.now(), 409);
    }

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(CustomBadRequestException.class)
    public ApiErrorResponse handleBadRequestException(CustomBadRequestException exception) {
        return new ApiErrorResponse(exception.getMessage(), HttpStatus.BAD_REQUEST, LocalDateTime.now(), 400);
    }

    @ResponseStatus(HttpStatus.UNAUTHORIZED)
    @ExceptionHandler(InvalidTokenException.class)
    public ApiErrorResponse handleInvalidTokenException(InvalidTokenException exception) {
        return new ApiErrorResponse(exception.getMessage(), HttpStatus.UNAUTHORIZED, LocalDateTime.now(), 401);
    }

    @ResponseStatus(HttpStatus.UNPROCESSABLE_ENTITY)
    @ExceptionHandler(CustomInvalidDtoException.class)
    public ApiErrorResponse handleInvalidDtoException(CustomInvalidDtoException exception) {
        return new ApiErrorResponse(exception.getMessage(), HttpStatus.UNPROCESSABLE_ENTITY, LocalDateTime.now(), 422);
    }

    @ResponseStatus(HttpStatus.UNPROCESSABLE_ENTITY)
    @ExceptionHandler(CustomInvalidFileException.class)
    public ApiErrorResponse handleInvalidFileException(CustomInvalidFileException exception) {
        return new ApiErrorResponse(exception.getMessage(), HttpStatus.UNPROCESSABLE_ENTITY, LocalDateTime.now(), 422);
    }

    @ResponseStatus(HttpStatus.CONFLICT)
    @ExceptionHandler(CustomAlreadyExistException.class)
    public ApiErrorResponse handleCustomAlreadyExistException(CustomAlreadyExistException exception) {
        return new ApiErrorResponse(exception.getMessage(), HttpStatus.CONFLICT, LocalDateTime.now(), 410);
    }

    @ResponseStatus(HttpStatus.FOUND)
    @ExceptionHandler(CustomDuplicateStudentException.class)
    public ApiErrorResponse handleCustomDuplicateStudentException(CustomDuplicateStudentException exception) {
        return new ApiErrorResponse(exception.getMessage(), HttpStatus.CONFLICT, LocalDateTime.now(), 411);
    }

    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    @ExceptionHandler(CustomInternalErrorException.class)
    public ApiErrorResponse handleInternalErrorException(CustomInternalErrorException exception) {
        return new ApiErrorResponse(exception.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR, LocalDateTime.now(), 500);
    }

    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    @ExceptionHandler(Exception.class)
    public ApiErrorResponse handleException(Exception exception) {
        return new ApiErrorResponse(exception.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR, LocalDateTime.now(), 500);
    }

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(MalformedJwtException.class)
    public ApiErrorResponse handleMalformedJwtException(MalformedJwtException e) {
        return new ApiErrorResponse(e.getMessage(), HttpStatus.UNAUTHORIZED, LocalDateTime.now(), 401);
    }


    @ResponseStatus(HttpStatus.UNAUTHORIZED)
    @ExceptionHandler(UsernameNotFoundException.class)
    public ApiErrorResponse handleUsernameNotFoundException(UsernameNotFoundException e) {
        return new ApiErrorResponse("user name or password is incorrect", HttpStatus.UNAUTHORIZED, LocalDateTime.now(), 401);
    }

    @ResponseStatus(HttpStatus.UNAUTHORIZED)
    @ExceptionHandler(BadCredentialsException.class)
    public ApiErrorResponse handleBadCredentialsException(BadCredentialsException e) {
        return new ApiErrorResponse("user name or password is incorrect", HttpStatus.UNAUTHORIZED, LocalDateTime.now(), 401);
    }

    @ResponseStatus(HttpStatus.UNAUTHORIZED)
    @ExceptionHandler(ExpiredJwtException.class)
    public ApiErrorResponse handleExpiredJwtException(ExpiredJwtException exception) {
        return new ApiErrorResponse(exception.getMessage(), HttpStatus.UNAUTHORIZED, LocalDateTime.now(), 401);
    }

    @ResponseStatus(HttpStatus.UNAUTHORIZED)
    @ExceptionHandler(SignatureException.class)
    public ApiErrorResponse handleSignatureException(SignatureException exception) {
        return new ApiErrorResponse(exception.getMessage(), HttpStatus.UNAUTHORIZED, LocalDateTime.now(), 401);
    }

    @ResponseStatus(HttpStatus.FORBIDDEN)
    @ExceptionHandler(AccessDeniedException.class)
    public ApiErrorResponse handleAccessDeniedException(AccessDeniedException exception) {
        return new ApiErrorResponse(exception.getMessage(), HttpStatus.FORBIDDEN, LocalDateTime.now(), 403);
    }

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(CustomIllegalArgumentException.class)
    public ApiErrorResponse handleCustomIllegalArgumentException(CustomIllegalArgumentException exception) {
        return new ApiErrorResponse(exception.getMessage(), HttpStatus.BAD_REQUEST, LocalDateTime.now(), 401);
    }

    @ExceptionHandler(ResendLimitExceededException.class)
    public ApiErrorResponse handleResendLimitExceeded(ResendLimitExceededException ex) {
        return new ApiErrorResponse(ex.getMessage(), HttpStatus.FORBIDDEN, LocalDateTime.now(), 401);
    }

    @ExceptionHandler(UserBlockedException.class)
    public ApiErrorResponse handleUserBlocked(UserBlockedException ex) {
        return new ApiErrorResponse(ex.getMessage(), HttpStatus.FORBIDDEN, LocalDateTime.now(), 401);
    }

    @ExceptionHandler(VerificationFailedException.class)
    public ApiErrorResponse handleVerificationFailed(VerificationFailedException ex) {
        return new ApiErrorResponse(ex.getMessage(), HttpStatus.FORBIDDEN, LocalDateTime.now(), 401);
    }
}