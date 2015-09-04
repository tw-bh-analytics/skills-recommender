package br.com.tw.bh.analytics.recommendation;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;

import javax.sql.DataSource;

import org.apache.mahout.cf.taste.common.TasteException;
import org.hsqldb.Server;
import org.hsqldb.jdbc.JDBCDataSource;
import org.hsqldb.persist.HsqlProperties;
import org.hsqldb.server.ServerAcl.AclFormatException;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import br.com.tw.bh.analytics.recommendation.utils.DatabaseUtils;

public class SkillRecommenderTest {

	private Server database;
	private DataSource dataSource;

	@Before
	public void spinUpDataSource() {
		try {
			HsqlProperties props = new HsqlProperties();
			props.setProperty("server.database.0", "jdbc:hsqldb:mem");
			props.setProperty("server.dbname.0", "recommendationdb");
			database = new org.hsqldb.Server();
			database.setProperties(props);
			database.start();
		} catch (IOException | AclFormatException e) {
			throw new RuntimeException(e);
		}

		try {
			Class.forName("com.mysql.jdbc.Driver");
		} catch (Exception e) {
			throw new RuntimeException(e);
		}

		JDBCDataSource hsqldbDataSource = new JDBCDataSource();
		hsqldbDataSource.setURL("jdbc:hsqldb:mem:recommendation");
		dataSource = hsqldbDataSource;
		DatabaseUtils.createTablesIfNotPresent(dataSource);
	}

	@After
	public void shutdownDataSource() {
		database.shutdown();
	}

	@Test
	public void testValidRecommendation() throws IOException, TasteException {
		Skills skills = new Skills(loadReaderFor("skills.csv"));
		People people = new People(loadReaderFor("people.csv"));
		SkillRecommender recommender = new SkillRecommender(loadReaderFor("skill_ratings.csv"), skills, people,
				dataSource);
		System.out.println(recommender.recommendSkillsFor(1));
	}

	private Reader loadReaderFor(String file) {
		InputStream s = SkillRecommenderTest.class.getClassLoader().getResourceAsStream(file);
		return new InputStreamReader(s);
	}
}
