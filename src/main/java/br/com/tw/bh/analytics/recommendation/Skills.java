package br.com.tw.bh.analytics.recommendation;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.csv.CSVParser;

public class Skills {

	private final Map<Long, Skill> skillsById;

	public Skills(File skillsFile) {
		try {
			this.skillsById = loadSkills(new CSVParser(new FileReader(skillsFile)));
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

	private Map<Long, Skill> loadSkills(CSVParser parser) throws IOException {
		Map<Long, Skill> result = new HashMap<>();
		for (String[] line : parser.getAllValues()) {
			long id = Long.valueOf(line[1]);
			result.put(id, new Skill(id, line[0]));
		}
		return result;
	}

	public Skill get(long skillId) {
		return skillsById.get(skillId);
	}
}
