package com.example.racquetcbr.model;
import jakarta.validation.constraints.*;
public class PlayerProfile {
 @NotBlank private String ageGroup="Adult"; @NotBlank private String playerLevel="Intermediate"; @NotBlank private String primaryStyle="Balanced";
 @NotBlank private String powerPreference="Medium", spinPreference="Medium", controlPreference="Medium", armSensitivity="Medium";
 @Min(150) @Max(400) private Double preferredWeight=285.0; @Min(70) @Max(140) private Double preferredHeadSize=100.0;
 @NotNull @Min(1) @Max(10) private Integer recommendationCount=3;
 private Integer preferredYear; private String brandPreference="";
 public String getAgeGroup(){return ageGroup;} public void setAgeGroup(String v){ageGroup=v;} public String getPlayerLevel(){return playerLevel;} public void setPlayerLevel(String v){playerLevel=v;}
 public String getPrimaryStyle(){return primaryStyle;} public void setPrimaryStyle(String v){primaryStyle=v;} public String getPowerPreference(){return powerPreference;} public void setPowerPreference(String v){powerPreference=v;}
 public String getSpinPreference(){return spinPreference;} public void setSpinPreference(String v){spinPreference=v;} public String getControlPreference(){return controlPreference;} public void setControlPreference(String v){controlPreference=v;}
 public String getArmSensitivity(){return armSensitivity;} public void setArmSensitivity(String v){armSensitivity=v;} public Double getPreferredWeight(){return preferredWeight;} public void setPreferredWeight(Double v){preferredWeight=v;}
 public Double getPreferredHeadSize(){return preferredHeadSize;} public void setPreferredHeadSize(Double v){preferredHeadSize=v;} public Integer getPreferredYear(){return preferredYear;} public void setPreferredYear(Integer v){preferredYear=v;}
 public Integer getRecommendationCount(){return recommendationCount;} public void setRecommendationCount(Integer v){recommendationCount=v;}
 public String getBrandPreference(){return brandPreference;} public void setBrandPreference(String v){brandPreference=v;}
}
