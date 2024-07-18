package com.acsendo.api.competences.record;

import com.acsendo.api.competences.interfaces.ResponseCompetencesEvaluation;
import com.acsendo.api.util.ConnectionUtil;

public record CompetencesGeneralEvaluationPagination(ConnectionUtil<CompetencesEvaluation> evaluations)
		implements ResponseCompetencesEvaluation {
}
