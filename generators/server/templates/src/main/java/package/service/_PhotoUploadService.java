package <%=packageName%>.service;

import <%=packageName%>.domain.Photo;
import <%=packageName%>.event.ProcessPhotoEvent;
import <%=packageName%>.repository.PhotoRepository;
import <%=packageName%>.service.util.ObjectId;
import com.amazonaws.AmazonClientException;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.regions.Region;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.model.DeleteObjectRequest;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.commons.lang3.StringUtils;
import org.imgscalr.Scalr;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.env.Environment;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.event.TransactionalEventListener;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.util.List;


@Service
public class PhotoUploadService {

    private final Logger log = LoggerFactory.getLogger(PhotoUploadService.class);

    private final PhotoRepository photoRepository;

    private final Environment env;

    private final ObjectMapper objectMapper;

    @Value("${aws.key}")
    private String key;

    @Value("${aws.secret}")
    private String secret;

    @Value("${aws.region}")
    private String region;

    @Value("${aws.bucket}")
    private String bucketName;

    private String awsImageIp;

    public PhotoUploadService(ObjectMapper mapper, Environment env, PhotoRepository repository) {
        this.objectMapper = mapper;
        this.env = env;
        this.photoRepository = repository;
    }

    private String getIp() {
        awsImageIp = "https://s3" + (StringUtils.isNotBlank(region) ? region.trim() : "") + ".amazonaws.com/" + bucketName + "/";
        return awsImageIp;
    }

    //    @ConditionalOnExpression("#{environment.acceptsProfiles('" + Constants.SPRING_PROFILE_SCHEDULED + "')}")
    @Scheduled(cron = "0 1 * * * *")
    public void uploadPhotos() {
        List<Photo> photos = photoRepository.findByFileIsNotNull();
        if (photos != null && !photos.isEmpty()) {
            photos.forEach(p -> processPhoto(p, true));
        }
    }

    //    @ConditionalOnExpression("#{environment.acceptsProfiles('" + Constants.SPRING_PROFILE_SCHEDULED + "')}")
    @Scheduled(cron = "0 3 * * * *")
    public void unprocessed() {
        List<Photo> photos = photoRepository.findByProcessedFalse();
        if (photos != null && !photos.isEmpty()) {
            photos.forEach(p -> processPhoto(p, true));
        }
    }

    @Async
    public void uploadPhotosAsync() {
        List<Photo> photos = photoRepository.findByFileIsNotNull();
        if (photos != null && !photos.isEmpty()) {
            photos.forEach(p -> processPhoto(p, true));
        }
    }

    public void generateUrlName(Photo photo) {
        photo.generateUuid();
        if (photo.getThumbnail() == null) {
            String thumb = getIp() + photo.getId() + "/" + photo.getThumbnailName();
            photo.setThumbnail(thumb);
        }

        if (photo.getMedium() == null) {
            String medium = getIp() + photo.getId() + "/" + photo.getMediumName();
            photo.setMedium(medium);
        }

        if (photo.getOriginal() == null) {
            String original = getIp() + photo.getId() + "/" + photo.getOriginalName();
            photo.setOriginal(original);
        }

        photoRepository.save(photo);

    }

    @Async
    @TransactionalEventListener(fallbackExecution = true)
    public void processPhotoEvent(ProcessPhotoEvent event) {
        processPhoto(event.get(), event.isUpdate());
    }

    @Async
    public void processPhoto(Photo photoToUpload, boolean update) {

        BasicAWSCredentials basicAWSCredentials = new BasicAWSCredentials(key, secret);
        AmazonS3Client s3client = new AmazonS3Client(basicAWSCredentials);
        if (StringUtils.isNotBlank(region)) {
            s3client.setRegion(Region.getRegion(Regions.fromName(region)));
        }

        Photo photo = photoRepository.findOne(photoToUpload.getId()).createUuid();

        log.info("Processing photos from async service...");
        if (photo.getFile() == null) {
            return;
        }

        log.info("Reading image...");
        ByteArrayInputStream stream = new ByteArrayInputStream(photo.getFile());
        BufferedImage image = null;
        try {
            image = ImageIO.read(stream);
        } catch (IOException e) {
            log.error("Error reading image", e);
        }

        if (image == null) {
            throw new IllegalArgumentException("Image is null for uploading");
        }

        generateUrlName(photo);

        try {

            File destFile = File.createTempFile("<%= baseName %>" + photo.getId(), ".png");

            BufferedImage resize150 = Scalr.resize(image, 200);
            ImageIO.write(resize150, "png", destFile);
            log.info("Uploading to s3...");
            s3client.putObject(new PutObjectRequest(bucketName + "/" + photo.getId(), photo.getThumbnailName(), destFile));

            BufferedImage resize300 = Scalr.resize(image, 400);
            ImageIO.write(resize300, "png", destFile);
            s3client.putObject(new PutObjectRequest(bucketName + "/" + photo.getId(), photo.getMediumName(), destFile));

            ImageIO.write(image, "png", destFile);
            s3client.putObject(new PutObjectRequest(bucketName + "/" + photo.getId(), photo.getOriginalName(), destFile));

            photo.setFile(null);

            photo.setProcessed(true);
            photoRepository.save(photo);

        } catch (IOException | AmazonClientException ase) {
            log.error("Error uploading to S3", ase);
        }

    }

    @Async
    public void deletePhoto(Photo photo) {
        BasicAWSCredentials basicAWSCredentials = new BasicAWSCredentials(key, secret);
        AmazonS3Client s3client = new AmazonS3Client(basicAWSCredentials);
        if (StringUtils.isNotBlank(region)) {
            s3client.setRegion(Region.getRegion(Regions.fromName(region)));
        }

        try {
            s3client.deleteObject(new DeleteObjectRequest(bucketName, photo.getId() + "/" + photo.getThumbnailName()));
            s3client.deleteObject(new DeleteObjectRequest(bucketName, photo.getId() + "/" + photo.getMediumName()));
            s3client.deleteObject(new DeleteObjectRequest(bucketName, photo.getId() + "/" + photo.getOriginalName()));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Async
    public void removePhoto(Photo photo) {

        BasicAWSCredentials basicAWSCredentials = new BasicAWSCredentials(key, secret);
        AmazonS3Client s3client = new AmazonS3Client(basicAWSCredentials);
        if (StringUtils.isNotBlank(region)) {
            s3client.setRegion(Region.getRegion(Regions.fromName(region)));
        }
        s3client.deleteObject(new DeleteObjectRequest(bucketName, photo.getId() + "/"));
    }

}
