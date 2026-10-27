package com.example.racquetcbr.web;

import com.example.racquetcbr.model.PlayerProfile;
import com.example.racquetcbr.model.RacquetCase;
import com.example.racquetcbr.model.Recommendation;
import com.example.racquetcbr.service.FeedbackCsvService;
import com.example.racquetcbr.service.RecommendationService;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.web.server.ResponseStatusException;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class RecommendationControllerFeedbackTest {
 @Test
 void recordsRankFromRecommendedOrder() throws Exception {
  RecommendationService recommendations = mock(RecommendationService.class);
  FeedbackCsvService feedback = mock(FeedbackCsvService.class);
  PlayerProfile profile = new PlayerProfile();
  profile.setPreferredYear(2026);
  when(recommendations.recommend(profile)).thenReturn(List.of(result("first"), result("second")));
  RecommendationController controller = new RecommendationController(recommendations, feedback);

  assertThat(controller.recommendationFeedback("second", "up", "Good fit", profile,
      new BeanPropertyBindingResult(profile, "profile"))).isEqualTo("redirect:/?feedback=thanks");

  verify(feedback).saveRecommendation("second", 2, "up", "Good fit", profile);
 }

 @Test
 void rejectsRacquetOutsideSubmittedResults() {
  RecommendationService recommendations = mock(RecommendationService.class);
  FeedbackCsvService feedback = mock(FeedbackCsvService.class);
  PlayerProfile profile = new PlayerProfile();
  when(recommendations.recommend(profile)).thenReturn(List.of(result("first")));
  RecommendationController controller = new RecommendationController(recommendations, feedback);

  assertThatThrownBy(() -> controller.recommendationFeedback("other", "up", "", profile,
      new BeanPropertyBindingResult(profile, "profile"))).isInstanceOf(ResponseStatusException.class);
 }

 private Recommendation result(String slug) {
  RacquetCase racquet = new RacquetCase();
  racquet.setSlug(slug);
  return new Recommendation(racquet, 80, "High", List.of(), List.of());
 }
}