package com.acsendo.api.competences.record;

import java.util.List;

public record Competence (
	String id,
	Long original_id,
	String name,
	String result,
	List<Question> questions,
	String comment){


}