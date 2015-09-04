package br.com.tw.bh.analytics.recommendation;

import static spark.Spark.get;
import static spark.Spark.post;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.Collection;
import java.util.List;

import javax.sql.DataSource;

import org.apache.mahout.cf.taste.common.TasteException;

import com.google.gson.Gson;
import com.jolbox.bonecp.BoneCPConfig;
import com.jolbox.bonecp.BoneCPDataSource;

public class App {

	private static String dataLocation;

	public static void main(String[] args) throws IOException, TasteException {
		loadFileLocation(args);

		final Skills skills = new Skills(new FileReader(loadFile("skills.csv")));
		final People people = new People(new FileReader(loadFile("people.csv")));
		final DataSource dataSource = loadDataSource();
		final SkillRecommender recommender = new SkillRecommender(new FileReader(loadFile("skill_ratings.csv")), skills,
				people, dataSource);
		final FeedbackHandler feedbackHandler = new FeedbackHandler(people, skills, dataSource);

		Gson gson = new Gson();

		get("/person", (req, res) -> {
			String name = req.queryParams("name");
			if (name == null) {
				res.status(400);
				return "";
			}

			Collection<Person> result = people.findWithName(name);
			return gson.toJsonTree(result);
		});

		get("/person/:id/recommendation", (req, res) -> {
			int id = Integer.valueOf(req.params(":id"));

			List<Skill> recommendedSkills = recommender.recommendSkillsFor(id);
			return gson.toJsonTree(recommendedSkills);
		});

		get("/person/:id/recommendation/:skillId/evaluation", (req, res) -> {
			int id = Integer.valueOf(req.params(":id"));
			int skillId = Integer.valueOf(req.params(":skillId"));

			boolean like = feedbackHandler.getFeedback(id, skillId);

			return gson.toJson(like);
		});

		post("/person/:id/recommendation/:skillId/evaluation", (req, res) -> {
			int id = Integer.valueOf(req.params(":id"));
			int skillId = Integer.valueOf(req.params(":skillId"));
			boolean like = Boolean.valueOf(req.queryParams("like"));

			feedbackHandler.registerFeedback(id, skillId, like);

			return "";
		});
	}

	private static DataSource loadDataSource() {
		try {
			Class.forName("com.mysql.jdbc.Driver");
		} catch (Exception e) {
			throw new RuntimeException(e);
		}

		BoneCPConfig config = new BoneCPConfig();
		config.setJdbcUrl("jdbc:mysql://localhost/test");
		config.setUsername("app");
		config.setPassword("ApP!");
		config.setMinConnectionsPerPartition(3);
		config.setMaxConnectionsPerPartition(10);
		config.setPartitionCount(1);
		return new BoneCPDataSource(config);
	}

	private static File loadFile(String name) {
		return new File(dataLocation, name);
	}

	private static void loadFileLocation(String[] args) {
		if (args.length == 0)
			throw new RuntimeException("Should have the data location folder as parameter.");
		dataLocation = args[0];
	}
}
