package br.com.tw.bh.analytics.recommendation;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import javax.sql.DataSource;

public class FeedbackHandler {

	private final DataSource dataSource;

	public FeedbackHandler(DataSource dataSource) {
		this.dataSource = dataSource;
	}

	public void registerFeedback(int personId, int skillId, boolean like) {
		try (Connection connection = dataSource.getConnection()) {
			PreparedStatement stmt = connection
					.prepareStatement("UPDATE recommendation SET feedback = ? WHERE person_id = ? AND skill_id = ?");
			stmt.setBoolean(1, like);
			stmt.setInt(2, personId);
			stmt.setLong(3, skillId);
			stmt.executeUpdate();
		} catch (SQLException e) {
			throw new RuntimeException(e);
		}
	}

	public Boolean getFeedback(int personId, int skillId) {
		try (Connection connection = dataSource.getConnection()) {
			PreparedStatement stmt = connection
					.prepareStatement("SELECT feedback FROM recommendation WHERE person_id = ? AND skill_id = ?");
			stmt.setInt(1, personId);
			stmt.setInt(2, skillId);
			ResultSet rs = stmt.executeQuery();

			if (rs.next()) {
				boolean result = rs.getBoolean(1);
				if (rs.wasNull())
					return null;
				else
					return result;
			} else {
				return null;
			}
		} catch (SQLException e) {
			throw new RuntimeException(e);
		}
	}
}
