package com.example.apidbhandling;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class ForeignKeysDao {
	private static final String DB_URL = System.getenv("DB_URL");
	private static final String DB_NAME = System.getenv("DB_NAME");
	private static final String DB_USER = System.getenv("DB_USER");
	private static final String DB_PASS = System.getenv("DB_PASS");

	//	private static final String DB_URL="jdbc:mysql://localhost:3306/";
	//	private static final String DB_NAME="swapiDB";
	//	private static final String DB_USER="javier";
	//	private static final String DB_PASS="kl3y-i_WMnD3CtpN@B2r-";

	private final String element1;
	private final String element2;
	private final String tableName1;
	private final String tableName2;

	public ForeignKeysDao(String element1, String element2, String tableName1, String tableName2) throws SQLException{
		this.element1 = element1;
		this.element2 = element2;
		this.tableName1 = tableName1;
		this.tableName2 = tableName2;
		createTable();
		insertForeignRelations();
	}

	private void createTable() throws SQLException {
		StringBuilder sb = new StringBuilder();
		sb.append("CREATE TABLE IF NOT EXISTS " + element1 + "_" + element2 + " (" + element1 + "_id INT NOT NULL, "+ element2 + "_id INT NOT NULL, ");
		sb.append("created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP, updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP, ");
		sb.append("PRIMARY KEY (" + element1 + "_id, " + element2 + "_id),");
		sb.append("CONSTRAINT fk_" + element1 + "_" + element2 + " FOREIGN KEY (" + element1 + "_id) REFERENCES " + tableName1 + "(id), ");
		sb.append("CONSTRAINT fk_" + element2 + "_" + element1 + " FOREIGN KEY (" + element2 + "_id) REFERENCES " + tableName2 + "(id))");
		String createTableString = sb.toString();
		//		System.out.println("createTableString: " + createTableString);
		try (Connection connection = DriverManager.getConnection(DB_URL + DB_NAME, DB_USER, DB_PASS);
				PreparedStatement statement = connection.prepareStatement(createTableString)) {
			statement.executeUpdate(createTableString);
		} catch (SQLException e) {
			e.printStackTrace();
			throw e;
		}
	}

	private void insertForeignRelations() throws SQLException {
		StringBuilder sb = new StringBuilder();
		sb.append("INSERT INTO " + element1 + "_" + element2 + " (" + element1 + "_id, "+ element2 + "_id) ");
		sb.append("SELECT " + tableName1 + ".id, "+ tableName2 + ".id ");
		sb.append("FROM " + tableName1 + ", "+ tableName2 + ", ");
		sb.append("JSON_TABLE("+tableName2+"."+element1+", '$[*]' COLUMNS("+element1+" VARCHAR(255) PATH '$')) AS "+tableName1+"_table ");
		sb.append("WHERE "+tableName1+".url = "+tableName1+"_table."+element1+";");
		String insertForeignRelationsString = sb.toString();
		//		System.out.println("insertForeignRelationsString: " + insertForeignRelationsString);
		try (Connection connection = DriverManager.getConnection(DB_URL + DB_NAME, DB_USER, DB_PASS);
				PreparedStatement statement = connection.prepareStatement(insertForeignRelationsString)) {
			statement.executeUpdate(insertForeignRelationsString);
		} catch (SQLException e) {
			e.printStackTrace();
			throw e;
		}
	}

	public void deleteTable() throws SQLException {
		String deleteTableString = "DROP TABLE IF EXISTS " + element1 + "_" + element2;
		try (Connection connection = DriverManager.getConnection(DB_URL + DB_NAME, DB_USER, DB_PASS);
				PreparedStatement statement = connection.prepareStatement(deleteTableString)) {
			statement.executeUpdate(deleteTableString);
		} catch (SQLException e) {
			e.printStackTrace();
			throw e;
		}
	}
}
