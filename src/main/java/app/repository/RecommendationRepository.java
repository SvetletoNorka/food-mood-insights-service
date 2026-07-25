package app.repository;

import app.model.Recommendation;
import app.model.RecommendationStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface RecommendationRepository extends JpaRepository<Recommendation, UUID> {

    List<Recommendation> findAllByUserIdOrderByCreatedAtDesc(UUID userId);

    List<Recommendation> findAllByUserIdAndStatusOrderByCreatedAtDesc(UUID userId, RecommendationStatus status);
}
