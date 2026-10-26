# Leave-One-Out Cross-Validation (LOOCV): Similarity Coverage

**Run date:** 2026-10-23  
**Case-base coverage:** **91.61%** (1,146 / 1,251 cases)

## Criterion

LOOCV was run with one fold per case. In each fold, one case was held out and excluded from the candidate pool. The fold counted as covered if at least one other eligible case had a displayed similarity score **greater than 90%**.

## Method

- Ran 1,251 LOOCV folds, using every record in `src/main/resources/data/racquets1.csv` once as the held-out query.
- Built each synthetic query from the held-out case's available age group, player level, primary style, arm-risk, weight, head size, power, spin, and control fields. Brand preference was left blank.
- Scored candidates with the application's `JColibriSimilarityService`, including its missing-value normalization, then rounded scores to the one-decimal percentage displayed by the application.
- Applied the application's eligibility rules: junior/non-junior compatibility and exclusion of high-arm-risk racquets for queries classified as high arm sensitivity.
- Counted a case as covered when any remaining eligible candidate scored strictly above 90%.

## Results

- Cases with at least one qualifying similar case: **1,146**
- Cases without a qualifying similar case: **105**
- Coverage: **1,146 / 1,251 × 100 = 91.61%**

## Interpretation

This LOOCV result is **similarity coverage**, not predictive accuracy or expert-validated recommendation success. Each query is synthesized from a racquet case, not from a real player's profile, and the threshold measures score proximity rather than whether a recommendation worked for a player.

The earlier 97.02% result evaluated 1,175 profiles compatible with the web-form categories. This report uses every case-base row as a held-out query, so its denominator and query construction differ.