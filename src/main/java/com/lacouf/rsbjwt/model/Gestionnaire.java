package com.lacouf.rsbjwt.model;

import com.lacouf.rsbjwt.model.auth.Credentials;
import com.lacouf.rsbjwt.model.auth.Role;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Entity
@DiscriminatorValue("G")
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class Gestionnaire extends UserApp {
	@Column(unique = true, nullable = false)
	private String phoneNumber;

	@OneToMany(mappedBy = "gestionnaire", cascade = CascadeType.ALL)
	private List<Contrat> contrats ;

	@Builder
	public Gestionnaire(
		Long id, String firstName, String lastName, String email, String password,
		String phoneNumber){
		super(firstName, lastName ,phoneNumber, Credentials.builder().email(email).password(password).role(Role.GESTIONNAIRE).build());
		this.phoneNumber = phoneNumber;
	}
}
