package br.com.tw.bh.analytics.recommendation;

import org.junit.Test;

import static org.junit.Assert.*;

public class IgnoreUnderRatedRecommendationsRescorerTest {

	@Test
	public void testRescoringInCaseOfValueLowerThanUserRate() {
		IgnoreUnderRatedRecommendationsRescorer rescorer = new IgnoreUnderRatedRecommendationsRescorer()
				.withSkillRating(1, 5);
		assertEquals(0, rescorer.rescore(1, 4), 0.1);
	}

	@Test
	public void testNotRescoringInCaseOfValueLowerThanUserRate() {
		IgnoreUnderRatedRecommendationsRescorer rescorer = new IgnoreUnderRatedRecommendationsRescorer()
				.withSkillRating(1, 3);
		assertEquals(4, rescorer.rescore(1, 4), 0.1);
	}
}
