package org.example.untitled.s3;

import jakarta.annotation.PostConstruct;
import org.springframework.stereotype.Service;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.*;
import software.amazon.awssdk.services.s3.presigner.S3Presigner;
import software.amazon.awssdk.services.s3.presigner.model.GetObjectPresignRequest;
import software.amazon.awssdk.services.s3.presigner.model.PresignedGetObjectRequest;
import software.amazon.awssdk.services.s3.presigner.model.PresignedPutObjectRequest;
import software.amazon.awssdk.services.s3.presigner.model.PutObjectPresignRequest;

import java.time.Duration;
import java.util.List;
import java.util.UUID;

@Service
public class S3Service {

    private final S3Client s3Client;
    private final S3Presigner s3Presigner;
    private final String BUCKET_NAME = "chum-bucket";

    public S3Service(S3Client s3Client, S3Presigner s3Presigner){
        this.s3Client = s3Client;
        this.s3Presigner = s3Presigner;
    }

    @PostConstruct
    public void init(){
        try {
            s3Client.putBucketCors(bucketCors -> bucketCors
                    .bucket(BUCKET_NAME)
                    .corsConfiguration(conf -> conf
                            .corsRules(CORSRule.builder()
                                    .allowedOrigins("*")
                                    .allowedMethods("GET", "PUT", "POST", "DELETE")
                                    .allowedHeaders("*")
                                    .build())
                            .build())
                    .build());
        } catch (Exception e){
            System.err.println("could not configure cors");
            System.err.println(e.getMessage());
        }
    }

    public List<String> listFiles(){
        ListObjectsV2Request listObjectsV2Request = ListObjectsV2Request.builder()
                .bucket(BUCKET_NAME)
                .build();

        ListObjectsV2Response listObjectsV2Response = s3Client.listObjectsV2(listObjectsV2Request);

        return listObjectsV2Response.contents().stream()
                .map(S3Object::key)
                .toList();
    }

    public String generateS3PreUploadUrl(String fileName, String contentType) {
        String fileIn = UUID.randomUUID().toString().substring(0, 8) + "-" +fileName;
        PutObjectPresignRequest preReq = PutObjectPresignRequest.builder()
                .signatureDuration(Duration.ofMinutes(10))
                .putObjectRequest(objReq -> objReq
                        .bucket(BUCKET_NAME)
                        .key(fileIn)
                        .contentType(contentType)
                        .build())
                .build();
        PresignedPutObjectRequest prePutObj = s3Presigner.presignPutObject(preReq);
        return prePutObj.url().toString();
    }

    public String generateS3DownloadUrl(String fileName) {
        GetObjectPresignRequest presignRequest = GetObjectPresignRequest.builder()
                .signatureDuration(Duration.ofMinutes(10))
                .getObjectRequest(objReq -> objReq.bucket(BUCKET_NAME)
                        .key(fileName)
                        .build())
                .build();
        PresignedGetObjectRequest presigneReq = s3Presigner.presignGetObject(presignRequest);
        return presigneReq.url().toString();
    }
}
