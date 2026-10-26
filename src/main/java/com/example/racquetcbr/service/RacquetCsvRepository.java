package com.example.racquetcbr.service;
import com.example.racquetcbr.model.RacquetCase;
import org.apache.commons.csv.*; import org.springframework.core.io.ClassPathResource; import org.springframework.stereotype.Repository;
import jakarta.annotation.PostConstruct; import java.io.*; import java.nio.charset.StandardCharsets; import java.util.*;
@Repository
public class RacquetCsvRepository {
 private final List<RacquetCase> cases=new ArrayList<>();
 @PostConstruct void load() throws IOException {
  try(Reader in=new InputStreamReader(new ClassPathResource("data/racquets1.csv").getInputStream(), StandardCharsets.UTF_8)){
   CSVParser p=CSVFormat.DEFAULT.builder().setHeader().setSkipHeaderRecord(true).setAllowMissingColumnNames(true).build().parse(in);
   for(CSVRecord r:p){ RacquetCase x=new RacquetCase(); x.setSlug(s(r,"slug")); x.setBrand(s(r,"brand")); x.setModel(s(r,"model")); x.setYear(i(r,"year"));
    x.setWeight(d(r,"weight")); x.setSwingweight(d(r,"swingweight")); x.setRa(d(r,"ra")); x.setBalanceMm(d(r,"balance_mm")); x.setHeadSize(d(r,"head_size")); x.setStringPattern(s(r,"string_pattern"));
    x.setSummary(s(r,"summaries_expert_en")); x.setPrimaryStyle(s(r,"primary_style","Primary style")); x.setPlayerLevel(s(r,"player_level","Player level")); x.setPowerClass(s(r,"power_class","Power preference")); x.setSpinClass(s(r,"spin_class","Spin preference")); x.setControlClass(s(r,"control_class","Control preference"));
    x.setArmRisk(s(r,"comfort_arm_risk","Arm sensitivity")); x.setAgeGroup(s(r,"age_group","Age group")); x.setStringType(s(r,"recommended_string_type","Recommended string type")); if(x.getSlug()!=null&&!x.getSlug().isBlank()) cases.add(x); }
  }
 }
 private static String s(CSVRecord r,String... keys){ for(String k:keys)try {String v=r.get(k); if(v!=null&&!v.isBlank())return v.trim();}catch(Exception ignored){} return null; }
 private static Double d(CSVRecord r,String k){ try {String v=s(r,k); return v==null||v.isBlank()?null:Double.valueOf(v);}catch(Exception e){return null;} }
 private static Integer i(CSVRecord r,String k){ Double v=d(r,k); return v==null?null:v.intValue(); }
 public List<RacquetCase> findAll(){return Collections.unmodifiableList(cases);} public int size(){return cases.size();}
}
