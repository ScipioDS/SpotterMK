package com.example.project1.controller;

import com.example.project1.service.PricePredictionService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
@RequiredArgsConstructor
public class LandingPageController {

    private final PricePredictionService pricePredictionService;

    @GetMapping("/")
    public String getIndexPage(Model model) {
        return "landing-page";
    }



}
