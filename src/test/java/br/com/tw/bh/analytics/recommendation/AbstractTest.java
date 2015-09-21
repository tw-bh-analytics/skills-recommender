package br.com.tw.bh.analytics.recommendation;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;

import javax.sql.DataSource;

import org.hsqldb.Server;
import org.hsqldb.jdbc.JDBCDataSource;
import org.hsqldb.lib.FileUtil;
import org.hsqldb.persist.HsqlProperties;
import org.hsqldb.server.ServerAcl.AclFormatException;
import org.junit.After;
import org.junit.Before;

import br.com.tw.bh.analytics.recommendation.utils.DatabaseUtils;

public class AbstractTest {

	private Server database;
	private DataSource dataSource;

	@Before
	public final void spinUpDataSource() throws IOException {
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

		FileUtil.deleteOrRenameDatabaseFiles("jdbc:hsqldb:mem:recommendation");

		JDBCDataSource hsqldbDataSource = new JDBCDataSource();
		hsqldbDataSource.setURL("jdbc:hsqldb:mem:recommendation");
		dataSource = hsqldbDataSource;
		DatabaseUtils.createTablesIfNotPresent(dataSource);
	}

	@After
	public final void shutdownDataSource() {
		database.shutdown();
	}

	protected DataSource getDataSource() {
		return dataSource;
	}

	protected Reader loadReaderFor(String file) {
		InputStream s = SkillRecommenderTest.class.getClassLoader().getResourceAsStream(file);
		return new InputStreamReader(s);
	}
}
