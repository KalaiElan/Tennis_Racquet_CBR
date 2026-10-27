package com.example.racquetcbr.web;

import com.example.racquetcbr.service.FeedbackCsvService;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(properties = "ADMIN_PASSWORD=test-only-secret")
@AutoConfigureMockMvc
class AdminDashboardTest {
 @Autowired MockMvc mvc;
 @MockitoBean FeedbackCsvService feedback;

 @BeforeEach
 void tables() throws Exception {
  when(feedback.appFeedback()).thenReturn(new FeedbackCsvService.FeedbackTable(
      List.of("timestamp", "first_name", "last_name", "rating", "comment"),
      List.of(List.of("2026-10-26", "Ada", "Smith", "5", "Useful"))));
  when(feedback.recommendationFeedback()).thenReturn(new FeedbackCsvService.FeedbackTable(
      List.of("timestamp", "racquet_slug", "rank", "vote", "reason"),
      List.of(List.of("2026-10-26", "head-frame", "2", "up", "Comfortable"))));
 }

 @Test
 void protectsBothAdminPages() throws Exception {
  mvc.perform(get("/admin/app")).andExpect(status().isUnauthorized());
  mvc.perform(get("/admin/recommendations").header("Authorization", basic("admin", "wrong")))
      .andExpect(status().isUnauthorized());
 }

 @Test
 void showsSeparateFeedbackPagesToAdmin() throws Exception {
  mvc.perform(get("/admin/app").header("Authorization", basic("admin", "test-only-secret")))
      .andExpect(status().isOk()).andExpect(content().string(org.hamcrest.Matchers.containsString("Ada")))
      .andExpect(content().string(org.hamcrest.Matchers.not(org.hamcrest.Matchers.containsString("head-frame"))));
  mvc.perform(get("/admin/recommendations").header("Authorization", basic("admin", "test-only-secret")))
      .andExpect(status().isOk()).andExpect(content().string(org.hamcrest.Matchers.containsString("head-frame")))
      .andExpect(content().string(org.hamcrest.Matchers.not(org.hamcrest.Matchers.containsString("Ada"))));
 }

 @Test
 void keepsPublicRecommendationFlowOpen() throws Exception {
  mvc.perform(get("/")).andExpect(status().isOk());
  mvc.perform(post("/recommend").param("recommendationCount", "1")).andExpect(status().isOk());
  mvc.perform(post("/feedback/app").param("firstName", "Ada").param("lastName", "Smith").param("rating", "5"))
      .andExpect(status().is3xxRedirection());
 }

 private String basic(String username, String password) {
  return "Basic " + Base64.getEncoder().encodeToString((username + ":" + password).getBytes(StandardCharsets.UTF_8));
 }
}