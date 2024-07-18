package com.acsendo.api.competences.record;

import java.util.List;

public record BehaviorForm (
	String id,
	Long original_id,
	String name,
	Boolean completed_behavior,
    List<QuestionForm> questions){

	
}