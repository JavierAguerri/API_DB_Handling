package com.example.apidbhandling;

import java.util.List;

import com.fasterxml.jackson.databind.JsonNode;

public class StarshipDao extends SwapiElementsAbstractDao {

	public StarshipDao(List<JsonNode> elements) throws Exception {
		super("starships", elements);
	}

}
