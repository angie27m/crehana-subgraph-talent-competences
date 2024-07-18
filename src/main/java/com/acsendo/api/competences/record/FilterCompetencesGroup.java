package com.acsendo.api.competences.record;

import java.util.List;

public record FilterCompetencesGroup (
    Long employee_id,
    Long evaluation_id,
	List<Long> questionnaires_ids) {}

