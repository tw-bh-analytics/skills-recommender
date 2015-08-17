package br.com.tw.bh.analytics.recommendation;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.mahout.cf.taste.common.TasteException;

public class SkillRecommender {

	private final Map<String, PersonSkillSetSimilarityRecommender> recommendersByRole = new HashMap<>();
	private final People people;

	public SkillRecommender(File ratingsFile, Skills skills, People people) throws IOException, TasteException {
		this.people = people;
		SkillRatings ratings = new SkillRatings(ratingsFile, skills, people);
		people.forEachRole(role -> recommendersByRole.put(role,
				new PersonSkillSetSimilarityRecommender(ratings.filterByRole(role), skills)));
	}

	public List<Skill> recommendSkillsFor(int personId) throws TasteException {
		Person person = people.get(personId);
		return recommendersByRole.get(person.getRole()).recommendSkillsFor(personId, 5);
	}
}
