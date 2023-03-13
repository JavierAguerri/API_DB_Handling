package com.example.apidbhandling;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

public class SwapiDBintializer {
	//	private static final String DB_URL = System.getenv("DB_URL");
	//	private static final String DB_NAME = System.getenv("DB_NAME");
	//	private static final String DB_USER = System.getenv("DB_USER");
	//	private static final String DB_PASS = System.getenv("DB_PASS");

	private final static String DB_URL="jdbc:mysql://localhost:3306/";
	private final static String DB_NAME="swapiDB";
	private final static String DB_USER="javier";
	private final static String DB_PASS="kl3y-i_WMnD3CtpN@B2r-";

	public static void start() throws SQLException {
		dropDB();
		createDatabase();
	}

	private static void dropDB() throws SQLException {
		try (Connection connection = DriverManager.getConnection(DB_URL, DB_USER, DB_PASS);
				Statement statement = connection.createStatement()) {
			statement.executeUpdate("DROP DATABASE IF EXISTS " + DB_NAME);
		} catch (SQLException e) {
			e.printStackTrace();
			throw e;
		}
	}

	private static void createDatabase() throws SQLException {
		try (Connection connection = DriverManager.getConnection(DB_URL, DB_USER, DB_PASS);
				Statement statement = connection.createStatement()) {
			statement.executeUpdate("CREATE DATABASE IF NOT EXISTS " + DB_NAME);
		} catch (SQLException e) {
			e.printStackTrace();
			throw e;
		}
	}
}
