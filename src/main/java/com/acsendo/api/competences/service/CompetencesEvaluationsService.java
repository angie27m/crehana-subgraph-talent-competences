package com.acsendo.api.competences.service;

import static com.acsendo.api.util.DataObjectUtil.getDouble;
import static com.acsendo.api.util.DataObjectUtil.getLong;

import java.io.UnsupportedEncodingException;
import java.lang.reflect.Type;
import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.acsendo.api.competences.dao.CompetencesEvaluationsDAO;
import com.acsendo.api.competences.dao.CompetencesQuestionDAO;
import com.acsendo.api.competences.dto.CompetencesEvaluationDTO;
import com.acsendo.api.competences.enumerations.EvaluationStateEnum;
import com.acsendo.api.competences.enumerations.QuestionnaireStateEnum;
import com.acsendo.api.competences.enumerations.RelationQuestionnaireEnum;
import com.acsendo.api.competences.interfaces.ResponseCompetencesConfiguration;
import com.acsendo.api.competences.interfaces.ResponseCompetencesEvaluation;
import com.acsendo.api.competences.interfaces.ResponseExecutionCompetences;
import com.acsendo.api.competences.interfaces.ResponseQuestionnaire;
import com.acsendo.api.competences.model.CommentCompetence;
import com.acsendo.api.competences.model.CompetenceQuestionnaireGroup;
import com.acsendo.api.competences.model.Question2;
import com.acsendo.api.competences.record.AvatarTalentType;
import com.acsendo.api.competences.record.Competence;
import com.acsendo.api.competences.record.CompetencesDetail;
import com.acsendo.api.competences.record.CompetencesEvaluation;
import com.acsendo.api.competences.record.CompetencesEvaluationConfiguration;
import com.acsendo.api.competences.record.CompetencesGeneralEvaluationPagination;
import com.acsendo.api.competences.record.DivisionTalentType;
import com.acsendo.api.competences.record.EmployeeCompetencesEvaluation;
import com.acsendo.api.competences.record.ExecutedCompetencesPayload;
import com.acsendo.api.competences.record.FilterCompetencesEvaluation;
import com.acsendo.api.competences.record.FilterCompetencesEvaluationQuestionnaire;
import com.acsendo.api.competences.record.FilterCompetencesGroup;
import com.acsendo.api.competences.record.JobTalentType;
import com.acsendo.api.competences.record.OptionResponse;
import com.acsendo.api.competences.record.Question;
import com.acsendo.api.competences.record.Questionnaire;
import com.acsendo.api.competences.record.QuestionnaireGeneralPagination;
import com.acsendo.api.competences.record.RelationLabelType;
import com.acsendo.api.competences.repository.CommentCompetenceRepository;
import com.acsendo.api.competences.repository.CompetenceQuestionnaireGroupRepository;
import com.acsendo.api.competences.repository.Question2Repository;
import com.acsendo.api.competences.repository.QuestionnaireRepository;
import com.acsendo.api.hcm.dao.QuestionnaireDAO;
import com.acsendo.api.hcm.enumerations.EntityState;
import com.acsendo.api.hcm.enumerations.EvaluationType;
import com.acsendo.api.hcm.model.Company;
import com.acsendo.api.hcm.model.Employee;
import com.acsendo.api.hcm.model.Evaluation;
import com.acsendo.api.hcm.model.Label;
import com.acsendo.api.hcm.model.LabelFlex;
import com.acsendo.api.hcm.repository.EmployeeRepository;
import com.acsendo.api.hcm.repository.EvaluationRepository;
import com.acsendo.api.hcm.repository.LabelFlexRepository;
import com.acsendo.api.hcm.repository.LabelRepository;
import com.acsendo.api.results.dao.PerformanceResultsDAO;
import com.acsendo.api.util.CompetencesValidationErrorUtil;
import com.acsendo.api.util.ConnectionCursorUtil;
import com.acsendo.api.util.ConnectionUtil;
import com.acsendo.api.util.PageInfoUtil;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import graphql.relay.ConnectionCursor;
import graphql.relay.DefaultEdge;
import graphql.relay.Edge;

/**
 * Contiene servicios relacionados con las evaluaciones de competencias
 */
@Service
public class CompetencesEvaluationsService {

	@Autowired
	private CompetencesEvaluationsDAO competencesEvaluationsDAO;

	@Autowired
	private QuestionnaireDAO questionnaireDao;

	@Autowired
	private CompetencesUtilService competencesUtilService;
	
	@Autowired
	private EmployeeRepository employeeRepository;

	@Autowired
	private LabelRepository labelRepository;
	
	@Autowired
	private EvaluationRepository evaluationRepository;
	
	@Autowired
	private LabelFlexRepository labelFlexRepository;
	
	@Autowired
	private QuestionnaireRepository questionnaireRepository;
	
	@Autowired
	private CompetenceQuestionnaireGroupRepository competenceQuestionnaireGroupRepository;
	
	@Autowired
	private Question2Repository questionRepository;
	
	@Autowired
	private CompetencesQuestionDAO questionDao;
	
	@Autowired
	private CommentCompetenceRepository commentRepository;
	
	@Autowired
	private PerformanceResultsDAO performanceResultDAO;
	
	
	
