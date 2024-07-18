package com.acsendo.api.competences.record;

public record FilterCompetencesQuestionnaireEvaluator (
    Long company_id,
    Long evaluation_id,
    Long evaluated_id,
    String name,
    Long division_id,
	Integer first,
	String after) {}

