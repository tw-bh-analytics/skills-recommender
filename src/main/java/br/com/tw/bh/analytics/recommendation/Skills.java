package br.com.tw.bh.analytics.recommendation;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.csv.CSVParser;

public class Skills {

	private final Map<Long, String> skillNamesById;

	public Skills(File skillsFile) {
		try {
			this.skillNamesById = loadSkills(new CSVParser(new FileReader(skillsFile)));
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

	private Map<Long, String> loadSkills(CSVParser parser) throws IOException {
		Map<Long, String> result = new HashMap<>();
		for (String[] line : parser.getAllValues()) {
			result.put(Long.valueOf(line[1]), line[0]);
		}
		return result;
	}

	public String getNameFor(long skillId) {
		return skillNamesById.get(skillId);
	}
}
