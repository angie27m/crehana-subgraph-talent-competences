package com.acsendo.api.competences.record;

import java.util.List;

public record CompetenceSummary (
	String id,
	Long original_id,
	String name,
	List<QuestionnaireSummary> questionnaire_summary_competences
		){

	
}