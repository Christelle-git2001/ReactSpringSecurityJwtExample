package com.lacouf.rsbjwt.model.Evaluation;

import jakarta.persistence.Embeddable;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
@Embeddable
public class SuperviseurInfo {
    private String firstName;
    private String lastName;
    private String fonction;
    private String phone;

    public SuperviseurInfo(String firstName, String lastName, String fonction, String phone){
        this.firstName = firstName;
        this.lastName = lastName;
        this.fonction = fonction;
        this.phone = phone;
    }

    public SuperviseurInfo() {

    }
}