	// Matriz que representa los dígitos hexadecimales
	private static final char[] hexArray = "0123456789ABCDEF".toCharArray();
	

	/**
	 * Obtiene listado de evaluaciones de competencias donde un empleado ha
	 * participado como evaluador, con su respectiva información relacionada. Además
	 * permite filtros por nombre de la evaluación y el estado de la misma.
	 * Implementa paginador y ordenamiento por estado y fecha de cierre.
	 * 
	 * @param input Información de entrada para la consulta
	 */
	public ResponseCompetencesEvaluation getCompetencesEvaluations(FilterCompetencesEvaluation input) {
		
		List<CompetencesEvaluation> compEvaluationsWithEmployees = new ArrayList<>();
		// Consulta evaluaciones del empleado
		List<CompetencesEvaluationDTO> listObjEvaluations = competencesEvaluationsDAO
				.getCompetencesEvaluationsByEmployee(input.employee_id(), input.filter_by().name(),
						input.filter_by().state(), null);
		
		List<CompetencesEvaluationDTO> compEvaluationswithAfter=new ArrayList<>();
		
		// Verifica que tenga evaluaciones y las filtra según el paginador
		if (listObjEvaluations != null) {
	        String cursorComplete = input.after() != null ? ConnectionCursorUtil.decodeCursor(input.after()) : null;
	        String[] split = input.after() != null ? cursorComplete.split(":") : null;
	        Integer positionAfter = cursorComplete != null ? split != null ? Integer.parseInt(split[1]) : null : null;

	        //Para saber el total de registros que quedan despues del cursor especificado
	        compEvaluationswithAfter=listObjEvaluations.stream()
	                .filter(tem -> cursorComplete == null || listObjEvaluations.indexOf(tem) > positionAfter).collect(Collectors.toList());
	        
	        List<CompetencesEvaluation> compEvaluationsFiltered = compEvaluationswithAfter.stream()
	                .limit(input.first()!=null? input.first() :compEvaluationswithAfter.size())
	                .map(this::convertToCompetencesEvaluation)
	                .collect(Collectors.toList());
	        

	        // Hace llamado de los empleados a evaluar en cada evaluación			
	        if (compEvaluationsFiltered != null && compEvaluationsFiltered.size() > 0) {
	            for (CompetencesEvaluation evaluation : compEvaluationsFiltered) {
	                List<Object[]> employees = competencesEvaluationsDAO.getEmployeesEvaluatedByEmployeeIdAndEvaluationId(
	                        evaluation.original_id(), input.employee_id());

	                List<EmployeeCompetencesEvaluation> employeesEvaluated = mapEmployees(employees);
	                compEvaluationsWithEmployees.add(evaluation.withEmployees(employeesEvaluated));
	            }
	        }
	    }

		// Unifica la información en los nodos del paginador, con su respectivo cursor
		List<Edge<CompetencesEvaluation>> listEdges = compEvaluationsWithEmployees.stream().map(eva -> {
			Optional<CompetencesEvaluationDTO> evaluationOpt = listObjEvaluations.stream()
					.filter(obj -> eva.original_id().equals(obj.getOriginalId())).findFirst();
			return new DefaultEdge<>(eva, ConnectionCursorUtil
					.encodeCursor("arrayconnection:" + listObjEvaluations.indexOf(evaluationOpt.get())));
		}).collect(Collectors.toUnmodifiableList());

		// Genera variables generales del paginador
		ConnectionCursor startCursor = listEdges.isEmpty() ? null : listEdges.get(0).getCursor();
		ConnectionCursor endCursor = listEdges.isEmpty() ? null : listEdges.get(listEdges.size() - 1).getCursor();
		boolean hasPreviousPage = input.after() != null;
		boolean hasNextPage = input.first() != null && compEvaluationswithAfter.size() > input.first();

		// Retorna información completa
		CompetencesGeneralEvaluationPagination competencesEvaluationsPagination = new CompetencesGeneralEvaluationPagination(
				new ConnectionUtil<CompetencesEvaluation>(listObjEvaluations.size(), listEdges,
						new PageInfoUtil(startCursor, endCursor, hasPreviousPage, hasNextPage)));		 
		return competencesEvaluationsPagination;
	}

	/*
	 * Convierte una evaluación de su DTO al record que se va a devolver
	 */
	private CompetencesEvaluation convertToCompetencesEvaluation(CompetencesEvaluationDTO compEvaluationDTO) {
		if(compEvaluationDTO!=null) {
			// Valida estado CLOSED según el progreso
			EvaluationStateEnum evaluationState = compEvaluationDTO.getEvaluationState2().equals("FINISHED")
					&& compEvaluationDTO.getPercentageResponse() < 100 ? EvaluationStateEnum.CLOSED
							: EvaluationStateEnum.valueOf(compEvaluationDTO.getEvaluationState2());
			CompetencesEvaluation competencesEvaluationObj = new CompetencesEvaluation(competencesUtilService.createNodeID("CompetencesEvaluation",compEvaluationDTO.getOriginalId()),
					compEvaluationDTO.getOriginalId(), compEvaluationDTO.getName(), compEvaluationDTO.getDateInitial(),
					compEvaluationDTO.getDateFinal(), compEvaluationDTO.getPercentageResponse().toString(), evaluationState,
					compEvaluationDTO.getFinishedQuestionnaires(), compEvaluationDTO.getTotalQuestionnaires(), null, null);
			return competencesEvaluationObj;
		}else {
		 return null;
		}
	}
	
