package app.service;

import app.model.RecommendationStatus;
import app.repository.RecommendationRepository;
import app.web.dto.CreateRecommendationRequest;
import app.web.dto.RecommendationResponse;
import app.web.dto.UpdateRecommendationRequest;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;

import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
@ActiveProfiles("test")
@SpringBootTest
class RecommendationServiceItTest {

    @Autowired
    private RecommendationService underTest;

    @Autowired
    private RecommendationRepository recommendationRepository;

    @Test
    void createAndUpdate_shouldPersistRecommendationInDatabase() {
        UUID userId = UUID.randomUUID();

        RecommendationResponse created = underTest.create(userId, CreateRecommendationRequest.builder()
                .moodScore(8)
                .energyScore(7)
                .foodNames(List.of("Chicken", "Rice"))
                .totalCalories(450.0)
                .totalProtein(40.0)
                .totalFat(10.0)
                .totalCarbs(40.0)
                .build());

        assertNotNull(created.getId());
        assertEquals(RecommendationStatus.ACTIVE, created.getStatus());
        assertEquals(1, recommendationRepository.findAllByUserIdOrderByCreatedAtDesc(userId).size());

        RecommendationResponse updated = underTest.updateStatus(created.getId(), UpdateRecommendationRequest.builder()
                .status(RecommendationStatus.APPLIED)
                .build());

        assertEquals(RecommendationStatus.APPLIED, updated.getStatus());
        assertEquals(RecommendationStatus.APPLIED,
                recommendationRepository.findById(created.getId()).orElseThrow().getStatus());
    }
}
