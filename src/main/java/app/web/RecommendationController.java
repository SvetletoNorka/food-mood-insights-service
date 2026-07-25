package app.web;

import app.model.RecommendationStatus;
import app.service.RecommendationService;
import app.web.dto.CreateRecommendationRequest;
import app.web.dto.RecommendationResponse;
import app.web.dto.UpdateRecommendationRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;
import java.util.UUID;

@RestController
@RequiredArgsConstructor
public class RecommendationController {

    private final RecommendationService recommendationService;

    @PostMapping("/api/v1/users/{userId}/recommendations")
    public ResponseEntity<RecommendationResponse> createRecommendation(
            @PathVariable UUID userId,
            @Valid @RequestBody CreateRecommendationRequest request) {

        RecommendationResponse response = recommendationService.create(userId, request);
        URI location = URI.create("/api/v1/recommendations/" + response.getId());

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .location(location)
                .body(response);
    }

    @GetMapping("/api/v1/users/{userId}/recommendations")
    public ResponseEntity<List<RecommendationResponse>> getRecommendations(
            @PathVariable UUID userId,
            @RequestParam(required = false) RecommendationStatus status) {

        return ResponseEntity.ok(recommendationService.findByUser(userId, status));
    }

    @PutMapping("/api/v1/recommendations/{id}")
    public ResponseEntity<RecommendationResponse> updateRecommendation(
            @PathVariable UUID id,
            @Valid @RequestBody UpdateRecommendationRequest request) {

        return ResponseEntity.ok(recommendationService.updateStatus(id, request));
    }
}
