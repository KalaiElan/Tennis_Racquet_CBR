# Tennis Racquet CBR Recommender

Maven application using Spring Boot 3, Java 17, Thymeleaf and jCOLIBRI 3.2. The supplied `racquets1.csv` is bundled under `src/main/resources/data`.

## Run

```bash
mvn clean spring-boot:run
```

Open `http://localhost:8080`.

## Feedback storage

Recommendation thumbs up/down (with an optional reason) append to `data/recommendation-feedback.csv`, including the submitted profile fields and the racquet's rank in separate columns. Existing rows are retained with blank values for the new columns.
App usefulness ratings from 1 to 5 include required first and last names and an optional comment in `data/app-feedback.csv`. Existing rows are retained with blank names. Treat this file as personal data and restrict access to it.
Both files are created on the first submission, separate from the bundled racquet dataset.
Set `FEEDBACK_CSV_DIRECTORY` to change the output directory. Do not serve this directory as static content.

For Docker, mount a named volume so feedback survives container replacement:

```bash
docker build -t tennis-racquet-cbr .
docker run --rm -p 8081:8080 -v racquet-feedback:/app/data tennis-racquet-cbr
```

CSV files are suitable for a single app instance; concurrent containers need shared transactional storage instead.

## Admin dashboard

The admin password defaults to the `ADMIN_PASSWORD` value in `src/main/resources/application.properties`. `ADMIN_USERNAME` defaults to `admin`. Open `/admin/app` for app ratings and `/admin/recommendations` for racquet feedback; your browser will request HTTP Basic credentials. The dashboard reads both CSV files without modifying them, including older rows with fewer columns.

Start the app with:

```powershell
java -jar target/tennis-racquet-cbr-1.0.0.jar --server.port=8081
```

For deployment, override the file value with an `ADMIN_PASSWORD` environment variable from your secret manager. The value in the properties file is plaintext and can be exposed if the file or a built image is shared; remove it from tracked configuration and rotate it before production use. Do not put the password directly in a Docker command.

Use HTTPS when accessing the admin dashboard over a network. Keep the CSV directory and credentials private; Basic authentication alone does not encrypt traffic. For Docker, pass the environment variables from your deployment's secret configuration, alongside the persistent `/app/data` volume.

## Design

1. Loads and normalizes CSV cases at startup.
2. Collects a player profile through Thymeleaf.
3. Applies age and arm-safety eligibility filters.
4. Uses an explicit jCOLIBRI `NNConfig` plus a null-safe weighted similarity model.
5. Re-ranks eligible cases and explains the requested number of recommendations (1–10).

The explicit scorer mirrors the `NNConfig` weights because the player query and racquet description have different Java shapes. This keeps the web form natural while retaining jCOLIBRI's case representation and configuration model.

## Important dataset notes

- The duplicate `string_pattern` header is tolerated by Apache Commons CSV.
- Blank numeric values become `null`, never zero.
- Review suspicious source classifications before production use.
- Recommendation output is informational; players with active arm pain should consult a qualified clinician or racquet technician.
