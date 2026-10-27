package com.example.racquetcbr.web;

import com.example.racquetcbr.service.FeedbackCsvService;
import java.io.IOException;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/admin")
public class AdminController {
 private final FeedbackCsvService feedback;

 public AdminController(FeedbackCsvService feedback) { this.feedback = feedback; }

 @GetMapping
 String home() { return "redirect:/admin/app"; }

 @GetMapping("/app")
 String app(Model model) throws IOException {
  model.addAttribute("section", "app");
  model.addAttribute("title", "App feedback");
  model.addAttribute("table", feedback.appFeedback());
  return "admin";
 }

 @GetMapping("/recommendations")
 String recommendations(Model model) throws IOException {
  model.addAttribute("section", "recommendations");
  model.addAttribute("title", "Recommendation feedback");
  model.addAttribute("table", feedback.recommendationFeedback());
  return "admin";
 }
}