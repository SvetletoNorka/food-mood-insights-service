package app.web.mapper;

import app.model.Recommendation;
import app.web.dto.RecommendationResponse;

public final class RecommendationMapper {

    private RecommendationMapper() {
    }

    public static RecommendationResponse toResponse(Recommendation recommendation) {
        return RecommendationResponse.builder()
                .id(recommendation.getId())
                .userId(recommendation.getUserId())
                .mealId(recommendation.getMealId())
                .title(recommendation.getTitle())
                .message(recommendation.getMessage())
                .status(recommendation.getStatus())
                .moodScore(recommendation.getMoodScore())
                .energyScore(recommendation.getEnergyScore())
                .foodNames(recommendation.getFoodNames())
                .totalCalories(recommendation.getTotalCalories())
                .totalProtein(recommendation.getTotalProtein())
                .totalFat(recommendation.getTotalFat())
                .totalCarbs(recommendation.getTotalCarbs())
                .createdAt(recommendation.getCreatedAt())
                .updatedAt(recommendation.getUpdatedAt())
                .build();
    }
}
