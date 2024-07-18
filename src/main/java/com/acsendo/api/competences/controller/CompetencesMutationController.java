package com.acsendo.api.competences.controller;

import org.springframework.graphql.data.method.annotation.MutationMapping;
import org.springframework.stereotype.Controller;

/**
 * Clase que contiene mutations para flujo de competencias en el perfil de
 * colaborador, en su nueva versión con GraphQL
 */
@Controller
public class CompetencesMutationController {

	@MutationMapping
	public TalentCompetencesMutations talent_competences() {

		return new TalentCompetencesMutations();
	}

}
