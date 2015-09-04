package br.com.tw.bh.analytics.recommendation;

import static org.junit.Assert.*;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.sql.SQLException;

import org.apache.mahout.cf.taste.common.TasteException;
import org.hsqldb.Server;
import org.hsqldb.persist.HsqlProperties;
import org.hsqldb.server.ServerAcl.AclFormatException;
import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import com.jolbox.bonecp.BoneCP;
import com.jolbox.bonecp.BoneCPConfig;

public class SkillRecommenderTest {

	private Server database;
	private BoneCP boneCP;

	@Before
	public void spinUpDataSource() {
		try {
			HsqlProperties props = new HsqlProperties();
			props.setProperty("server.database.0", "jdbc:hsqldb:mem:recommendationdb");
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

		try {
			BoneCPConfig config = new BoneCPConfig();
			config.setJdbcUrl("jdbc:hsqldb:mem:recommendationdb");
			config.setMinConnectionsPerPartition(3);
			config.setMaxConnectionsPerPartition(10);
			config.setPartitionCount(1);
			boneCP = new BoneCP(config);
		} catch (SQLException e) {
			throw new RuntimeException(e);
		}
	}

	@After
	public void shutdownDataSource() {
		boneCP.shutdown();
		database.shutdown();
	}
	
	@Test
	public void testValidRecommendation() throws IOException, TasteException {
		Skills skills = new Skills(loadReaderFor("skills.csv"));
		People people = new People(loadReaderFor("people.csv"));
		SkillRecommender recommender = new SkillRecommender(loadReaderFor("skill_ratings.csv"), skills, people, boneCP);
	}

	private Reader loadReaderFor(String file) {
		InputStream s = SkillRecommenderTest.class.getClassLoader().getResourceAsStream(file);
		return new InputStreamReader(s);
	}
}
