# LOOCV Average Similarity of Top Three Recommendations

**Run date:** 2026-10-24  
**LOOCV folds:** 1,251  
**Top-three recommendations evaluated:** 3,753  
**Mean top-three similarity per request:** **97.47%**

## Metric

For each held-out case, the application ranked eligible cases by its displayed similarity percentage and returned the top three. The request-level score is the arithmetic mean of those three displayed percentages. The reported result is the arithmetic mean of the request-level scores across all 1,251 LOOCV folds:

```text
requestMean(i) = (score(i, 1) + score(i, 2) + score(i, 3)) / 3
LOOCV mean = sum(requestMean(i)) / 1,251
```

All scores were rounded to the application's displayed precision of one decimal place before averaging.

## Results

| Recommendation rank | Mean displayed similarity |
| --- | ---: |
| 1 | 97.86% |
| 2 | 97.48% |
| 3 | 97.09% |
| Mean of each request's top three | **97.47%** |

All 1,251 folds returned three eligible recommendations, so the calculation includes 3,753 recommendations. Since every request contributes exactly three scores, averaging the 1,251 request-level means gives the same result as pooling all 3,753 recommendation scores.

## LOOCV Procedure

- Used all 1,251 usable records in `src/main/resources/data/racquets1.csv` as held-out queries, one fold per record.
- Excluded the held-out record itself from that fold's candidate pool.
- Built each synthetic query from the held-out case's available age group, player level, primary style, arm-risk, weight, head size, power, spin, and control fields. Brand preference was blank.
- Applied the application's junior/non-junior compatibility and high-arm-risk eligibility filters.
- Ranked remaining candidates with the application's weighted similarity scorer and used the top three, matching the app's K=3 recommendation count.

## Interpretation and Limitations

The **97.47% is average similarity, not measured accuracy**. The case base has no expert-approval or player-outcome labels, so the score cannot establish that a recommendation is correct, suitable, or successful for a player. The queries are synthesized from racquet cases rather than real player profiles. Similar or duplicate racquets can remain in the candidate pool after the exact held-out record is removed, which may make the result optimistic.
