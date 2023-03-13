package com.example.apidbhandling;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Collections;
import java.util.Scanner;

public class SwapiMainMethodsDao implements SwapiInterface{
	private static final String DB_URL = System.getenv("DB_URL");
	private static final String DB_NAME = System.getenv("DB_NAME");
	private static final String DB_USER = System.getenv("DB_USER");
	private static final String DB_PASS = System.getenv("DB_PASS");

	//	private static final String DB_URL="jdbc:mysql://localhost:3306/";
	//	private static final String DB_NAME="swapiDB";
	//	private static final String DB_USER="javier";
	//	private static final String DB_PASS="kl3y-i_WMnD3CtpN@B2r-";

	private static SwapiMainMethodsDao instance = null;

	private SwapiMainMethodsDao(){}

	public static SwapiMainMethodsDao getInstance() {
		if (instance == null) {
			synchronized (SwapiMainMethodsDao.class) {
				if (instance == null) {
					instance = new SwapiMainMethodsDao();
				}
			}
		}
		return instance;
	}

	@Override
	public void listPeopleWithFilms () throws SQLException {
		System.out.println("1. List all people with the number of films they appear in and the list of their titles:\n");
		StringBuilder sb = new StringBuilder();
		sb.append("SELECT p.name, COUNT(fp.films_id) AS num_films, GROUP_CONCAT(f.title SEPARATOR ', ') AS film_titles ");
		sb.append("FROM people p ");
		sb.append("JOIN films_characters fp ON p.id = fp.characters_id ");
		sb.append("JOIN films f ON fp.films_id = f.id ");
		sb.append("GROUP BY p.id ");
		String queryString = sb.toString();
		//		System.out.println("listPeopleWithFilms queryString: " + queryString);
		try (Connection connection = DriverManager.getConnection(DB_URL + DB_NAME, DB_USER, DB_PASS);
				PreparedStatement statement = connection.prepareStatement(queryString);
				ResultSet resultSet = statement.executeQuery()) {
			while (resultSet.next()) {
				String name = resultSet.getString("name");
				int numFilms = resultSet.getInt("num_films");
				String filmTitles = resultSet.getString("film_titles");
				System.out.println(name + " appeared in " + numFilms + " films: " + filmTitles);
			}
			System.out.println("----------- \n");
		} catch (SQLException e) {
			e.printStackTrace();
			throw e;
		}
	}

	@Override
	public void pilotMostFrequentStarshipFilms() throws SQLException {
		System.out.println("2. The list of films will be displayed to allow the selection of a group of them. Once selected, search for the person who pilots the ship that appears the most in those movies.\n");
		listTableWithFields("films", new String[]{"id", "title"}, null);
		String filmIds = handleUserInput();
		String starshipId = mostFrequentStarshipFilms(filmIds);
		System.out.println("The starship which appears the most in those movies is:");
		listTableWithFields("starships",new String[]{"id", "name"}, starshipId);
		System.out.println("The list of pilots for that starship is:");
		listPilotNamesOfStarship(starshipId);
	}

	private static void listTableWithFields(String table, String[] fields, String id) throws SQLException {
		StringBuilder sb = new StringBuilder();
		sb.append("SELECT " + String.join(",", fields) + " FROM " + table);
		if (id != null) {
			sb.append(" WHERE id = ?");
		}
		String selectFilmsString = sb.toString();
		//				System.out.println("selectFilmsString: " + selectFilmsString);
		try (Connection connection = DriverManager.getConnection(DB_URL + DB_NAME, DB_USER, DB_PASS);
				PreparedStatement statement = connection.prepareStatement(selectFilmsString)) {
			if (id != null) {
				statement.setString(1, id);
			}
			ResultSet resultSet = statement.executeQuery();
			while (resultSet.next()) {
				StringBuilder sbOutput = new StringBuilder();
				for (String field : fields) {
					String value = resultSet.getString(field);
					sbOutput.append(" " + field + ": " + value + " -");
				}
				sbOutput.deleteCharAt(sbOutput.length() - 1);
				System.out.println(sbOutput.toString());
			}
			System.out.println();
		} catch (SQLException e) {
			e.printStackTrace();
			throw e;
		}
	}

	private static String handleUserInput() {
		System.out.println("Please input the ids of all the films you want to select. Type a comma betwen them. Input example: <2,4,5>");
		System.out.println("The application will return the name of the person who pilots the starship which appears the most in those films.");
		Scanner scanner = new Scanner(System.in); 
		String filmIds = scanner.nextLine();
		scanner.close();
		System.out.println();
		return filmIds;
	}

	private static String mostFrequentStarshipFilms(String films) throws SQLException {
		String[] filmArray = films.split(",");
		StringBuilder sb = new StringBuilder();
		sb.append("SELECT id FROM starships WHERE id = (");
		sb.append("SELECT starships_id FROM films_starships WHERE films_id IN (");
		sb.append(String.join(",", Collections.nCopies(filmArray.length, "?")));
		sb.append(") GROUP BY starships_id ORDER BY COUNT(*) DESC LIMIT 1)");
		String mostFrequentStarshipString = sb.toString();
		//				System.out.println("mostFrequentStarshipString: " + mostFrequentStarshipString);
		try (Connection connection = DriverManager.getConnection(DB_URL + DB_NAME, DB_USER, DB_PASS);
				PreparedStatement statement = connection.prepareStatement(mostFrequentStarshipString)) {
			for (int i = 0; i < filmArray.length; i++) {
				statement.setString(i+1, filmArray[i]);
			}
			ResultSet resultSet = statement.executeQuery();
			if (resultSet.next()) {
				String id = resultSet.getString("id");
				return id;
			} else {
				return null;
			}
		} catch (SQLException e) {
			e.printStackTrace();
			throw e;
		}
	}

	private static void listPilotNamesOfStarship(String starshipId) throws SQLException {
		StringBuilder sb = new StringBuilder();
		sb.append("SELECT p.name FROM people p JOIN starships_pilots sp ON p.id = sp.pilots_id ");
		sb.append("WHERE sp.starships_id = ?");
		String listPilotNamesString = sb.toString();
		try (Connection connection = DriverManager.getConnection(DB_URL + DB_NAME, DB_USER, DB_PASS);
				PreparedStatement statement = connection.prepareStatement(listPilotNamesString)) {
			statement.setString(1, starshipId);
			ResultSet resultSet = statement.executeQuery();
			while (resultSet.next()) {
				String name = resultSet.getString("name");
				System.out.println(name);
			}
		} catch (SQLException e) {
			e.printStackTrace();
			throw e;
		}
	}

}
