package com.example.apidbhandling;

import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;

import org.apache.http.HttpEntity;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.util.EntityUtils;

public class SwapiAPIclient {
    private static final String SWAPI_BASE_URL = "https://swapi.py4e.com/api/";
    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();
    
    public static List<JsonNode> getAllResults(String resource) {
    	List<JsonNode> allResults = new ArrayList<>();
        String url = SWAPI_BASE_URL + resource;
        String nextUrl = url;
        while (nextUrl != null && !nextUrl.isEmpty()) {
            HttpGet request = new HttpGet(nextUrl);
            JsonNode response = null;
			try {
				response = sendRequest(request);
//				System.out.println(response);
			} catch (Exception e) {
				e.printStackTrace();
				System.out.println("Error when requesting: " + resource);
			}
            for (JsonNode result : response.get("results")) {
                allResults.add(result);
            }
            nextUrl = getNextUrl(response.get("next"));
        }
        return allResults;
    }

    private static JsonNode sendRequest(HttpGet request) throws Exception {
    	try {
    		HttpClient httpClient = HttpClientBuilder.create().build();
    		HttpEntity httpEntity = httpClient.execute(request).getEntity();
    		String responseString = EntityUtils.toString(httpEntity);
    		return OBJECT_MAPPER.readTree(responseString);   		
    	} catch (Exception e) {
    		e.printStackTrace();
    		throw e;
    	}
    }

    private static String getNextUrl(JsonNode nextJson) {
        if (nextJson == null || nextJson.isNull()) {
            return null;
        }
        return nextJson.asText();
    }
    
    public static void prettyPrint(List<JsonNode> jsonNodes) {
        ObjectMapper objectMapper = new ObjectMapper();
        ObjectWriter objectWriter = objectMapper.writerWithDefaultPrettyPrinter();
        for (JsonNode jsonNode : jsonNodes) {
            try {
                String json = objectWriter.writeValueAsString(jsonNode);
                System.out.println(json);
            } catch (JsonProcessingException e) {
                e.printStackTrace();
            }
        }
    }
}
