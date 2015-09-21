package br.com.tw.bh.analytics.recommendation;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.io.IOException;

import org.apache.mahout.cf.taste.common.TasteException;
import org.junit.Before;
import org.junit.Test;

public class FeedbackHandlerTest extends AbstractTest {

	private static final int RECOMMENDED_SKILL = 13;
	private static final int USER_WITH_RECOMMENDATION = 1;
	private static final int USER_WITHOUT_RECOMMENDATION = 2;

	@Before
	public void createRecommendation() throws IOException, TasteException {
		Skills skills = new Skills(loadReaderFor("skills.csv"));
		People people = new People(loadReaderFor("people.csv"));
		SkillRecommender recommender = new SkillRecommender(loadReaderFor("skill_ratings.csv"), skills, people,
				getDataSource());
		recommender.recommendSkillsFor(USER_WITH_RECOMMENDATION);
	}

	@Test
	public void testGetUnexistantFeedback() throws IOException, TasteException {
		FeedbackHandler feedbackHandler = new FeedbackHandler(this.getDataSource());
		Boolean feedback = feedbackHandler.getFeedback(USER_WITHOUT_RECOMMENDATION, RECOMMENDED_SKILL);
		assertNull(feedback);
	}

	@Test
	public void testRegisterAndGetFeedback() throws IOException, TasteException {
		FeedbackHandler feedbackHandler = new FeedbackHandler(this.getDataSource());
		Boolean feedback = feedbackHandler.getFeedback(USER_WITH_RECOMMENDATION, RECOMMENDED_SKILL);
		assertNull(feedback);

		feedbackHandler.registerFeedback(USER_WITH_RECOMMENDATION, RECOMMENDED_SKILL, true);
		feedback = feedbackHandler.getFeedback(USER_WITH_RECOMMENDATION, RECOMMENDED_SKILL);
		assertTrue(feedback);

		feedbackHandler.registerFeedback(USER_WITH_RECOMMENDATION, RECOMMENDED_SKILL, false);
		feedback = feedbackHandler.getFeedback(USER_WITH_RECOMMENDATION, RECOMMENDED_SKILL);
		assertFalse(feedback);
	}
}
