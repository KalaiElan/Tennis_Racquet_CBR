package com.example.racquetcbr.web;
import com.example.racquetcbr.model.PlayerProfile; import com.example.racquetcbr.service.RecommendationService;
import jakarta.validation.Valid; import org.springframework.stereotype.Controller; import org.springframework.ui.Model; import org.springframework.validation.BindingResult; import org.springframework.web.bind.annotation.*;
@Controller
public class RecommendationController { private final RecommendationService service; public RecommendationController(RecommendationService s){service=s;}
 @GetMapping("/") String home(Model m){m.addAttribute("profile",new PlayerProfile());m.addAttribute("caseCount",service.caseCount());m.addAttribute("availableYears",service.availableYears());return "index";}
 @PostMapping("/recommend") String recommend(@Valid @ModelAttribute("profile") PlayerProfile p, BindingResult errors, Model m){m.addAttribute("caseCount",service.caseCount());m.addAttribute("availableYears",service.availableYears());if(errors.hasErrors())return "index";m.addAttribute("recommendations",service.recommend(p));return "index";}
}
