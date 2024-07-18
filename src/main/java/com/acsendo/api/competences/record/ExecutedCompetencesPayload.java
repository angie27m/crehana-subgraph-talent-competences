package com.acsendo.api.competences.record;

import com.acsendo.api.competences.interfaces.ResponseExecutionCompetences;

public record ExecutedCompetencesPayload(
		Boolean executed) implements ResponseExecutionCompetences {
	
}
