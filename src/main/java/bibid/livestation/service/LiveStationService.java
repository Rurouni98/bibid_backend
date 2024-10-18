package bibid.livestation.service;

import bibid.dto.ResponseDto;
import bibid.exception.errorCode.MakeSignatureException;
import bibid.livestation.dto.*;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.apache.tomcat.util.codec.binary.Base64;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Service
public class LiveStationService {
    @Value("${ncp.accessKey}")
    String accessKey;
    @Value("${ncp.secretKey}")
    String secretKey;
    @Value("bucket119")
    String bucket;

    String liveStationUrl = "https://livestation.apigw.ntruss.com/api/v2/channels";

    public String makeSignature(String timestamp, String method, String signUrl) {
        try {
            String space = " ";
            String newLine = "\n";

            String message = new StringBuilder()
                    .append(method)
                    .append(space)
                    .append(signUrl)
                    .append(newLine)
                    .append(timestamp)
                    .append(newLine)
                    .append(accessKey)
                    .toString();

            SecretKeySpec signingKey = new SecretKeySpec(secretKey.getBytes("UTF-8"), "HmacSHA256");
            Mac mac = Mac.getInstance("HmacSHA256");
            mac.init(signingKey);

            byte[] rawHmac = mac.doFinal(message.getBytes("UTF-8"));
            String encodeBase64String = Base64.encodeBase64String(rawHmac);

            return encodeBase64String;
        } catch (Exception e) {
            throw new MakeSignatureException();
        }
    }

