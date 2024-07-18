package com.acsendo.api.competences.service;

import static com.acsendo.api.util.DataObjectUtil.getDouble;
import static com.acsendo.api.util.DataObjectUtil.getLong;
import static com.acsendo.api.util.DataObjectUtil.getString;

import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.acsendo.api.competences.dao.CompetencesQuestionDAO;
import com.acsendo.api.competences.enumerations.EvaluationStateEnum;
import com.acsendo.api.competences.enumerations.QualifyTypeEnum;
import com.acsendo.api.competences.enumerations.QuestionnaireStateEnum;
import com.acsendo.api.competences.enumerations.RelationQuestionnaireEnum;
import com.acsendo.api.competences.interfaces.ResponseCompetenceGeneralForm;
import com.acsendo.api.competences.interfaces.ResponseExecutionCompetences;
import com.acsendo.api.competences.interfaces.ResponseQualifySummary;
import com.acsendo.api.competences.interfaces.ResponseQuestionnaire;
import com.acsendo.api.competences.interfaces.ResponseQuestionnaireQualify;
import com.acsendo.api.competences.model.Behavior;
import com.acsendo.api.competences.model.CommentCompetence;
import com.acsendo.api.competences.model.Competence;
import com.acsendo.api.competences.model.Question2;
import com.acsendo.api.competences.record.AvatarTalentType;
import com.acsendo.api.competences.record.BehaviorForm;
import com.acsendo.api.competences.record.CommentForm;
import com.acsendo.api.competences.record.CompetenceForm;
import com.acsendo.api.competences.record.CompetenceGeneralForm;
import com.acsendo.api.competences.record.CompetenceSummary;
import com.acsendo.api.competences.record.CompetencesEvaluation;
import com.acsendo.api.competences.record.DivisionTalentType;
import com.acsendo.api.competences.record.EmployeeCompetencesEvaluation;
import com.acsendo.api.competences.record.EmployeeTalentPagination;
import com.acsendo.api.competences.record.EmployeeTalentType;
import com.acsendo.api.competences.record.Evaluator;
import com.acsendo.api.competences.record.ExecutedCompetencesPayload;
import com.acsendo.api.competences.record.FilterCompetencesQuestionnaireEvaluator;
import com.acsendo.api.competences.record.JobTalentType;
import com.acsendo.api.competences.record.QualifyPayload;
import com.acsendo.api.competences.record.QualifySummary;
import com.acsendo.api.competences.record.QuestionForm;
import com.acsendo.api.competences.record.Questionnaire;
import com.acsendo.api.competences.record.QuestionnaireEvaluators;
import com.acsendo.api.competences.record.QuestionnaireGeneralPagination;
import com.acsendo.api.competences.record.QuestionnaireQualify;
import com.acsendo.api.competences.record.QuestionnaireSummary;
import com.acsendo.api.competences.repository.CommentCompetenceRepository;
import com.acsendo.api.competences.repository.Question2Repository;
import com.acsendo.api.competences.repository.QuestionnaireRepository;
import com.acsendo.api.customReports.util.LambdaRedshiftUtil;
import com.acsendo.api.hcm.dao.TalentUtilDAO;
import com.acsendo.api.hcm.enumerations.QuestionnaireState;
import com.acsendo.api.hcm.model.Company;
import com.acsendo.api.hcm.model.Employee;
import com.acsendo.api.hcm.model.Evaluation;
import com.acsendo.api.hcm.model.Label;
import com.acsendo.api.hcm.model.LabelFlex;
import com.acsendo.api.hcm.model.User;
import com.acsendo.api.hcm.repository.EmployeeRepository;
import com.acsendo.api.hcm.repository.EvaluationRepository;
import com.acsendo.api.hcm.repository.LabelFlexRepository;
import com.acsendo.api.hcm.repository.LabelRepository;
import com.acsendo.api.hcm.repository.UserRepository;
import com.acsendo.api.results.dao.PerformanceResultsDAO;
import com.acsendo.api.util.CompetencesValidationErrorUtil;
import com.acsendo.api.util.ConnectionCursorUtil;
import com.acsendo.api.util.ConnectionUtil;
import com.acsendo.api.util.PageInfoUtil;
import com.google.gson.Gson;

import graphql.relay.ConnectionCursor;
import graphql.relay.DefaultEdge;
import graphql.relay.Edge;

@Service
public class CompetencesQuestionnaireService {

	@Autowired
	private QuestionnaireRepository questionnaireRepository;

	@Autowired
	private Question2Repository questionRepository;

	@Autowired
	private EmployeeRepository employeeRepository;

	@Autowired
	private LabelRepository labelRepository;

	@Autowired
	private CompetencesUtilService competencesUtilService;
	
	@Autowired
	private EvaluationRepository evaluationRepository;
	
	@Autowired
	private CompetencesQuestionDAO questionDao;
	
	@Autowired
	private TalentUtilDAO talentUtilDAO;
	
	@Autowired
	private LabelFlexRepository labelFlexRepository;
	
	@Autowired
	private CommentCompetenceRepository commentCompetenceRepository;
	
	@Autowired
	private PerformanceResultsDAO performanceResultDAO;
	
	@Autowired
	private UserRepository userRepository;
	
	@Autowired
	private LambdaRedshiftUtil lambdaRedshiftUtil;
	
	
	private static final char[] hexArray = "0123456789ABCDEF".toCharArray();
   
	
	/**
	 * Método para eliminar un questionnaire por su identificador
	 * 
	 * @param questionnaireId, Identificador del questionnaire
	 */
	public ResponseExecutionCompetences deleteQuestionnaire(Long questionnaireId) {

		Optional<com.acsendo.api.competences.model.Questionnaire> opt = questionnaireRepository.findById(questionnaireId);

		if (opt.isPresent()) {

			com.acsendo.api.competences.model.Questionnaire questionnaire = opt.get();
			questionnaire.setState(QuestionnaireState.CANCELED);
			questionnaireRepository.save(questionnaire);
			// List<Question2> questions=
			// questionRepository.findByQuestionnaire(questionnaire);
			// questionRepository.deleteAllInBatch(questions);
			return new ExecutedCompetencesPayload(Boolean.TRUE);

		} else {
			return CompetencesValidationErrorUtil
					.getResponseValidationError(CompetencesValidationErrorUtil.QUESTIONNAIRE_NOT_FOUND);
		}

	}


	
	/**
	 * Obtiene los cuestionarios de competencias que son evaluadores
	 * 
	 * @param evaluationId Identificador de la evaluación.
	 * @param employeeId   Identificador del empleado logueado
	 * @param evaluatedId  Identificador del empleado que se quiere consultar sus
	 *                     evaluadores.
	 * @param first        Indica la cantidad de resgistros a devolver
	 * @param after        Indica el cursor desde donde se deben devolver las demás
	 */
	public ResponseQuestionnaire getCompetencesQuestionnairesEvaluators(Long evaluationId, Long employeeId, Long evaluatedId,
			Integer first, String after) {
		
		// Obtiene los cuestionarios del repositorio
		List<Object[]> questionnaires = questionnaireRepository.getCompetencesQuestionnaireEvaluators(evaluationId,
				evaluatedId);
		List<Questionnaire> questionnairesFiltered = new ArrayList<>();
		
		// Filtra y convierte los cuestionarios a un formato compatible
		if (questionnaires != null && questionnaires.size() > 0) {
			String cursorComplete = after != null ? ConnectionCursorUtil.decodeCursor(after) : null;
			String[] split = after != null ? cursorComplete.split(":") : null;
			Integer positionAfter = cursorComplete != null ? split != null ? Integer.parseInt(split[1]) : null : null;

			questionnairesFiltered = questionnaires.stream()
					.filter(tem -> cursorComplete == null || questionnaires.indexOf(tem) > positionAfter)
					.map(quest -> convertQuestionnaireObjectToRecord(quest, evaluatedId, employeeId)).collect(Collectors.toList());
		}

		List<Edge<Questionnaire>> listEdges = questionnairesFiltered.stream().limit(first!=null?first : questionnairesFiltered.size()).map(questi -> {
		    Optional<Object[]> optionalMatch = questionnaires.stream()
		            .filter(questionnaire -> questi.original_id().equals(getLong(questionnaire[0])))
		            .findFirst();
		    int index = optionalMatch.map(questionnaires::indexOf).orElse(-1); 
		    return new DefaultEdge<>(questi,
		            ConnectionCursorUtil.encodeCursor("arrayconnection:" + index));
		}).collect(Collectors.toUnmodifiableList());


		// Genera variables generales del paginador
		ConnectionCursor startCursor = listEdges.isEmpty() ? null : listEdges.get(0).getCursor();
		ConnectionCursor endCursor = listEdges.isEmpty() ? null : listEdges.get(listEdges.size() - 1).getCursor();
		boolean hasPreviousPage = after != null;
		boolean hasNextPage = first != null && questionnairesFiltered.size() > first;

		// Retorna información completa
		QuestionnaireGeneralPagination pagination = new QuestionnaireGeneralPagination(null,
				new ConnectionUtil<com.acsendo.api.competences.record.Questionnaire>(questionnaires.size(), listEdges,
						new PageInfoUtil(startCursor, endCursor, hasPreviousPage, hasNextPage)));
		return pagination;
	}

