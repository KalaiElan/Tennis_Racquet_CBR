# Tennis Racquet CBR Recommender

Maven application using Spring Boot 3, Java 17, Thymeleaf and jCOLIBRI 3.2. The supplied `racquets1.csv` is bundled under `src/main/resources/data`.

## Run

```bash
mvn clean spring-boot:run
```

Open `http://localhost:8080`.

## Design

1. Loads and normalizes CSV cases at startup.
2. Collects a player profile through Thymeleaf.
3. Applies age and arm-safety eligibility filters.
4. Uses an explicit jCOLIBRI `NNConfig` plus a null-safe weighted similarity model.
5. Re-ranks eligible cases and explains the top three.

The explicit scorer mirrors the `NNConfig` weights because the player query and racquet description have different Java shapes. This keeps the web form natural while retaining jCOLIBRI's case representation and configuration model.

## Important dataset notes

- The duplicate `string_pattern` header is tolerated by Apache Commons CSV.
- Blank numeric values become `null`, never zero.
- Review suspicious source classifications before production use.
- Recommendation output is informational; players with active arm pain should consult a qualified clinician or racquet technician.
