package com.example.racquetcbr.model;
import es.ucm.fdi.gaia.jcolibri.cbrcore.Attribute;
import es.ucm.fdi.gaia.jcolibri.cbrcore.CaseComponent;
public class RacquetCase implements CaseComponent {
 private String slug, brand, model, primaryStyle, playerLevel, powerClass, spinClass, controlClass, armRisk, ageGroup, stringType, stringPattern, summary;
 private Integer year; private Double weight, swingweight, ra, balanceMm, headSize;
 @Override public Attribute getIdAttribute(){ return new Attribute("slug", RacquetCase.class); }
 public String displayName(){ return brand+" "+model+(year==null?"":" ("+year+")"); }
 public String getSlug(){return slug;} public void setSlug(String v){slug=v;} public String getBrand(){return brand;} public void setBrand(String v){brand=v;}
 public String getModel(){return model;} public void setModel(String v){model=v;} public Integer getYear(){return year;} public void setYear(Integer v){year=v;}
 public Double getWeight(){return weight;} public void setWeight(Double v){weight=v;} public Double getSwingweight(){return swingweight;} public void setSwingweight(Double v){swingweight=v;}
 public Double getRa(){return ra;} public void setRa(Double v){ra=v;} public Double getBalanceMm(){return balanceMm;} public void setBalanceMm(Double v){balanceMm=v;}
 public Double getHeadSize(){return headSize;} public void setHeadSize(Double v){headSize=v;} public String getStringPattern(){return stringPattern;} public void setStringPattern(String v){stringPattern=v;}
 public String getPrimaryStyle(){return primaryStyle;} public void setPrimaryStyle(String v){primaryStyle=v;} public String getPlayerLevel(){return playerLevel;} public void setPlayerLevel(String v){playerLevel=v;}
 public String getPowerClass(){return powerClass;} public void setPowerClass(String v){powerClass=v;} public String getSpinClass(){return spinClass;} public void setSpinClass(String v){spinClass=v;}
 public String getControlClass(){return controlClass;} public void setControlClass(String v){controlClass=v;} public String getArmRisk(){return armRisk;} public void setArmRisk(String v){armRisk=v;}
 public String getAgeGroup(){return ageGroup;} public void setAgeGroup(String v){ageGroup=v;} public String getStringType(){return stringType;} public void setStringType(String v){stringType=v;}
 public String getSummary(){return summary;} public void setSummary(String v){summary=v;}
}