	/**
	 * Convierte un objeto cuestionario a un registro de cuestionario.
	 *
	 * @param quest       Objeto cuestionario a convertir.
	 * @param employeeId  Identificador del empleado logueado
	 * @param evaluatedId Identificador del empleado que se quiere consultar sus
	 *                    evaluadores.
	 * @return Cuestionario convertido.
	 */
	private Questionnaire convertQuestionnaireObjectToRecord(Object[] quest, Long evaluatedId, Long employeeId) {
		QuestionnaireStateEnum state = QuestionnaireStateEnum.valueOf(quest[1].toString());
		String avatarUrl = "/fotoemp?id=" + quest[2];
		AvatarTalentType avatarType = new AvatarTalentType(competencesUtilService.getUrlEnviroment() + avatarUrl,
				avatarUrl);
		DivisionTalentType divisionType = new DivisionTalentType(getLong(quest[7]), (String) quest[8]);
		JobTalentType jobType = new JobTalentType(getLong(quest[5]), (String) quest[6]);
		EmployeeCompetencesEvaluation evaluator = new EmployeeCompetencesEvaluation(
				competencesUtilService.createNodeID("EmployeeCompetencesEvaluation", getLong(quest[2])),
				getLong(quest[2]), avatarType, (String) quest[3], divisionType, jobType);
		String relation = changeRelationQuestionnaire((String) quest[4]);
		String relationLabel = getRelationLabel(relation, evaluatedId);
		Boolean allowDelete = quest[9] != null ? getString(quest[9]).equals(employeeId.toString()) : Boolean.FALSE;
		
		Questionnaire questionnaire = new Questionnaire(
				competencesUtilService.createNodeID("Questionnaire", getLong(quest[0])), getLong(quest[0]), state,
				evaluator, relationLabel, RelationQuestionnaireEnum.valueOf(relation), "", 0.0, "", null,
				allowDelete);

		return questionnaire;
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
	
	
	
	
	/**Método que crea uno o varios questionnaires, según la información 
	 * @throws UnsupportedEncodingException 
	 * @throws NoSuchAlgorithmException */
	public ResponseExecutionCompetences createQuestionnaire(QuestionnaireEvaluators input) throws NoSuchAlgorithmException, UnsupportedEncodingException {
		
	
		Optional<Employee> evaluated=employeeRepository.findById(input.evaluated_id());

		Optional<Evaluation> evaluation= evaluationRepository.findById(input.evaluation_id());
		
		if(evaluation.isPresent() && evaluated.isPresent()) {
			
			for(Evaluator evaluator: input.evaluators()) {
				
				com.acsendo.api.competences.model.Questionnaire questionnaire=questionnaireRepository.
						findFirstByEvaluationIdAndEvaluatedIdAndEvaluatorId(evaluation.get().getId(), evaluated.get().getId(),  evaluator.id());
				
				if(questionnaire==null) {
					questionnaire=new com.acsendo.api.competences.model.Questionnaire();
					questionnaire.setState(QuestionnaireState.ASSIGNED);
					questionnaire.setEvaluation(evaluation.get());
					questionnaire.setEvaluated(evaluated.get());
					questionnaire.setRelation(evaluator.relation().toString());
					Employee evaluatorEmp=new Employee();
					evaluatorEmp.setId(evaluator.id());
					questionnaire.setEvaluator(evaluatorEmp);
					questionnaire.setOwner(input.employee_id().toString());
				
					//Guardamos el questionnaire
					questionnaire=questionnaireRepository.save(questionnaire);
					//Creamos las preguntas e identificamos competencias silimares
					createQuestionsAndIdentifySimilarCompetencesKey(questionnaire);
				}else {
					return CompetencesValidationErrorUtil
							.getResponseValidationError(CompetencesValidationErrorUtil.QUESTIONNAIRE_ALREADY_EXISTS);
				}
				
			}
		   
			return new ExecutedCompetencesPayload(Boolean.TRUE);
			
		}else {
			if(!evaluation.isPresent()) {
			return CompetencesValidationErrorUtil
					.getResponseValidationError(CompetencesValidationErrorUtil.EVALUATION_NOT_FOUND);
			}else if(!evaluated.isPresent()) {
				return CompetencesValidationErrorUtil
						.getResponseValidationError(CompetencesValidationErrorUtil.EMPLOYEE_NOT_FOUND);
			}
		}
		
		 return new ExecutedCompetencesPayload(Boolean.FALSE);
		
	}
	
	
	/**
	 * Método que crea las preguntas del questionnaire
	 * @param Questionnaire
	 * */
	private void createQuestionsAndIdentifySimilarCompetencesKey(com.acsendo.api.competences.model.Questionnaire questionnaire) throws NoSuchAlgorithmException, UnsupportedEncodingException {
		
	
		String language=questionnaire.getEvaluation().getCompany().getLanguageCode();
		//Preguntamos si la evaluación tiene la configuración multiidiomas, si es así debe tener en cuenta el idioma del empleado, sino el de la empresa
		if(questionDao.companyHasConfiguration(questionnaire.getEvaluation().getCompany(), 77L) && questionnaire.getEvaluated().getPerson().getLanguage() != null ) {
			language=questionnaire.getEvaluated().getPerson().getLanguage();
			
		}
		
		List<Question2> questions=questionRepository.findByQuestionnaireId(questionnaire.getId());
		questions = createQuestions(questions, questionnaire, language);
		if(questions.size()>0) {
			
			//Se analiza las competencias similares 
			analizeCompetencesQuestionnaires(questionnaire, questions, language);
		}
	}
	
	
	/**
	 * Método que compara las preguntas del Questionnaire a guardar con los demás questionnaires del evaluador para identificar las comeptencias similares
	 * 
	 * **/
	 private void analizeCompetencesQuestionnaires(com.acsendo.api.competences.model.Questionnaire questionnaire,  
			 List<Question2> questionsReference, String language) throws NoSuchAlgorithmException, UnsupportedEncodingException {
		 
		 
		List<com.acsendo.api.competences.model.Questionnaire> questionnaires=
					questionnaireRepository.getQuestionnaireToEvaluateInProgress(questionnaire.getEvaluation().getId(),questionnaire.getEvaluator().getId());
			
		 //Recorremos todos los cuestionarios a analizar
	     forGeneral: for(com.acsendo.api.competences.model.Questionnaire questionnaireToAnalize: questionnaires) {  
	    	   
	            // Validamos que tenga preguntas creadas, si no, las crea
	    	    List<Question2> questionsToAnalize = questionRepository.findByQuestionnaireId(questionnaireToAnalize.getId());
	    	    questionsToAnalize=createQuestions(questionsToAnalize, questionnaire, language);
				
				//Validamos que tengan el mismo tamaño los arownerreglos de las preguntas
				if(questionsReference.size()!=questionsToAnalize.size()) {
	               continue;
					
				}else {
					//Recorremos las preguntas del cuestionario a analizar
					for(Question2 ques:questionsToAnalize) {
						//Validamos que el comportamiento del cuestionario a analizar, esté en el cuestionario de referencia. 
						//Si no no son iguales y salta al for de los cuestionarios
					  Long countQuestion=questionsReference.stream().filter(q->q.getBehavior().getId()==ques.getBehavior().getId()).count();
					  if(countQuestion==null || countQuestion==0) {
						  continue forGeneral;
					   }  
					}
					
					//Revisa si el questionnaire que concuerda tiene la llave de similar, si sí copia la llave
					if(questionnaireToAnalize.getSimilarCompetenceKey()!=null && questionnaire.getSimilarCompetenceKey()==null) {
						questionnaire.setSimilarCompetenceKey(questionnaireToAnalize.getSimilarCompetenceKey());
						questionnaireRepository.save(questionnaire);
						return;
					}else {
						//Si no crea la llave y la copia en el questionnaire analizado
						if(questionnaire.getSimilarCompetenceKey()==null) {
							MessageDigest salt;
							salt = MessageDigest.getInstance("SHA-256");
							salt.update(UUID.randomUUID().toString().getBytes("UTF-8"));
							String digest = bytesToHex(salt.digest());
						    questionnaire.setSimilarCompetenceKey(digest);
							questionnaire=questionnaireRepository.save(questionnaire);
						}
				
						questionnaireToAnalize.setSimilarCompetenceKey(questionnaire.getSimilarCompetenceKey());
						questionnaireRepository.save(questionnaireToAnalize);
					}
					
				}
	    	
	    }
	 }
	 
	 

	   /**
	   * Método que crea preguntas de un cuestionario
    	**/
	  private List<Question2> createQuestions(List<Question2> questions, com.acsendo.api.competences.model.Questionnaire questionnaire, String language) {
				
			if (questions != null && questions.size() > 0) {
				return questions;
			}else {
					
				questions = questionDao.queryQuestionByQuestionnaireTailorMade(questionnaire, language);
						// Creamos las preguntas de acuerdo a las competencias del colaborador
				questionRepository.saveAll(questions);
				questions=questionRepository.findByQuestionnaireId(questionnaire.getId());
					
				return questions;
			}
				
				
		}
	
	 
	  private  String bytesToHex(byte[] bytes) {
	        char[] hexChars = new char[bytes.length * 2];
	        for (int j = 0; j < bytes.length; j++) {
	            int v = bytes[j] & 0xFF;
	            hexChars[j * 2] = hexArray[v >>> 4];
	            hexChars[j * 2 + 1] = hexArray[v & 0x0F];
	        }
	        return new String(hexChars);
	  }

	  /**
	   * Obtiene listado de evaluadores disponibles a evaluar una evaluación
	   * 
	   * @param input Contiene información para filtrar
	   */
	  public EmployeeTalentPagination getCompetencesQuestionnairesAvailableEvaluators(
			  FilterCompetencesQuestionnaireEvaluator input) {

		  List<EmployeeTalentType> employeesList = new ArrayList<>();
		  // Se obtienen ids de evaluadores que tiene la persona en esta evaluación
		  List<Long> evaluatorsIds = new ArrayList<>();
		  evaluatorsIds.addAll(questionnaireRepository.getEvaluatorIdsByEvaluationId(input.evaluation_id(), input.evaluated_id()));
		  evaluatorsIds.add(input.evaluated_id());		
		  
		  // Se obtiene listado de empleados excluyendo los evaluadores
		  List<Object[]> usersList = talentUtilDAO.getEmployeeListByCompany(input.company_id(), input.name(), input.division_id(), evaluatorsIds);
		  List<Object[]> usersByFilter = new ArrayList<>();
		  if (usersList != null && usersList.size() > 0) {

			  String cursorComplete = input.after() != null ? ConnectionCursorUtil.decodeCursor(input.after()) : null;
			  String[] split = input.after() != null ? cursorComplete.split(":") : null;
			  Integer positionAfter = cursorComplete != null ? split != null ? Integer.parseInt(split[1]) : null
					  : null;

			  usersByFilter =  input.after() != null
					  ? usersList.stream().filter(tem -> usersList.indexOf(tem) > positionAfter).limit(input.first())
							  .collect(Collectors.toList())
							  : usersList;
		  }

		  Integer first=input.first() != null? input.first() : usersByFilter.size();
		  List<Edge<EmployeeTalentType>> listEdges = usersByFilter.stream().limit(first).map(emp -> {
			  String avatarUrl = competencesUtilService.getUrlEnviroment() + "/fotoemp?id=" + emp[0];
			  AvatarTalentType avatarType = new AvatarTalentType(competencesUtilService.getUrlEnviroment() + avatarUrl, avatarUrl);
			  DivisionTalentType divisionType = new DivisionTalentType(getLong(emp[4]), (String) emp[5]);
			  JobTalentType jobType = new JobTalentType(getLong(emp[2]), (String) emp[3]);
			  EmployeeTalentType employee = new EmployeeTalentType(
					  competencesUtilService.createNodeID("EmployeeTalentType", getLong(emp[0])), getLong(emp[0]), avatarType,
					  (String) (emp[1]), divisionType, jobType);
			  employeesList.add(employee);

			  return new DefaultEdge<>(employee,
					  ConnectionCursorUtil.encodeCursor("arrayconnection:" + usersList.indexOf(emp)));
		  }).collect(Collectors.toUnmodifiableList());

		  ConnectionCursor startCursor = listEdges.isEmpty() ? null : listEdges.get(0).getCursor();
		  ConnectionCursor endCursor = listEdges.isEmpty() ? null : listEdges.get(listEdges.size() - 1).getCursor();
		  boolean hasPreviousPage = input.after() != null;
		  boolean hasNextPage = input.first() != null && usersByFilter.size() > input.first();

		  EmployeeTalentPagination pagination = new EmployeeTalentPagination(
				  new ConnectionUtil<EmployeeTalentType>(usersList.size(), listEdges,
						  new PageInfoUtil(startCursor, endCursor, hasPreviousPage, hasNextPage)));
		  return pagination;

	  }	
	  
	  
	  /** 
	   * Método que obtiene las competencias junto a los comportamientos y preguntas de uno o varios questionnaires
	   * 
	   * */
	  public ResponseCompetenceGeneralForm getCompetencesAndQuestionsByQuestionnaires(List<Long> questionnairesId, Long evaluationId, Long employeeId,Integer first, String after) {
		  
		  
		 Optional<Employee> employee=employeeRepository.findById(employeeId);
		 
		 Optional<Evaluation> evaluation= evaluationRepository.findById(evaluationId);
		 
		 
		 if(employee.isPresent() && evaluation.isPresent()) {
		 
			  String language=getLanguage(employee);
			  
			  List<com.acsendo.api.competences.model.Questionnaire> questionnaires=questionnaireRepository.getQuestionnairesByIds(questionnairesId);
			  List<Questionnaire> questRecord=questionnaires.stream().map(q->convertQuestionnaireToRecord(q)).collect(Collectors.toList());
	
			  
			  //Si tiene la configuración que muestre los resultados de la autoevaluación se consulta.
			  Boolean withAuto= questionDao.companyHasConfiguration(evaluation.get().getCompany(), 191L);
			  List<Question2>  questionsAuto=withAuto? questionRepository.findQuestionAutobyQuestionnairesEvaluatedId(questionnairesId) : new ArrayList<>() ;
			  
			  
			  //Consultamos todas las preguntas de los questionnaires
			  List<Question2> questions= questionRepository.findByQuestionnairesId(questionnairesId);
			  
			  //Filtramos las competencias
			  List<Competence> competences=questions.stream().map(q-> q.getBehavior().getCompetenceLevel().getCompetence())
					  .distinct().collect(Collectors.toList());
			  
			  //Consultamos todos los comentarios por competencia si tiene habilitado que se muestren
			  List<CommentCompetence> commentsCompetences= evaluation.get().getCommentCompetence() != null && evaluation.get().getCommentCompetence() ? 
					  commentCompetenceRepository.findCommentsByQuestionnairesId(questionnairesId) : new ArrayList<>();
			  
			  String cursorComplete = after!= null ? ConnectionCursorUtil.decodeCursor(after) : null;
		      String[] split = after != null ? cursorComplete.split(":") : null;
		      Integer positionAfter = cursorComplete != null ? split != null ? Integer.parseInt(split[1]) : null : null;
	
		       //Para saber el total de registros que quedan despues del cursor especificado
			  List<Competence> competencesFiltered=competences.stream().filter(tem -> cursorComplete == null || competences.indexOf(tem) > positionAfter).collect(Collectors.toList());
			  
			  
			  Integer total= first!=null? first : competences.size();
			   // Unifica la información en los nodos del paginador, con su respectivo cursor
			  List<Edge<CompetenceForm>> listEdges = competencesFiltered.stream().limit(total).map(comp -> {
					return new DefaultEdge<>(createNewCompetenceForm(questions, comp, language, commentsCompetences, questionsAuto, evaluation.get(), questionnairesId.size()), 
							ConnectionCursorUtil.encodeCursor("arrayconnection:" +  competencesFiltered.indexOf(comp)));
				}).collect(Collectors.toUnmodifiableList());
	
				// Genera variables generales del paginador
				ConnectionCursor startCursor = listEdges.isEmpty() ? null : listEdges.get(0).getCursor();
				ConnectionCursor endCursor = listEdges.isEmpty() ? null : listEdges.get(listEdges.size() - 1).getCursor();
				boolean hasPreviousPage = after != null;
				boolean hasNextPage = first != null && competencesFiltered.size() > first;
	
				Map<String, List<CommentForm>> mapGeneralComments= createGeneralComments(questionnairesId, evaluation.get(), questionnaires);
				// Retorna información completa
				return  new CompetenceGeneralForm(createEvaluationRecord(evaluation.get()),questRecord,
						new ConnectionUtil<CompetenceForm>(competences.size(), listEdges,
								new PageInfoUtil(startCursor, endCursor, hasPreviousPage, hasNextPage)),
						mapGeneralComments.get("general1"), mapGeneralComments.get("general2"),mapGeneralComments.get("general3")
						);
		  }
			
			return new CompetenceGeneralForm(null, new ArrayList<>(),null, new ArrayList<>(), new ArrayList<>(), new ArrayList<>());
		  
	  }
	  
	  
    /**
     * Crea un Record de Evaluation
     * */
	private CompetencesEvaluation createEvaluationRecord(Evaluation evaluation) {
		
		SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
		return new CompetencesEvaluation(competencesUtilService.createNodeID("CompetencesEvaluation",evaluation.getId()),
				evaluation.getId(), evaluation.getName(), dateFormat.format(evaluation.getStartDate()), dateFormat.format(evaluation.getEndDate()),
				null,EvaluationStateEnum.valueOf(evaluation.getEvaluationState2().toString()),
				null, null, null, evaluation.getStartText());
	
	}
	  
	  
	  
	/**
	 * Método que crea un nuevo CompetenceForm con sus respectivos comportamientos y preguntas y comentarios
	 * 
	 * */
	private CompetenceForm createNewCompetenceForm(List<Question2> questions, Competence competence, String language,
                         List<CommentCompetence> commentsCompetences, List<Question2> questionsAuto, Evaluation evaluation, Integer totalQuestionnaires) {
		
			  
			  List<Behavior> behaviors=questions.stream().filter(q->q.getBehavior().getCompetenceLevel().getCompetence().getId()==competence.getId())
					  .map(q-> q.getBehavior())
					  .distinct().collect(Collectors.toList());
			  
			  List<BehaviorForm> behaviorsForm=new ArrayList<>();
			  
			  Boolean requiredComments=evaluation.getRequiredComments()!=null? true : false;
			  Boolean commentCompetenceRequired=false;
		      Boolean commentQuestionRequired=false;
				 
			  if(requiredComments) {
					 //Se consulta si los campos de comentarios son obligatorios para calcular si las preguntas y competencias estan completadas
					  Gson gson = new Gson();
					  @SuppressWarnings("unchecked")
					  Map<String, String> map = gson.fromJson(evaluation.getRequiredComments(), Map.class);
					 if(map!=null) {
					  commentCompetenceRequired=map.get("AFFCOMPETENCE") != null ? true : false;
					  commentQuestionRequired=map.get("AFFQUESTION") != null ? true : false;
					 }
			  }
				 
			  Boolean showCommentQuestion= evaluation.getAffirmationSuggestion()!=null?  evaluation.getAffirmationSuggestion() :false;
			  Boolean showCommentCompetence=evaluation.getCommentCompetence() != null ? evaluation.getCommentCompetence() :false;
					
			  //Indica la cantidad de comportamientos completados
			  Integer totalBehaviorsCompleted=0;

			  for(Behavior behavior: behaviors) {
	
				  List<Question2> questionsByBehavior=questions.stream().filter(q->q.getBehavior().getId()==behavior.getId()).collect(Collectors.toList());
				  
				  List<QuestionForm> questionsForm=new ArrayList<>();
				  Integer totalQuestionsCompleted=0;
				  //Recorremos las preguntas por Comportamiento
				  for(Question2 q: questionsByBehavior) {
					  
					  //Validamos las autoevaluaciones para saber el resultado
					 Optional<Question2> autoQ=questionsAuto.stream().filter(auto-> {
						 return auto.getBehavior().getId()==behavior.getId() 
							  && auto.getQuestionnaire().getEvaluated().getId().equals(q.getQuestionnaire().getEvaluated().getId());}).findFirst();
					  Double responseAuto=autoQ.isPresent()? autoQ.get().getResponse() : null;
					  
					  Boolean completedQuestion= q.getResponse()!=null && (!showCommentQuestion || (showCommentQuestion && !commentQuestionRequired)
					   || (showCommentQuestion && commentQuestionRequired && q.getSuggestions()!=null));
					  if(completedQuestion) {
						  totalQuestionsCompleted+=1;
					  }
					  QuestionForm questionForm=new QuestionForm(competencesUtilService.createNodeID("QuestionForm", q.getId()), q.getId(), q.getResponse(), completedQuestion,
							  							q.getQuestionnaire().getId(), q.getSuggestions(), responseAuto);
					  questionsForm.add(questionForm);
				
				  };
				 

				 String behaviorName=getLabelFlexByLanguageAndCode(language, behavior.getLabel());
				 //Se busca la traducción del nombre del comportamiento para el idioma del evaluador
				 Boolean completedBehavior=questionsByBehavior.size()== totalQuestionsCompleted;
				 if(completedBehavior) {
					 totalBehaviorsCompleted+=1;
				 }
				 BehaviorForm behaviorForm=new BehaviorForm(competencesUtilService.createNodeID("BehaviorForm", behavior.getId()), behavior.getId(),behaviorName ,completedBehavior,questionsForm);
				 behaviorsForm.add(behaviorForm);
				  
			  }
			  
			//Se busca la traducción del nombre de la competencia para el idioma del evaluador, en caso de no tener traducción se busca con el idioma de la empresa
			String competenceName = getLabelFlexByLanguageAndCode(language, competence.getName());
			
			//Se busca la traducción de la descripción de la competencia para el idioma del evaluador
			String competenceDescription = behaviors!=null && behaviors.size()>0 ? getLabelFlexByLanguageAndCode(language, behaviors.get(0).getCompetenceLevel().getDescription()): "";
			List<CommentForm> commentsForm=searchAndCreateCommentsCompetenceForm(commentsCompetences, competence.getId());
			
			//Una competencia por tipo de evaluación por comportamiento esta completa cuando tiene una respuesta en una competencia
		    // Por escala esta completa cuando todas las preguntas tienen respuesta
		    Boolean validateCompletedCompetenceAllTypes=evaluation.getModelCompetenceWeight()? totalBehaviorsCompleted>=1  : behaviors.size()== totalBehaviorsCompleted;
		    
			Boolean completedCompetence=validateCompletedCompetenceAllTypes&& (!showCommentCompetence  || (showCommentCompetence && !commentCompetenceRequired)
			 || (showCommentCompetence && commentCompetenceRequired &&  totalQuestionnaires==commentsForm.size()));
			return new CompetenceForm(competencesUtilService.createNodeID("CompetenceForm", competence.getId()), competence.getId(), 
					competenceName, competenceDescription, completedCompetence, behaviorsForm, commentsForm);

			
		
	}
	
	
	/**
	 * Método que busca y crea el arreglo de comentarios por competencia y crea el CommentCompetenceForm
	 * */
	private List<CommentForm> searchAndCreateCommentsCompetenceForm(List<CommentCompetence> commentsCompetences, Long competenceId) {
		
		List<CommentForm> forms=new ArrayList<>();
		
		if(commentsCompetences!=null && commentsCompetences.size()>0) {
			
			List<CommentCompetence> cfiltered=commentsCompetences.stream().filter(cc-> cc.getCompetence().getId()==competenceId).collect(Collectors.toList());
			
			cfiltered.stream().forEach(comment->{
				CommentForm form=new CommentForm(competencesUtilService.createNodeID("CommentCompetenceForm", comment.getId()), comment.getId(), comment.getQuestionnaire().getId(), comment.getComments());
				forms.add(form);
			});
		}
		
		return forms;
		
	}
	  
	
	/**
	 * Método que consulta un label de flex por codigo e idioma
	 * 
	 * */
    private String getLabelFlexByLanguageAndCode(String language, String code) {
    	
    	 String behaviorName="";
    	 LabelFlex label = labelFlexRepository.findByLanguageCodeAndCode(language, code);
		 if(label!=null && !label.getLabel().isEmpty()) {
			 behaviorName=label.getLabel();
		 }else {
			 behaviorName=code;
		 }
		 
		 return behaviorName;
			
    } 
	  
	  
    
    /**
     * Método que obtiene el idioma configurado para el empleado o para la empresa dependiendo de la configuración
     * 
     * */
	private String getLanguage(Optional<Employee> employee) {
		String language = "es";

		// Preguntamos si la evaluación tiene la configuración multiidiomas, si es así
		// debe tener en cuenta el idioma del empleado, sino el de la empresa
		if (employee.isPresent()) {
			if (questionDao.companyHasConfiguration(employee.get().getCompany(), 77L)
						&& employee.get().getPerson().getLanguage() != null) {
					language = employee.get().getPerson().getLanguage();
			} else {
					language = employee.get().getCompany().getLanguageCode();
			}
		}
		
		return language;
	}
	  
	  
	
	
	/**
	 * Método que crea las 3 listas de los comentarios generales, cada listado tiene el questionnaire y el comentario respectivo
	 * 
	 **/
	private Map<String, List<CommentForm>> createGeneralComments(List<Long> questionnairesIds, Evaluation evaluation,List<com.acsendo.api.competences.model.Questionnaire> questionnaires) {
	
	
		List<CommentForm> general1=new ArrayList<>();
		List<CommentForm> general2=new ArrayList<>();
		List<CommentForm> general3=new ArrayList<>();
	
		
	     if((evaluation.getGeneralSuggestion()!=null && evaluation.getGeneralSuggestion()) || 
	    		 (evaluation.getGeneralSuggestion2()!=null && evaluation.getGeneralSuggestion2()) || 
	    		 (evaluation.getGeneralSuggestion3()!=null && evaluation.getGeneralSuggestion3())) {
	    	 
	    	 
	    	 
	    	 questionnaires.stream().forEach(ques->{
	    		 
	    		 if(evaluation.getGeneralSuggestion()!=null && evaluation.getGeneralSuggestion()) {
	    			 String suggestion=ques.getSuggestions()!=null? ques.getSuggestions() :"";
		    		 CommentForm cGeneral1=new CommentForm(competencesUtilService.createNodeID("GeneralCommentForm1", ques.getId()), null, ques.getId(), suggestion);
		    		 general1.add(cGeneral1);
	    		 }
	    		 
	    		 if(evaluation.getGeneralSuggestion2()!=null && evaluation.getGeneralSuggestion2()) {
	    			 String suggestion2=ques.getSuggestions2()!=null? ques.getSuggestions2() :"";
		    		 CommentForm cGeneral2=new CommentForm(competencesUtilService.createNodeID("GeneralCommentForm2", ques.getId()), null, ques.getId(), suggestion2);
		    		 general2.add(cGeneral2);
	    		 }
	    		 
	    		 if(evaluation.getGeneralSuggestion3()!=null && evaluation.getGeneralSuggestion3()) {
	    			 String suggestion3=ques.getSuggestions3()!=null? ques.getSuggestions3() :"";
		    		 CommentForm cGeneral3=new CommentForm(competencesUtilService.createNodeID("GeneralCommentForm3", ques.getId()), null, ques.getId(), suggestion3);
		    		 general3.add(cGeneral3);
	    		 }
	    		 
	    	 });
	     }
	     
	     Map<String, List<CommentForm>> general=new HashMap<>();
	     general.put("general1", general1);
	     general.put("general2", general2);
	     general.put("general3", general3);
	     
	     
	    return general;
	  
	}
	
	/**
	 * Convierte un cuestionario a un registro de cuestionario.
	 * Validamos si el questionnaire tiene preguntas creadas, si no tiene, se las crea
	 *
	 * @param quest cuestionario a convertir.
	 * @return Cuestionario convertido.
	 */
	private Questionnaire convertQuestionnaireToRecord(com.acsendo.api.competences.model.Questionnaire quest) {
		
		List<Question2> questions=questionRepository.findByQuestionnaireId(quest.getId());
		String language=quest.getEvaluation().getCompany().getLanguageCode();
		//Preguntamos si la evaluación tiene la configuración multiidiomas, si es así debe tener en cuenta el idioma del empleado, sino el de la empresa
		if(questionDao.companyHasConfiguration(quest.getEvaluation().getCompany(), 77L) && quest.getEvaluated().getPerson().getLanguage() != null ) {
			language=quest.getEvaluated().getPerson().getLanguage();
			
		}
		//Valida los questions
		createQuestions(questions, quest, language);
		
		QuestionnaireStateEnum state = QuestionnaireStateEnum.valueOf(quest.getState().toString());
		String avatarUrl = "/fotoemp?id=" + quest.getEvaluated().getId();
		AvatarTalentType avatarType = new AvatarTalentType(competencesUtilService.getUrlEnviroment() + avatarUrl,
				avatarUrl);
	
		DivisionTalentType division= quest.getEvaluated().getJobRoleList()==null ? null :
				new DivisionTalentType( quest.getEvaluated().getJobRoleList().get(0).getDivision().getId(),quest.getEvaluated().getJobRoleList().get(0).getDivision().getName());
		JobTalentType job=quest.getEvaluated().getJobRoleList()==null ? null :
			new JobTalentType( quest.getEvaluated().getJobRoleList().get(0).getJob().getId(),quest.getEvaluated().getJobRoleList().get(0).getJob().getName());
		EmployeeCompetencesEvaluation evaluator = new EmployeeCompetencesEvaluation(
				competencesUtilService.createNodeID("EmployeeCompetencesEvaluation", quest.getEvaluated().getId()),
				quest.getEvaluated().getId(), avatarType, quest.getEvaluated().getPerson().getName(), division, job);
		String relation = changeRelationQuestionnaire(quest.getRelation());
		String relationLabel = getRelationLabel(relation, quest.getEvaluated().getId());
		
		Questionnaire questionnaire = new Questionnaire(
				competencesUtilService.createNodeID("Questionnaire", quest.getId()), quest.getId(), state,
				evaluator, relationLabel, RelationQuestionnaireEnum.valueOf(relation), "", 0.0, "", null,
				false);

		return questionnaire;
	}
	
	
	
	
	/**
	 * Método que guarda las respuestas de las preguntas de competencias de un questionnaire o los comentarios por comportamiento, 
	 * por competencia o generales
	 * @param input QuestionnaireQuealify con la información necesaria para guardar.
	 * @return ResponseQuestionnaireQualify 
	 * 
	 * */
	public ResponseQuestionnaireQualify qualifyQuestionnaires(QuestionnaireQualify input) {
		
		Boolean isQuestionReponse=input.type().equals(QualifyTypeEnum.QUESTION_RESPONSE);
		Boolean isQuestionComment=input.type().equals(QualifyTypeEnum.QUESTION_COMMENT);
		Boolean isCompetenceComment=input.type().equals(QualifyTypeEnum.COMPETENCE_COMMENT);
		Boolean isGeneralComment1=input.type().equals(QualifyTypeEnum.GENERAL_COMMENT1);
		Boolean isGeneralComment2=input.type().equals(QualifyTypeEnum.GENERAL_COMMENT2);
		Boolean isGeneralComment3=input.type().equals(QualifyTypeEnum.GENERAL_COMMENT3);
		
		if(input!=null) {
			
			if(isQuestionReponse || isQuestionComment)   {
				return saveQuestion(input, isQuestionReponse);
		    }
		
            if(isCompetenceComment) {
            	return saveCommentCompetence(input);
			}
            
            if(isGeneralComment1 || isGeneralComment2 || isGeneralComment3) {
				
            	Optional<com.acsendo.api.competences.model.Questionnaire> questionnaire=questionnaireRepository.findById(input.questionnaire_id());
			    
                if(questionnaire.isPresent()) {
                	if(isGeneralComment1) {
                		questionnaire.get().setSuggestions(input.general_comment1());
                	}else if(isGeneralComment2) {
                		questionnaire.get().setSuggestions2(input.general_comment2());
                	}else {
                		questionnaire.get().setSuggestions3(input.general_comment3());
                	}
                  if(questionnaire.get().getState().equals(QuestionnaireState.ASSIGNED)) {
                	  questionnaire.get().setState(QuestionnaireState.IN_PROGRESS);
                  }
                  questionnaireRepository.save(questionnaire.get());
                 return new QualifyPayload(null, null, null, null, null, null, true);
                 
                }else {
                	return CompetencesValidationErrorUtil
        					.getResponseValidationError(CompetencesValidationErrorUtil.QUESTIONNAIRE_NOT_FOUND);
                }
            }
			
		}
		
		 return new QualifyPayload(null, null, null, null, null, null, false);
		
	}

	
	/**
	 * Método que valida el guardado de una pregunta de un questionnaire, y 
	 * devuelve el estado de completitud de su comportamiento y competencia o un mensaje de error en caso de que un campo requerido esté nulo
	 * 
	 * */
    private ResponseQuestionnaireQualify saveQuestion(QuestionnaireQualify input, Boolean isQuestionReponse) {
			
		if(input.question_id()!=null && input.questionnaire_id()!=null) {
			
				Optional<Question2> opt= questionRepository.findById(input.question_id());
				if(opt.isPresent()) {
					Question2 question=opt.get();
					
					if(isQuestionReponse)  {
						
						if((question.getEvaluation().getModelCompetenceWeight()==null || !question.getEvaluation().getModelCompetenceWeight()) && input.question_response()!=null) {
						    question.setResponse(input.question_response());
						    
						}else if(question.getEvaluation().getModelCompetenceWeight()) {
							
						   //Cuando se guarda una pregunta y respuesta de comentario por comportamiento, se deben borrar las otras respuestas de esa competencia
						   // Solo puede haber una respuesta por competencia
						   List<Question2> allQuestions=questionRepository.findByQuestionnaireIdAndCompetenceId(input.questionnaire_id(),
								   question.getBehavior().getCompetenceLevel().getCompetence().getId());
						   allQuestions.stream().forEach(q-> q.setResponse(null));
						   questionRepository.saveAll(allQuestions);
						   question.setResponse(question.getBehavior().getWeight());
						   
						}else {
							CompetencesValidationErrorUtil
							.getResponseValidationError(CompetencesValidationErrorUtil.FIELD_CANNOT_BE_NULL +": Response is null");
						}
					} else {
						question.setSuggestions(input.question_comment());
					}
					question= questionRepository.save(question);
					List<Long> questionnairesIds= input.all_questionnaires()!=null? input.all_questionnaires() : new ArrayList<>();
					if(input.all_questionnaires()==null) {
						questionnairesIds.add(input.questionnaire_id());
					}
					
					if(question.getQuestionnaire().getState().equals(QuestionnaireState.ASSIGNED)) {
						  com.acsendo.api.competences.model.Questionnaire quest=question.getQuestionnaire();
	                	  quest.setState(QuestionnaireState.IN_PROGRESS);
	                	  questionnaireRepository.save(quest);
	                 }
					
					return calculateCompetencesAndBehaviorCompleted(question.getBehavior().getCompetenceLevel().getCompetence().getId(), questionnairesIds, question, question.getEvaluation());
				
				}else {
					return CompetencesValidationErrorUtil
							.getResponseValidationError(CompetencesValidationErrorUtil.QUESTION_NOT_FOUND);
				}
			
			} else {
					return CompetencesValidationErrorUtil
							.getResponseValidationError(CompetencesValidationErrorUtil.FIELD_CANNOT_BE_NULL+ ": question_id and questionnaire_id");
				
			}
			
    }
    
    
    /**
     *  Método que valida y guarda un comentario por competencia de un questionnaire
     *  Devuelve el estado de completitud de la competencia
     *  
     * */
    private ResponseQuestionnaireQualify saveCommentCompetence(QuestionnaireQualify input) {
    	
    	
    	if(input.competence_id()!=null && input.questionnaire_id()!=null) {
    	
	      	CommentCompetence comment=commentCompetenceRepository.findTop1ByCompetenceIdAndQuestionnaireIdOrderById(input.competence_id(), input.questionnaire_id());
	    	Optional<com.acsendo.api.competences.model.Questionnaire> questionnaire=questionnaireRepository.findById(input.questionnaire_id());
	    	
	    	if(questionnaire.isPresent()) {
	      	
		    	if(comment!=null) {
		    		comment.setComments(input.comment_competence());
		    	}else {
		    		comment=new CommentCompetence();
		    		comment.setComments(input.comment_competence());
		    		Competence competence=new Competence();
		    		competence.setId(input.competence_id());
		    		comment.setCompetence(competence);
		    		comment.setQuestionnaire(questionnaire.get());
		    	}
		    	
		    	comment=commentCompetenceRepository.save(comment);
		    	List<Long> questionnairesIds= input.all_questionnaires()!=null? input.all_questionnaires() : new ArrayList<>();
				if(input.all_questionnaires()==null) {
					questionnairesIds.add(input.questionnaire_id());
				}
				if(questionnaire.get().getState().equals(QuestionnaireState.ASSIGNED)) {
                	  questionnaire.get().setState(QuestionnaireState.IN_PROGRESS);
                	  questionnaireRepository.save(questionnaire.get());
                 }
				return calculateCompetencesAndBehaviorCompleted(input.competence_id(), questionnairesIds, null,questionnaire.get().getEvaluation());
	    	}else {
	    		return CompetencesValidationErrorUtil
				.getResponseValidationError(CompetencesValidationErrorUtil.QUESTIONNAIRE_NOT_FOUND);
	    	}
    	}else {
    		return CompetencesValidationErrorUtil
					.getResponseValidationError(CompetencesValidationErrorUtil.FIELD_CANNOT_BE_NULL + ": questionnaire_id and competence_id");
    	}
    	
    }
	
	
    /**
     * Método que valida si la pregunta, comportamiento y competencia especificada fueron completadas
     * @return QualifyPayload
     * 
     * */
	private QualifyPayload calculateCompetencesAndBehaviorCompleted(Long competenceId, List<Long> questionnairesId, Question2 question,  Evaluation evaluation) {
		
		 
		 Boolean requiredComments=evaluation.getRequiredComments()!=null? true : false;
		 Boolean commentCompetenceRequired=false;
		 Boolean commentQuestionRequired=false;
		 
		 if(requiredComments) {
			 //Se consulta si los campos de comentarios son obligatorios para calcular si las preguntas y competencias estan completadas
			  Gson gson = new Gson();
			  @SuppressWarnings("unchecked")
			  Map<String, String> map = gson.fromJson(evaluation.getRequiredComments(), Map.class);
			 
			  if(map!=null) {
				  commentCompetenceRequired=map.get("AFFCOMPETENCE") != null ? true : false;
				  commentQuestionRequired=map.get("AFFQUESTION") != null ? true : false;
			  }
		  }
		 
		  Boolean showCommentQuestion= evaluation.getAffirmationSuggestion()!=null?  evaluation.getAffirmationSuggestion() :false;
		  Boolean showCommentCompetence=evaluation.getCommentCompetence() != null ? evaluation.getCommentCompetence() :false;
				
		  Boolean completedQuestionSaved=false;
		  Boolean completedBehaviorSaved=false;
		  
		  //Consultamos todas las preguntas de los questionnaires y competencia
		  List<Question2> questions= questionRepository.findByQuestionnairesIdAndCompetenceId(questionnairesId, competenceId);
		  
		  List<Behavior> behaviors=questions.stream()
				  .map(q-> q.getBehavior())
				  .distinct().collect(Collectors.toList());
				
		  //Indica la cantidad de comportamientos completados
		  Integer totalBehaviorsCompleted=0;

		  for(Behavior behavior: behaviors) {

			  List<Question2> questionsByBehavior=questions.stream().filter(q->q.getBehavior().getId()==behavior.getId()).collect(Collectors.toList());
			  
			  Integer totalQuestionsCompleted=0;
			  //Recorremos las preguntas por Comportamiento para revisar si están completas
			  for(Question2 q: questionsByBehavior) {
				   Boolean completedQuestion= q.getResponse()!=null && (!showCommentQuestion ||  (showCommentQuestion && !commentQuestionRequired)
				  ||  (showCommentQuestion && commentQuestionRequired && q.getSuggestions()!=null));
				  if(completedQuestion) {
					  if(question!=null && q.getId().equals(question.getId())) {
						  completedQuestionSaved=true;
					  }
					  totalQuestionsCompleted+=1;
				  }
			  }
			 
			 //Se valida si el comportamiento está completado
			 Boolean completedBehavior=questionsByBehavior.size()== totalQuestionsCompleted;
			 if(completedBehavior) {
				 if(question!=null && question.getBehavior().getId()==behavior.getId()) {
					 completedBehaviorSaved=true;
				 }
				 totalBehaviorsCompleted+=1;
			 }
		  }
		  
		//Consultamos todos los comentarios por competencia si tiene habilitado que se muestren y verificamos si está completa la competencia
	    List<CommentCompetence> commentsCompetences= evaluation.getCommentCompetence() != null && evaluation.getCommentCompetence() ? 
				  commentCompetenceRepository.findCommentsByQuestionnairesIdAndCompetenceId(questionnairesId, competenceId) : new ArrayList<>();
	    //Una competencia por tipo de evaluación por comportamiento esta completa cuando tiene una respuesta en una competencia
	    // Por escala esta completa cuando todas las preguntas tienen respuesta
	    Boolean validateCompletedCompetenceAllTypes=evaluation.getModelCompetenceWeight()? totalBehaviorsCompleted>=1  : behaviors.size()== totalBehaviorsCompleted;
	    
		Boolean completedCompetence= validateCompletedCompetenceAllTypes && (!showCommentCompetence || (showCommentCompetence && !commentCompetenceRequired)
		 || (showCommentCompetence && commentCompetenceRequired &&  questionnairesId.size()==commentsCompetences.size()));
		

		return new QualifyPayload(competenceId , completedCompetence, question!=null? question.getBehavior().getId() : null,
				completedBehaviorSaved, question!=null? question.getId() : null, completedQuestionSaved, true);
		
	}
	
	
	/**
	 * Método que obtiene el resumen de resultados general de las personas que se estan evaluando junto a sus resultados por competencias
	 * @param List<Long> questionnairesId Identificadores de los cuestionarios
	 * @param evaluationId Identificador de la evaluación
	 * @param employeeId Identificdor del empleado
	 * */
	public ResponseQualifySummary getQualifySummary(List<Long> questionnairesId, Long evaluation_id, Long employeeId) {
		
		if(questionnairesId!=null && questionnairesId.size()>0 && evaluation_id!=null) {
			
			Optional<Evaluation> optEv=evaluationRepository.findById(evaluation_id);
			
			if(optEv.isPresent()) {
				
				String language=optEv.get().getCompany().getLanguageCode();
				Optional<Employee> employee=employeeRepository.findById(employeeId);
				//Preguntamos si la evaluación tiene la configuración multiidiomas, si es así debe tener en cuenta el idioma del empleado, sino el de la empresa
				if( employee.isPresent() && questionDao.companyHasConfiguration(optEv.get().getCompany(), 77L) && employee.get().getPerson().getLanguage() != null ) {
					language=employee.get().getPerson().getLanguage();
				}
				

				//Resultados por competencias
				List<Object[]> resultsComp=questionDao.getCompetencesResultByQuestionnaire(language, questionnairesId, optEv.get());
				List<Object[]> resultIndividualEmployee=performanceResultDAO.getEmployeesResultsByQuestionnaires(questionnairesId, optEv.get());
		        
				
				List<QuestionnaireSummary> questionnairesSummary=new ArrayList<>();
				List<CompetenceSummary> competencesSummary=new ArrayList<>();
				
				Integer positionResultEmp=3;
				Integer positionResultComp=4;
				if(optEv.get().getCompany().getCompetencesResultFormat().equals("SCALE")) {
					   positionResultEmp=2;
					   positionResultComp=3;
				}
				
				//Resultados individuales
			    for(Object[] res: resultIndividualEmployee){
					QuestionnaireSummary summary=new QuestionnaireSummary(getLong(res[4]),  getDouble(res[positionResultEmp]));
					questionnairesSummary.add(summary);
				};
				
				
				////Filtramos las competencias
				List<Competence> competences=resultsComp.stream().map(q-> newCompetence(q))
						  .distinct().collect(Collectors.toList());
				  
				//Resultados por competencia
				for(Competence competence: competences) {
					  
					  List<QuestionnaireSummary> questionnairesSummaryByCompetence=new ArrayList<>();
					  List<Object[]> resultsByComp=resultsComp.stream().filter(obj-> getLong(obj[0]).equals(competence.getId())).collect(Collectors.toList());
					  
					  for(Object[] object: resultsByComp) {
							QuestionnaireSummary summary=new QuestionnaireSummary(getLong(object[2]),  getDouble(object[positionResultComp]));
							questionnairesSummaryByCompetence.add(summary);
					  }
					  
					  CompetenceSummary competenceSummary= new CompetenceSummary(competencesUtilService.createNodeID("Competence", competence.getId()), competence.getId(), competence.getName(), questionnairesSummaryByCompetence);
					  competencesSummary.add(competenceSummary);
				}
				
				return new QualifySummary(questionnairesSummary, competencesSummary);
			
			}else {
				return CompetencesValidationErrorUtil
						.getResponseValidationError(CompetencesValidationErrorUtil.EVALUATION_NOT_FOUND);
			}
			
		}
		
		return CompetencesValidationErrorUtil
				.getResponseValidationError(CompetencesValidationErrorUtil.FIELD_CANNOT_BE_NULL);
	
	}
	  
	
	
	/*
	 * Crea una nueva entidad Competencia con su id y nombre
	 * */
	private Competence newCompetence(Object[] obj) {
		
		Competence comp=new Competence();
		comp.setId(getLong(obj[0]));
		comp.setName(getString(obj[1]));
		
		return comp;
	}
	
	
	
	
	/***
	 * Método que finaliza los questionnaires, valida si tiene todas las preguntas contestadas 
	 * Cambia la fecha de finalización del cuestionario.
	 * Envía a redshift el aviso que se contestó una evaluación
	 * */
	public ResponseExecutionCompetences finishQuestionnaires(List<Long> questionnairesIds, Long evaluationId, Long employeeId) {
		
		
		Boolean completed=false;
		
		Optional<Evaluation> optEv= evaluationRepository.findById(evaluationId);
		
		if(optEv.isPresent()) {
				
				for(Long id:questionnairesIds){
					completed=validateFinishedQuestionsAnswered(id, optEv.get());
				};
				
				if(completed) {
					List<com.acsendo.api.competences.model.Questionnaire> questionnaires= questionnaireRepository.getQuestionnairesByIds(questionnairesIds);
					questionnaires.stream().forEach(quest->{
						if(!quest.getState().equals(QuestionnaireState.FINISHED)) {
							quest.setState(QuestionnaireState.FINISHED);
							quest.setEndDate(new Date());
						}
					});
					
					questionnaireRepository.saveAll(questionnaires);
					//Se agrega evento de resetear evaluación de competencias en redshift
					try {
						User user = userRepository.getUserByEmployeeId(employeeId);
						lambdaRedshiftUtil.sendInformationForLambdaAndRedshift(optEv.get().getCompany().getId(), user.getId(), evaluationId, false, 
								"COMPETENCES", "GUARDAR RESPUESTA EN EVALUACIÓN DE COMPETENCIAS");
					} catch (Exception e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					
					return new ExecutedCompetencesPayload(Boolean.TRUE);
					
				}else {
					return CompetencesValidationErrorUtil
							.getResponseValidationError(CompetencesValidationErrorUtil.QUESTIONNAIRE_CANT_FINISH);
				}
		}else {
			return CompetencesValidationErrorUtil
					.getResponseValidationError(CompetencesValidationErrorUtil.EVALUATION_NOT_FOUND);
		}
		
	}
	
	/**
	 * Método para validar si se contestaron todas las preguntas de la evaluación
	 * */
	private boolean validateFinishedQuestionsAnswered(Long questionnaireId, Evaluation evaluation) {
		
		
		if(!evaluation.getModelCompetenceWeight()) {
			Integer countInNull=questionRepository.countQuestionsInNull(questionnaireId);
			
			if(countInNull!=null && countInNull==0) {
				return true;
			}
		}else {
			Integer countInNull=questionRepository.countQuestionsAnsweredByBehavior(questionnaireId);
			if(countInNull!=null && countInNull<=0) {
				return true;
			}
		}
		
	   return false;
		
	}

}
