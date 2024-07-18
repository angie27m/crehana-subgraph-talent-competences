package com.acsendo.api.competences.record;

public record OptionResponse (
		String label,
		String key,
		String value,
		String percentage,
		String parent_key,
		Boolean other,
		Integer order) {
}
