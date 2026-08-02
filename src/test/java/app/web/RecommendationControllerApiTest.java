package app.web;

import app.model.RecommendationStatus;
import app.service.RecommendationService;
import app.web.dto.CreateRecommendationRequest;
import app.web.dto.RecommendationResponse;
import app.web.dto.UpdateRecommendationRequest;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import static org.hamcrest.Matchers.hasSize;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ActiveProfiles("test")
@WebMvcTest(RecommendationController.class)
class RecommendationControllerApiTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private RecommendationService recommendationService;

    @Test
    void createRecommendation_shouldReturn201() throws Exception {
        UUID userId = UUID.randomUUID();
        RecommendationResponse response = RecommendationResponse.builder()
                .id(UUID.randomUUID())
                .userId(userId)
                .title("Keep this combination")
                .message("Great meal")
                .status(RecommendationStatus.ACTIVE)
                .moodScore(8)
                .energyScore(8)
                .createdAt(LocalDateTime.now())
                .build();

        when(recommendationService.create(eq(userId), any(CreateRecommendationRequest.class))).thenReturn(response);

        CreateRecommendationRequest request = CreateRecommendationRequest.builder()
                .moodScore(8)
                .energyScore(8)
                .foodNames(List.of("Chicken"))
                .build();

        mockMvc.perform(post("/api/v1/users/{userId}/recommendations", userId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(header().exists("Location"))
                .andExpect(jsonPath("$.title").value("Keep this combination"))
                .andExpect(jsonPath("$.status").value("ACTIVE"));
    }

    @Test
    void getRecommendations_shouldReturn200() throws Exception {
        UUID userId = UUID.randomUUID();
        RecommendationResponse response = RecommendationResponse.builder()
                .id(UUID.randomUUID())
                .userId(userId)
                .title("Title")
                .message("Message")
                .status(RecommendationStatus.ACTIVE)
                .createdAt(LocalDateTime.now())
                .build();

        when(recommendationService.findByUser(userId, null)).thenReturn(List.of(response));

        mockMvc.perform(get("/api/v1/users/{userId}/recommendations", userId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].title").value("Title"));
    }

    @Test
    void updateRecommendation_shouldReturn200() throws Exception {
        UUID id = UUID.randomUUID();
        RecommendationResponse response = RecommendationResponse.builder()
                .id(id)
                .userId(UUID.randomUUID())
                .title("Title")
                .message("Message")
                .status(RecommendationStatus.DISMISSED)
                .createdAt(LocalDateTime.now())
                .build();

        when(recommendationService.updateStatus(eq(id), any(UpdateRecommendationRequest.class))).thenReturn(response);

        mockMvc.perform(put("/api/v1/recommendations/{id}", id)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(
                                UpdateRecommendationRequest.builder().status(RecommendationStatus.DISMISSED).build())))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("DISMISSED"));
    }

    @Test
    void createRecommendation_whenInvalidBody_shouldReturn400() throws Exception {
        UUID userId = UUID.randomUUID();
        CreateRecommendationRequest request = CreateRecommendationRequest.builder()
                .moodScore(null)
                .energyScore(8)
                .build();

        mockMvc.perform(post("/api/v1/users/{userId}/recommendations", userId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").exists());
    }
}
