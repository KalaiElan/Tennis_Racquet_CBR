# Tennis Racquet CBR Recommender

Maven application using Spring Boot 3, Java 17, Thymeleaf and jCOLIBRI 3.2. The supplied `racquets1.csv` is bundled under `src/main/resources/data`.

## Contents

- [Run](#run)
- [CBR cycle](#cbr-cycle)
- [Design](#design)
- [Feedback storage](#feedback-storage)
- [Admin dashboard](#admin-dashboard)
- [Important dataset notes](#important-dataset-notes)

## Run

```bash
mvn clean spring-boot:run
```

On this Windows machine, if `mvn` is not on your PATH, use the portable Maven download from the project directory:

```powershell
& "$env:TEMP\racquet-build-tools\apache-maven-3.9.9\bin\mvn.cmd" spring-boot:run
```

Open `http://localhost:8080`.

## CBR Cycle

Case-based reasoning commonly follows four steps: Retrieve, Reuse, Revise, and Retain. In this application:

1. **Retrieve:** Load the racquet cases from the bundled CSV, remove cases that fail the age and arm-safety rules, then compare the submitted player profile with the remaining cases. Rank them by similarity, with selected brand and year preferences included in the final score.
2. **Reuse:** Present the top 1-10 eligible racquets (as chosen by the user), with match scores, reasons, cautions, and available expert summaries and string suggestions. These cases are suggestions, not guaranteed playing outcomes.
3. **Revise:** A player can give each recommendation a thumbs up or down with an optional reason. This feedback can inform later human review, but it does not automatically revise the current result or its similarity weights.
4. **Retain:** Recommendation feedback and app ratings are saved to separate CSV files and viewable in the admin dashboard. The app does not automatically turn feedback into new racquet cases or update the bundled case base; changes to the case base require a separate review and data update.

Thus, retrieval and reuse happen during each recommendation request, while revision and retention of learned cases remain manual processes.

## Design

1. Loads and normalizes CSV cases at startup.
2. Collects a player profile through Thymeleaf.
3. Applies age and arm-safety eligibility filters.
4. Uses an explicit jCOLIBRI `NNConfig` plus a null-safe weighted similarity model.
5. Re-ranks eligible cases and explains the requested number of recommendations (1–10).

The explicit scorer mirrors the `NNConfig` weights because the player query and racquet description have different Java shapes. This keeps the web form natural while retaining jCOLIBRI's case representation and configuration model.

## Feedback storage

Recommendation thumbs up/down (with an optional reason) append to `data/recommendation-feedback.csv`, including the submitted profile fields and the racquet's rank in separate columns. Existing rows are retained with blank values for the new columns.
App usefulness ratings from 1 to 5 include required first and last names and an optional comment in `data/app-feedback.csv`. Existing rows are retained with blank names. Treat this file as personal data and restrict access to it.
Both files are created on the first submission, separate from the bundled racquet dataset.
Set `FEEDBACK_CSV_DIRECTORY` to change the output directory. Do not serve this directory as static content.

For Docker, mount a named volume so feedback survives container replacement:

```bash
docker build -t tennis-racquet-cbr .
docker run --rm -p 8080:8080 -v racquet-feedback:/app/data tennis-racquet-cbr
```

CSV files are suitable for a single app instance; concurrent containers need shared transactional storage instead.

## Admin dashboard

The admin password defaults to the `ADMIN_PASSWORD` value in `src/main/resources/application.properties`. `ADMIN_USERNAME` defaults to `admin`. Open `/admin/app` for app ratings and `/admin/recommendations` for racquet feedback; your browser will request HTTP Basic credentials. The dashboard reads both CSV files without modifying them, including older rows with fewer columns.

Start the app with:

```powershell
java -jar target/tennis-racquet-cbr-1.0.0.jar
```

For deployment, override the file value with an `ADMIN_PASSWORD` environment variable from your secret manager. The value in the properties file is plaintext and can be exposed if the file or a built image is shared; remove it from tracked configuration and rotate it before production use. Do not put the password directly in a Docker command.

Use HTTPS when accessing the admin dashboard over a network. Keep the CSV directory and credentials private; Basic authentication alone does not encrypt traffic. For Docker, pass the environment variables from your deployment's secret configuration, alongside the persistent `/app/data` volume.

## Important dataset notes

- The duplicate `string_pattern` header is tolerated by Apache Commons CSV.
- Blank numeric values become `null`, never zero.
- Review suspicious source classifications before production use.
- Recommendation output is informational; players with active arm pain should consult a qualified clinician or racquet technician.
