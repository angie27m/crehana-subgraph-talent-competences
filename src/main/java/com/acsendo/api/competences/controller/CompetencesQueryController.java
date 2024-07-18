package com.acsendo.api.competences.controller;

import org.springframework.graphql.data.method.annotation.QueryMapping;
import org.springframework.stereotype.Controller;

/**
 * Clase que contiene queries para flujo de competencias en el perfil de
 * colaborador, en su nueva versión con GraphQL
 */
@Controller
public class CompetencesQueryController {

	@QueryMapping
	public TalentCompetencesQueries talent_competences() {

		return new TalentCompetencesQueries();
	}

}
