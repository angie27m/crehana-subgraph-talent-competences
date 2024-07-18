package com.acsendo.api.competences.record;

import java.util.List;

public record CompetenceForm (
	String id,
	Long original_id,
	String name,
	String description,
	Boolean completed_competence,
	List<BehaviorForm> behaviors,
	List<CommentForm> comments){

	
}