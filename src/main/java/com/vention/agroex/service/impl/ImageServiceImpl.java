package com.vention.agroex.service.impl;


import com.vention.agroex.entity.Image;
import com.vention.agroex.entity.Lot;
import com.vention.agroex.exception.ImageException;
import com.vention.agroex.dto.ImageDTO;
import com.vention.agroex.props.MinioProperties;
import com.vention.agroex.repository.ImageRepository;
import com.vention.agroex.service.ImageService;
import io.minio.*;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.InputStream;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class ImageServiceImpl implements ImageService {

    private final MinioClient minioClient;
    private final MinioProperties minioProperties;
    private final ImageRepository imageRepository;

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
            saveImage(inputStream, fileName);
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
        } catch (Exception e) {
            throw new ImageException("Error during deleting image: " + e.getMessage());
        }
    }

    private String generateStringFileName(MultipartFile multipartFile) {
        String extension = StringUtils.getFilenameExtension(multipartFile.getOriginalFilename());
        if (extension == null || extension.isEmpty()) {
            throw new ImageException("Invalid file name!");
        }
        return String.format("%s.%s", UUID.randomUUID(), extension);
    }

    @SneakyThrows
    private void saveImage(InputStream inputStream, String fileName) {
        minioClient.putObject(
                PutObjectArgs.builder()
                        .stream(inputStream, inputStream.available(), -1)
                        .object(fileName)
                        .bucket(minioProperties.getBucket())
                        .build()
        );
    }

    private void createBucket() {
        try {
            boolean found = minioClient.bucketExists(
                    BucketExistsArgs
                            .builder()
                            .bucket(minioProperties.getBucket())
                            .build()
            );
            if (!found)
                minioClient.makeBucket(
                        MakeBucketArgs.builder()
                                .bucket(minioProperties.getBucket())
                                .build()
                );
        } catch (Exception e) {
            throw new ImageException("Error during creating image bucket: " + e.getMessage());
        }
    }
}
