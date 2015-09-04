package br.com.tw.bh.analytics.recommendation;

import java.io.IOException;
import java.io.Reader;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.function.Consumer;
import java.util.stream.Collectors;

import org.apache.commons.csv.CSVParser;

public class People {

	private final Map<Long, Person> peopleById = new HashMap<>();
	private final Set<String> roles = new HashSet<>();

	public People(Reader peopleReader) {
		try {
			loadPeople(new CSVParser(peopleReader));
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

	private void loadPeople(CSVParser parser) throws IOException {
		for (String[] line : parser.getAllValues()) {
			long id = Long.valueOf(line[0]);
			Person person = new Person(id, line[1], line[2], line[3]);
			peopleById.put(id, person);
			roles.add(person.getRole());
		}
	}

	public Person get(long personId) {
		return peopleById.get(personId);
	}

	public void forEachRole(Consumer<String> action) {
		Objects.requireNonNull(action);
		for (String role : roles) {
			action.accept(role);
		}
	}

	public Collection<Person> findWithName(String name) {
		return peopleById.values().stream().filter(person -> person.matchName(name)).collect(Collectors.toList());
	}
}
