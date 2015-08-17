package br.com.tw.bh.analytics.recommendation;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.mahout.cf.taste.common.TasteException;
import org.apache.mahout.cf.taste.impl.neighborhood.ThresholdUserNeighborhood;
import org.apache.mahout.cf.taste.impl.recommender.GenericUserBasedRecommender;
import org.apache.mahout.cf.taste.impl.similarity.PearsonCorrelationSimilarity;
import org.apache.mahout.cf.taste.model.DataModel;
import org.apache.mahout.cf.taste.neighborhood.UserNeighborhood;
import org.apache.mahout.cf.taste.recommender.IDRescorer;
import org.apache.mahout.cf.taste.recommender.RecommendedItem;
import org.apache.mahout.cf.taste.recommender.UserBasedRecommender;
import org.apache.mahout.cf.taste.similarity.UserSimilarity;

public class PersonSkillSetSimilarityRecommender {

	private static final double NEIGHBORHOOD_THRESHOLD = 0.5;
	private final DataModel model;
	private final UserBasedRecommender recommender;
	private final Skills skills;

	public PersonSkillSetSimilarityRecommender(SkillRatings ratings, Skills skills) {
		this.model = ratings.getDataModel();
		this.skills = skills;

		try {
			UserSimilarity similarity = new PearsonCorrelationSimilarity(model);
			UserNeighborhood neighborhood = new ThresholdUserNeighborhood(NEIGHBORHOOD_THRESHOLD, similarity, model);
			recommender = new GenericUserBasedRecommender(model, neighborhood, similarity);
		} catch (TasteException e) {
			throw new RuntimeException(e);
		}
	}

	public List<Skill> recommendSkillsFor(int userId, int numberOfRecommendations) throws TasteException {
		List<RecommendedItem> recommendations = recommender.recommend(userId, numberOfRecommendations,
				new IgnoreUnderRatedRecommendationsRescorer(model, userId), true);
		List<Skill> skillsToRecommend = new ArrayList<>();
		recommendations.forEach(recommendation -> skillsToRecommend.add(skills.get(recommendation.getItemID())));
		return skillsToRecommend;
	}
}

class IgnoreUnderRatedRecommendationsRescorer implements IDRescorer {

	private final Map<Long, Float> ratingBySkillForUser;

	public IgnoreUnderRatedRecommendationsRescorer(DataModel model, int userId) {
		ratingBySkillForUser = new HashMap<>();
		try {
			model.getPreferencesFromUser(userId)
					.forEach(preference -> ratingBySkillForUser.put(preference.getItemID(), preference.getValue()));
		} catch (TasteException e) {
			throw new RuntimeException(e);
		}
	}

	@Override
	public double rescore(long skillId, double rating) {
		if (ratingBySkillForUser.containsKey(skillId)) {
			float originalRating = ratingBySkillForUser.get(skillId);
			if (originalRating >= rating)
				return 0;
		}
		return rating;
	}

	@Override
	public boolean isFiltered(long rating) {
		return rating < 2;
	}
}
