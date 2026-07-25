package app.web.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CreateRecommendationRequest {

    private UUID mealId;

    @NotNull
    @Min(1)
    @Max(10)
    private Integer moodScore;

    @NotNull
    @Min(1)
    @Max(10)
    private Integer energyScore;

    private List<String> foodNames;

    private Double totalCalories;

    private Double totalProtein;

    private Double totalFat;

    private Double totalCarbs;
}
