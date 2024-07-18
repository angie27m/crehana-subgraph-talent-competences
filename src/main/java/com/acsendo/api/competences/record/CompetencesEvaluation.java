package com.acsendo.api.competences.record;

import java.util.Date;
import java.util.List;

import com.acsendo.api.competences.enumerations.EvaluationStateEnum;

public record CompetencesEvaluation (
	String id,
	Long original_id,
	String name,
	String date_initial,
	String date_final,
	String percentage_response,
	EvaluationStateEnum evaluation_state_2, 
	Integer finished_questionnaires,
	Integer total_questionnaires,
	List<EmployeeCompetencesEvaluation> employees,
	String description) {

	

	public CompetencesEvaluation withEmployees(List<EmployeeCompetencesEvaluation> newEmployees) {
        return new CompetencesEvaluation(
                id,
                original_id,
                name,
                date_initial,
                date_final,
                percentage_response,
                evaluation_state_2,
                finished_questionnaires,
                total_questionnaires,
                newEmployees,
                description
        );
    }
}