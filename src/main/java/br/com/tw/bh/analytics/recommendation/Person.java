package br.com.tw.bh.analytics.recommendation;

public class Person {

	private final long id;
	private final String name;
	private final String role;
	private final String grade;

	public Person(long id, String name, String role, String grade) {
		this.id = id;
		this.name = name;
		this.role = role;
		this.grade = grade;
	}

	public long getId() {
		return id;
	}

	public String getName() {
		return name;
	}

	public String getRole() {
		return role;
	}

	public String getGrade() {
		return grade;
	}

	@Override
	public String toString() {
		return name;
	}

	public boolean hasRole(String role) {
		return this.role.equals(role);
	}

	public boolean matchName(String name) {
		return this.name.toLowerCase().startsWith(name.toLowerCase());
	}
}
