package com.acsendo.api.competences.record;


import java.util.List;

import com.acsendo.api.competences.interfaces.ResponseCompetenceGeneralForm;
import com.acsendo.api.util.ConnectionUtil;

public record CompetenceGeneralForm(
		CompetencesEvaluation evaluation,
		List<Questionnaire> questionnaires,
		ConnectionUtil<CompetenceForm> competences,
		List<CommentForm> general_comments1,
		List<CommentForm> general_comments2,
		List<CommentForm> general_comments3
		)
		implements ResponseCompetenceGeneralForm {
}
