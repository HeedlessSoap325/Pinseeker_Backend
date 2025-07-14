package de.heedlesssoap.pinseekerbackend.services;

import de.heedlesssoap.pinseekerbackend.entities.ApplicationUser;
import de.heedlesssoap.pinseekerbackend.entities.Log;
import de.heedlesssoap.pinseekerbackend.repositories.LogRepository;
import de.heedlesssoap.pinseekerbackend.repositories.UserRepository;
import de.heedlesssoap.pinseekerbackend.utils.Constants;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.FileAlreadyExistsException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.UUID;

@Service
public class ImageService {
    private final Path uploadPath;
    final UserRepository userRepository;
    final LogRepository logRepository;

    public ImageService(UserRepository userRepository, LogRepository logRepository, @Value("${file.upload-dir}") String images_upload_dir) throws IOException {
        this.uploadPath = Paths.get(images_upload_dir).toAbsolutePath().normalize();
        Files.createDirectories(this.uploadPath);
        this.userRepository = userRepository;
        this.logRepository = logRepository;
    }

    private void checkImage(MultipartFile image) throws IllegalArgumentException {
        String contentType = image.getContentType();
        if (image.getOriginalFilename() == null || contentType == null || !contentType.startsWith("image/")) {
            throw new IllegalArgumentException(Constants.INVALID_FILE_TYPE);
        }
    }

    private void safeDeleteImage(Path base_path, String imageURL) throws IOException {
        if(imageURL == null){
            throw new IOException(Constants.IMAGE_DID_NOT_EXIST);
        }
        boolean success = Files.deleteIfExists(this.uploadPath.resolve(base_path).resolve(imageURL));
        if(!success){
            throw new IllegalArgumentException(Constants.FILE_NOT_DELETED);
        }
    }

    private void saveImage(Path base_path, String filename, MultipartFile image) throws IOException, FileAlreadyExistsException {
        Path targetLocation = this.uploadPath.resolve(base_path).resolve(filename);
        Files.createDirectories(targetLocation.getParent());
        Files.copy(image.getInputStream(), targetLocation);
    }

    public void updateUserProfilePicture(MultipartFile profilePicture, ApplicationUser user) throws IOException, IllegalArgumentException {
        checkImage(profilePicture);

        Path profile_picture_dir = Paths.get(Constants.PROFILE_PICTURE_UPLOAD_DIR);
        if(user.getHasProfilePicture()){
            safeDeleteImage(profile_picture_dir, user.getProfilePicture());
        }

        String originalFilename = profilePicture.getOriginalFilename();
        String extension = originalFilename.substring(originalFilename.lastIndexOf("."));
        String filename = user.getUsername() + extension;

        saveImage(profile_picture_dir, filename, profilePicture);

        user.setHasProfilePicture(true);
        user.setProfilePicture(filename);

        userRepository.save(user);
    }

    public void updateLogImage(MultipartFile image, Log log) throws IOException {
        checkImage(image);

        Path log_image_dir = Paths.get(Constants.LOG_IMAGE_UPLOAD_DIR);
        if(log.getHasImage()){
            safeDeleteImage(log_image_dir, log.getImageURL());
        }

        String filename = UUID.randomUUID() + "_" + image.getOriginalFilename();
        saveImage(log_image_dir, filename, image);

        log.setHasImage(true);
        log.setImageURL(filename);

        logRepository.save(log);
    }

    public void deleteProfilePicture(ApplicationUser user) throws IOException {
        Path profile_picture_dir = Paths.get(Constants.PROFILE_PICTURE_UPLOAD_DIR);
        safeDeleteImage(profile_picture_dir, user.getProfilePicture());

        user.setHasProfilePicture(false);
        user.setProfilePicture(null);

        userRepository.save(user);
    }

    public void deleteLogImage(Log log) throws IOException {
        Path log_image_dir = Paths.get(Constants.LOG_IMAGE_UPLOAD_DIR);
        safeDeleteImage(log_image_dir, log.getImageURL());

        log.setHasImage(false);
        log.setImageURL(null);

        logRepository.save(log);
    }
}