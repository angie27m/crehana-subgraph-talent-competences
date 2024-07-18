package com.acsendo.api.competences.record;

import java.util.List;

import com.acsendo.api.competences.interfaces.ResponseCompetencesConfiguration;

public record CompetencesEvaluationConfiguration(
		// Indica si se deben mostrar comentarios por competencia
		boolean show_comment_competence,
		// Indica si se deben mostrar comentarios generales
		boolean show_comment_general,
		// Indica si el comentario por competencia es obligatorio
		boolean required_comment_competence,
		// Indica si el comentario general es obligatorio
		boolean required_comment_general,
		// Si es obligatorio algún comentario, indica el mínimo de caracteres
		Integer min_comment_length,
		// Indica si la evaluación tiene activado el modelo por comportamiento
		boolean has_active_behaviors_model,
		// Contiene las opciones de respuesta de la evaluación
		List<OptionResponse> options_response,
		// Indica si la descripción de la competencia está activa
		boolean has_description_competence_active,
		// Indica si se deben mostrar las autoevaluaciones de los empleados a cargo
		boolean show_autoevaluations_employees,
		// Indica si se deben mostrar comentarios por comportamiento
		boolean show_comment_behavior,
		// Indica si el comentario por comportamiento es obligatorio
		boolean required_comment_behavior,
		// Indica si debe mostrar la opción de NO SÉ o NO APLICA
		boolean show_dont_know,
		// Indica si debe mostrar la caja de comentario adicional N.2
		boolean show_comment_general2,
		// Indica si el comentario por comportamiento general 2 es obligatorio
		boolean required_comment_general2,
		// Indica si debe mostrar la caja de comentario adicional N.3
		boolean show_comment_general3,
		// Indica si el comentario por comportamiento general 3 es obligatorio
		boolean required_comment_general3,
		// Contiene el label configurado en el comentario general
		String label_comment,
		// Contiene el label configurado en el comentario adicional 1
		String label_comment2,
		// Contiene el label configurado en el comentario adicional 2
		String label_comment3,
		// Contiene la información de inicio de la evaluación (encabezado) con la explicación de las opciones
		String options_explanations,
		// Indica si se muestra ícono de evaluadores
		boolean show_evaluators_icon,
		// Indica si se debe ocultar la opción de agregar evaluadores en el líder
		boolean hide_add_evaluators_leader,
		// Indica si se debe permitir editar la evaluación
		boolean show_edit_evaluation,
		// Habilitar resultado de las evaluaciones a los evaluadores
		boolean enable_result_evaluate,
		// Mostrar todos los colaboradores cuando el líder elige evaluador
		boolean show_all_relations,
		// Permite asignar competencias por el líder
		boolean assign_competences_by_leader,
		// Muestra resumen de competencias
		boolean show_competences_brief,
		// Listado de relaciones usadas en una evaluación
		List<RelationLabelType> relations,
		//Mostrar la tabla o tab de evaluadores
		boolean show_evaluators_table,
		// Indica si debe mostrar el tab de Mi equipo
		boolean show_my_team_tab,
		// Permite deshabilitar la calificación masiva
		boolean disable_mass_qualification) implements ResponseCompetencesConfiguration {
	
	 public CompetencesEvaluationConfiguration withRequiredComments(boolean required_comment_competence, boolean required_comment_general, boolean required_comment_behavior, boolean required_comment_general2, boolean required_comment_general3) {
	        return new CompetencesEvaluationConfiguration(
	            show_comment_competence,
	            show_comment_general,
	            required_comment_competence,
	            required_comment_general,
	            min_comment_length,
	            has_active_behaviors_model,
	            options_response,
	            has_description_competence_active,
	            show_autoevaluations_employees,
	            show_comment_behavior,
	            required_comment_behavior,
	            show_dont_know,
	            show_comment_general2,
	            required_comment_general2,
	            show_comment_general3,
	            required_comment_general3,
	            label_comment,
	            label_comment2,
	            label_comment3,
	            options_explanations,
	            show_evaluators_icon,
	            hide_add_evaluators_leader,
	            show_edit_evaluation,
	            enable_result_evaluate,
	            show_all_relations,
	            assign_competences_by_leader,
	            show_competences_brief,
	            relations, 
	            show_evaluators_table,
	            show_my_team_tab,
	            disable_mass_qualification
	        );
	    }
	 
	 public CompetencesEvaluationConfiguration withOptionsResponse(List<OptionResponse> options_response) {
	        return new CompetencesEvaluationConfiguration(
	            show_comment_competence,
	            show_comment_general,
	            required_comment_competence,
	            required_comment_general,
	            min_comment_length,
	            has_active_behaviors_model,
	            options_response,
	            has_description_competence_active,
	            show_autoevaluations_employees,
	            show_comment_behavior,
	            required_comment_behavior,
	            show_dont_know,
	            show_comment_general2,
	            required_comment_general2,
	            show_comment_general3,
	            required_comment_general3,
	            label_comment,
	            label_comment2,
	            label_comment3,
	            options_explanations,
	            show_evaluators_icon,
	            hide_add_evaluators_leader,
	            show_edit_evaluation,
	            enable_result_evaluate,
	            show_all_relations,
	            assign_competences_by_leader,
	            show_competences_brief,
	            relations,
	            show_evaluators_table,
	            show_my_team_tab,
	            disable_mass_qualification
	        );
	    }
	 
	 public CompetencesEvaluationConfiguration withLabelComment(String label_comment) {
	        return new CompetencesEvaluationConfiguration(
	            show_comment_competence,
	            show_comment_general,
	            required_comment_competence,
	            required_comment_general,
	            min_comment_length,
	            has_active_behaviors_model,
	            options_response,
	            has_description_competence_active,
	            show_autoevaluations_employees,
	            show_comment_behavior,
	            required_comment_behavior,
	            show_dont_know,
	            show_comment_general2,
	            required_comment_general2,
	            show_comment_general3,
	            required_comment_general3,
	            label_comment,
	            label_comment2,
	            label_comment3,
	            options_explanations,
	            show_evaluators_icon,
	            hide_add_evaluators_leader,
	            show_edit_evaluation,
	            enable_result_evaluate,
	            show_all_relations,
	            assign_competences_by_leader,
	            show_competences_brief,
	            relations,
	            show_evaluators_table,
	            show_my_team_tab,
	            disable_mass_qualification
	        );
	    }
	 
	 public CompetencesEvaluationConfiguration withLabelComment2(String label_comment2) {
	        return new CompetencesEvaluationConfiguration(
	            show_comment_competence,
	            show_comment_general,
	            required_comment_competence,
	            required_comment_general,
	            min_comment_length,
	            has_active_behaviors_model,
	            options_response,
	            has_description_competence_active,
	            show_autoevaluations_employees,
	            show_comment_behavior,
	            required_comment_behavior,
	            show_dont_know,
	            show_comment_general2,
	            required_comment_general2,
	            show_comment_general3,
	            required_comment_general3,
	            label_comment,
	            label_comment2,
	            label_comment3,
	            options_explanations,
	            show_evaluators_icon,
	            hide_add_evaluators_leader,
	            show_edit_evaluation,
	            enable_result_evaluate,
	            show_all_relations,
	            assign_competences_by_leader,
	            show_competences_brief,
	            relations,
	            show_evaluators_table,
	            show_my_team_tab,
	            disable_mass_qualification
	        );
	    }
	 
	 public CompetencesEvaluationConfiguration withLabelComment3(String label_comment3) {
	        return new CompetencesEvaluationConfiguration(
	            show_comment_competence,
	            show_comment_general,
	            required_comment_competence,
	            required_comment_general,
	            min_comment_length,
	            has_active_behaviors_model,
	            options_response,
	            has_description_competence_active,
	            show_autoevaluations_employees,
	            show_comment_behavior,
	            required_comment_behavior,
	            show_dont_know,
	            show_comment_general2,
	            required_comment_general2,
	            show_comment_general3,
	            required_comment_general3,
	            label_comment,
	            label_comment2,
	            label_comment3,
	            options_explanations,
	            show_evaluators_icon,
	            hide_add_evaluators_leader,
	            show_edit_evaluation,
	            enable_result_evaluate,
	            show_all_relations,
	            assign_competences_by_leader,
	            show_competences_brief,
	            relations,
	            show_evaluators_table,
	            show_my_team_tab,
	            disable_mass_qualification
	        );
	    }
	 
	 public CompetencesEvaluationConfiguration withOptionsExplanations(String options_explanations) {
	        return new CompetencesEvaluationConfiguration(
	            show_comment_competence,
	            show_comment_general,
	            required_comment_competence,
	            required_comment_general,
	            min_comment_length,
	            has_active_behaviors_model,
	            options_response,
	            has_description_competence_active,
	            show_autoevaluations_employees,
	            show_comment_behavior,
	            required_comment_behavior,
	            show_dont_know,
	            show_comment_general2,
	            required_comment_general2,
	            show_comment_general3,
	            required_comment_general3,
	            label_comment,
	            label_comment2,
	            label_comment3,
	            options_explanations, 
	            show_evaluators_icon,
	            hide_add_evaluators_leader,
	            show_edit_evaluation,
	            enable_result_evaluate,
	            show_all_relations,
	            assign_competences_by_leader,
	            show_competences_brief,
	            relations,
	            show_evaluators_table,
	            show_my_team_tab,
	            disable_mass_qualification
	        );
	    }
	 
}