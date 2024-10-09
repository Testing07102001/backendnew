package org.kreyzon.stripe.entity;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.time.LocalDate;
@Getter
@Setter
@Entity
public class Student {
    @Getter
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String name;

    private String fullname;

    private String nric;

//    private String studentId;

    private String email;

    private String phone;

    private String gender;

    private LocalDate dob;
    private String password;
//    private String address;
//    private String postalcode;
//    private String race;
//    private String eduction;
//    private String employmentstatus;
//    private String salary;
//    private String companyname;
//    private String corporatecompanysponsored;
    private String designation;
    private String status;
//    private String mode;
    private String type;
    private String approve;

    @Setter
    @Getter
    private String profileUrl;
    private String remarks;

//
//    @Column(name = "intake_id") // Map to the intake_id column in the database
//    private String intakeId; // Intake ID field

    public Student() {
    }
    @Override public String toString() {
        return name; // Assuming 'name' is the field you want to represent in toString()
         }


    // getters and setters

}