    public String createChannel(String name) {
        try {
            String title = name.replaceAll(" ", "");
            StringBuilder urlBuilder = new StringBuilder();
            urlBuilder.append(liveStationUrl);
            String url = urlBuilder.toString();
            String signUrl = url.substring(url.indexOf(".com") + 4);

            //요청 바디 만들기
            CdnDTO cdnDTO = CdnDTO.builder()
                    .createCdn(true)
                    .cdnType("GLOBAL_EDGE")
                    .profileId(null)
                    .regionType("KOREA")
                    .build();
            RecordDTO recordDTO = RecordDTO.builder()
                    .type("AUTO_UPLOAD")
                    .format("MP4")
                    .bucketName(bucket)
                    .filePath("/")
                    .accessControl("PUBLIC_READ")
                    .build();
            LiveStationRequestDTO requestDTO = LiveStationRequestDTO.builder()
                    .channelName(title)
                    .cdn(cdnDTO)
                    .qualitySetId(3)
                    .useDvr(true)
                    .immediateOnAir(true)
                    .timemachineMin(360)
                    .record(recordDTO)
                    .isStreamFailOver(false)
                    .build();

            ObjectMapper objectMapper = new ObjectMapper();
            String jsonBody = objectMapper.writeValueAsString(requestDTO);

            String timestamp = String.valueOf(System.currentTimeMillis());
            String method = "POST";
            String sig = makeSignature(timestamp, method, signUrl);

            //요청 헤더 만들기
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            headers.set("x-ncp-apigw-timestamp", timestamp);
            headers.set("x-ncp-iam-access-key", accessKey);
            headers.set("x-ncp-apigw-signature-v2", sig);
            headers.set("x-ncp-region_code", "KR");

            HttpEntity<String> body = new HttpEntity<>(jsonBody, headers);

            RestTemplate restTemplate = new RestTemplate();
            restTemplate.setRequestFactory(new HttpComponentsClientHttpRequestFactory());

            ResponseEntity<LiveStationResponseDTO> response = restTemplate.exchange(new URI(url), HttpMethod.POST, body, LiveStationResponseDTO.class);

            return response.getBody().getContent().getChannelId();  // channelId 반환
        } catch (URISyntaxException e) {
            throw new RuntimeException(e.getMessage());
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e.getMessage());
        } catch (Exception e) {
            throw new RuntimeException(e.getMessage());
        }
    }

    public LiveStationInfoDTO getChannelInfo(String channelID) {
        try {
            StringBuilder urlBuilder = new StringBuilder();
            urlBuilder.append(liveStationUrl);
            urlBuilder.append("/");
            urlBuilder.append(channelID);
            String url = urlBuilder.toString();

            String signUrl = url.substring(url.indexOf(".com") + 4);

            String timestamp = String.valueOf(System.currentTimeMillis());
            String method = "GET";
            String sig = makeSignature(timestamp, method, signUrl);

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            headers.set("x-ncp-apigw-timestamp", timestamp);
            headers.set("x-ncp-iam-access-key", accessKey);
            headers.set("x-ncp-apigw-signature-v2", sig);
            headers.set("x-ncp-region_code", "KR");

            HttpEntity<LiveStationResponseDTO> httpEntity = new HttpEntity<>(headers);

            RestTemplate restTemplate = new RestTemplate();
            ResponseEntity<LiveStationResponseDTO> response = restTemplate.exchange(new URI(url), HttpMethod.GET, httpEntity, LiveStationResponseDTO.class);

            System.out.println(response);

            //보내줄 데이터 가공
            LiveStationInfoDTO dto = LiveStationInfoDTO.builder()
                    .channelId(channelID)
                    .channelName(response.getBody().getContent().getChannelName())
                    .channelStatus(response.getBody().getContent().getChannelStatus())
                    .cdnInstanceNo(response.getBody().getContent().getCdn().getInstanceNo())
                    .cdnStatus(response.getBody().getContent().getCdn().getStatusName())
                    .publishUrl(response.getBody().getContent().getPublishUrl())
                    .streamKey(response.getBody().getContent().getStreamKey())
                    .build();
            return dto;

        } catch (URISyntaxException e) {
            throw new RuntimeException(e.getMessage());
        } catch (Exception e) {
            throw new RuntimeException(e.getMessage());
        }

    }

    public List<LiveStationUrlDTO> getServiceURL(String channelID, String urlType) {
        try {
            StringBuilder urlBuilder = new StringBuilder();
            urlBuilder.append(liveStationUrl);
            urlBuilder.append("/");
            urlBuilder.append(channelID);
            urlBuilder.append("/serviceUrls?serviceUrlType=");
            urlBuilder.append(urlType);
            String url = urlBuilder.toString();

            String signUrl = url.substring(url.indexOf(".com") + 4);

            String timestamp = String.valueOf(System.currentTimeMillis());
            String method = "GET";
            String sig = makeSignature(timestamp, method, signUrl);

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            headers.set("x-ncp-apigw-timestamp", timestamp);
            headers.set("x-ncp-iam-access-key", accessKey);
            headers.set("x-ncp-apigw-signature-v2", sig);
            headers.set("x-ncp-region_code", "KR");

            HttpEntity<ServiceUrlDTO> httpEntity = new HttpEntity<>(headers);

            // HTTP 요청을 보내기 위해 RestTemplate 객체 생성
            RestTemplate restTemplate = new RestTemplate();

            ResponseEntity<ServiceUrlDTO> response = restTemplate.exchange(new URI(url), HttpMethod.GET, httpEntity, ServiceUrlDTO.class);
            System.out.println(response);
            //보내줄 데이터 가공

            List<LiveStationUrlDTO> dtoList = response.getBody().getContents().stream()
                    .map(contentDTO -> LiveStationUrlDTO.builder()
                            .channelId(channelID)
                            .name(contentDTO.getName())
                            .url(contentDTO.getUrl())
                            .build())
                    .collect(Collectors.toList());

            return dtoList;
        } catch (URISyntaxException e) {
            throw new RuntimeException(e.getMessage());
        } catch (Exception e) {
            throw new RuntimeException(e.getMessage());
        }

    }

    public ResponseEntity<?> deleteChannel(String channelID) {
        ResponseDto<LiveStationInfoDTO> responseDTO = new ResponseDto<>();
        try {
            StringBuilder urlBuilder = new StringBuilder();
            urlBuilder.append(liveStationUrl);
            urlBuilder.append("/");
            urlBuilder.append(channelID);
            String url = urlBuilder.toString();

            String signUrl = url.substring(url.indexOf(".com") + 4);

            String timestamp = String.valueOf(System.currentTimeMillis());
            String method = "DELETE";
            String sig = makeSignature(timestamp, method, signUrl);

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            headers.set("x-ncp-apigw-timestamp", timestamp);
            headers.set("x-ncp-iam-access-key", accessKey);
            headers.set("x-ncp-apigw-signature-v2", sig);
            headers.set("x-ncp-region_code", "KR");

            HttpEntity<String> httpEntity = new HttpEntity<>(headers);

            // HTTP 요청을 보내기 위해 RestTemplate 객체 생성
            RestTemplate restTemplate = new RestTemplate();

            ResponseEntity<LiveStationResponseDTO> response = restTemplate.exchange(new URI(url), HttpMethod.DELETE, httpEntity, LiveStationResponseDTO.class);

            LiveStationInfoDTO dto = LiveStationInfoDTO.builder()
                    .channelId(channelID)
                    .channelName(response.getBody().getContent().getChannelName())
                    .cdnInstanceNo(response.getBody().getContent().getCdn().getInstanceNo())
                    .build();

            responseDTO.setItem(dto);
            responseDTO.setStatusCode(HttpStatus.OK.value());

            return ResponseEntity.ok().body(responseDTO);
        } catch (URISyntaxException e) {
            responseDTO.setErrorMessage(e.getMessage());
            responseDTO.setStatusCode(HttpStatus.BAD_REQUEST.value());

            return ResponseEntity.badRequest().body(responseDTO);
        }
    }

    public RecordVodDTO getRecord(String channelID) {
        try {
            StringBuilder urlBuilder = new StringBuilder();
            urlBuilder.append(liveStationUrl);
            urlBuilder.append("/");
            urlBuilder.append(channelID);
            urlBuilder.append("/records");

            String url = urlBuilder.toString();

            String signUrl = url.substring(url.indexOf(".com") + 4);

            String timestamp = String.valueOf(System.currentTimeMillis());
            String method = "GET";
            String sig = makeSignature(timestamp, method, signUrl);

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            headers.set("x-ncp-apigw-timestamp", timestamp);
            headers.set("x-ncp-iam-access-key", accessKey);
            headers.set("x-ncp-apigw-signature-v2", sig);
            headers.set("x-ncp-region_code", "KR");

            HttpEntity<String> httpEntity = new HttpEntity<>(headers);

            RestTemplate restTemplate = new RestTemplate();
            ResponseEntity<RecordResponseDTO> response = restTemplate.exchange(new URI(url), HttpMethod.GET, httpEntity, RecordResponseDTO.class);
            RecordVodDTO dto = null;
            System.out.println(response.getBody().getContentDTO().getRecordList());

            for (String key : response.getBody().getContentDTO().getRecordList().keySet()) {
                RecordInfoDTO recordInfoDTO = response.getBody().getContentDTO().getRecordList().get(key);
                if (recordInfoDTO.getRecordType().equals("MP4")) {
                    dto = RecordVodDTO.builder()
                            .channelId(recordInfoDTO.getChannelId())
                            .fileName(recordInfoDTO.getFileName())
                            .uploadPath(recordInfoDTO.getUploadPath())
                            .recordType(recordInfoDTO.getRecordType())
                            .build();
                }
            }

            return dto;
        }catch (URISyntaxException e) {
            throw new RuntimeException(e.getMessage());
        }
    }
}
