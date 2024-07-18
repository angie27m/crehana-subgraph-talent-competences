package com.acsendo.api.competences.record;

import com.acsendo.api.competences.enumerations.RelationQuestionnaireEnum;

public record RelationLabelType (
		RelationQuestionnaireEnum value, 
		String label) {
}
