package org.kreyzon.stripe.entity.wsq;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;

@Entity
@Getter
@Setter
public class WSQFacilitator {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String salutation;
    private String usertype;
    private String name;
    @JsonProperty("nric")  // Maps the incoming JSON key "NRIC" to the field "nric"
    @Column(name = "nric") // Define the correct column name in the database
    private String nric;
    private String email;
    private String phone;
    private String educational_level;
    private String qualification;
    private String domain;
    private String gender;
    private String password;
    @JsonProperty("prooftype")
    private String prooftype;
}
