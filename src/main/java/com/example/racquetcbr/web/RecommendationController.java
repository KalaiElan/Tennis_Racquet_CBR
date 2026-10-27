package com.example.racquetcbr.web;
import com.example.racquetcbr.model.PlayerProfile; import com.example.racquetcbr.service.RecommendationService; import com.example.racquetcbr.service.FeedbackCsvService;
import jakarta.validation.Valid; import java.io.IOException; import org.springframework.http.HttpStatus; import org.springframework.stereotype.Controller; import org.springframework.ui.Model; import org.springframework.validation.BindingResult; import org.springframework.web.bind.annotation.*; import org.springframework.web.server.ResponseStatusException;
@Controller
public class RecommendationController { private final RecommendationService service; private final FeedbackCsvService feedback;
 public RecommendationController(RecommendationService s, FeedbackCsvService feedback){service=s;this.feedback=feedback;}
 @GetMapping("/") String home(Model m){m.addAttribute("profile",new PlayerProfile());m.addAttribute("caseCount",service.caseCount());m.addAttribute("availableYears",service.availableYears());return "index";}
 @PostMapping("/recommend") String recommend(@Valid @ModelAttribute("profile") PlayerProfile p, BindingResult errors, Model m){m.addAttribute("caseCount",service.caseCount());m.addAttribute("availableYears",service.availableYears());if(errors.hasErrors())return "index";m.addAttribute("recommendations",service.recommend(p));return "index";}
 @PostMapping("/feedback/recommendation") String recommendationFeedback(@RequestParam("slug") String slug, @RequestParam("vote") String vote, @RequestParam(value="reason",required=false) String reason,
     @Valid @ModelAttribute PlayerProfile profile, BindingResult errors) throws IOException {
  if (errors.hasErrors()) throw new ResponseStatusException(HttpStatus.BAD_REQUEST,"Invalid profile");
  var results = service.recommend(profile);
  int rank = -1;
  for (int index = 0; index < results.size(); index++) if (slug.equals(results.get(index).racquet().getSlug())) {rank=index+1;break;}
  if (rank < 1) throw new ResponseStatusException(HttpStatus.BAD_REQUEST,"Racquet is not in the submitted recommendations");
  try {feedback.saveRecommendation(slug,rank,vote,reason,profile);} catch(IllegalArgumentException ex){throw new ResponseStatusException(HttpStatus.BAD_REQUEST,ex.getMessage());}
  return "redirect:/?feedback=thanks";
 }
 @PostMapping("/feedback/app") String appFeedback(@RequestParam("firstName") String firstName, @RequestParam("lastName") String lastName,
     @RequestParam("rating") Integer rating, @RequestParam(value="comment",required=false) String comment) throws IOException {
  try {feedback.saveApp(firstName,lastName,rating,comment);} catch(IllegalArgumentException ex){throw new ResponseStatusException(HttpStatus.BAD_REQUEST,ex.getMessage());}
  return "redirect:/?feedback=thanks";
 }
}
