# LOOCV Similarity Coverage at a 95% Threshold

**Run date:** 2026-10-23  
**Threshold:** Strictly greater than 95% displayed similarity  
**Case-base coverage:** **89.21%** (1,116 / 1,251 folds)

## Criterion

This Leave-One-Out Cross-Validation (LOOCV) run used one fold per case-base record. In each fold, one case was held out and excluded from the candidate set. A fold counted as covered if at least one other eligible case scored strictly above 95% after rounding to the one-decimal percentage displayed by the application.

## Method

- Loaded all 1,251 usable records from `src/main/resources/data/racquets1.csv`.
- Built each synthetic query from the held-out case's available age group, player level, primary style, arm-risk, weight, head size, power, spin, and control values. Brand preference was left blank.
- Scored remaining candidates using the application's custom `JColibriSimilarityService` scorer, including its missing-value normalization.
- Applied the application's eligibility rules: junior/non-junior compatibility and exclusion of high-arm-risk racquets when the query's arm sensitivity is high.
- Rounded each similarity score to the application's displayed one-decimal percentage before applying the strict `> 95%` threshold.

## Results

- LOOCV folds: **1,251**
- Folds with at least one other eligible case scoring above 95%: **1,116**
- Folds without a qualifying case: **135**
- Coverage: **1,116 / 1,251 x 100 = 89.21%**

For comparison, the separate 90% threshold report found 1,146 covered folds out of 1,251 (91.61%). The stricter threshold reduces coverage by 30 folds, or about 2.40 percentage points.

## Interpretation and Limitations

This is LOOCV **similarity coverage**, not conventional predictive accuracy or expert-validated recommendation success. Each query is synthesized from a racquet case rather than a real player's profile. The threshold measures whether a sufficiently similar eligible case exists, not whether a player preferred or benefited from its recommendation. Similar or duplicate racquets may remain after the exact held-out record is removed.

The case base does not currently include profile-specific expert approvals or player outcome labels. Measuring actual recommendation success requires collecting those outcomes and evaluating them against held-out player profiles.
