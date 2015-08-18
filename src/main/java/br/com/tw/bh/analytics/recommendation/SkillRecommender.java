package br.com.tw.bh.analytics.recommendation;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.mahout.cf.taste.common.TasteException;

public class SkillRecommender {

	private final Map<String, PersonSkillSetSimilarityRecommender> similarityRecommendersByRole = new HashMap<>();
	private final Map<String, GradeRecommender> gradeRecommendersByRole = new HashMap<>();
	private final People people;

	public SkillRecommender(File ratingsFile, Skills skills, People people) throws IOException, TasteException {
		this.people = people;
		SkillRatings ratings = new SkillRatings(ratingsFile, skills, people);
		people.forEachRole(role -> similarityRecommendersByRole.put(role,
				new PersonSkillSetSimilarityRecommender(ratings.filterByRole(role), skills)));
		people.forEachRole(role -> gradeRecommendersByRole.put(role,
				new GradeRecommender(people, ratings.filterByRole(role), skills)));
	}

	public List<Skill> recommendSkillsFor(int personId) throws TasteException {
		Person person = people.get(personId);
		List<Skill> recommendations = similarityRecommendersByRole.get(person.getRole()).recommendSkillsFor(personId,
				5);
		recommendations.addAll(gradeRecommendersByRole.get(person.getRole()).recommendSkillsFor(personId, 3));
		return recommendations;
	}
}
