package com.vention.agroex.service.impl;


import com.vention.agroex.dto.Image;
import com.vention.agroex.entity.ImageEntity;
import com.vention.agroex.exception.ImageException;
import com.vention.agroex.props.MinioProperties;
import com.vention.agroex.service.ImageServiceStorage;
import io.minio.*;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.InputStream;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class ImageServiceStorageImpl implements ImageServiceStorage {

    private final MinioClient minioClient;
    private final MinioProperties minioProperties;

    @Qualifier("supportedExtensions")
    private final Map<String, String> supportedExtensions;

    @PostConstruct
    private void init() {
        try {
            createBucket();
        }
        catch (ImageException e){
            throw new ImageException(e.getMessage());
        }
    }

    @Override
    public String uploadToStorage(Image image) throws ImageException {
        MultipartFile file = image.getFile();
        if (file.getOriginalFilename() == null || file.getOriginalFilename().isBlank())
            throw new ImageException("Invalid file name");
        String fileName = generateStringFileName(file);
        try {
            InputStream inputStream = file.getInputStream();
            saveImage(inputStream, fileName, file);
            return fileName;
        } catch (Exception e) {
            throw new ImageException("Image upload exception: " + e.getMessage());
        }
    }

    @Override
    public void remove(ImageEntity imageEntity) {
        try {
            minioClient.removeObject(
                    RemoveObjectArgs.builder()
                            .object(imageEntity.getName())
                            .bucket(minioProperties.getBucket())
                            .build()
            );
        }
        catch (Exception e){
            throw new ImageException("Error during deleting image: "+e.getMessage());
        }
    }

    @Override
    public void removeAll(List<ImageEntity> imageEntities) {
        imageEntities.forEach(this::remove);
    }

    @SneakyThrows
    private void saveImage(InputStream inputStream, String fileName, MultipartFile file) throws ImageException {
        String extension = getFileExtension(file);
        if (!supportedExtensions.containsKey(extension))
            throw new ImageException("Incorrect image extension!");
        minioClient.putObject(
                PutObjectArgs.builder()
                        .stream(inputStream, inputStream.available(), -1)
                        .object(fileName)
                        .bucket(minioProperties.getBucket())
                        .contentType(supportedExtensions.get(extension))
                        .build()
        );
    }


    private void createBucket() {
        try (InputStream policyJson = getClass().getClassLoader().getResourceAsStream("minio/minio-config.json")){
            boolean found = minioClient.bucketExists(
                    BucketExistsArgs
                            .builder()
                            .bucket(minioProperties.getBucket())
                            .build()
            );
            if (!found) {
                minioClient.makeBucket(
                        MakeBucketArgs.builder()
                                .bucket(minioProperties.getBucket())
                                .build()
                );
                SetBucketPolicyArgs policyArgs = SetBucketPolicyArgs.builder()
                        .bucket("images")
                        .config(new String(policyJson.readAllBytes()))
                        .build();
                minioClient.setBucketPolicy(policyArgs);
            }
        }
        catch (Exception e){
            throw new ImageException("Error during creating image bucket: "+e.getMessage());
        }
    }

    private String generateStringFileName(MultipartFile multipartFile) throws ImageException {
        String extension = getFileExtension(multipartFile);
        return String.format("%s.%s", UUID.randomUUID(), extension);
    }

    private String getFileExtension(MultipartFile multipartFile) throws ImageException {
        String extension = StringUtils.getFilenameExtension(multipartFile.getOriginalFilename());
        if (extension == null || extension.isEmpty()) {
            throw new ImageException("Unsupported file extension!");
        }
        return extension;
    }
}
