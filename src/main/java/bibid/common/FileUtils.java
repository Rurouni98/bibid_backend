package bibid.common;

import bibid.dto.AuctionImageDto;
import bibid.dto.ProfileImageDto;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.UUID;

@Component
public class FileUtils {

    @Value("${cloud.aws.s3.bucket.name}")
    private String bucket;

    private final S3Client s3;

    public FileUtils(S3Client s3Client) {
        this.s3 = s3Client;
    }

    public ProfileImageDto parserFileInfo(MultipartFile multipartFile, String directory) {
        String now = new SimpleDateFormat("yyyyMMddHHmmss").format(new Date());
        String uuid = UUID.randomUUID().toString();
        String originalName = multipartFile.getOriginalFilename();
        String fileName = uuid + "_" + now + "_" + originalName;
        String key = directory + fileName;

        try {
            PutObjectRequest putRequest = PutObjectRequest.builder()
                    .bucket(bucket)
                    .key(key)
                    .contentType(multipartFile.getContentType())
                    .acl("public-read") // R2에서 지원 가능, Cloudflare에 따라 무시될 수도 있음
                    .build();

            s3.putObject(putRequest, RequestBody.fromBytes(multipartFile.getBytes()));

        } catch (IOException e) {
            System.out.println("파일 업로드 실패: " + e.getMessage());
        }

        ProfileImageDto dto = new ProfileImageDto();
        dto.setNewfilename(fileName);
        dto.setOriginalname(originalName);
        dto.setFilesize(multipartFile.getSize());
        dto.setFilepath(directory);
        dto.setFiletype(
                multipartFile.getContentType() != null && multipartFile.getContentType().startsWith("image")
                        ? "image"
                        : "etc"
        );
        return dto;
    }

    public AuctionImageDto auctionImageParserFileInfo(MultipartFile multipartFile, String directory) {
        String now = new SimpleDateFormat("yyyyMMddHHmmss").format(new Date());
        String uuid = UUID.randomUUID().toString();
        String originalName = multipartFile.getOriginalFilename();
        String fileName = uuid + "_" + now + "_" + originalName;
        String key = directory + fileName;

        try {
            PutObjectRequest putRequest = PutObjectRequest.builder()
                    .bucket(bucket)
                    .key(key)
                    .contentType(multipartFile.getContentType())
                    .acl("public-read")
                    .build();

            s3.putObject(putRequest, RequestBody.fromBytes(multipartFile.getBytes()));

        } catch (IOException e) {
            System.out.println("파일 업로드 실패: " + e.getMessage());
        }

        AuctionImageDto dto = new AuctionImageDto();
        dto.setFilename(fileName);
        dto.setFileoriginname(originalName);
        dto.setFilepath(directory);
        dto.setFilesize(multipartFile.getSize());
        dto.setFiletype(
                multipartFile.getContentType() != null && multipartFile.getContentType().startsWith("image")
                        ? "image"
                        : "etc"
        );
        return dto;
    }
}