	/**
	 * Convierte una lista de empleados a una lista de objetos EmployeeCompetencesEvaluation.
	 *
	 * @param employees Una lista de matrices de objetos que representan datos de empleados.
	 *                  Cada matriz de objetos debe contener el ID y el nombre del empleado.
	 * @return Una lista de objetos EmployeeCompetencesEvaluation que representan a los empleados mapeados.
	 */
	private List<EmployeeCompetencesEvaluation> mapEmployees(List<Object[]> employees) {
	    return employees.parallelStream()
	            .map(this::mapEmployee)
	            .collect(Collectors.toList());
	}

	/**
	 * Convierte un empleado representado por una matriz de objetos a un objeto EmployeeCompetencesEvaluation.
	 *
	 * @param res Una matriz de objetos que contiene datos del empleado, donde res[0] es el ID del empleado,
	 *            y res[1] es el nombre del empleado.
	 * @return Un objeto EmployeeCompetencesEvaluation que representa al empleado mapeado.
	 */
	private EmployeeCompetencesEvaluation mapEmployee(Object[] res) {
	    String avatarUrl = "/fotoemp?id=" + res[0];
	    AvatarTalentType avatarType = new AvatarTalentType(competencesUtilService.getUrlEnviroment() + avatarUrl, avatarUrl);
	    return new EmployeeCompetencesEvaluation(
	            competencesUtilService.createNodeID("EmployeeCompetencesEvaluation", getLong(res[0])),
	            getLong(res[0]),
	            avatarType,
	            (String) res[1],
	            null,
	            null
	    );
	}

	/**
	 * Método que obtiene todos los cuestionarios por evaluar un colaborador
	 * 
	 * @param evaluationId Identificador de la evaluación
	 * @param evaluatorId  Identificador de la persona que esta evaluando
	 * @param first        cantidad de registros a devolver paginados
	 * @param after        Indica desde donde se debe consultar los registros 
	 */
	public ResponseQuestionnaire getQuestionnairesByEvaluationAndEvaluator(
			FilterCompetencesEvaluationQuestionnaire input) {

		String states = input.filter_by() != null ? input.filter_by().state()
				: "'ASSIGNED', 'IN_PROGRESS', 'FINISHED', 'CONFIGURE'";

		List<Object[]> all = questionnaireDao.getQuestionnairesByEvaluationAndEvaluator(input.evaluation_id(),
				input.employee_id(), states);

		List<Questionnaire> questionnaires = createQuestionnaireList(all, input.employee_id());
		List<Questionnaire> integratedQuestionnaires = new ArrayList<>();
		List<Questionnaire> questionnairesFiltered = new ArrayList<>();

		// Verifica que existan cuestionarios por evaluar
		if (questionnaires != null && questionnaires.size() > 0) {
			
			// Realiza la agrupación de cuestionarios por la llave de su grupo
			Map<String, List<Questionnaire>> groupedQuestionnaires = questionnaires.stream()
			        .filter(q -> q.group_key() != null)
			        .collect(Collectors.groupingBy(Questionnaire::group_key));

			// Itera sobre los valores del mapa (listas de cuestionarios)
			for (Map.Entry<String, List<Questionnaire>> entry : groupedQuestionnaires.entrySet()) {
			    String groupKey = entry.getKey();
			    List<Questionnaire> group = entry.getValue();

			    // Crea un nuevo objeto Questionnaire que integra todos los cuestionarios del grupo
			    Questionnaire integratedQuestionnaire = integrateQuestionnaires(group, groupKey);

			    // Agrega el nuevo objeto "cuestionario agrupador" a la lista 
			    integratedQuestionnaires.add(integratedQuestionnaire);
			}

			// Agrega a la lista los cuestionarios con group_key en null
			List<Questionnaire> nullGroupQuestionnaires = questionnaires.stream()
			        .filter(q -> q.group_key() == null)
			        .collect(Collectors.toList());

			integratedQuestionnaires.addAll(nullGroupQuestionnaires);
			
			String cursorComplete = input.after() != null ? ConnectionCursorUtil.decodeCursor(input.after()) : null;
			String[] split = input.after() != null ? cursorComplete.split(":") : null;
			Integer positionAfter = cursorComplete != null ? split != null ? Integer.parseInt(split[1]) : null : null;

			questionnairesFiltered = input.after() != null
							? integratedQuestionnaires.stream().filter(q -> integratedQuestionnaires.indexOf(q) > positionAfter)
								.collect(Collectors.toList())
							: integratedQuestionnaires;
		}

		Integer first= input.first() != null? input.first() : questionnairesFiltered.size(); 
		List<Edge<Questionnaire>> listEdges = questionnairesFiltered.stream().limit(first).map(quest -> {
			return new DefaultEdge<>(quest,
					ConnectionCursorUtil.encodeCursor("arrayconnection:" + questionnaires.indexOf(quest)));
		}).collect(Collectors.toUnmodifiableList());

		// Genera variables generales del paginador
		ConnectionCursor startCursor = listEdges.isEmpty() ? null : listEdges.get(0).getCursor();
		ConnectionCursor endCursor = listEdges.isEmpty() ? null : listEdges.get(listEdges.size() - 1).getCursor();
		boolean hasPreviousPage = input.after() != null;
		boolean hasNextPage = input.first() != null && questionnairesFiltered.size() > input.first();

		List<CompetencesEvaluationDTO> evaluations=competencesEvaluationsDAO
				.getCompetencesEvaluationsByEmployee(input.employee_id(), null, null, input.evaluation_id());
		// Información de la evaluación
		CompetencesEvaluationDTO evaluationDTO =evaluations.size()>0? evaluations.get(0) : null;
		if (questionnaires != null && questionnaires.size() > 0) {
			evaluationDTO = competencesEvaluationsDAO.
					getCompetencesEvaluationsByEmployee(input.employee_id(), null, null, input.evaluation_id()).get(0);			
		}

		
		// Retorna información completa
		QuestionnaireGeneralPagination pagination = new QuestionnaireGeneralPagination(
				evaluationDTO != null ? convertToCompetencesEvaluation(evaluationDTO) : null,
				new ConnectionUtil<Questionnaire>(questionnaires.size(), listEdges,
						new PageInfoUtil(startCursor, endCursor, hasPreviousPage, hasNextPage)));

		return pagination;

	}

