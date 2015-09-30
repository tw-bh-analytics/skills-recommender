package br.com.tw.bh.analytics.recommendation;

public class SkillFeedback {

	private long id;
	private String name;
	private Boolean feedback;

	public SkillFeedback(Skill skill) {
		this.id = skill.getId();
		this.name = skill.getName();
	}

	public void setFeedback(Boolean feedback) {
		this.feedback = feedback;
	}

	public long getId() {
		return id;
	}

	public String getName() {
		return name;
	}

	public Boolean getFeedback() {
		return feedback;
	}
}
