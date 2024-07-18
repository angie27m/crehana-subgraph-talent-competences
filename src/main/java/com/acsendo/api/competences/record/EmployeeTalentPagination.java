package com.acsendo.api.competences.record;

import com.acsendo.api.competences.interfaces.TalentResponseEmployee;
import com.acsendo.api.util.ConnectionUtil;

public record EmployeeTalentPagination(
		ConnectionUtil<EmployeeTalentType> employees
		) implements TalentResponseEmployee {

}