	/**
	 * Método que convierte una lista de Objects a Questionaires
	 */
	private List<Questionnaire> createQuestionnaireList(List<Object[]> all, Long employeeId) {

		List<Questionnaire> questionnaires = new ArrayList<>();

		questionnaires = all.stream().map(ques -> getNewQuestionnaire(ques, employeeId)).collect(Collectors.toList());

		return questionnaires;
	}

	/**
	 * Método que crea un Questionnaire a partir de un Object[]
	 * 
	 */
	private Questionnaire getNewQuestionnaire(Object[] data, Long employeeId) {

		QuestionnaireStateEnum state = QuestionnaireStateEnum.valueOf((String) data[1]);
		String avatarUrl = "/fotoemp?id=" + data[2];
		AvatarTalentType avatarType = new AvatarTalentType(competencesUtilService.getUrlEnviroment() + avatarUrl, avatarUrl);
		DivisionTalentType divisionType = new DivisionTalentType(getLong(data[4]), (String) data[5]);
		JobTalentType jobType = new JobTalentType(getLong(data[10]), (String) data[11]);
		EmployeeCompetencesEvaluation evaluated = new EmployeeCompetencesEvaluation(competencesUtilService.createNodeID("EmployeeCompetencesEvaluation", getLong(data[2])),
				getLong(data[2]), avatarType, (String) (data[3]), divisionType, jobType);
		String relation = changeRelationQuestionnaire((String) data[6]);
		String relationLabel = getRelationLabel(relation, employeeId);
		String similarCompetencesKey = data[6] != null ? (String) data[7] : "";

		Integer countQuestions = ((BigInteger) data[8]).intValue();
		Integer countResponses = ((BigInteger) data[9]).intValue();

		Double progress = countQuestions != null && countQuestions > 0
				? (double) ((countResponses * 100) / countQuestions)
				: 0;
		Questionnaire quest = new Questionnaire(competencesUtilService.createNodeID("Questionnaire", getLong(data[0])), getLong(data[0]), state, evaluated, relationLabel,
				RelationQuestionnaireEnum.valueOf(relation), similarCompetencesKey, progress, (String) data[12], null, null);

		return quest;

	}

	/**
	 * Método que intercambia las relaciones de Boss y Subordinate
	 */
	private String changeRelationQuestionnaire(String relation) {

		switch (relation) {
		case "BOSS":
			return "SUBORDINATE";
		case "SUBORDINATE":
			return "BOSS";
		default:
			return relation;
		}
	}
	
	/**
	 * Crea un nuevo objeto Questionnaire que representa un cuestionario agrupador.
	 * Este cuestionario agrupa una lista de cuestionarios y calcula su estado y
	 * progreso en función de la cantidad de cuestionarios finalizados y totales en el grupo.
	 *
	 * @param questionnaireList La lista de cuestionarios a agrupar.
	 * @param key               La clave de grupo utilizada para la agrupación.
	 * @return Un nuevo objeto Questionnaire que representa el cuestionario agrupador.
	 */
	private Questionnaire integrateQuestionnaires(List<Questionnaire> questionnaireList, String key) {
		// Calcula la cantidad total de cuestionarios y la cantidad de cuestionarios finalizados
		int countQuestions = questionnaireList.size();
		int countFinished = (int) questionnaireList.stream().filter(q -> q.state() == QuestionnaireStateEnum.FINISHED)
				.count();

		// Determina el estado del cuestionario agrupador en función de la cantidad de cuestionarios finalizados
		QuestionnaireStateEnum state;
		if (countFinished == countQuestions) {
			state = QuestionnaireStateEnum.FINISHED;
		} else if (countFinished == 0) {
			state = QuestionnaireStateEnum.ASSIGNED;
		} else {
			state = QuestionnaireStateEnum.IN_PROGRESS;
		}
		// Calcula el progreso del cuestionario agrupador según los cuestionarios finalizados y totales
		Double progress = countQuestions > 0 ? (double) ((countFinished * 100) / countQuestions) : 0;
		Questionnaire quest = new Questionnaire(
				competencesUtilService.createNodeID("Group", questionnaireList.get(0).original_id()),
				questionnaireList.get(0).original_id(), state, null, "", null, "", progress, key, questionnaireList, null);

		return quest;
	}

