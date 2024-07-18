package com.acsendo.api.competences.record;

import com.acsendo.api.competences.interfaces.ResponseCompetenceGeneralForm;
import com.acsendo.api.competences.interfaces.ResponseCompetencesConfiguration;
import com.acsendo.api.competences.interfaces.ResponseCompetencesEvaluation;
import com.acsendo.api.competences.interfaces.ResponseExecutionCompetences;
import com.acsendo.api.competences.interfaces.ResponseQualifySummary;
import com.acsendo.api.competences.interfaces.ResponseQuestionnaire;
import com.acsendo.api.competences.interfaces.ResponseQuestionnaireDetail;
import com.acsendo.api.competences.interfaces.ResponseQuestionnaireQualify;
import com.acsendo.api.competences.interfaces.TalentResponseEmployee;

public record CompetencesResponseValidationError (
	String code,
	String message) implements ResponseCompetencesEvaluation, ResponseQuestionnaire, ResponseCompetencesConfiguration,
ResponseExecutionCompetences,ResponseQuestionnaireDetail, TalentResponseEmployee ,ResponseCompetenceGeneralForm, ResponseQuestionnaireQualify,
ResponseQualifySummary{}