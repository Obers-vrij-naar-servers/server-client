package config;

public class AfspConfigurationException extends RuntimeException
{
    public AfspConfigurationException() {
    }

    public AfspConfigurationException(String message) {
        super(message);
    }

    public AfspConfigurationException(String message, Throwable cause) {
        super(message, cause);
    }

    public AfspConfigurationException(Throwable cause) {
        super(cause);
    }

    public AfspConfigurationException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