	/**
	 * Obtiene la etiqueta de la relación traducida en el idioma especificado
	 * 
	 * @param relation   Código de la relación que se quiere traducir
	 * @param employeeId Identificador del empleado
	 */
	private String getRelationLabel(String relation, Long employeeId) {
		Optional<Employee> employeeOpt = employeeRepository.findById(employeeId);
		if (employeeOpt.isPresent()) {
			Label labelRelation = getLabelSubject(relation.toLowerCase(), employeeOpt.get().getCompany(), "common");
			String languageCode = employeeOpt.get().getPerson().getLanguage() != null
					? employeeOpt.get().getPerson().getLanguage()
					: employeeOpt.get().getCompany().getLanguageCode() != null
							? employeeOpt.get().getCompany().getLanguageCode()
							: "es";
			return competencesUtilService.getLabelByLanguage(languageCode, labelRelation);
		}
		return "";
	}

	/**
	 * Obtiene etiqueta según su código y módulo al que pertenece
	 * 
	 * @param code    Código etiqueta
	 * @param company Entidad que contiene la información de empresa
	 * @param module  Módulo al que pertenece la etiqueta
	 */
	public Label getLabelSubject(String code, Company company, String module) {
		Label labelSubject = null;
		labelSubject = labelRepository.findByCompanyIdAndModuleAndCode(company.getId(), module, code);
		if (labelSubject == null) {
			labelSubject = labelRepository.findByModuleCode("common", code);
		}
		return labelSubject;
	}
	
	/**
	 * Obtiene la configuración de la evaluación de competencias 
	 * 
	 * @param evaluation_id Identificador de la evaluación
	 * @param employee_id Identificador del empleado
	 */
	public ResponseCompetencesConfiguration getCompetencesEvaluationConfiguration(Long evaluation_id, Long employee_id) {
		Optional<Evaluation> evaluationOpt = evaluationRepository.findById(evaluation_id);
		
		if (evaluationOpt.isPresent()) {
			Evaluation evaluation = evaluationOpt.get();
			CompetencesEvaluationConfiguration evaluationConfiguration = new CompetencesEvaluationConfiguration(
					evaluation.getCommentCompetence() != null ? evaluation.getCommentCompetence() : false, // show_comment_competence
					evaluation.getGeneralSuggestion() != null ? evaluation.getGeneralSuggestion() : false, // show_comment_general
					false, // required_comment_competence
					false, // required_comment_general
					evaluation.getMinCommentLength() != null ? evaluation.getMinCommentLength() : 0, // min_comment_length
					evaluation.getModelCompetenceWeight() != null ? evaluation.getModelCompetenceWeight() : false, // has_active_behaviors
					null, // options_response
					competencesUtilService.companyHasConfiguration(evaluation.getCompany(), 210L), // has_description_active
					competencesUtilService.companyHasConfiguration(evaluation.getCompany(), 191L), // show_autoevaluations
					evaluation.getAffirmationSuggestion() != null ? evaluation.getAffirmationSuggestion() : false, // show_comment_behavior
					false, // required_comment_behavior
					evaluation.getDontknow() != null ? evaluation.getDontknow() : false, // show_dont_know
					evaluation.getGeneralSuggestion2() != null ? evaluation.getGeneralSuggestion2() : false, // show_comment_general2
					false, // required_comment_general2
					evaluation.getGeneralSuggestion3() != null ? evaluation.getGeneralSuggestion3() : false, // show_comment_general3
					false, // required_comment_general3
					null, // label_comment
					null, // label_comment2
					null, // label_comment3
					null, // options_explanations
					competencesUtilService.companyHasConfiguration(evaluation.getCompany(), 259L), // show_evaluators_icon
					competencesUtilService.companyHasConfiguration(evaluation.getCompany(), 252L), // hide_add_evaluators_leader
					competencesUtilService.companyHasConfiguration(evaluation.getCompany(), 216L), // show_edit_evaluation
					evaluation.getEnabledResultEvaluate() != null ? evaluation.getEnabledResultEvaluate() : false, // enable_result_evaluate
					evaluation.getShowAllRelations() != null ? evaluation.getShowAllRelations() : false, // show_all_relations
					competencesUtilService.companyHasConfiguration(evaluation.getCompany(), 267L), // assign_competences_by_leader
					competencesUtilService.companyHasConfiguration(evaluation.getCompany(), 122L), // show_competences_brief
					getRelationsByEvaluations(evaluation_id, employee_id),
					competencesUtilService.companyHasConfiguration(evaluation.getCompany(), 250L), // show tab evaluators
					competencesUtilService.companyHasConfiguration(evaluation.getCompany(), 213L), // show_my_team_tab
					competencesUtilService.companyHasConfiguration(evaluation.getCompany(), 285L) // disable_mass_qualification
			);

			// Si están activas la visualización de comentarios, buscamos si son requeridos
			if ((evaluation.getCommentCompetence() != null || evaluation.getGeneralSuggestion() != null
					|| evaluation.getAffirmationSuggestion() != null || evaluation.getGeneralSuggestion2() != null
					|| evaluation.getGeneralSuggestion3() != null)
					&& (evaluation.getRequiredComments() != null && !evaluation.getRequiredComments().isEmpty())) {
				Gson gson = new Gson();
				@SuppressWarnings("unchecked")
				Map<String, String> map = gson.fromJson(evaluation.getRequiredComments(), Map.class);
				evaluationConfiguration = evaluationConfiguration.withRequiredComments(
						map.get("AFFCOMPETENCE") != null ? true : false, map.get("AFFGENERAL") != null ? true : false,
						map.get("AFFQUESTION") != null ? true : false, map.get("AFFGENERAL2") != null ? true : false,
						map.get("AFFGENERAL3") != null ? true : false);
			}

			String language = evaluation.getCompany().getLanguageCode();
			Optional<Employee> evaluated = null;
			if (employee_id != null) {
				evaluated = employeeRepository.findById(employee_id);
			}

			// Si la evaluación tiene la configuración de multi-idioma, debe tener en cuenta el idioma del empleado, sino el de la empresa
			if (competencesUtilService.companyHasConfiguration(evaluation.getCompany(), 77L) && evaluated != null
					&& evaluated.get().getPerson().getLanguage() != null) {
				language = evaluated.get().getPerson().getLanguage();
			}
			// Si tiene modelo por comportamientos, busca las opciones de respuesta
			if (!evaluationConfiguration.has_active_behaviors_model()) {
				evaluationConfiguration = evaluationConfiguration.withOptionsResponse(
						getOptionsConfiguredByEvaluation(evaluation, evaluation.getCompany(), language));
			}

			// Se buscan los labels de los comentarios siempre y cuando se estén activos para mostrar
			if (evaluationConfiguration.show_comment_general()) {
				evaluationConfiguration = evaluationConfiguration.withLabelComment(findLabelComments(evaluation, "GENERAL_SUGGESTION", language));
			}
			if (evaluationConfiguration.show_comment_general2()) {
				evaluationConfiguration = evaluationConfiguration.withLabelComment2(findLabelComments(evaluation, "ADITIONAL_SUGGESTION", language));
			}
			if (evaluationConfiguration.show_comment_general3()) {
				evaluationConfiguration = evaluationConfiguration.withLabelComment3(findLabelComments(evaluation, "ADITIONAL_SUGGESTION_THREE_BOX", language));
			}
			// Se busca si tiene explicación de las opciones
			if (evaluation.getOptionsExplanations() != null && !evaluation.getOptionsExplanations().trim().isEmpty()) {
				evaluationConfiguration = evaluationConfiguration.withOptionsExplanations(evaluation.getOptionsExplanations());
			}

			return evaluationConfiguration;
		} else {
			return CompetencesValidationErrorUtil.getResponseValidationError(CompetencesValidationErrorUtil.EVALUATION_NOT_FOUND);
		}
	}

