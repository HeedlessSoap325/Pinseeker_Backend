package de.heedlesssoap.pinseekerbackend.utils;

public class Constants {
    public static final String USERNAME_ALREADY_EXISTS = "This Username is already taken, please choose another one.";
    public static final String USERNAME_NOT_FOUND = "There is no user with this name.";
    public static final String USER_NOT_ENABLED = "This user has not been enabled. Please finish setting up your account before logging in again.";
    public static final String BAD_CREDENTIALS = "Bad credentials";
    public static final String NO_RSA_KEY = "Your Client has not provided a public RSA Key. Please try again.";

    public static final String CHAT_ALREADY_EXISTS = "This Chat already exists.";
    public static final String CHAT_NOT_FOUND = "This Chat does not exist.";
    public static final String CHAT_NOT_WRITABLE = "This Chat is read-only, because the other Participant deleted his Account.";
    public static final String CHAT_INVALID_SIZE = "The Chat you requested did not have a member-count of 2.";

    public static final String INVALID_TOKEN = "The provided Token is invalid. Try logging in again.";
    public static final String INVALID_PIN = "The provided Pin-Data is invalid or malformed.";

    public static final String MESSAGE_NOT_FOUND = "This Message does not exist.";
    public static final String MESSAGE_BLANK = "Message has been incorrectly formed! Encrypted contents can not be blank.";

    public static final String NOT_A_MEMBER = "You are not a Member of this Chat.";

    public static final String ACTION_SUCCESSFUL = "Action was performed successfully.";
    public static final String ACCESS_DENIED = "You are not authorized to perform this action.";
    public static final String NOT_ALLOWED = "The action you tried to perform is not allowed.";

    public static final String PIN_NOT_FOUND = "This Pin does not exist.";
    public static final String PIN_ALREADY_EXISTS = "This Pin already exists.";
    public static final String PIN_PREMIUM_ONLY = "This Pin is premium-only.";

    public static final String LOG_NOT_FOUND = "This Log does not exist.";

    public static final Boolean IS_IN_DEVELOPMENT = true;
    public static final String DEVELOPMENT_DEFAULT_ADMIN_NAME = "admin";
    public static final String DEVELOPMENT_DEFAULT_ADMIN_PASSWORD = "password";

    public static final String INVALID_FILE_TYPE = "You have tried to upload a non-image File.";
    public static final String FILE_NOT_DELETED = "The previous Image could not be deleted.";
    public static final String IMAGE_DID_NOT_EXIST = "There is no image to delete.";
    public static final String FILE_ALREADY_EXISTS = "Image could not be saved, because it already exists. Please Try again.";
    public static final String IMAGES_UPLOAD_DIR = "uploads";
    public static final String PROFILE_PICTURE_UPLOAD_DIR = "profile_pictures";
    public static final String LOG_IMAGE_UPLOAD_DIR = "log_images";
}