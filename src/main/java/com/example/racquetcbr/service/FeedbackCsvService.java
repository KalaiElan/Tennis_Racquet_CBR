package com.example.racquetcbr.service;

import com.example.racquetcbr.model.PlayerProfile;
import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;
import java.nio.ByteBuffer;
import java.nio.charset.CharacterCodingException;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.charset.CodingErrorAction;
import java.nio.file.Files;
import java.nio.file.FileSystemException;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.nio.file.StandardOpenOption;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVPrinter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class FeedbackCsvService {
 private static final String[] RECOMMENDATION_HEADER = {"timestamp", "racquet_slug", "rank", "vote", "reason",
     "age_group", "player_level", "primary_style", "arm_sensitivity", "power_preference", "spin_preference",
     "control_preference", "preferred_weight", "preferred_head_size", "preferred_year", "brand_preference", "recommendation_count"};
 private static final List<String> LEGACY_HEADER = List.of("timestamp", "racquet_slug", "vote", "reason");
 private static final String[] APP_HEADER = {"timestamp", "first_name", "last_name", "rating", "comment"};
 private static final List<String> LEGACY_APP_HEADER = List.of("timestamp", "rating", "comment");
 private final Path directory;
 private final RacquetCsvRepository racquets;

 public record FeedbackTable(List<String> columns, List<List<String>> rows) {}

 public FeedbackCsvService(@Value("${feedback.csv.directory:data}") String directory, RacquetCsvRepository racquets) {
  this.directory = Path.of(directory);
  this.racquets = racquets;
 }

 public FeedbackTable appFeedback() throws IOException {
  return read("app-feedback.csv", List.of(APP_HEADER), LEGACY_APP_HEADER);
 }

 public FeedbackTable recommendationFeedback() throws IOException {
  return read("recommendation-feedback.csv", List.of(RECOMMENDATION_HEADER), LEGACY_HEADER);
 }

 private synchronized FeedbackTable read(String filename, List<String> columns, List<String> legacy) throws IOException {
  Path file = directory.resolve(filename);
  if (Files.notExists(file) || Files.size(file) == 0) return new FeedbackTable(columns, List.of());
  try (Reader reader = openCsvReader(file);
    CSVParser parser = CSVFormat.DEFAULT.builder().setHeader().setSkipHeaderRecord(true).build().parse(reader)) {
   List<String> actual = parser.getHeaderNames();
   if (!actual.equals(columns) && !actual.equals(legacy)) throw new IOException("Unrecognized feedback CSV header");
   List<List<String>> rows = new ArrayList<>();
   for (var record : parser) {
  List<String> row = new ArrayList<>();
  for (String column : columns) row.add(actual.contains(column) ? record.get(column) : "");
  rows.add(row);
   }
   Collections.reverse(rows);
   return new FeedbackTable(columns, rows);
  }
 }

 public void saveRecommendation(String slug, int rank, String vote, String reason, PlayerProfile profile) throws IOException {
  if (slug == null || racquets.findAll().stream().noneMatch(r -> slug.equals(r.getSlug()))) throw new IllegalArgumentException("Unknown racquet");
  if (rank < 1 || rank > 10 || profile == null) throw new IllegalArgumentException("Invalid recommendation context");
  if (!"up".equals(vote) && !"down".equals(vote)) throw new IllegalArgumentException("Invalid vote");
  String note = clean(reason);
  append("recommendation-feedback.csv", RECOMMENDATION_HEADER,
      Instant.now().toString(), slug, String.valueOf(rank), vote, note,
      text(profile.getAgeGroup()), text(profile.getPlayerLevel()), text(profile.getPrimaryStyle()), text(profile.getArmSensitivity()),
      text(profile.getPowerPreference()), text(profile.getSpinPreference()), text(profile.getControlPreference()),
      text(profile.getPreferredWeight()), text(profile.getPreferredHeadSize()), text(profile.getPreferredYear()),
      text(profile.getBrandPreference()), text(profile.getRecommendationCount()));
 }

 private String text(Object value) { return value == null ? "" : clean(value.toString()); }

 public void saveApp(String firstName, String lastName, Integer rating, String comment) throws IOException {
  if (rating == null || rating < 1 || rating > 5) throw new IllegalArgumentException("Rating must be between 1 and 5");
  String givenName = name(firstName), familyName = name(lastName);
  append("app-feedback.csv", APP_HEADER,
    Instant.now().toString(), givenName, familyName, rating.toString(), clean(comment));
 }

 private String name(String value) {
  if (value == null || value.isBlank() || value.length() > 100 || value.contains("\n") || value.contains("\r"))
   throw new IllegalArgumentException("First and last name must be 1-100 characters");
  return clean(value);
 }

 private String clean(String text) {
  if (text == null) return "";
  if (text.length() > 1000) throw new IllegalArgumentException("Feedback is too long");
  String value = text.trim();
  return !value.isEmpty() && "=+-@\t\r\n".indexOf(value.charAt(0)) >= 0 ? "'" + value : value;
 }

 private synchronized void append(String filename, String[] header, String... values) throws IOException {
  Files.createDirectories(directory);
  Path file = directory.resolve(filename);
  if (Files.exists(file) && Files.size(file) > 0) {
   if ("recommendation-feedback.csv".equals(filename)) upgradeRecommendationFile(file);
   if ("app-feedback.csv".equals(filename)) upgradeAppFile(file);
  }
  boolean needsHeader = Files.notExists(file) || Files.size(file) == 0;
  try (CSVPrinter printer = new CSVPrinter(Files.newBufferedWriter(file, StandardCharsets.UTF_8,
      StandardOpenOption.CREATE, StandardOpenOption.APPEND), CSVFormat.DEFAULT)) {
   if (needsHeader) printer.printRecord((Object[]) header);
   printer.printRecord((Object[]) values);
  }
 }

 private void upgradeRecommendationFile(Path file) throws IOException {
    Path replacement = null;
  try (Reader reader = openCsvReader(file);
      CSVParser parser = CSVFormat.DEFAULT.builder().setHeader().setSkipHeaderRecord(true).build().parse(reader)) {
   if (parser.getHeaderNames().equals(List.of(RECOMMENDATION_HEADER))) return;
   if (!parser.getHeaderNames().equals(LEGACY_HEADER)) throw new IOException("Unrecognized recommendation feedback CSV header");
     replacement = Files.createTempFile(directory, "recommendation-feedback-", ".csv");
    try (CSVPrinter printer = new CSVPrinter(Files.newBufferedWriter(replacement, StandardCharsets.UTF_8), CSVFormat.DEFAULT)) {
     printer.printRecord((Object[]) RECOMMENDATION_HEADER);
     for (var record : parser) {
      printer.printRecord(record.get("timestamp"), record.get("racquet_slug"), "", record.get("vote"), record.get("reason"),
          "", "", "", "", "", "", "", "", "", "", "", "");
     }
    }
  }
  if (replacement != null) replaceFile(replacement, file);
 }

 private void upgradeAppFile(Path file) throws IOException {
  Path replacement = null;
  try (Reader reader = openCsvReader(file);
    CSVParser parser = CSVFormat.DEFAULT.builder().setHeader().setSkipHeaderRecord(true).build().parse(reader)) {
   if (parser.getHeaderNames().equals(List.of(APP_HEADER))) return;
   if (!parser.getHeaderNames().equals(LEGACY_APP_HEADER)) throw new IOException("Unrecognized app feedback CSV header");
   replacement = Files.createTempFile(directory, "app-feedback-", ".csv");
   try (CSVPrinter printer = new CSVPrinter(Files.newBufferedWriter(replacement, StandardCharsets.UTF_8), CSVFormat.DEFAULT)) {
  printer.printRecord((Object[]) APP_HEADER);
  for (var record : parser) {
   printer.printRecord(record.get("timestamp"), "", "", record.get("rating"), record.get("comment"));
  }
   }
  }
  if (replacement != null) {
   replaceFile(replacement, file);
  }
 }

 private void replaceFile(Path replacement, Path file) throws IOException {
  try { Files.move(replacement, file, StandardCopyOption.REPLACE_EXISTING); }
  catch (FileSystemException locked) {
   try {
    Files.copy(replacement, file, StandardCopyOption.REPLACE_EXISTING);
    Files.delete(replacement);
   } catch (IOException failure) {
    throw new IOException("Could not upgrade feedback CSV; replacement retained at " + replacement, failure);
   }
  }
 }

   private Reader openCsvReader(Path file) throws IOException {
    byte[] bytes = Files.readAllBytes(file);
    int offset = 0;
    Charset charset = StandardCharsets.UTF_8;
    if (bytes.length >= 3 && (bytes[0] & 0xFF) == 0xEF && (bytes[1] & 0xFF) == 0xBB && (bytes[2] & 0xFF) == 0xBF) {
     offset = 3;
    } else if (bytes.length >= 2 && (bytes[0] & 0xFF) == 0xFF && (bytes[1] & 0xFF) == 0xFE) {
     charset = StandardCharsets.UTF_16LE;
     offset = 2;
    } else if (bytes.length >= 2 && (bytes[0] & 0xFF) == 0xFE && (bytes[1] & 0xFF) == 0xFF) {
     charset = StandardCharsets.UTF_16BE;
     offset = 2;
    }
    try {
     String text = charset.newDecoder().onMalformedInput(CodingErrorAction.REPORT)
       .onUnmappableCharacter(CodingErrorAction.REPORT).decode(ByteBuffer.wrap(bytes, offset, bytes.length - offset)).toString();
     return new StringReader(text);
    } catch (CharacterCodingException invalidUtf) {
     return new StringReader(Charset.forName("windows-1252").decode(ByteBuffer.wrap(bytes)).toString());
    }
   }
}