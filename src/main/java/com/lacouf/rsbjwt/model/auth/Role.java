package com.lacouf.rsbjwt.model.auth;

import java.util.HashSet;
import java.util.Set;

public enum Role{
	// TODO Remplacer les classe biblio par les classe appropriee
	GESTIONNAIRE("ROLE_GESTIONNAIRE"),
	/*PREPOSE("ROLE_PREPOSE"),
	EMPRUNTEUR("ROLE_EMPRUNTEUR"),*/
	PROFESSEUR("ROLE_PROFESSEUR");

	private final String string;
	private final Set<Role> managedRoles = new HashSet<>();

	static{
		/*GESTIONNAIRE.managedRoles.add(PREPOSE);
		GESTIONNAIRE.managedRoles.add(EMPRUNTEUR);*/
		GESTIONNAIRE.managedRoles.add(GESTIONNAIRE);
		GESTIONNAIRE.managedRoles.add(PROFESSEUR);
	}

	Role(String string){
		this.string = string;
	}

	@Override
	public String toString(){
		return string;
	}

}
