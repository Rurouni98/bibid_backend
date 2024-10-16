package bibid.exception.errorCode;

public class ObjectStorageException extends RuntimeException {
    @Override
    public synchronized Throwable fillInStackTrace() {
        return this;
    }
}