	/**
	 * Obtiene listado de opciones de configuración de evaluación (Escalas, labels)
	 */	
	public List<OptionResponse> getOptionsConfiguredByEvaluation(Evaluation evaluation, Company company, String lang) {

		Gson gson = new Gson();

		// Obtener configuración como lista de mapas
		List<Map<String, Object>> tmpList = null;
		if (evaluation.getConfiguration() != null) {
			Type listType = new TypeToken<List<Map<String, Object>>>(){}.getType();
			tmpList = gson.fromJson(evaluation.getConfiguration(), listType);

		}

		// Lista para almacenar las opciones
		List<OptionResponse> optionsDetail = new ArrayList<>();

		// Si la lista no está vacía, procesar las opciones
		if (tmpList != null && !tmpList.isEmpty()) {
			for (Map<String, Object> map : tmpList) {

				// Obtener valores según el idioma
				String key = (String) map.get("key");
				String label = "";
				switch (lang) {
				case "es":
					label = (String) map.get("label");
					if (label.trim().isEmpty()) {
						label = key;
					}
					break;
				case "pt":
					label = (String) map.get("ptlabel");
					if (label.trim().isEmpty()) {
						label = key;
					}
					break;
				case "en":
					label = (String) map.get("enlabel");
					if (label.trim().isEmpty()) {
						label = key;
					}
					break;
				}

				// Asignar valores a la opción
				OptionResponse opt = new OptionResponse(label, key, (String) map.get("value"),
						map.get("percentage") == null ? "0.0" : (String) map.get("percentage").toString(), 
						(String) map.get("parentKey"), (Boolean) map.get("other"),
						(Integer) map.get("order"));

				// Agregar la opción a la lista
				optionsDetail.add(opt);
			}
		}

		return optionsDetail;
	}

