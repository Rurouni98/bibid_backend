package bibid.exception.response;

import bibid.exception.errorCode.ErrorCode;
import com.google.gson.annotations.SerializedName;
import lombok.Getter;
import lombok.ToString;

@ToString

@Getter
public class ErrorResponse {
    @SerializedName("statusCode")
    private int code;

    @SerializedName("errorMessage")
    private String message;

    public ErrorResponse(ErrorCode errorCode) {
        this.code = errorCode.getCode();
        this.message = errorCode.getMessage();
    }

    public ErrorResponse(int code, String message) {
        this.code = code;
        this.message = message;
    }
}
