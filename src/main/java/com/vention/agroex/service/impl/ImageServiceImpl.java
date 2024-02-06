package com.vention.agroex.service.impl;


import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.vention.agroex.entity.Image;
import com.vention.agroex.entity.Lot;
import com.vention.agroex.exception.ImageException;
import com.vention.agroex.dto.ImageDTO;
import com.vention.agroex.props.MinioProperties;
import com.vention.agroex.repository.ImageRepository;
import com.vention.agroex.service.ImageService;
import io.minio.*;
import jakarta.annotation.PostConstruct;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.InputStream;
import java.util.*;

@Slf4j
@Service
@RequiredArgsConstructor
public class ImageServiceImpl implements ImageService {

    private final MinioClient minioClient;
    private final MinioProperties minioProperties;
    private final ImageRepository imageRepository;
    private Map<String, String> supportedExtensions = new HashMap<>();
    private ObjectMapper mapper = new ObjectMapper();
    private File supportedExtensionsFile = new File("src/main/resources/minio/supported-extensions.json");

    @PostConstruct
    private void init() {
        try {
            mapper.findAndRegisterModules();
            mapper.enable(SerializationFeature.INDENT_OUTPUT);

            log.info("Reading supported file extensions from JSON file");
            supportedExtensions = mapper.readValue(supportedExtensionsFile, new TypeReference<>() {});
            log.info("Supported file extensions uploaded from JSON file");
        } catch (Exception e) {
            throw new ImageException("Error while supported file extensions list initialization");
        }
    }

    @Override
    public Image getById(Long id) {
        return imageRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Image with this id not found!"));
    }

    @Override
    public Image getByName(String name) {
        return imageRepository.findByName(name)
                .orElseThrow(() -> new EntityNotFoundException("Image with this name not found!"));
    }

    @Override
    @Transactional
    public String upload(ImageDTO image, Lot lot) {
        createBucket();
        MultipartFile file = image.getFile();
        if (file.getOriginalFilename() == null || file.getOriginalFilename().isBlank())
            throw new ImageException("Invalid file name");
        String fileName = generateStringFileName(file);
        try {
            InputStream inputStream = file.getInputStream();
            saveImage(inputStream, fileName, file);
            imageRepository.save(Image
                    .builder()
                    .name(fileName)
                    .lot(lot)
                    .build()
            );
        } catch (Exception e) {
            throw new ImageException("Image upload exception: " + e.getMessage());
        }
        return fileName;
    }

    @Override
    @Transactional
    public void remove(Image image) {
        try {
            minioClient.removeObject(
                    RemoveObjectArgs.builder()
                            .object(image.getName())
                            .bucket(minioProperties.getBucket())
                            .build()
            );
            imageRepository.delete(image);
        }
        catch (Exception e){
            throw new ImageException("Error during deleting image: "+e.getMessage());
        }
    }

    @SneakyThrows
    private void saveImage(InputStream inputStream, String fileName, MultipartFile file){
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

    private void createBucket(){
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

    private String generateStringFileName(MultipartFile multipartFile){
        String extension = getFileExtension(multipartFile);
        return String.format("%s.%s", UUID.randomUUID(), extension);
    }

    private String getFileExtension(MultipartFile multipartFile){
        String extension = StringUtils.getFilenameExtension(multipartFile.getOriginalFilename());
        if (extension == null || extension.isEmpty()) {
            throw new ImageException("Invalid file name!");
        }
        return extension;
    }
}
