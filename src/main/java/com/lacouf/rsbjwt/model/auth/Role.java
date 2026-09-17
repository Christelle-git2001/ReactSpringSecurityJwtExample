package com.lacouf.rsbjwt.model.auth;

import java.util.HashSet;
import java.util.Set;

public enum Role{
	// TODO Remplacer les classe biblio par les classe appropriee
	GESTIONNAIRE("ROLE_GESTIONNAIRE"),
	EMPLOYEUR("ROLE_EMPLOYEUR");

	private final String string;
	private final Set<Role> managedRoles = new HashSet<>();

	static{
		GESTIONNAIRE.managedRoles.add(EMPLOYEUR);
	}

	Role(String string){
		this.string = string;
	}

	@Override
	public String toString(){
		return string;
	}

}
