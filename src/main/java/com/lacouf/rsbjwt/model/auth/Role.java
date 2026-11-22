package com.lacouf.rsbjwt.model.auth;

import java.util.HashSet;
import java.util.Set;

public enum Role{
	GESTIONNAIRE("ROLE_GESTIONNAIRE"),
	ETUDIANT("ROLE_ETUDIANT"),
	PROFESSEUR("ROLE_PROFESSEUR"),
	EMPLOYEUR("ROLE_EMPLOYEUR");

	private final String string;
	private final Set<Role> managedRoles = new HashSet<>();

	static{
		GESTIONNAIRE.managedRoles.add(GESTIONNAIRE);
	}

	Role(String string){
		this.string = string;
	}

	@Override
	public String toString(){
		return string;
	}

}
