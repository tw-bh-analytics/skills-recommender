package br.com.tw.bh.analytics.recommendation;

import java.io.IOException;

import javax.sql.DataSource;

import org.hsqldb.Server;
import org.hsqldb.jdbc.JDBCDataSource;
import org.hsqldb.persist.HsqlProperties;
import org.hsqldb.server.ServerAcl.AclFormatException;
import org.junit.After;
import org.junit.Before;

import br.com.tw.bh.analytics.recommendation.utils.DatabaseUtils;

public class AbstractTest {

	private Server database;
	private DataSource dataSource;

	@Before
	public final void spinUpDataSource() {
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
	public final void shutdownDataSource() {
		database.shutdown();
	}

	public DataSource getDataSource() {
		return dataSource;
	}
}
