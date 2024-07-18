package com.acsendo.api.competences.record;

import java.util.List;

import com.acsendo.api.competences.enumerations.QualifyTypeEnum;

public record QuestionnaireQualify (
		 Long question_id,
		 Double question_response,
		 List<Long>  all_questionnaires,
		 Long questionnaire_id,
		 String question_comment,
		 Long  competence_id,
		 String  comment_competence,
		 String general_comment1,
		 String general_comment2,
		 String general_comment3,
		 QualifyTypeEnum  type ) {	
}
