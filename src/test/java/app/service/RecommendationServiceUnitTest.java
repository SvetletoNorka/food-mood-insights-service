package app.service;

import app.exception.InvalidRecommendationOperationException;
import app.exception.RecommendationNotFoundException;
import app.model.Recommendation;
import app.model.RecommendationStatus;
import app.repository.RecommendationRepository;
import app.web.dto.CreateRecommendationRequest;
import app.web.dto.RecommendationResponse;
import app.web.dto.UpdateRecommendationRequest;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class RecommendationServiceUnitTest {

    @Mock
    private RecommendationRepository recommendationRepository;

    @InjectMocks
    private RecommendationService recommendationService;

    @Test
    void create_whenHighMoodAndEnergy_shouldCreatePositiveRecommendation() {
        UUID userId = UUID.randomUUID();
        CreateRecommendationRequest request = CreateRecommendationRequest.builder()
                .mealId(UUID.randomUUID())
                .moodScore(9)
                .energyScore(8)
                .foodNames(List.of("Chicken", "Rice"))
                .totalCalories(500.0)
                .totalProtein(40.0)
                .totalFat(10.0)
                .totalCarbs(50.0)
                .build();

        when(recommendationRepository.save(any(Recommendation.class))).thenAnswer(invocation -> {
            Recommendation recommendation = invocation.getArgument(0);
            recommendation.setId(UUID.randomUUID());
            return recommendation;
        });

        RecommendationResponse response = recommendationService.create(userId, request);

        assertEquals("Keep this combination", response.getTitle());
        assertEquals(RecommendationStatus.ACTIVE, response.getStatus());
        assertEquals(userId, response.getUserId());
        assertTrue(response.getMessage().contains("Chicken"));
        verify(recommendationRepository).save(any(Recommendation.class));
    }

    @Test
    void create_whenLowMoodAndEnergy_shouldCreateRecoveryRecommendation() {
        UUID userId = UUID.randomUUID();
        CreateRecommendationRequest request = CreateRecommendationRequest.builder()
                .moodScore(2)
                .energyScore(3)
                .foodNames(List.of("Doner"))
                .totalCarbs(80.0)
                .totalProtein(10.0)
                .build();

        when(recommendationRepository.save(any(Recommendation.class))).thenAnswer(invocation -> {
            Recommendation recommendation = invocation.getArgument(0);
            recommendation.setId(UUID.randomUUID());
            return recommendation;
        });

        RecommendationResponse response = recommendationService.create(userId, request);

        assertEquals("Try a lighter balance", response.getTitle());
        assertEquals(RecommendationStatus.ACTIVE, response.getStatus());
    }

    @Test
    void findByUser_whenStatusProvided_shouldFilterByStatus() {
        UUID userId = UUID.randomUUID();
        Recommendation recommendation = Recommendation.builder()
                .id(UUID.randomUUID())
                .userId(userId)
                .title("Title")
                .message("Message")
                .status(RecommendationStatus.ACTIVE)
                .createdAt(LocalDateTime.now())
                .build();

        when(recommendationRepository.findAllByUserIdAndStatusOrderByCreatedAtDesc(userId, RecommendationStatus.ACTIVE))
                .thenReturn(List.of(recommendation));

        List<RecommendationResponse> result = recommendationService.findByUser(userId, RecommendationStatus.ACTIVE);

        assertEquals(1, result.size());
        assertEquals("Title", result.get(0).getTitle());
    }

    @Test
    void updateStatus_whenNotFound_shouldThrow() {
        UUID id = UUID.randomUUID();
        when(recommendationRepository.findById(id)).thenReturn(Optional.empty());

        assertThrows(RecommendationNotFoundException.class, () ->
                recommendationService.updateStatus(id, UpdateRecommendationRequest.builder()
                        .status(RecommendationStatus.APPLIED)
                        .build()));
    }

    @Test
    void updateStatus_whenSettingActive_shouldThrow() {
        UUID id = UUID.randomUUID();
        Recommendation recommendation = Recommendation.builder()
                .id(id)
                .userId(UUID.randomUUID())
                .title("Title")
                .message("Message")
                .status(RecommendationStatus.ACTIVE)
                .createdAt(LocalDateTime.now())
                .build();
        when(recommendationRepository.findById(id)).thenReturn(Optional.of(recommendation));

        assertThrows(InvalidRecommendationOperationException.class, () ->
                recommendationService.updateStatus(id, UpdateRecommendationRequest.builder()
                        .status(RecommendationStatus.ACTIVE)
                        .build()));
    }

    @Test
    void updateStatus_whenAlreadyDismissed_shouldThrow() {
        UUID id = UUID.randomUUID();
        Recommendation recommendation = Recommendation.builder()
                .id(id)
                .userId(UUID.randomUUID())
                .title("Title")
                .message("Message")
                .status(RecommendationStatus.DISMISSED)
                .createdAt(LocalDateTime.now())
                .build();
        when(recommendationRepository.findById(id)).thenReturn(Optional.of(recommendation));

        assertThrows(InvalidRecommendationOperationException.class, () ->
                recommendationService.updateStatus(id, UpdateRecommendationRequest.builder()
                        .status(RecommendationStatus.APPLIED)
                        .build()));
    }

    @Test
    void updateStatus_whenActive_shouldApply() {
        UUID id = UUID.randomUUID();
        Recommendation recommendation = Recommendation.builder()
                .id(id)
                .userId(UUID.randomUUID())
                .title("Title")
                .message("Message")
                .status(RecommendationStatus.ACTIVE)
                .createdAt(LocalDateTime.now())
                .build();
        when(recommendationRepository.findById(id)).thenReturn(Optional.of(recommendation));
        when(recommendationRepository.save(any(Recommendation.class))).thenAnswer(invocation -> invocation.getArgument(0));

        RecommendationResponse response = recommendationService.updateStatus(id, UpdateRecommendationRequest.builder()
                .status(RecommendationStatus.APPLIED)
                .build());

        assertEquals(RecommendationStatus.APPLIED, response.getStatus());
        assertNotNull(response.getUpdatedAt());
    }

    @Test
    void create_shouldCoverRemainingInsightBranches() {
        UUID userId = UUID.randomUUID();
        when(recommendationRepository.save(any(Recommendation.class))).thenAnswer(invocation -> {
            Recommendation recommendation = invocation.getArgument(0);
            recommendation.setId(UUID.randomUUID());
            return recommendation;
        });

        assertEquals("Mood needs attention", recommendationService.create(userId, CreateRecommendationRequest.builder()
                .moodScore(3).energyScore(6).foodNames(List.of("Pizza")).build()).getTitle());

        assertEquals("Carb-heavy crash risk", recommendationService.create(userId, CreateRecommendationRequest.builder()
                .moodScore(6).energyScore(3).totalCarbs(90.0).totalProtein(10.0).build()).getTitle());

        assertEquals("Rebuild your energy", recommendationService.create(userId, CreateRecommendationRequest.builder()
                .moodScore(6).energyScore(3).totalCarbs(10.0).totalProtein(20.0).build()).getTitle());

        assertEquals("Steady day insight", recommendationService.create(userId, CreateRecommendationRequest.builder()
                .moodScore(6).energyScore(6).build()).getTitle());
    }

    @Test
    void findByUser_whenNoStatus_shouldReturnAll() {
        UUID userId = UUID.randomUUID();
        when(recommendationRepository.findAllByUserIdOrderByCreatedAtDesc(userId))
                .thenReturn(List.of(Recommendation.builder()
                        .id(UUID.randomUUID())
                        .userId(userId)
                        .title("All")
                        .message("Msg")
                        .status(RecommendationStatus.ACTIVE)
                        .createdAt(LocalDateTime.now())
                        .build()));

        assertEquals(1, recommendationService.findByUser(userId, null).size());
    }
}
