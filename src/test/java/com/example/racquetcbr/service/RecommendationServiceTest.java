package com.example.racquetcbr.service;

import com.example.racquetcbr.model.PlayerProfile;
import com.example.racquetcbr.model.RacquetCase;
import com.example.racquetcbr.model.Recommendation;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class RecommendationServiceTest {
    @Test
    void selectedYearPrefersMatchingRacquets() {
        RacquetCase current = racquet(2024);
        RacquetCase older = racquet(2023);
        RecommendationService service = serviceWith(current, older);
        PlayerProfile profile = new PlayerProfile();
        profile.setPreferredYear(2024);

        List<Recommendation> recommendations = service.recommend(profile);

        assertThat(recommendations.get(0).racquet()).isSameAs(current);
        assertThat(recommendations.get(0).reasons()).contains("Matches your selected year (2024)");
    }

    @Test
    void anyYearLeavesSimilarityRankingUnchanged() {
        RacquetCase first = racquet(2023);
        RacquetCase second = racquet(2024);
        RecommendationService service = serviceWith(first, second);

        List<Recommendation> recommendations = service.recommend(new PlayerProfile());

        assertThat(recommendations.get(0).racquet()).isSameAs(first);
        assertThat(recommendations).allSatisfy(recommendation -> assertThat(recommendation.score()).isEqualTo(80.0));
    }

    @Test
    void selectedRecommendationCountControlsResultSize() {
        RecommendationService service = serviceWith(racquet(2024), racquet(2023), racquet(2022), racquet(2021), racquet(2020));
        PlayerProfile profile = new PlayerProfile();
        profile.setRecommendationCount(4);

        assertThat(service.recommend(profile)).hasSize(4);
    }

    @Test
    void defaultRecommendationCountRemainsThree() {
        RecommendationService service = serviceWith(racquet(2024), racquet(2023), racquet(2022), racquet(2021));

        assertThat(service.recommend(new PlayerProfile())).hasSize(3);
    }

    private RecommendationService serviceWith(RacquetCase... racquets) {
        RacquetCsvRepository repository = mock(RacquetCsvRepository.class);
        JColibriSimilarityService similarity = mock(JColibriSimilarityService.class);
        when(repository.findAll()).thenReturn(List.of(racquets));
        when(similarity.similarity(org.mockito.ArgumentMatchers.any(), org.mockito.ArgumentMatchers.any())).thenReturn(0.8);
        return new RecommendationService(repository, similarity);
    }

    private RacquetCase racquet(int year) {
        RacquetCase racquet = new RacquetCase();
        racquet.setYear(year);
        return racquet;
    }
}