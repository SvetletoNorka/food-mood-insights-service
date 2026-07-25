package app.service;

import app.exception.RecommendationNotFoundException;
import app.model.Recommendation;
import app.model.RecommendationStatus;
import app.repository.RecommendationRepository;
import app.web.dto.CreateRecommendationRequest;
import app.web.dto.RecommendationResponse;
import app.web.dto.UpdateRecommendationRequest;
import app.web.mapper.RecommendationMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class RecommendationService {

    private final RecommendationRepository recommendationRepository;

    public RecommendationResponse create(UUID userId, CreateRecommendationRequest request) {
        InsightContent content = buildInsight(request);

        String foodNames = request.getFoodNames() == null || request.getFoodNames().isEmpty()
                ? null
                : request.getFoodNames().stream().collect(Collectors.joining(", "));

        Recommendation recommendation = Recommendation.builder()
                .userId(userId)
                .mealId(request.getMealId())
                .title(content.title())
                .message(content.message())
                .status(RecommendationStatus.ACTIVE)
                .moodScore(request.getMoodScore())
                .energyScore(request.getEnergyScore())
                .foodNames(foodNames)
                .totalCalories(request.getTotalCalories())
                .totalProtein(request.getTotalProtein())
                .totalFat(request.getTotalFat())
                .totalCarbs(request.getTotalCarbs())
                .createdAt(LocalDateTime.now())
                .build();

        return RecommendationMapper.toResponse(recommendationRepository.save(recommendation));
    }

    @Transactional(readOnly = true)
    public List<RecommendationResponse> findByUser(UUID userId, RecommendationStatus status) {
        List<Recommendation> recommendations = status == null
                ? recommendationRepository.findAllByUserIdOrderByCreatedAtDesc(userId)
                : recommendationRepository.findAllByUserIdAndStatusOrderByCreatedAtDesc(userId, status);

        return recommendations.stream()
                .map(RecommendationMapper::toResponse)
                .toList();
    }

    public RecommendationResponse updateStatus(UUID recommendationId, UpdateRecommendationRequest request) {
        Recommendation recommendation = recommendationRepository.findById(recommendationId)
                .orElseThrow(() -> new RecommendationNotFoundException(
                        "Recommendation with id [%s] not found.".formatted(recommendationId)));

        if (request.getStatus() == RecommendationStatus.ACTIVE) {
            throw new IllegalArgumentException("Status cannot be set back to ACTIVE.");
        }

        recommendation.setStatus(request.getStatus());
        recommendation.setUpdatedAt(LocalDateTime.now());

        return RecommendationMapper.toResponse(recommendationRepository.save(recommendation));
    }

    private InsightContent buildInsight(CreateRecommendationRequest request) {
        int mood = request.getMoodScore();
        int energy = request.getEnergyScore();
        String foods = request.getFoodNames() == null || request.getFoodNames().isEmpty()
                ? "this meal"
                : String.join(", ", request.getFoodNames());

        double carbs = request.getTotalCarbs() != null ? request.getTotalCarbs() : 0;
        double protein = request.getTotalProtein() != null ? request.getTotalProtein() : 0;

        if (mood >= 7 && energy >= 7) {
            return new InsightContent(
                    "Keep this combination",
                    "You felt great after eating %s. Consider repeating similar meals when you need a boost."
                            .formatted(foods));
        }

        if (mood <= 4 && energy <= 4) {
            return new InsightContent(
                    "Try a lighter balance",
                    "Mood and energy were low after %s. Next time, try more protein and fewer dense carbs."
                            .formatted(foods));
        }

        if (mood <= 4) {
            return new InsightContent(
                    "Mood needs attention",
                    "Your mood dipped after %s. Hydration, smaller portions, or more colorful veggies may help."
                            .formatted(foods));
        }

        if (energy <= 4 && carbs > protein * 2) {
            return new InsightContent(
                    "Carb-heavy crash risk",
                    "Energy was low after a carb-heavy meal (%s). Balance the next plate with more protein."
                            .formatted(foods));
        }

        if (energy <= 4) {
            return new InsightContent(
                    "Rebuild your energy",
                    "Energy felt low after %s. A snack with protein and complex carbs later may help."
                            .formatted(foods));
        }

        return new InsightContent(
                "Steady day insight",
                "After %s your scores were balanced (mood %d, energy %d). Keep tracking to spot stronger patterns."
                        .formatted(foods, mood, energy));
    }

    private record InsightContent(String title, String message) {
    }
}
