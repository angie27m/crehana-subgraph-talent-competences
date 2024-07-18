package com.acsendo.api.competences.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.SchemaMapping;
import org.springframework.stereotype.Controller;

import com.acsendo.api.competences.interfaces.ResponseExecutionCompetences;
import com.acsendo.api.competences.interfaces.ResponseQuestionnaireQualify;
import com.acsendo.api.competences.record.CompetencesResponseInternalError;
import com.acsendo.api.competences.record.CompetencesResponseValidationError;
import com.acsendo.api.competences.record.FilterCompetencesGroup;
import com.acsendo.api.competences.record.QuestionnaireEvaluators;
import com.acsendo.api.competences.record.QuestionnaireQualify;
import com.acsendo.api.competences.service.CompetencesEvaluationsService;
import com.acsendo.api.competences.service.CompetencesQuestionnaireService;

/**
 * Clase que contiene mutations para flujo de competencias en el perfil de
 * colaborador, en su nueva versión con GraphQL
 */
@Controller
public class TalentCompetencesMutations {

	@Autowired
	private CompetencesEvaluationsService competencesEvaluationsService;
	
	@Autowired
	private CompetencesQuestionnaireService questionnaireService;
	
	@SchemaMapping(typeName = "TalentCompetencesMutations")
	public ResponseExecutionCompetences create_competence_questionnaire_group(@Argument FilterCompetencesGroup input) {
		try {
	        // Validación de argumentos 
	        if (input == null || input.employee_id() == null || input.evaluation_id() == null || 
	        		input.questionnaires_ids() == null || input.questionnaires_ids().size() < 2) {
	            return new CompetencesResponseValidationError("400", "Argumentos inválidos.");
	        }
	        // Llamada al servicio
	        return competencesEvaluationsService.createCompetenceQuestionnaireGroup(input);	        
	    } catch (Exception e) {
	        // Log de excepción para diagnóstico
	        e.printStackTrace();
	        return new CompetencesResponseInternalError("500", e.getMessage());
	    }
	}

	@SchemaMapping(typeName = "TalentCompetencesMutations")
	public ResponseExecutionCompetences delete_competence_questionnaire_group(@Argument Long evaluation_id, @Argument String group_key) {
		try {
	        // Validación de argumentos 
	        if (group_key == null || evaluation_id == null) {
	            return new CompetencesResponseValidationError("400", "Argumentos inválidos.");
	        }
	        // Llamada al servicio
	        return competencesEvaluationsService.deleteCompetenceQuestionnaireGroup(evaluation_id, group_key);	        
	    } catch (Exception e) {
	        // Log de excepción para diagnóstico
	        e.printStackTrace();
	        return new CompetencesResponseInternalError("500", e.getMessage());
	    }
	}
	
	
	@SchemaMapping(typeName = "TalentCompetencesMutations")
	public ResponseExecutionCompetences delete_competence_questionnaire_evaluator(@Argument Long questionnaire_id) {
		try {
	        // Validación de argumentos 
	        if (questionnaire_id == null) {
	            return new CompetencesResponseValidationError("400", "Argumentos inválidos.");
	        }
	        // Llamada al servicio
	        return questionnaireService.deleteQuestionnaire(questionnaire_id);    
	    } catch (Exception e) {
	        // Log de excepción para diagnóstico
	        e.printStackTrace();
	        return new CompetencesResponseInternalError("500", e.getMessage());
	    }
	}
	
	
	@SchemaMapping(typeName = "TalentCompetencesMutations")
	public ResponseExecutionCompetences create_competence_questionnaire_evaluators(@Argument QuestionnaireEvaluators input) {
		try {
	        // Validación de argumentos 
	        if (input == null || input.evaluated_id()==null || input.evaluation_id()==null || input.evaluators()==null || input.employee_id()==null) {
	            return new CompetencesResponseValidationError("400", "Argumentos inválidos.");
	        }
	        // Llamada al servicio
	        return questionnaireService.createQuestionnaire(input);    
	    } catch (Exception e) {
	        // Log de excepción para diagnóstico
	        e.printStackTrace();
	        return new CompetencesResponseInternalError("500", e.getMessage());
	    }
	}
	
	@SchemaMapping(typeName = "TalentCompetencesMutations")
	public ResponseQuestionnaireQualify qualify_competence_questionnaire(@Argument QuestionnaireQualify input) {
		try {
	        // Validación de argumentos 
	        if (input == null || input.type()==null) {
	            return new CompetencesResponseValidationError("400", "Argumentos inválidos.");
	        }
	        // Llamada al servicio
	        return questionnaireService.qualifyQuestionnaires(input);    
	    } catch (Exception e) {
	        // Log de excepción para diagnóstico
	        e.printStackTrace();
	        return new CompetencesResponseInternalError("500", e.getMessage());
	    }
	}
	
	
	
	@SchemaMapping(typeName = "TalentCompetencesMutations")
	public ResponseExecutionCompetences finish_competence_questionnaires(@Argument List<Long> questionnaires_id, @Argument Long evaluation_id, @Argument Long employee_id) {
		try {
	        // Validación de argumentos 
	        if (questionnaires_id == null || evaluation_id==null  || employee_id==null) {
	            return new CompetencesResponseValidationError("400", "Argumentos inválidos.");
	        }
	        // Llamada al servicio
	        return questionnaireService.finishQuestionnaires(questionnaires_id, evaluation_id, employee_id);    
	    } catch (Exception e) {
	        // Log de excepción para diagnóstico
	        e.printStackTrace();
	        return new CompetencesResponseInternalError("500", e.getMessage());
	    }
	}
}
