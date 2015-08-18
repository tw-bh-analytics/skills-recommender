package br.com.tw.bh.analytics.recommendation;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.TreeSet;
import java.util.stream.Collectors;

public class GradeRecommender {

	private final Map<String, Map<Skill, AverageRating>> averageRatingByGrade = new HashMap<>();
	private final People people;
	private final SkillRatings ratings;

	public GradeRecommender(People people, SkillRatings ratings, Skills skills) {
		this.people = people;
		this.ratings = ratings;
		ratings.forEachPerson(person -> addStatistictsForGrade(person.getGrade(), ratings.getRatingFor(person)));
	}

	private void addStatistictsForGrade(String grade, Map<Skill, Integer> ratings) {
		if (!averageRatingByGrade.containsKey(grade))
			averageRatingByGrade.put(grade, new HashMap<>());
		Map<Skill, AverageRating> stats = averageRatingByGrade.get(grade);
		for (Entry<Skill, Integer> entry : ratings.entrySet()) {
			if (!stats.containsKey(entry.getKey()))
				stats.put(entry.getKey(), new AverageRating());
			stats.get(entry.getKey()).register(entry.getValue());
		}
	}

	public List<Skill> recommendSkillsFor(int personId, int numberOfRecommendations) {
		Person person = people.get(personId);
		Map<Skill, Integer> rating = ratings.getRatingFor(person);
		Map<Skill, AverageRating> averageRating = averageRatingByGrade.get(person.getGrade());
		return recommendSkills(rating, averageRating, numberOfRecommendations);
	}

	private List<Skill> recommendSkills(Map<Skill, Integer> rating, Map<Skill, AverageRating> averageRating,
			int numberOfRecommendations) {
		Set<VariationFromAverage> variation = new TreeSet<>();
		rating.keySet().forEach(
				skill -> variation.add(new VariationFromAverage(skill, rating.get(skill), averageRating.get(skill))));
		return variation.stream().filter(v -> v.hasToImproveSkill()).limit(numberOfRecommendations)
				.map(v -> v.getSkill()).collect(Collectors.toList());
	}
}

class AverageRating {

	private double total;
	private double ammount;

	public void register(int value) {
		total += value;
		ammount++;
	}

	public double average() {
		return total / ammount;
	}
}

class VariationFromAverage implements Comparable<VariationFromAverage> {

	private final Skill skill;
	private final double variation;

	public VariationFromAverage(Skill skill, Integer rating, AverageRating averageRating) {
		this.skill = skill;
		this.variation = rating.doubleValue() - averageRating.average();
	}

	public Skill getSkill() {
		return skill;
	}

	public boolean hasToImproveSkill() {
		return variation < 0.0;
	}

	@Override
	public int compareTo(VariationFromAverage o) {
		int result = (int) (100.0 * (variation - o.variation));
		if (result == 0)
			result = skill.compareTo(o.skill);
		return result;
	}
}