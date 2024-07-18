package com.acsendo.api.competences.record;

import com.acsendo.api.competences.interfaces.ResponseQuestionnaire;
import com.acsendo.api.util.ConnectionUtil;

public record QuestionnaireGeneralPagination(
		CompetencesEvaluation evaluation,
		ConnectionUtil<Questionnaire> questionnaires)
		implements ResponseQuestionnaire {
}
