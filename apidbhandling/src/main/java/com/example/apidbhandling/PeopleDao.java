package com.example.apidbhandling;

import java.util.List;

import com.fasterxml.jackson.databind.JsonNode;

public class PeopleDao extends SwapiElementsAbstractDao {

	public PeopleDao(List<JsonNode> elements) throws Exception {
		super("people", elements);
	}

}
