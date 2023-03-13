package com.example.apidbhandling;

import java.io.FileReader;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

public abstract class SwapiElementsAbstractDao {
	private static final String DB_URL = System.getenv("DB_URL");
	private static final String DB_NAME = System.getenv("DB_NAME");
	private static final String DB_USER = System.getenv("DB_USER");
	private static final String DB_PASS = System.getenv("DB_PASS");

	//	private static final String DB_URL="jdbc:mysql://localhost:3306/";
	//	private static final String DB_NAME="swapiDB";
	//	private static final String DB_USER="javier";
	//	private static final String DB_PASS="kl3y-i_WMnD3CtpN@B2r-";

	private static final String FIELDS_FOLDER_PATH = "src/config/";
	private final LinkedHashMap<String, String> fieldPropsMap = new LinkedHashMap<>();
	private final String element;
	private final List<JsonNode> elements;

	public SwapiElementsAbstractDao(String element, List<JsonNode> elements) throws Exception {
		this.element = element;
		this.elements = elements;
		loadFieldProps();
		createTable();
		insertData();
	}

	private void loadFieldProps() throws IOException {
		ObjectMapper objectMapper = new ObjectMapper();
		String elementFieldPropsPath = FIELDS_FOLDER_PATH + element + "Fields.json"; 
		try {
			FileReader reader = new FileReader(elementFieldPropsPath);
			JsonNode rootNode = objectMapper.readTree(reader);
			for (JsonNode fieldNode : rootNode) {
				String name = fieldNode.get("name").asText();
				String typeSQL = fieldNode.get("typeSQL").asText();
				fieldPropsMap.put(name, typeSQL);
			}
		} catch (IOException e) {
			e.printStackTrace();
			throw e;
		}
	}

	private void createTable() throws SQLException {
		StringBuilder sb = new StringBuilder();
		sb.append("CREATE TABLE IF NOT EXISTS " + element + " (id INT NOT NULL AUTO_INCREMENT PRIMARY KEY, ");
		for (Map.Entry<String, String> entry : fieldPropsMap.entrySet()) {
			String name = entry.getKey();
			String typeSQL = entry.getValue();
			sb.append(name + " " + typeSQL + ",");
		}
		sb.deleteCharAt(sb.length() - 1);
		sb.append(")");
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

	public void insertData() throws SQLException {
		try (Connection connection = DriverManager.getConnection(DB_URL + DB_NAME, DB_USER, DB_PASS)) {
			StringBuilder sb = new StringBuilder();
			sb.append("INSERT INTO " + element + "(");
			for (Entry<String, String> entry : fieldPropsMap.entrySet()) {
				sb.append(entry.getKey() + ",");
			}
			sb.deleteCharAt(sb.length() - 1);
			sb.append(") SELECT j.* FROM JSON_TABLE(?, '$' COLUMNS (");
			for (Entry<String, String> entry : fieldPropsMap.entrySet()) {
				sb.append(entry.getKey() + " " + entry.getValue() + " PATH \"$."+entry.getKey() + "\",");
			}
			sb.deleteCharAt(sb.length() - 1);
			sb.append(")) AS j");
			//			System.out.println(sb.toString());
			PreparedStatement preparedStatement = connection.prepareStatement(sb.toString());
			for (JsonNode element : elements) {
				preparedStatement.setString(1, element.toString());
				preparedStatement.addBatch();
				//				System.out.println(element.toString());
			}

			preparedStatement.executeBatch();
		} catch (SQLException e) {
			e.printStackTrace();
			throw e;
		}
	}
}
