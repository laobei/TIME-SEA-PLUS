package com.cn.bdth.utils;

import com.aliyun.oss.OSS;
import com.aliyun.oss.OSSClientBuilder;
import com.aliyun.oss.model.ObjectMetadata;
import com.cn.bdth.exceptions.UploadException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Base64;
import java.util.Objects;
import java.util.UUID;

/**
 * 雨纷纷旧故里草木深
 *
 * @author 时间海 @github dulaiduwang003
 * @version 1.0
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class AliUploadUtils {

    @Value("${ali-oss.endpoint}")
    private String endpoint;

    @Value("${ali-oss.accessKey}")
    private String accessKey;

    @Value("${ali-oss.secretKey}")
    private String secretKey;

    @Value("${ali-oss.bucketName}")
    private String bucketName;

    public String uploadFile(final MultipartFile file, final String path, final String newFileName) {

        OSS ossClient = new OSSClientBuilder()
                .build(endpoint, accessKey, secretKey);

        try (InputStream inputStream = file.getInputStream()) {
            String originalFileName = file.getOriginalFilename();

            assert originalFileName != null;
            String fileName ;
            fileName = Objects.requireNonNullElseGet(newFileName, () -> UUID.randomUUID() + originalFileName.substring(originalFileName.lastIndexOf('.')));

            String filePath = path + "/" + fileName;
            ObjectMetadata objectMetadata = new ObjectMetadata();
            objectMetadata.setContentType("image/jpg");
            ossClient.putObject(bucketName, filePath, inputStream, objectMetadata);
            return "/" + filePath;
        } catch (IOException e) {
            log.error("无法将图片上传到阿里云。错误消息： {} 错误类： {}", e.getMessage(), e.getClass());
            throw new UploadException();
        } finally {
            ossClient.shutdown();
        }
    }

    public String uploadBase64(final String base64, String path) throws IOException {
        byte[] imageBytes = Base64.getDecoder().decode(base64);
        ByteArrayInputStream inputStream = new ByteArrayInputStream(imageBytes);
        // 生成随机的图片文件名
        final String fileName = UUID.randomUUID().toString() + ".jpg";
        MultipartFile multipartFile = new MockMultipartFile(fileName, inputStream);
        return uploadFile(multipartFile, path,fileName);
    }
}
