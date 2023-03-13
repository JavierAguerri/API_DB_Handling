package com.example.apidbhandling;

import java.util.List;

import com.fasterxml.jackson.databind.JsonNode;

public class FilmDao extends SwapiElementsAbstractDao {

	public FilmDao(List<JsonNode> elements) throws Exception {
		super("films", elements);
	}

}
