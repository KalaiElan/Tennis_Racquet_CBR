# Grouped 5-Fold Success@3 Evaluation

**Run date:** 2026-10-23  
**Average Success Percentage (overall):** **96.34%** (1,132 / 1,175 profiles)  
**Mean of the five fold percentages:** **96.33%**

## Criterion

A profile is counted as successful when at least one of its three highest-ranked eligible racquet recommendations has a displayed similarity score **greater than 90%**. Since recommendations are score-ranked, this is equivalent to checking whether the top recommendation exceeds 90%.

## Method

- Evaluated all 1,251 catalog cases using deterministic 5-fold cross-validation.
- Grouped records by normalized brand and model before splitting, so records sharing a brand/model stayed in the same fold. The 221 groups were shuffled with seed `20261023` and assigned round-robin to folds.
- Used the held-out cases with player-level and primary-style values available in the web form as synthetic query profiles. This produced 1,175 evaluated profiles.
- For each profile, ranked candidates from the other four folds, applying the application's eligibility rules and similarity weights from [JColibriSimilarityService.java](src/main/java/com/example/racquetcbr/service/JColibriSimilarityService.java) and [RecommendationService.java](src/main/java/com/example/racquetcbr/service/RecommendationService.java).
- Applied the strict `> 90` threshold to the application's one-decimal displayed score.

## Fold Results

| Fold | Successful profiles | Evaluated profiles | Success percentage |
| --- | ---: | ---: | ---: |
| 1 | 249 | 260 | 95.77% |
| 2 | 197 | 204 | 96.57% |
| 3 | 168 | 176 | 95.45% |
| 4 | 284 | 296 | 95.95% |
| 5 | 234 | 239 | 97.91% |
| **Overall** | **1,132** | **1,175** | **96.34%** |
| **Mean fold rate** |  |  | **96.33%** |

## Interpretation

This is a **synthetic similarity-threshold hit rate**, not expert- or user-validated recommendation success. Query profiles are generated from catalog racquet attributes rather than real player assessments. Grouping reduces leakage from exact brand/model duplicates across folds, but other similar racquets and correlated catalog entries remain. The high percentage therefore only indicates that the scorer often finds catalog items above its own 90% threshold.

The dataset has no profile-specific expert approvals or user outcome feedback. Measuring real Success@3 requires collecting those labels and evaluating held-out player profiles against them.