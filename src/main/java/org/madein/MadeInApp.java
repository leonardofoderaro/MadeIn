package org.madein;

import java.sql.Connection;
import java.sql.Driver;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Properties;

import org.eclipse.aether.resolution.ArtifactResult;

public class MadeInApp {

	public static void main( String[] args ) throws Exception {

		// initalize the loader
		MadeIn madein = new MadeIn();

		// install a dependency
		ArtifactResult result = madein.install("org.xerial:sqlite-jdbc:3.8.11.1");

		// we can show where it has been downloaded
		System.out.println("Artifact installed at " + result.getArtifact().getFile());

		// use it!
		try {
			Driver d = (Driver)Class.forName("org.sqlite.JDBC", false, madein.getClassLoader()).newInstance();

			Connection conn = d.connect("jdbc:sqlite:test.db", new Properties());

			String sql = "";
			Statement stmt = null;

			try {
				stmt = conn.createStatement();
				sql = "CREATE TABLE TEST (MESSAGE TEXT)"; 
				stmt.executeUpdate(sql);

				stmt.close();

			} catch (SQLException sqlex) {

			}

			sql = "insert into test(message) values('hello world!')";
			stmt.executeUpdate(sql);
			stmt.close();

			sql = "select message from test";

			ResultSet rs = stmt.executeQuery(sql);

			while (rs.next()) {
				String msg = rs.getString("message");
				System.out.println(msg);
			}

			rs.close();

			conn.close();

		} catch ( Exception e ) {
			System.err.println( e.getClass().getName() + ": " + e.getMessage() );
			e.printStackTrace();
			System.exit(0);
		}
	

	}
}
