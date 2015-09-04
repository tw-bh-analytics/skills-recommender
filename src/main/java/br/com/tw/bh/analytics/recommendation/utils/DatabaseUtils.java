package br.com.tw.bh.analytics.recommendation.utils;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;

import javax.sql.DataSource;

public class DatabaseUtils {

	public static void createTablesIfNotPresent(DataSource dataSource) {
		try (Connection connection = dataSource.getConnection(); Statement stmt = connection.createStatement()) {
			stmt.execute("CREATE TABLE recommendation (person_id int, index int, skill_id int)");
		} catch (SQLException e) {
			throw new RuntimeException(e);
		}
	}
}
