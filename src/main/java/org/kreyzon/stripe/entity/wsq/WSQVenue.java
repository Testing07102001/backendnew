package org.kreyzon.stripe.entity.wsq;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
@Entity
@Getter
@Setter
public class WSQVenue {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private int roomno;
    private int unit;
    private String block;
    private int floor;
    private String street;
    private String building;
    private  String postalcode;
    private String wheelchair;
}
