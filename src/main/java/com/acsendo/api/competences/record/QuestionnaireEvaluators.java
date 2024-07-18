package com.acsendo.api.competences.record;

import java.util.List;

public record QuestionnaireEvaluators (
	Long evaluation_id, 
	Long evaluated_id,
	Long employee_id,
	List<Evaluator> evaluators)  {


}