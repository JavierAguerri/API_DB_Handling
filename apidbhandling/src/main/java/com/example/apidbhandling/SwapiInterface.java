package com.example.apidbhandling;

import java.sql.SQLException;

public interface SwapiInterface {
	public void listPeopleWithFilms () throws SQLException;
	public void pilotMostFrequentStarshipFilms() throws SQLException;
}
