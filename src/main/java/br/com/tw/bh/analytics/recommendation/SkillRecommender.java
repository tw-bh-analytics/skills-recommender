package br.com.tw.bh.analytics.recommendation;

import java.io.File;
import java.io.IOException;
import java.util.List;

import org.apache.mahout.cf.taste.common.TasteException;

public class SkillRecommender {

	private PersonSkillSetSimilarityRecommender personSkillSetSimilarityRecommender;

	public SkillRecommender(File ratings, Skills skills, People people) throws IOException, TasteException {
		this.personSkillSetSimilarityRecommender = new PersonSkillSetSimilarityRecommender(ratings, skills);
	}

	public List<Skill> recommendSkillsFor(int personId) throws TasteException {
		return personSkillSetSimilarityRecommender.recommendSkillsFor(personId, 5);
	}
}
