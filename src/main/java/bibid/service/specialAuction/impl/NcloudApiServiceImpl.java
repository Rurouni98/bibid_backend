package bibid.service.specialAuction.impl;

import bibid.dto.NcloudApiRequestDTO;
import bibid.service.specialAuction.NcloudApiService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.codec.binary.Base64;
import org.springframework.http.*;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.net.URI;
import java.nio.charset.StandardCharsets;

@Service
@RequiredArgsConstructor
@Slf4j
public class NcloudApiServiceImpl implements NcloudApiService {

    // 메서드 문자열을 HttpMethod로 변환하는 함수
    private HttpMethod resolveHttpMethod(String method) {
        switch (method.toUpperCase()) {
            case "GET":
                return HttpMethod.GET;
            case "POST":
                return HttpMethod.POST;
            case "PUT":
                return HttpMethod.PUT;
            case "DELETE":
                return HttpMethod.DELETE;
            default:
                throw new IllegalArgumentException("지원하지 않는 HTTP 메서드입니다: " + method);
        }
    }

    public String callNcloudApi(NcloudApiRequestDTO requestDTO) {
        try {
            String method = requestDTO.getMethod();
            String apiUrl = requestDTO.getApiUrl();
            String accessKey = requestDTO.getAccessKey();
            String secretKey = requestDTO.getSecretKey();
            String requestBody = requestDTO.getRequestBody();

            // 현재 시간을 밀리초로 계산
            String timestamp = String.valueOf(System.currentTimeMillis());

            // 서명 생성
            String signUrl = apiUrl.substring(apiUrl.indexOf(".com") + 4);
            String signature = makeSignature(method, signUrl, timestamp, accessKey, secretKey);

            // 요청 헤더 설정
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            headers.set("x-ncp-apigw-timestamp", timestamp);
            headers.set("x-ncp-iam-access-key", accessKey);
            headers.set("x-ncp-apigw-signature-v2", signature);

            // 요청 본문 설정
            HttpEntity<String> requestEntity = new HttpEntity<>(requestBody, headers);

            // RestTemplate 사용하여 API 호출
            RestTemplate restTemplate = new RestTemplate(new HttpComponentsClientHttpRequestFactory());
            HttpMethod httpMethod = resolveHttpMethod(method); // 메서드를 변환
            ResponseEntity<String> responseEntity = restTemplate.exchange(new URI(apiUrl), httpMethod, requestEntity, String.class);

            return responseEntity.getBody();
        } catch (Exception e) {
            throw new RuntimeException("API 호출 중 오류 발생", e);
        }
    }

    // 서명 생성 함수
    private String makeSignature(String method, String url, String timestamp, String accessKey, String secretKey) {
        try {
            String space = " ";
            String newLine = "\n";
            String message = method + space + url + newLine + timestamp + newLine + accessKey;

            SecretKeySpec signingKey = new SecretKeySpec(secretKey.getBytes(StandardCharsets.UTF_8), "HmacSHA256");
            Mac mac = Mac.getInstance("HmacSHA256");
            mac.init(signingKey);

            byte[] rawHmac = mac.doFinal(message.getBytes(StandardCharsets.UTF_8));
            return Base64.encodeBase64String(rawHmac);
        } catch (Exception e) {
            throw new RuntimeException("서명 생성 중 오류 발생", e);
        }
    }
}
