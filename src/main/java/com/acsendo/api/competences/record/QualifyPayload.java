package com.acsendo.api.competences.record;

import com.acsendo.api.competences.interfaces.ResponseQuestionnaireQualify;

public record QualifyPayload (
		Long competence_id,
	    Boolean completed_competence,
	    Long behavior_id,
	    Boolean completed_behavior,
	    Long question_id,
	    Boolean completed_question,
	    Boolean success) implements ResponseQuestionnaireQualify{	
}
