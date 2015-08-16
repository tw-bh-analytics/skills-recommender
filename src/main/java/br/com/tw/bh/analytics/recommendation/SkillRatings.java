package br.com.tw.bh.analytics.recommendation;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.commons.csv.CSVParser;
import org.apache.mahout.cf.taste.impl.model.file.FileDataModel;
import org.apache.mahout.cf.taste.model.DataModel;

public class SkillRatings {

	private final Map<Person, Map<Skill, Integer>> ratings = new HashMap<>();

	public SkillRatings(File skillRatingsFile, Skills skills, People people) {
		try {
			for (String[] line : new CSVParser(new FileReader(skillRatingsFile)).getAllValues()) {
				Person person = people.get(Long.valueOf(line[0]));
				Skill skill = skills.get(Long.valueOf(line[1]));
				int rating = Integer.valueOf(line[2]);
				if (!ratings.containsKey(person))
					ratings.put(person, new HashMap<>());
				ratings.get(person).put(skill, rating);
			}
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

	public DataModel getDataModel() {
		try {
			File tmpDataModel = File.createTempFile("skill-recommender", "data-model");
			writeRatingsTo(tmpDataModel);
			return new FileDataModel(tmpDataModel);
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

	private void writeRatingsTo(File tmpDataModel) {
		try (FileWriter w = new FileWriter(tmpDataModel)) {
			for (Entry<Person, Map<Skill, Integer>> people : ratings.entrySet()) {
				for (Entry<Skill, Integer> skills : people.getValue().entrySet()) {
					w.write(people.getKey().getId() + "," + skills.getKey().getId() + "," + skills.getValue() + "\n");
				}
			}
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}
}
