package com.zero9platform.common.aws.s3;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.zero9platform.common.enums.ExceptionCode;
import com.zero9platform.common.exception.CustomException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.UUID;

@Slf4j(topic = "s3_file_upload")
@Service
@RequiredArgsConstructor
public class S3Service {

    private final AmazonS3 amazonS3;

    @Value("${cloud.aws.s3.bucket}")
    private String bucket;

    // s3 파일 업로드
    public String upload(MultipartFile multipartFile, String filter) {

        if (multipartFile == null || multipartFile.isEmpty()) {
            throw new CustomException(ExceptionCode.FILE_NOT_FOUND);
        }

        String fileName = filter + "/" + createFileName(multipartFile.getOriginalFilename());

        try {
            ObjectMetadata metadata = new ObjectMetadata();
            metadata.setContentLength(multipartFile.getSize());
            metadata.setContentType(multipartFile.getContentType());

            amazonS3.putObject(new PutObjectRequest(bucket, fileName, multipartFile.getInputStream(), metadata));

            return fileName;
        } catch (IOException e) {
            log.error("S3 업로드 실패 - fileName={}", fileName, e);

            throw new CustomException(ExceptionCode.FILE_UPLOAD_FAIL);
        }
    }

    /**
     * S3 이미지 삭제
     */
    public void s3Delete(String key) {

        if (key == null || key.isBlank()) {
            return; // 삭제할 대상 없음
        }

        try {
            amazonS3.deleteObject(bucket, key);

            log.info("S3 삭제 성공 - key={}", key);
        } catch (Exception e) {
            log.error("S3 삭제 실패 - key={}", key, e);
        }
    }

    // 파일 랜덤 이름 생성
    private String createFileName(String originalFilename) {
        return UUID.randomUUID() + "_" + originalFilename;
    }
}