package bibid.exception.errorCode;

public class MakeSignatureException extends RuntimeException {
    @Override
    public synchronized Throwable fillInStackTrace() {
        return this;
    }
}
