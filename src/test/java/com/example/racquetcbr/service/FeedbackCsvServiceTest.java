package com.example.racquetcbr.service;

import com.example.racquetcbr.model.PlayerProfile;
import com.example.racquetcbr.model.RacquetCase;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class FeedbackCsvServiceTest {
 @TempDir Path directory;

 @Test
 void storesVotesAndRatingsInSeparateCsvFiles() throws Exception {
  RacquetCase racquet = new RacquetCase();
  racquet.setSlug("frame-2026");
  RacquetCsvRepository repository = mock(RacquetCsvRepository.class);
  when(repository.findAll()).thenReturn(List.of(racquet));
  FeedbackCsvService service = new FeedbackCsvService(directory.toString(), repository);
  assertThat(service.appFeedback().rows()).isEmpty();
  assertThat(service.recommendationFeedback().rows()).isEmpty();
  PlayerProfile profile = new PlayerProfile();
  profile.setPreferredYear(2026);
  profile.setBrandPreference("Head");
  profile.setRecommendationCount(5);

  service.saveRecommendation("frame-2026", 2, "up", "Comfortable, even on volleys", profile);
  service.saveRecommendation("frame-2026", 2, "down", "=SUM(1,2)", profile);
  service.saveApp("Anne, Marie", "O'Neil", 5, "Easy to use\nand helpful");

  try (CSVParser votes = CSVParser.parse(directory.resolve("recommendation-feedback.csv"), StandardCharsets.UTF_8,
      CSVFormat.DEFAULT.builder().setHeader().setSkipHeaderRecord(true).build());
      CSVParser app = CSVParser.parse(directory.resolve("app-feedback.csv"), StandardCharsets.UTF_8,
      CSVFormat.DEFAULT.builder().setHeader().setSkipHeaderRecord(true).build())) {
    var voteRecords = votes.getRecords();
    var appRecords = app.getRecords();
    assertThat(voteRecords).hasSize(2);
    assertThat(voteRecords.get(0).get("rank")).isEqualTo("2");
    assertThat(voteRecords.get(0).get("age_group")).isEqualTo("Adult");
    assertThat(voteRecords.get(0).get("preferred_year")).isEqualTo("2026");
    assertThat(voteRecords.get(0).get("brand_preference")).isEqualTo("Head");
    assertThat(voteRecords.get(0).get("recommendation_count")).isEqualTo("5");
    assertThat(voteRecords.get(0).get("reason")).isEqualTo("Comfortable, even on volleys");
    assertThat(voteRecords.get(1).get("reason")).isEqualTo("'=SUM(1,2)");
    assertThat(appRecords).hasSize(1);
    assertThat(appRecords.get(0).get("first_name")).isEqualTo("Anne, Marie");
    assertThat(appRecords.get(0).get("last_name")).isEqualTo("O'Neil");
    assertThat(appRecords.get(0).get("rating")).isEqualTo("5");
    assertThat(appRecords.get(0).get("comment")).isEqualTo("Easy to use\nand helpful");
  }
  assertThat(service.appFeedback().rows()).hasSize(1);
  assertThat(service.recommendationFeedback().rows()).hasSize(2);
 }

 @Test
 void rejectsInvalidSubmissionsWithoutCreatingFiles() throws Exception {
  FeedbackCsvService service = new FeedbackCsvService(directory.toString(), mock(RacquetCsvRepository.class));

  assertThatThrownBy(() -> service.saveRecommendation("unknown", 1, "up", "okay", new PlayerProfile())).isInstanceOf(IllegalArgumentException.class);
  assertThatThrownBy(() -> service.saveApp("Anne", "O'Neil", 6, "no")).isInstanceOf(IllegalArgumentException.class);
  assertThatThrownBy(() -> service.saveApp(" ", "O'Neil", 5, "no")).isInstanceOf(IllegalArgumentException.class);
  assertThatThrownBy(() -> service.saveApp("Anne", "", 5, "no")).isInstanceOf(IllegalArgumentException.class);
  assertThat(Files.list(directory).toList()).isEmpty();
 }

 @Test
 void upgradesLegacyRowsWithoutLosingTheirVotes() throws Exception {
  Path file = directory.resolve("recommendation-feedback.csv");
  Files.writeString(file, "timestamp,racquet_slug,vote,reason\n2026-10-27T02:28:06Z,frame-2026,up,Old feedback\n");
  RacquetCase racquet = new RacquetCase();
  racquet.setSlug("frame-2026");
  RacquetCsvRepository repository = mock(RacquetCsvRepository.class);
  when(repository.findAll()).thenReturn(List.of(racquet));
  FeedbackCsvService service = new FeedbackCsvService(directory.toString(), repository);

  assertThat(service.recommendationFeedback().rows().get(0).get(2)).isEmpty();

  service.saveRecommendation("frame-2026", 1, "down", "New feedback", new PlayerProfile());

  try (CSVParser parser = CSVParser.parse(file, StandardCharsets.UTF_8,
      CSVFormat.DEFAULT.builder().setHeader().setSkipHeaderRecord(true).build())) {
   var rows = parser.getRecords();
   assertThat(rows).hasSize(2);
   assertThat(rows.get(0).get("vote")).isEqualTo("up");
   assertThat(rows.get(0).get("reason")).isEqualTo("Old feedback");
   assertThat(rows.get(0).get("rank")).isEmpty();
   assertThat(rows.get(1).get("vote")).isEqualTo("down");
   assertThat(rows.get(1).get("rank")).isEqualTo("1");
  }
 }

 @Test
 void upgradesLegacyAppFeedbackWithoutLosingRatings() throws Exception {
  Path file = directory.resolve("app-feedback.csv");
  Files.writeString(file, "timestamp,rating,comment\n2026-10-27T02:28:39Z,5,Nice App as per my needs\n");
  FeedbackCsvService service = new FeedbackCsvService(directory.toString(), mock(RacquetCsvRepository.class));

  assertThat(service.appFeedback().rows().get(0).get(1)).isEmpty();

  service.saveApp("Anne", "O'Neil", 4, "Useful");

  try (CSVParser parser = CSVParser.parse(file, StandardCharsets.UTF_8,
    CSVFormat.DEFAULT.builder().setHeader().setSkipHeaderRecord(true).build())) {
   var rows = parser.getRecords();
   assertThat(rows).hasSize(2);
   assertThat(rows.get(0).get("rating")).isEqualTo("5");
   assertThat(rows.get(0).get("comment")).isEqualTo("Nice App as per my needs");
   assertThat(rows.get(0).get("first_name")).isEmpty();
   assertThat(rows.get(0).get("last_name")).isEmpty();
   assertThat(rows.get(1).get("first_name")).isEqualTo("Anne");
   assertThat(rows.get(1).get("last_name")).isEqualTo("O'Neil");
   assertThat(rows.get(1).get("rating")).isEqualTo("4");
  }
 }
}