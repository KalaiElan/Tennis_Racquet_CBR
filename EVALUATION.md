# Success@3 Threshold Evaluation

**Run date:** 2026-10-23  
**Average Success Percentage (Success@3 threshold):** **97.02%** (1,140 / 1,175 profiles)

## Criterion

A profile is counted as successful when at least one of its three highest-ranked racquet recommendations has a displayed similarity score **greater than 90%**. Recommendations are ranked by score, so this is equivalent to checking whether the highest-ranked eligible recommendation exceeds 90%.

## Method

- Used leave-one-case-out retrieval over the 1,251 racquet records in `src/main/resources/data/racquets1.csv`.
- Built a synthetic query from each held-out record whose player-level and primary-style values are available in the web form, yielding 1,175 evaluated profiles.
- Excluded the held-out record from that fold's candidate pool, then applied the application's eligibility filters and similarity weights from [JColibriSimilarityService.java](src/main/java/com/example/racquetcbr/service/JColibriSimilarityService.java) and [RecommendationService.java](src/main/java/com/example/racquetcbr/service/RecommendationService.java).
- Used the application's one-decimal displayed score and the strict `> 90` threshold.

## Interpretation

This is a **synthetic similarity-threshold hit rate**, not a measure of expert- or user-validated recommendation success. The query profiles are derived from racquet records rather than real player assessments, and similar or duplicate racquets may still be present in a fold after the exact held-out row is removed. These factors can make the result optimistic.

The dataset does not currently contain profile-specific expert approvals or user outcome feedback. To measure real Success@3, collect those labels for player profiles and evaluate whether at least one approved or successful racquet appears in each profile's top three.