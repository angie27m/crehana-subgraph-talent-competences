package com.acsendo.api.competences.record;

import java.util.List;

import com.acsendo.api.competences.enumerations.QuestionnaireStateEnum;
import com.acsendo.api.competences.enumerations.RelationQuestionnaireEnum;

public record Questionnaire (
	String id,
	Long original_id,
	QuestionnaireStateEnum state, 
	EmployeeCompetencesEvaluation evaluated,
	String relation,
	RelationQuestionnaireEnum relation_enum,
	String similar_competences_key,
	Double progress,
	String group_key,
	List<Questionnaire> grouped_questionnaires,
	Boolean allow_delete) {


}



