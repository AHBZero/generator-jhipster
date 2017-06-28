package <%=packageName%>.domain.push;

import <%=packageName%>.domain.Installation;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.util.regex.Pattern;

/**
 * Validator that will validate if the device token matches to {@code VariantType} specific pattern.
 */
public class DeviceTokenValidator implements ConstraintValidator<DeviceTokenCheck, Installation> {
    /**
     * Pattern for iOS is pretty well defined as the library we use for sending assumes HEX.
     * @see <a href="https://github.com/notnoop/java-apns/blob/20c10ebd22e15a55c0c1c12695c535d37435dcfd/src/main/java/com/notnoop/apns/internal/Utilities.java#L114">notnoop apns</a>
     */
    private static final Pattern IOS_DEVICE_TOKEN = Pattern.compile("(?i)[a-f0-9 -]{64,}");
    /**
     * Pattern for android is harder to define that is why we kept it lenient it is at least 100 characters long and can
     * consist of digits, alphas, - and _ all have one of these separators
     */
    private static final Pattern ANDROID_DEVICE_TOKEN = Pattern.compile("(?i)[0-9a-z\\-_:]{100,}");
    /**
     * Pattern for windows is a uri that can be 1024 characters long
     * @see <a href="http://blogs.windows.com/windows_phone/b/wpdev/archive/2013/10/22/recommended-practices-for-using-microsoft-push-notification-service-mpns.aspx?Redirected=true">Windows developer blog</a>
     */
    private static final Pattern WINDOWS_DEVICE_TOKEN = Pattern.compile("https?://.{0,1024}");


    @Override
    public void initialize(DeviceTokenCheck constraintAnnotation) {
    }

    @Override
    public boolean isValid(Installation installation, ConstraintValidatorContext context) {
        final String deviceToken = installation.getDeviceToken();
        if (installation.getPlatform() == null || deviceToken == null) {
            return false;
        }
        final Platform type = installation.getPlatform();

        return isValidDeviceTokenForVariant(deviceToken, type);
    }


    /**
     * Helper to run quick up-front validations.
     *
     * @param deviceToken the submitted device token
     * @param type type of the variant
     * @return true if the token is valid
     */
    public static boolean isValidDeviceTokenForVariant(final String deviceToken, final Platform type) {
        switch (type) {
            case IOS:
                return IOS_DEVICE_TOKEN.matcher(deviceToken).matches();
            case ANDROID:
                return ANDROID_DEVICE_TOKEN.matcher(deviceToken).matches();
        }
        return false;
    }
}
