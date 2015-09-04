package br.com.tw.bh.analytics.recommendation;

import java.io.IOException;
import java.io.Reader;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.mahout.cf.taste.common.TasteException;

import com.jolbox.bonecp.BoneCP;

public class SkillRecommender {

	private final Map<String, PersonSkillSetSimilarityRecommender> similarityRecommendersByRole = new HashMap<>();
	private final Map<String, GradeRecommender> gradeRecommendersByRole = new HashMap<>();
	private final People people;
	private final BoneCP connectionPool;

	public SkillRecommender(Reader ratingsReader, Skills skills, People people, BoneCP connectionPool)
			throws IOException, TasteException {
		this.people = people;
		this.connectionPool = connectionPool;
		SkillRatings ratings = new SkillRatings(ratingsReader, skills, people);
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
		save(personId, recommendations);
		return recommendations;
	}

	private void save(int personId, List<Skill> recommendations) {
		try (Connection connection = connectionPool.getConnection()) {
			for (int i = 0; i < recommendations.size(); i++) {
				PreparedStatement stmt = connection
						.prepareStatement("INSERT INTO recommendation (person_id, index, skill_id) VALUES (?, ?, ?)");
				stmt.setInt(1, personId);
				stmt.setInt(2, i);
				stmt.setLong(3, recommendations.get(i).getId());
				stmt.execute();
			}
		} catch (SQLException e) {
			throw new RuntimeException(e);
		}
	}
}
