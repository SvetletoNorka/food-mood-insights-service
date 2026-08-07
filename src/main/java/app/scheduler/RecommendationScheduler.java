package app.scheduler;

import app.service.RecommendationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Slf4j
@Component
@RequiredArgsConstructor
public class RecommendationScheduler {

    private static final int STALE_DAYS = 30;

    private final RecommendationService recommendationService;

    @Scheduled(cron = "0 0 3 * * *")
    public void dismissStaleActiveRecommendations() {
        LocalDateTime cutoff = LocalDateTime.now().minusDays(STALE_DAYS);
        int dismissed = recommendationService.dismissStaleActiveRecommendations(cutoff);
        log.info("Scheduled cron job dismissed {} ACTIVE recommendations older than {}",
                dismissed, cutoff);
    }

    @Scheduled(fixedRate = 1_800_000, initialDelay = 60_000)
    public void reportActiveRecommendationsCount() {
        long count = recommendationService.countActiveRecommendations();
        log.info("Scheduled fixed-rate job: active recommendations count={}", count);
    }
}
