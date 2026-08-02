package app.web.exception;

import app.exception.InvalidRecommendationOperationException;
import app.exception.RecommendationNotFoundException;
import app.web.dto.ErrorResponseDto;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.servlet.resource.NoResourceFoundException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class GlobalExceptionHandlerUnitTest {

    private final GlobalExceptionHandler underTest = new GlobalExceptionHandler();

    @Test
    void handleNotFound_shouldReturn404() {
        ResponseEntity<ErrorResponseDto> response = underTest.handleNotFound(
                new RecommendationNotFoundException("missing"));

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertEquals("missing", response.getBody().getMessage());
    }

    @Test
    void handleInvalidOperation_shouldReturn400() {
        ResponseEntity<ErrorResponseDto> response = underTest.handleInvalidOperation(
                new InvalidRecommendationOperationException("bad op"));

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals("bad op", response.getBody().getMessage());
    }

    @Test
    void handleTypeMismatch_shouldReturn400() {
        MethodArgumentTypeMismatchException ex = mock(MethodArgumentTypeMismatchException.class);
        when(ex.getName()).thenReturn("id");

        ResponseEntity<ErrorResponseDto> response = underTest.handleTypeMismatch(ex);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals("Invalid value for parameter 'id'.", response.getBody().getMessage());
    }

    @Test
    void handleNoResource_shouldReturn404() {
        ResponseEntity<ErrorResponseDto> response = underTest.handleNoResource(
                new NoResourceFoundException(org.springframework.http.HttpMethod.GET, "/missing"));

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    }

    @Test
    void handleGeneric_shouldReturn500() {
        ResponseEntity<ErrorResponseDto> response = underTest.handleGeneric(new RuntimeException("boom"));

        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
    }
}
