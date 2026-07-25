package app.web.dto;

import app.model.RecommendationStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RecommendationResponse {

    private UUID id;
    private UUID userId;
    private UUID mealId;
    private String title;
    private String message;
    private RecommendationStatus status;
    private Integer moodScore;
    private Integer energyScore;
    private String foodNames;
    private Double totalCalories;
    private Double totalProtein;
    private Double totalFat;
    private Double totalCarbs;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
