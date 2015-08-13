package br.com.tw.bh.analytics.recommendation;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.csv.CSVParser;

public class People {

	private final Map<Long, Person> peopleById;

	public People(File peopleFile) {
		try {
			this.peopleById = loadPeople(new CSVParser(new FileReader(peopleFile)));
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

	private Map<Long, Person> loadPeople(CSVParser parser) throws IOException {
		Map<Long, Person> result = new HashMap<>();
		for (String[] line : parser.getAllValues()) {
			long id = Long.valueOf(line[0]);
			result.put(id, new Person(id, line[1], line[2], line[3]));
		}
		return result;
	}

	public Person get(long personId) {
		return peopleById.get(personId);
	}
}
