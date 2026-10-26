package com.example.racquetcbr.service;
import com.example.racquetcbr.model.*; import es.ucm.fdi.gaia.jcolibri.cbrcore.Attribute;
import es.ucm.fdi.gaia.jcolibri.method.retrieve.NNretrieval.NNConfig;
import es.ucm.fdi.gaia.jcolibri.method.retrieve.NNretrieval.similarity.global.Average;
import org.springframework.stereotype.Service; import java.util.*;
@Service
public class JColibriSimilarityService {
 private static final Map<String,Integer> LEVEL=Map.ofEntries(
  Map.entry("Junior",0), Map.entry("Beginner/Recreational",1), Map.entry("Beginner",1), Map.entry("Recreational",1),
  Map.entry("Beginner-Intermediate",2), Map.entry("Beginner–Intermediate",2), Map.entry("Intermediate",3),
  Map.entry("Intermediate-Advanced",4), Map.entry("Intermediate–Advanced",4), Map.entry("Advanced",5));
 // jCOLIBRI configuration is exposed here so attribute names/weights remain explicit and reusable.
 public NNConfig createJColibriConfiguration(){
  NNConfig c=new NNConfig(); c.setDescriptionSimFunction(new Average());
  c.setWeight(new Attribute("playerLevel",RacquetCase.class),0.20); c.setWeight(new Attribute("primaryStyle",RacquetCase.class),0.18);
  c.setWeight(new Attribute("armRisk",RacquetCase.class),0.15); c.setWeight(new Attribute("weight",RacquetCase.class),0.12);
  c.setWeight(new Attribute("powerClass",RacquetCase.class),0.08); c.setWeight(new Attribute("spinClass",RacquetCase.class),0.08); c.setWeight(new Attribute("controlClass",RacquetCase.class),0.08);
  c.setWeight(new Attribute("headSize",RacquetCase.class),0.05); c.setWeight(new Attribute("swingweight",RacquetCase.class),0.03); c.setWeight(new Attribute("balanceMm",RacquetCase.class),0.02); c.setWeight(new Attribute("stringPattern",RacquetCase.class),0.01); return c;
 }
 public double similarity(PlayerProfile q,RacquetCase c){
    double s=0,w=0; s+=.20*level(q.getPlayerLevel(),c.getPlayerLevel());w+=.20; s+=.18*style(q.getPrimaryStyle(),c.getPrimaryStyle());w+=.18;
    s+=.15*arm(q.getArmSensitivity(),c.getArmRisk());w+=.15; if(c.getWeight()!=null&&q.getPreferredWeight()!=null){s+=.12*num(q.getPreferredWeight(),c.getWeight(),50);w+=.12;}
    if(known(q.getPowerPreference())&&known(c.getPowerClass())){s+=.08*ordinal(q.getPowerPreference(),c.getPowerClass());w+=.08;}
    if(known(q.getSpinPreference())&&known(c.getSpinClass())){s+=.08*ordinal(q.getSpinPreference(),c.getSpinClass());w+=.08;}
    if(known(q.getControlPreference())&&known(c.getControlClass())){s+=.08*ordinal(q.getControlPreference(),c.getControlClass());w+=.08;}
  if(c.getHeadSize()!=null&&q.getPreferredHeadSize()!=null){s+=.05*num(q.getPreferredHeadSize(),c.getHeadSize(),15);w+=.05;} return w==0?0:s/w;
 }
 private double num(double a,double b,double tol){return Math.max(0,1-Math.abs(a-b)/tol);} private double level(String a,String b){if(!known(a)||!known(b))return .25; Integer x=LEVEL.get(a),y=LEVEL.get(b); if(x==null||y==null)return a.equalsIgnoreCase(b)?1:.25; return Math.max(0,1-Math.abs(x-y)/3.0);}
 private double ordinal(String a,String b){return num(rank(a),rank(b),2);} private int rank(String x){if(x==null)return 2; return switch(x.toLowerCase()){case "low"->1;case "high"->3;default->2;};}
 private boolean known(String value){return value!=null&&!value.isBlank()&&!"Unknown".equalsIgnoreCase(value);}
 private double arm(String sensitivity,String risk){if(!known(sensitivity)||!known(risk))return .5;if("High".equalsIgnoreCase(risk)){if("High".equalsIgnoreCase(sensitivity))return 0;if("Medium".equalsIgnoreCase(sensitivity)||"Mild".equalsIgnoreCase(sensitivity))return .6;return .8;}if("Medium".equalsIgnoreCase(risk)&&"High".equalsIgnoreCase(sensitivity))return .6;return 1;}
 private double style(String a,String b){if(!known(a)||!known(b))return .25;if(a.equalsIgnoreCase(b))return 1;Set<String> left=styleParts(a),right=styleParts(b);if(!Collections.disjoint(left,right))return .75;if(Set.of(a,b).contains("Balanced"))return .75;if(Set.of(a,b).contains("Power")&&Set.of(a,b).contains("Control"))return .2;return .55;}
 private Set<String> styleParts(String value){return new HashSet<>(Arrays.asList(value.toLowerCase(Locale.ROOT).split("[-/]")));}
}
