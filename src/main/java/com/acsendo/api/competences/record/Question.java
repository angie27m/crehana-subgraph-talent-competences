package com.acsendo.api.competences.record;

public record Question (
	String id,
	Long original_id,
	String name,
	Double response,
	String suggestion) {


}