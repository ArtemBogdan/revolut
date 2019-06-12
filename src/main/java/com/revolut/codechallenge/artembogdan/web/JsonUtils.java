package com.revolut.codechallenge.artembogdan.web;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.revolut.codechallenge.artembogdan.service.ErrorCatalogue;
import com.revolut.codechallenge.artembogdan.service.ServiceException;

public class JsonUtils {
	private JsonUtils() {}; // disable initialization
	
	private static final Gson gson = initGson();
	
	private static Gson initGson() {
		GsonBuilder builder = new GsonBuilder();
		return builder.
				setPrettyPrinting().
				create();
	}

	public static <T> T safeGsonParse(String json, Class<T> clazz) throws ServiceException {
		try {
			return gson.fromJson(json, clazz);
		} catch (Exception e) {
			throw new ServiceException(ErrorCatalogue.INVALID_INPUT_PARAMETER, 
					"Error while parsing request [" + e.getMessage() + "]");
		}
	}

	public static String toJsonString( Object o ) {
		return gson.toJson(o);
	}
}
