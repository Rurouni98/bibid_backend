package bibid.exception.controller;

import bibid.exception.errorCode.ErrorCode;
import bibid.exception.response.ErrorResponse;
import com.google.gson.Gson;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.io.PrintWriter;

@Slf4j
@Component
public class CustomAuthenticationEntryPoint implements AuthenticationEntryPoint {
    @Override
    public void commence(HttpServletRequest request,
                         HttpServletResponse response,
                         AuthenticationException authException) throws IOException {
        try {
            String exception = request.getAttribute("exception").toString();
            //토큰이 없을 경우
            if (exception.equals(ErrorCode.UNKNOWN_ERROR.getCode())) {
                setResponse(response, ErrorCode.UNKNOWN_ERROR);
            }
            //변조 에러
            else if (exception.equals(ErrorCode.MALFORMED_JWT.getCode())) {
                setResponse(response, ErrorCode.MALFORMED_JWT);
            }
            //토큰 만료된 경우
            else if (exception.equals(ErrorCode.EXPIRED_TOKEN.getCode())) {
                setResponse(response, ErrorCode.EXPIRED_TOKEN);
            }
            //권한이 없는 경우
            else if (exception.equals(ErrorCode.ACCESS_DENIED.getCode())) {
                setResponse(response, ErrorCode.ACCESS_DENIED);
            }
            //지원되지 않는 토큰인 경우
            else if (exception.equals(ErrorCode.UNSUPPORTED_TOKEN.getCode())) {
                setResponse(response, ErrorCode.UNSUPPORTED_TOKEN);
            }

        } catch (Exception e) {
            setResponse(response, ErrorCode.EXCEPTION);
        }
    }

    //한글 출력을 위해 getWriter() 사용
    private void setResponse(HttpServletResponse response, ErrorCode errorCode) throws IOException {
        response.setContentType("application/json;charset=UTF-8");
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);

        Gson gson = new Gson();
        String jsonResponse = gson.toJson(new ErrorResponse(errorCode));

        try (PrintWriter writer = response.getWriter()) {
            writer.write(jsonResponse);
        }
    }
}
