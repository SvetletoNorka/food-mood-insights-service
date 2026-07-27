package app.exception;

public class InvalidRecommendationOperationException extends RuntimeException {

    public InvalidRecommendationOperationException(String message) {
        super(message);
    }
}
