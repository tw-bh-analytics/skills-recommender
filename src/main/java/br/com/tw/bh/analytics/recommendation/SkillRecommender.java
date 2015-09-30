package br.com.tw.bh.analytics.recommendation;

import java.io.IOException;
import java.io.Reader;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.sql.DataSource;

import org.apache.mahout.cf.taste.common.TasteException;

public class SkillRecommender {

	private final Map<String, PersonSkillSetSimilarityRecommender> similarityRecommendersByRole = new HashMap<>();
	private final Map<String, GradeRecommender> gradeRecommendersByRole = new HashMap<>();
	private final People people;
	private final DataSource connectionPool;
	private final Skills skills;

	public SkillRecommender(Reader ratingsReader, Skills skills, People people, DataSource connectionPool)
			throws IOException, TasteException {
		this.people = people;
		this.connectionPool = connectionPool;
		this.skills = skills;
		SkillRatings ratings = new SkillRatings(ratingsReader, skills, people);
		people.forEachRole(role -> similarityRecommendersByRole.put(role,
				new PersonSkillSetSimilarityRecommender(ratings.filterByRole(role), skills)));
		people.forEachRole(role -> gradeRecommendersByRole.put(role,
				new GradeRecommender(people, ratings.filterByRole(role), skills)));
	}

	public List<SkillFeedback> recommendSkillsFor(int personId) throws TasteException {
		Person person = people.get(personId);
		List<SkillFeedback> recommendations = load(personId);
		if (recommendations.isEmpty()) {
			recommendations = toFeedback(
					similarityRecommendersByRole.get(person.getRole()).recommendSkillsFor(personId, 5));
			recommendations
					.addAll(toFeedback(gradeRecommendersByRole.get(person.getRole()).recommendSkillsFor(personId, 3)));
			save(personId, recommendations);
		}
		return recommendations;
	}

	private List<SkillFeedback> toFeedback(List<Skill> skills) {
		List<SkillFeedback> result = new ArrayList<>();
		for (Skill skill : skills) {
			result.add(new SkillFeedback(skill));
		}
		return result;
	}

	private List<SkillFeedback> load(int personId) {
		try (Connection connection = connectionPool.getConnection()) {
			PreparedStatement stmt = connection.prepareStatement(
					"SELECT skill_id, feedback FROM recommendation WHERE person_id = ? ORDER BY index_");
			stmt.setInt(1, personId);
			ResultSet rs = stmt.executeQuery();

			List<SkillFeedback> recommendations = new ArrayList<>();
			while (rs.next()) {
				long skillId = rs.getLong(1);
				SkillFeedback skill = new SkillFeedback(skills.get(skillId));
				String feedback = rs.getString(2);
				if (feedback != null)
					skill.setFeedback("0".equals(feedback));
				recommendations.add(skill);
			}
			return recommendations;
		} catch (SQLException e) {
			throw new RuntimeException(e);
		}
	}

	private void save(int personId, List<SkillFeedback> recommendations) {
		try (Connection connection = connectionPool.getConnection()) {
			for (int i = 0; i < recommendations.size(); i++) {
				PreparedStatement stmt = connection.prepareStatement(
						"INSERT INTO recommendation (person_id, index_, skill_id, feedback) VALUES (?, ?, ?, null)");
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
