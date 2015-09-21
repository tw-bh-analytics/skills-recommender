package br.com.tw.bh.analytics.recommendation.utils;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import javax.sql.DataSource;

public class DatabaseUtils {

	public static void createTablesIfNotPresent(DataSource dataSource) {
		if (!tableRecommendationExists(dataSource))
			createTableRecommendation(dataSource);
	}

	private static void createTableRecommendation(DataSource dataSource) {
		try (Connection connection = dataSource.getConnection(); Statement stmt = connection.createStatement()) {
			stmt.execute("CREATE TABLE recommendation (person_id int, index int, skill_id int, feedback boolean)");
		} catch (SQLException e) {
			throw new RuntimeException(e);
		}
	}

	private static boolean tableRecommendationExists(DataSource dataSource) {
		try (Connection connection = dataSource.getConnection()) {
			PreparedStatement stmt = connection
					.prepareStatement("SELECT TABLE_NAME FROM INFORMATION_SCHEMA.SYSTEM_TABLES WHERE TABLE_NAME = ?");
			stmt.setString(1, "RECOMMENDATION");
			ResultSet rs = stmt.executeQuery();
			if (rs.next()) {
				return rs.getString(1) != null;
			} else
				return false;
		} catch (SQLException e) {
			throw new RuntimeException(e);
		}
	}
}
