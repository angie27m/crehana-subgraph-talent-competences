package com.acsendo.api.competences.record;

public record QuestionForm (
	String id,
	Long original_id,
	Double response ,
	Boolean completed_question,
	Long questionnaire_id,
	String comment,
	Double response_auto){


}