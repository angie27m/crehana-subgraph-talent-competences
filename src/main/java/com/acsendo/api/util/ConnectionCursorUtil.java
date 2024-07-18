package com.acsendo.api.util;

import java.nio.charset.StandardCharsets;
import java.util.Base64;

import graphql.relay.ConnectionCursor;
import graphql.relay.DefaultConnectionCursor;

public class ConnectionCursorUtil {

	public static ConnectionCursor encodeCursor(String inputString) {
		return new DefaultConnectionCursor(Base64.getEncoder().encodeToString(inputString.getBytes(StandardCharsets.UTF_8)));
	}

	public static String decodeCursor(String cursor) {
		return new String(Base64.getDecoder().decode(cursor));
	}

}