	/**
	 * Método que busca los labels configurados para un tipo de comentario especifico 
	 * (General o los adicionales) u otro label diferente
	 **/
	private String findLabelComments(Evaluation evaluation, String commentType, String languageCode) {

		String evaluationType = EvaluationType.THREESIXTY.toString();

		String code = evaluationType.toString().concat("_").concat(evaluation.getId() + "_").concat(commentType);
		LabelFlex label = labelFlexRepository.findByLanguageCodeAndCode(languageCode, code);

		if (label == null) {
			code = evaluationType.concat("_DEFAULT_").concat(commentType);
			label = labelFlexRepository.findByLanguageCodeAndCode(languageCode, code);
		}
		return label.getLabel();
	}

	/**
	 * Obtiene listado de relaciones por evaluacion
	 * 
	 * @param evaluationId Identificador de la evaluación
	 * @param employeeId Identificador del empleado, para determinar idioma
	 */
	public List<RelationLabelType> getRelationsByEvaluations(Long evaluationId, Long employeeId) {
		List<RelationLabelType> relations = new ArrayList<RelationLabelType>();
		List<String> relationList = questionnaireRepository.getRelationByEvaluationId(evaluationId);
		if (relationList.isEmpty()) {
			return null;
		} else {
			relationList.stream().forEach(r -> {
				RelationLabelType relationType = new RelationLabelType(RelationQuestionnaireEnum.valueOf(r), getRelationLabel(r, employeeId));
				relations.add(relationType);
			});
		}
		return relations;
	}	
	
	/**
	 * Crea un grupo de cuestionarios de competencias asociados a una evaluación y
	 * evaluador específicos
	 * 
	 * @param input Un objeto de filtro que contiene la información necesaria para la creación del grupo
	 * @return Una respuesta que indica si la operación se realizó con éxito
	 * @throws NoSuchAlgorithmException     Si el algoritmo SHA-256 no está disponible
	 * @throws UnsupportedEncodingException Si la codificación UTF-8 no está soportada
	 */
	@Transactional
	public ResponseExecutionCompetences createCompetenceQuestionnaireGroup(FilterCompetencesGroup input)
			throws NoSuchAlgorithmException, UnsupportedEncodingException {

		// Validar la existencia de la evaluación
		Optional<Evaluation> evaluationOpt = evaluationRepository.findById(input.evaluation_id());
		if (evaluationOpt.isEmpty()) {
			return CompetencesValidationErrorUtil
					.getResponseValidationError(CompetencesValidationErrorUtil.EVALUATION_NOT_FOUND);
		}

		// Validar la existencia del empleado (evaluador)
		Optional<Employee> employeeOpt = employeeRepository.findById(input.employee_id());
		if (employeeOpt.isEmpty()) {
			return CompetencesValidationErrorUtil
					.getResponseValidationError(CompetencesValidationErrorUtil.EMPLOYEE_NOT_FOUND);
		}

		// Generar una clave única para el grupo utilizando SHA-256
		MessageDigest salt = MessageDigest.getInstance("SHA-256");
		salt.update(UUID.randomUUID().toString().getBytes("UTF-8"));
		String groupKey = bytesToHex(salt.digest());
		
		// Verificar si algún cuestionario ya existe en un grupo
		boolean questionnaireFoundInGroup = input.questionnaires_ids().stream()
	            .anyMatch(questionnaireId -> competenceQuestionnaireGroupRepository.findByQuestionnaireId(questionnaireId));

	    if (questionnaireFoundInGroup) {
	        return CompetencesValidationErrorUtil.getResponseValidationError(
	                CompetencesValidationErrorUtil.QUESTIONNAIRE_FOUND_IN_GROUP);
	    }

	    // Crear y guardar objetos de la agrupación
	    List<CompetenceQuestionnaireGroup> questionnaireGroups = input.questionnaires_ids().stream()
	            .map(questionnaireId -> {
	                CompetenceQuestionnaireGroup questionnaireGroup = new CompetenceQuestionnaireGroup();
	                questionnaireGroup.setEvaluation(evaluationOpt.get());
	                questionnaireGroup.setEvaluator(employeeOpt.get());
	                questionnaireGroup.setQuestionnaire(questionnaireRepository.findById(questionnaireId).get());
	                questionnaireGroup.setGroupKey(groupKey);
	                return questionnaireGroup;
	            })
	            .collect(Collectors.toList());

	    competenceQuestionnaireGroupRepository.saveAll(questionnaireGroups);
	    
		return new ExecutedCompetencesPayload(Boolean.TRUE);
	}

	/**
	 * Convierte un arreglo de bytes en una cadena hexadecimal
	 * 
	 * @param bytes El arreglo de bytes a convertir
	 * @return La cadena hexadecimal representando los bytes
	 */
	private String bytesToHex(byte[] bytes) {
		char[] hexChars = new char[bytes.length * 2];
		for (int j = 0; j < bytes.length; j++) {
			int v = bytes[j] & 0xFF;
			hexChars[j * 2] = hexArray[v >>> 4];
			hexChars[j * 2 + 1] = hexArray[v & 0x0F];
		}
		return new String(hexChars);
	}

