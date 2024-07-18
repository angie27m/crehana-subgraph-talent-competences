package com.acsendo.api.competences.record;

import java.util.List;

import com.acsendo.api.competences.interfaces.ResponseQualifySummary;

public record QualifySummary (
		
	List<QuestionnaireSummary> questionnaires_general_summary,
	List<CompetenceSummary> competences_summary
		) implements ResponseQualifySummary{

	
}