package com.revolut.codechallenge.artembogdan.dao.impl;

import static org.junit.Assert.*;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.revolut.codechallenge.artembogdan.dao.DatabaseException;

public class InmemoryPostgressDatabaseTest {
	
	private InmemoryPostgressDatabase database;

	@Before
	public void setUp() throws Exception {
		this.database = new InmemoryPostgressDatabase();
	}

	@After
	public void tearDown() throws Exception {
		this.database.close();
	}

	@Test
	public void test() throws DatabaseException, SQLException {
		database.transactionalOperation(() -> {
			Connection conn = database.getConnection();

			assertNotNull(conn);
			PreparedStatement ps = conn.prepareStatement(
					"INSERT INTO account(customer_name, user_name) VALUES(?, ?)", 
					Statement.RETURN_GENERATED_KEYS);
			
			for ( int i=0; i<100; i++ ) {
				ps.setString(1, "User #"+i);
				ps.setString(2, "artem");
				ps.execute();
			}
			
			conn.commit();
			Statement st = conn.createStatement();
			ResultSet rs = st.executeQuery("SELECT count(*) from account");
			assertTrue(rs.next());
			assertEquals(100, rs.getInt(1));
			assertFalse(rs.next());

			return null;
		});
	}

}