	/**
	 * Elimina un grupo de cuestionarios de competencias asociado a una evaluación
	 *
	 * @param evaluationId Identificador único de la evaluación
	 * @param groupKey     Clave única que identifica el grupo de cuestionarios a eliminar
	 * @return Contiene la respuesta sobre el éxito de la operación
	 */
	public ResponseExecutionCompetences deleteCompetenceQuestionnaireGroup(Long evaluationId, String groupKey) {
		
		try {
			// Validar la existencia de la evaluación
			Optional<Evaluation> evaluationOpt = evaluationRepository.findById(evaluationId);
			if (evaluationOpt.isEmpty()) {
				return CompetencesValidationErrorUtil.getResponseValidationError(CompetencesValidationErrorUtil.EVALUATION_NOT_FOUND);
			}
			
			// Validar la existencia de la llave del grupo
			if (!competenceQuestionnaireGroupRepository.existsByGroupKey(groupKey)) {
				return CompetencesValidationErrorUtil.getResponseValidationError(CompetencesValidationErrorUtil.GROUP_KEY_NOT_FOUND);
			}
			
			// Cambio de estado de los grupos de cuestionarios a "DELETED"
			List<CompetenceQuestionnaireGroup> questionnaireGroupList = competenceQuestionnaireGroupRepository.findByGroupKey(groupKey);
			questionnaireGroupList.stream().forEach(questionnaireG -> {
				questionnaireG.setState(EntityState.DELETED);
				competenceQuestionnaireGroupRepository.save(questionnaireG);
			});
			
			return new ExecutedCompetencesPayload(Boolean.TRUE);			
		} catch (Exception e) {
			return new ExecutedCompetencesPayload(Boolean.FALSE);	
		}
	}
	
	
	
	
	/**
	 * Método que devuelve el resultado o detalle por competencias y preguntas
	 *  
	 **/
	public CompetencesDetail  getQuestionnairesDetail(Long questionnaireId, Long employeeId){
		
		List<Competence> competences=new ArrayList<>();
		
		Optional<com.acsendo.api.competences.model.Questionnaire> questionnaire = questionnaireRepository.findById(questionnaireId);
		
		if(questionnaire.isPresent()) {
			
			List<Question2> questions=questionRepository.findByQuestionnaireId(questionnaireId);
			
			String language=questionnaire.get().getEvaluation().getCompany().getLanguageCode();
			Optional<Employee> employee=employeeRepository.findById(employeeId);
			//Preguntamos si la evaluación tiene la configuración multiidiomas, si es así debe tener en cuenta el idioma del empleado, sino el de la empresa
			if( employee.isPresent() && questionDao.companyHasConfiguration(questionnaire.get().getEvaluation().getCompany(), 77L) && employee.get().getPerson().getLanguage() != null ) {
				language=employee.get().getPerson().getLanguage();
			}
			
			List<Long> questionnairesId=new ArrayList<>();
			questionnairesId.add(questionnaireId);
			//Resultados por competencias
			List<Object[]> resultsComp=questionDao.getCompetencesResultByQuestionnaire(language, questionnairesId, questionnaire.get().getEvaluation());
			List<Object[]> resultIndividualEmployee=performanceResultDAO.getEmployeesResultsByQuestionnaires(questionnairesId, questionnaire.get().getEvaluation());
	        
			//Listado de comentarios
			List<CommentCompetence> commentsCompetences = commentRepository.findByQuestionnaireId(questionnaireId);
			
			Integer positionResultComp=4;
			Integer positionResultEmp=3;
			if(questionnaire.get().getEvaluation().getCompany().getCompetencesResultFormat().equals("SCALE")) {
				   positionResultComp=3;
				   positionResultEmp=2;
			}
			
			DecimalFormat df = new DecimalFormat("#.##");
			DecimalFormatSymbols dfs = DecimalFormatSymbols.getInstance();
			dfs.setDecimalSeparator('.');
			df.setDecimalFormatSymbols(dfs);
			
	
			for(Object[] object: resultsComp){
			
				String id=competencesUtilService.createNodeID("Competence", getLong(object[0]));
				Long original_id=getLong(object[0]);
				String name=(String) object[1];
				Double res= getDouble(object[positionResultComp]);
				String result = df.format(res);
			
			  List<Question> questiosCompetence=questions.stream().filter(q-> q.getBehavior().getCompetenceLevel().getCompetence().getId()==original_id)
					  .map(q->new Question(competencesUtilService.createNodeID("Question", q.getId()), q.getId(), q.getDescription(), q.getResponse(), q.getSuggestions()==null? "" :q.getSuggestions()))
						   .collect(Collectors.toList());
			   
			   Optional<CommentCompetence> commentOpt=commentsCompetences.stream().filter(c-> c.getCompetence().getId()==original_id).findFirst(); 
			   String comment="";
			   if(commentOpt.isPresent()) {
				   comment=commentOpt.get().getComments();
			   }
			  
			   Competence competence=new Competence(id, original_id, name, result, questiosCompetence, comment);
			   competences.add(competence);
			
			}	

			Double resultE=resultIndividualEmployee!=null && resultIndividualEmployee.size()>0? getDouble(resultIndividualEmployee.get(0)[positionResultEmp]) : 0.0;
			String resultEmployee = df.format(resultE);
			
			return new CompetencesDetail(resultEmployee, competences, questionnaire.get().getSuggestions(), questionnaire.get().getSuggestions2(), questionnaire.get().getSuggestions3());
		
		}
		
		return new CompetencesDetail(null, null, null, null, null);
		
	}
	

}
