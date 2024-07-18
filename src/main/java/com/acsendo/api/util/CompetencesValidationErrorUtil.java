package com.acsendo.api.util;

import com.acsendo.api.competences.record.CompetencesResponseValidationError;

public class CompetencesValidationErrorUtil {

	public static String CODE_ERROR = "404";

	public static String COMPANY_NOT_FOUND = "Company not found";

	public static String EVALUATION_NOT_FOUND = "Evaluation competences not found";

	public static String EMPLOYEE_NOT_FOUND = "Employee not found";

	public static String QUESTION_NOT_FOUND = "Question not found";

	public static String OPTION_NOT_FOUND = "Option response not found";
	
	public static String GROUP_KEY_NOT_FOUND = "Group key not found";
	
	public static String QUESTIONNAIRE_FOUND_IN_GROUP = "Questionnaire already exists in a group";
	
	public static String QUESTIONNAIRE_NOT_FOUND= "Questionnaire not found";
	
	public static String QUESTIONNAIRE_ALREADY_EXISTS="Questionnaire alredy exists with that evaluated and evaluator";
	
	public static String FIELD_CANNOT_BE_NULL="Field(s) required for type can't be null";
	
	public static String QUESTIONNAIRE_CANT_FINISH = "Questionnaire(s) can't finish because there are questions without responses";

	public static CompetencesResponseValidationError getResponseValidationError(String message) {

		return new CompetencesResponseValidationError(CODE_ERROR, message);

	}

}
