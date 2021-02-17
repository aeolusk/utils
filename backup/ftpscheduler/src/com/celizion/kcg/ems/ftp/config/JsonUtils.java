package com.celizion.kcg.ems.ftp.config;

import com.google.gson.JsonElement;
import com.google.gson.JsonNull;
import com.google.gson.JsonPrimitive;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class JsonUtils {
	public static JsonElement find(JsonElement parent, String tagName) {
		if (!parent.isJsonObject())
			return JsonNull.INSTANCE;

		JsonElement findElement = parent.getAsJsonObject().get(tagName);
		if (findElement == null)
			return JsonNull.INSTANCE;
		return findElement;
	}

	public static String findAnd2String(JsonElement parentElement, String tagName) {
		return element2String(find(parentElement, tagName));
	}

	public static int findAnd2Int(JsonElement parentElement, String tagName, int defValue) {
		JsonElement find = find(parentElement, tagName);
		if (find.isJsonPrimitive() && find.getAsJsonPrimitive().isNumber())
			return find.getAsNumber().intValue();
		return defValue;
	}
	
	public static boolean isTrue(JsonElement parentElement, String tagName) {
		JsonElement find = find(parentElement, tagName);
		if (find.isJsonPrimitive() && find.getAsJsonPrimitive().isBoolean())
			return find.getAsBoolean();
		
		log.debug(tagName + "'s value isn't type boolean.");
		return false;
	}

	public static String element2String(JsonElement element) {
		if (!element.isJsonPrimitive())
			return null;

		JsonPrimitive type = element.getAsJsonPrimitive();

		if (type.isBoolean())
			return "" + element.getAsBoolean();

		if (type.isNumber())
			return "" + element.getAsNumber();

		return element.getAsString();
	}
}
