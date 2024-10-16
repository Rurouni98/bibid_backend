package bibid.exception.errorCode;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum ErrorCode {
    UNKNOWN_ERROR(1001, "토큰이 존재하지 않습니다."),
    MALFORMED_JWT(1002, "변조된 토큰입니다."),
    EXPIRED_TOKEN(1003, "만료된 토큰입니다."),
    ACCESS_DENIED(1004, "권한이 없습니다."),
    UNSUPPORTED_TOKEN(1005, "지원되지 않는 토큰입니다."),

    NULL_POINT(2001, "NULL 값입니다."),
    NO_SUCH_ELEMENT(2002,"찾고자 하는 데이터가 DB에 없습니다."),
    CLASS_CAST(2003, "데이터 타입이 틀립니다."),
    STACK_OVER_FLOW(2004, "스택 오버 플로우 에러. 백엔드 문제일 확률 높음"),
    DATA_FORMAT(2005, "입력한 데이터 형식이 잘못 되었습니다. 아마 프론트 문제 확률 높음"),

    MAKE_SIGNATURE(3001, "NCP API 요청 시 생성할 SIGNATURE 오류입니다."),
    OBJECT_STORAGE(3002, "Object Storage 오류 입니다."),
    URL_SYNTAX(3003, "서버 내부 API 요청시 URL 오류 입니다."),

    RUN_TIME(9997, "RUN_TIME 예외입니다. 백엔드 문제일 확률 높음"),
    ILLEGAL_STATE(9998, "ILLEGAL_STATE 예외입니다. 백엔드 문제일 확률 높음"),
    EXCEPTION(9999, "예외입니다. 백엔드에서 세부화하든 프론트 문제든 난 몰라");

    private int code;
    private String message;
}
