package umc.stockoneqback.global.exception;

public class ApplicationException extends RuntimeException {
    private final ErrorCode code;

    public ApplicationException(ErrorCode code) {
        super(code.getMessage());
        this.code = code;
    }

    public static ApplicationException type(ErrorCode code) {
        return new ApplicationException(code);
    }
}
