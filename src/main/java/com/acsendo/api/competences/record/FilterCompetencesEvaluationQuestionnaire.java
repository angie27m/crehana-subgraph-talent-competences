package com.acsendo.api.competences.record;

public record FilterCompetencesEvaluationQuestionnaire (
    Long employee_id,
    Long evaluation_id,
    FilterByCompetencesEvaluation filter_by,
	Integer first,
	String after) {}

