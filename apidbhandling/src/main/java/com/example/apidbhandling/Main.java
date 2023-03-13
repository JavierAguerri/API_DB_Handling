package com.example.apidbhandling;

import java.sql.SQLException;
import java.util.List;

import com.fasterxml.jackson.databind.JsonNode;

public class Main {
	public static void main( String[] args ) {
		List<JsonNode> allFilms = null;
		List<JsonNode> allPeople = null;
		List<JsonNode> allStarships = null;
		allPeople = SwapiAPIclient.getAllResults("people");
		allStarships = SwapiAPIclient.getAllResults("starships");
		allFilms = SwapiAPIclient.getAllResults("films");

		try {
			SwapiDBintializer.start();
			System.out.println("Database initialized successfully!");
		} catch (SQLException e) {
			System.out.println("Error initializing database.");
		}
		try {
			FilmDao filmDao = new FilmDao(allFilms);
			PeopleDao peopleDao = new PeopleDao(allPeople);
			StarshipDao starshipDao = new StarshipDao(allStarships);
			System.out.println("Films, People, Starships tables created and populated successfully!");
		} catch (Exception e) {
			System.out.println("Error initializing Films, People, Starships tables.");
		}
		try {
			ForeignKeysDao foreignKeysDaoFilmsCharacters = new ForeignKeysDao("films", "characters", "films", "people");
			ForeignKeysDao foreignKeysDaoFilmsStarships = new ForeignKeysDao("films", "starships", "films", "starships");
			ForeignKeysDao foreignKeysDaoStarshipsPilots = new ForeignKeysDao("starships", "pilots", "starships", "people");
			System.out.println("ForeignKeys tables created and populated successfully!");
		} catch (Exception e) {
			System.out.println("Error initializing ForeignKeys tables.");
		}
		SwapiMainMethodsDao swapiMainMethodsDao = SwapiMainMethodsDao.getInstance();
		System.out.println();
		try {
			// Listar todas las personas con el número de películas en las que aparece y el listado de sus títulos
			swapiMainMethodsDao.listPeopleWithFilms();
		} catch (Exception e) {
			System.out.println("Error when listing people and movies they showed up in.");
		}
		try {
			// Se mostrará el listado de películas para permitir seleccionar un grupo de ellas, una vez seleccionadas, buscar quien es la persona que conduce la nave que más veces aparece en esas películas.
			swapiMainMethodsDao.pilotMostFrequentStarshipFilms();
		} catch (Exception e) {
			System.out.println("Error when listing movies and selecting a group of them.");
		}
	}
}
