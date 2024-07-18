package com.acsendo.api.competences.service;

import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.acsendo.api.company.repository.CompanyRepository;
import com.acsendo.api.competences.interfaces.ResponseCompetencesConfiguration;
import com.acsendo.api.hcm.enumerations.Environment;
import com.acsendo.api.hcm.model.Company;
import com.acsendo.api.hcm.model.Label;
import com.acsendo.api.hcm.repository.LabelRepository;

/**
 * Contiene servicios y métodos transversales, los cuales pueden ser usados en
 * múltiples partes del flujo, o son genéricos para la compañía
 */
@Service
public class CompetencesUtilService {

	@Autowired
	private LabelRepository labelRepository;

	@Autowired
	private CompanyRepository companyRepository;
	

	/**
	 * Se obtiene la url según el ambiente donde se ejecute la aplicación
	 */
	public String getUrlEnviroment() {
		String systemEnvironment = System.getenv("STAGE");
		if (systemEnvironment == null || systemEnvironment.isEmpty()) {
			systemEnvironment = "testing";
		}
		// Url aplicación
		Environment environment = Environment.valueOf(systemEnvironment);
		systemEnvironment = environment.getSystemEnvironment();
		return systemEnvironment;
	}

	/**
	 * Obtiene valor de etiqueta según el lenguaje de la empresa
	 * 
	 * @param languageCode Lenguaje
	 * @param label        Entidad que contiene la etiqueta
	 */
	public String getLabelByLanguage(String languageCode, Label label) {
		String labelStr = "";
		if (label != null) {
			if (languageCode.equals("es")) {
				labelStr = label.getSpanish() != null ? label.getSpanish() : "";
			} else if (languageCode.equals("en")) {
				labelStr = label.getEnglish() != null ? label.getEnglish() : "";
			} else if (languageCode.equals("pt")) {
				labelStr = label.getPortuguese() != null ? label.getPortuguese() : "";
			}
		}
		return labelStr;
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
	 * Método que encripta id para los nodos de GraphQL
	 * 
	 * @param nameNode Nombre de la entidad o nodo
	 * @param idNode   Identificador de la entidad
	 */
	public String createNodeID(String nameNode, Long idNode) {
		String encryptedID = nameNode + ":" + idNode;
		return Base64.getEncoder().encodeToString(encryptedID.getBytes(StandardCharsets.UTF_8));
	}

	/**
	 * Método que verifica si una empresa tiene una configuración
	 * 
	 * @param company         Entidad que contiene información de la empresa
	 * @param configurationId Identificador de la compañía
	 */
	public Boolean companyHasConfiguration(Company company, Long configurationId) {
		Optional<String[]> opt = companyRepository.getConfigurationsByIdAndCompanyId(company.getId(), configurationId);

		if (opt.isPresent()) {
			if (opt.get().length > 0) {
				return true;
			}
		}
		return false;
	}

	/**
	 * Método que se encarga de obtener todas las etiquetas de un módulo en un
	 * idioma determinado
	 * 
	 * @param company_id Identificador de la compañía
	 * @param language   Idioma de las etiquetas
	 */
	public ResponseCompetencesConfiguration getCompetencesLabels(Long company_id, String language) {
		// TODO Auto-generated method stub
		return null;
	}

}