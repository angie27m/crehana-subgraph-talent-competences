package com.acsendo.api.competences.record;

public record EmployeeTalentType(
		String id,
		Long original_id,
		AvatarTalentType avatar,
		String full_name,
		DivisionTalentType division,
		JobTalentType job
) {

}
