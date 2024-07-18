package com.acsendo.api.competences.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.SchemaMapping;
import org.springframework.stereotype.Controller;

import com.acsendo.api.competences.interfaces.ResponseCompetenceGeneralForm;
import com.acsendo.api.competences.interfaces.ResponseCompetencesConfiguration;
import com.acsendo.api.competences.interfaces.ResponseCompetencesEvaluation;
import com.acsendo.api.competences.interfaces.ResponseQualifySummary;
import com.acsendo.api.competences.interfaces.ResponseQuestionnaire;
import com.acsendo.api.competences.interfaces.ResponseQuestionnaireDetail;
import com.acsendo.api.competences.interfaces.TalentResponseEmployee;
import com.acsendo.api.competences.record.CompetencesResponseInternalError;
import com.acsendo.api.competences.record.CompetencesResponseValidationError;
import com.acsendo.api.competences.record.FilterCompetencesEvaluation;
import com.acsendo.api.competences.record.FilterCompetencesEvaluationQuestionnaire;
import com.acsendo.api.competences.record.FilterCompetencesQuestionnaireEvaluator;
import com.acsendo.api.competences.service.CompetencesEvaluationsService;
import com.acsendo.api.competences.service.CompetencesQuestionnaireService;

/**
 * Clase que contiene queries para flujo de competencias en el perfil de
 * colaborador, en su nueva versión con GraphQL
 */
@Controller
public class TalentCompetencesQueries {

	@Autowired
	private CompetencesEvaluationsService competencesEvaluationsService;
	
	@Autowired
	private CompetencesQuestionnaireService questionnaireService;
	

	@SchemaMapping(typeName = "TalentCompetencesQueries")
	public ResponseCompetencesEvaluation get_competences_evaluations(@Argument FilterCompetencesEvaluation input) {
		try {
			return competencesEvaluationsService.getCompetencesEvaluations(input);
		} catch (Exception e) {
			return new CompetencesResponseInternalError("500", e.getMessage());
		}
	}		
	
	@SchemaMapping(typeName = "TalentCompetencesQueries")
	public ResponseQuestionnaire get_competences_evaluation_questionnaires(@Argument FilterCompetencesEvaluationQuestionnaire input) {
		try {
			
			return competencesEvaluationsService.getQuestionnairesByEvaluationAndEvaluator(input);
		} catch (Exception e) {
			return new CompetencesResponseInternalError("500", e.getMessage());
		}
	}
	
	@SchemaMapping(typeName = "TalentCompetencesQueries")
	public ResponseCompetencesConfiguration get_competences_evaluation_configuration(@Argument Long evaluation_id, @Argument Long employee_id) {
		try {			
			return competencesEvaluationsService.getCompetencesEvaluationConfiguration(evaluation_id, employee_id);
		} catch (Exception e) {
			return new CompetencesResponseInternalError("500", e.getMessage());
		}
	}
	
	
	@SchemaMapping(typeName = "TalentCompetencesQueries")
	public ResponseQuestionnaireDetail get_competences_evaluation_questionnaire_detail(@Argument Long questionnaire_id, @Argument Long employee_id) {
		try {			
			return competencesEvaluationsService.getQuestionnairesDetail(questionnaire_id, employee_id);
		} catch (Exception e) {
			return new CompetencesResponseInternalError("500", e.getMessage());
		}
	}
	
	@SchemaMapping(typeName = "TalentCompetencesQueries")
	public ResponseQuestionnaire get_competences_questionnaires_evaluators(@Argument Long employee_id, @Argument Long evaluation_id,
			@Argument Long evaluated_id, @Argument Integer first, @Argument String after) {
		try {			
			return questionnaireService.getCompetencesQuestionnairesEvaluators(evaluation_id, employee_id, evaluated_id, first, after);
		} catch (Exception e) {
			return new CompetencesResponseInternalError("500", e.getMessage());
		}
	}
	
	@SchemaMapping(typeName = "TalentCompetencesQueries")
	public TalentResponseEmployee get_competences_questionnaires_available_evaluators(@Argument FilterCompetencesQuestionnaireEvaluator input) {
		try {			
			return questionnaireService.getCompetencesQuestionnairesAvailableEvaluators(input);
		} catch (Exception e) {
			return new CompetencesResponseInternalError("500", e.getMessage());
		}
	}
	
	@SchemaMapping(typeName = "TalentCompetencesQueries")
	public ResponseCompetenceGeneralForm get_competences_questions_by_questionnaires(@Argument List<Long> questionnaires_id, @Argument Long  evaluation_id, 
			@Argument Long  employee_id, @Argument Integer first, @Argument String after ) {
		try {			
			return questionnaireService.getCompetencesAndQuestionsByQuestionnaires(questionnaires_id, evaluation_id, employee_id, first, after);
		} catch (Exception e) {
			return new CompetencesResponseInternalError("500", e.getMessage());
		}
	}
	
	
	@SchemaMapping(typeName = "TalentCompetencesQueries")
	public ResponseQualifySummary get_competences_qualify_summary(@Argument List<Long> questionnaires_id, @Argument Long  evaluation_id, 
			@Argument Long  employee_id ) {
		try {		
			  // Validación de argumentos 
	        if (questionnaires_id == null || evaluation_id==null || employee_id==null) {
	            return new CompetencesResponseValidationError("400", "Argumentos inválidos.");
	        }
	        
			return questionnaireService.getQualifySummary(questionnaires_id, evaluation_id, employee_id);
		} catch (Exception e) {
			return new CompetencesResponseInternalError("500", e.getMessage());
		}
	}

}