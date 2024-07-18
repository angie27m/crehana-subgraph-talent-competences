package com.acsendo.api.competences.record;

import com.acsendo.api.competences.enumerations.RelationQuestionnaireEnum;

public record Evaluator (
	Long id, 
	RelationQuestionnaireEnum relation) {


}