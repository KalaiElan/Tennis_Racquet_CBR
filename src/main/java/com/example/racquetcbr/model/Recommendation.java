package com.example.racquetcbr.model;
import java.util.List;
public record Recommendation(RacquetCase racquet, double score, String confidence, List<String> reasons, List<String> cautions) {}
