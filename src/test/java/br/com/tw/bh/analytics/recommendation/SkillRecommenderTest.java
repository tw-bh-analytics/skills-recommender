package br.com.tw.bh.analytics.recommendation;

import static org.junit.Assert.assertEquals;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.List;

import org.apache.mahout.cf.taste.common.TasteException;
import org.junit.Test;

public class SkillRecommenderTest extends AbstractTest {

	@Test
	public void testValidRecommendation() throws IOException, TasteException {
		Skills skills = new Skills(loadReaderFor("skills.csv"));
		People people = new People(loadReaderFor("people.csv"));
		SkillRecommender recommender = new SkillRecommender(loadReaderFor("skill_ratings.csv"), skills, people,
				getDataSource());
		List<Skill> recommendation = recommender.recommendSkillsFor(1);
		assertEquals(4, recommendation.size());
	}

	private Reader loadReaderFor(String file) {
		InputStream s = SkillRecommenderTest.class.getClassLoader().getResourceAsStream(file);
		return new InputStreamReader(s);
	}
}
