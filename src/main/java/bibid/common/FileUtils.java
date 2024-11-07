package bibid.common;

import bibid.config.NaverConfiguration;
import bibid.dto.AuctionImageDto;
import bibid.dto.ProfileImageDto;
import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.client.builder.AwsClientBuilder;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import com.amazonaws.services.s3.model.CannedAccessControlList;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PutObjectRequest;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.UUID;

@Component
public class FileUtils {

    @Value("${cloud.aws.s3.bucket.name}")
    String bucket;

    private final AmazonS3 s3;

    public FileUtils(NaverConfiguration naverConfiguration) {
        s3 = AmazonS3ClientBuilder.standard()
                .withEndpointConfiguration(
                        new AwsClientBuilder.EndpointConfiguration(
                                naverConfiguration.getEndPoint(),
                                naverConfiguration.getRegionName()
                        )
                )
                .withCredentials(
                        new AWSStaticCredentialsProvider(
                                new BasicAWSCredentials(
                                        naverConfiguration.getAccessKey(),
                                        naverConfiguration.getSecretKey()
                                )
                        )
                )
                .build();
    }

    public ProfileImageDto parserFileInfo(MultipartFile multipartFile, String directory) {
        String bucketName = bucket;

        ProfileImageDto profileImageDto = new ProfileImageDto();

        // 다른 사용자가 같은 파일명의 파일을 업로드 했을 때
        // 덮어써지는 것을 방지하기 위해서 파일명을 랜덤값_날짜시간_파일명으로 지정
        SimpleDateFormat format = new SimpleDateFormat("yyyyMMddHHmmss");
        Date nowDate = new Date();

        String nowDateStr = format.format(nowDate);

        UUID uuid = UUID.randomUUID();

        String fileName =  uuid.toString() + "_" + nowDateStr + "_" + multipartFile.getOriginalFilename();

        // Object Storage에 파일 업로드
        try(InputStream fileInputStream = multipartFile.getInputStream()) {
            ObjectMetadata objectMetadata = new ObjectMetadata();
            objectMetadata.setContentType(multipartFile.getContentType());

            PutObjectRequest putObjectRequest = new PutObjectRequest(
                    bucketName,
                    directory + fileName,
                    fileInputStream,
                    objectMetadata
            ).withCannedAcl(CannedAccessControlList.PublicRead);

            s3.putObject(putObjectRequest);
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }

        File uploadFile = new File(directory + fileName);
        String type = "";

        try {
            type = Files.probeContentType(uploadFile.toPath());
        } catch(IOException ie) {
            System.out.println(ie.getMessage());
        }

        if(!type.equals("")) {
            if(type.startsWith("image")) {
                profileImageDto.setFiletype("image");
            } else {
                profileImageDto.setFiletype("etc");
            }
        } else {
            profileImageDto.setFiletype("etc");
        }

        profileImageDto.setNewfilename(fileName);
        profileImageDto.setFilesize(multipartFile.getSize());
        profileImageDto.setOriginalname(multipartFile.getOriginalFilename());
        profileImageDto.setFilepath(directory);

        return profileImageDto;
    }

    public AuctionImageDto auctionImageParserFileInfo(MultipartFile multipartFile, String directory) {
        String bucketName = bucket;

        AuctionImageDto auctionImageDto = new AuctionImageDto();

        // 다른 사용자가 같은 파일명의 파일을 업로드 했을 때
        // 덮어써지는 것을 방지하기 위해서 파일명을 랜덤값_날짜시간_파일명으로 지정
        SimpleDateFormat format = new SimpleDateFormat("yyyyMMddHHmmss");
        Date nowDate = new Date();

        String nowDateStr = format.format(nowDate);

        UUID uuid = UUID.randomUUID();

        String fileName =  uuid.toString() + "_" + nowDateStr + "_" + multipartFile.getOriginalFilename();

        // Object Storage에 파일 업로드
        try(InputStream fileInputStream = multipartFile.getInputStream()) {
            ObjectMetadata objectMetadata = new ObjectMetadata();
            objectMetadata.setContentType(multipartFile.getContentType());

            PutObjectRequest putObjectRequest = new PutObjectRequest(
                    bucketName,
                    directory + fileName,
                    fileInputStream,
                    objectMetadata
            ).withCannedAcl(CannedAccessControlList.PublicRead);

            s3.putObject(putObjectRequest);
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }

        File uploadFile = new File(directory + fileName);
        String type = "";

        try {
            type = Files.probeContentType(uploadFile.toPath());
        } catch(IOException ie) {
            System.out.println(ie.getMessage());
        }

        if(!type.equals("")) {
            if(type.startsWith("image")) {
                auctionImageDto.setFiletype("image");
            } else {
                auctionImageDto.setFiletype("etc");
            }
        } else {
            auctionImageDto.setFiletype("etc");
        }

        auctionImageDto.setFilename(fileName);
        auctionImageDto.setFileoriginname(multipartFile.getOriginalFilename());
        auctionImageDto.setFilepath(directory);
        auctionImageDto.setFilesize(multipartFile.getSize());

        return auctionImageDto;
    }
}
