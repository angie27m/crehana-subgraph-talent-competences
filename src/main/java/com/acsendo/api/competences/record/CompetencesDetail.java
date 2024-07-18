package com.acsendo.api.competences.record;

import java.util.List;

import com.acsendo.api.competences.interfaces.ResponseQuestionnaireDetail;

public record CompetencesDetail (
	String employee_result,
	List<Competence> competences,
	String general_comment1,
	String general_comment2,
	String general_comment3
		) implements ResponseQuestionnaireDetail{


}