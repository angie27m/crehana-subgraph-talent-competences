package com.acsendo.api.competences.record;

public record FilterCompetencesEvaluation (
    Long employee_id,
    FilterByCompetencesEvaluation filter_by,
	Integer first,
	String after) {}

