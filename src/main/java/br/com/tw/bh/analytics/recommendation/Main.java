package br.com.tw.bh.analytics.recommendation;

import java.io.File;
import java.io.IOException;
import java.util.List;

import org.apache.mahout.cf.taste.common.TasteException;

public class Main {

	public static void main(String[] args) throws IOException, TasteException {
		Skills skills = new Skills(new File(Main.class.getClassLoader().getResource("skills.csv").getPath()));
		People people = new People(new File(Main.class.getClassLoader().getResource("people.csv").getPath()));
		SkillRecommender recommender = new SkillRecommender(
				new File(Main.class.getClassLoader().getResource("skill-ratings.csv").getPath()), skills, people);
		List<Skill> recommendedSkills = recommender.recommendSkillsFor(16902); // TODO: Put your user ID here
		System.out.println(recommendedSkills);
	}
}
