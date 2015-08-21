package br.com.tw.bh.analytics.recommendation;

import static spark.Spark.get;

import java.io.File;
import java.io.IOException;
import java.util.Collection;
import java.util.List;

import org.apache.mahout.cf.taste.common.TasteException;

import com.google.gson.Gson;

public class App {

	private static String dataLocation;

	public static void main(String[] args) throws IOException, TasteException {
		loadFileLocation(args);

		final Skills skills = new Skills(loadFile("skills.csv"));
		final People people = new People(loadFile("people.csv"));
		final SkillRecommender recommender = new SkillRecommender(loadFile("skill_ratings.csv"), skills, people);

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
