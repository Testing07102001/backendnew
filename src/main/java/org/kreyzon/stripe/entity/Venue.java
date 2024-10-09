package org.kreyzon.stripe.entity;

import javax.persistence.*;

@Entity
@Table(
        name = "venue"
)
public class Venue {
    @Id
    @GeneratedValue
    private Long id;
    private String roomno;
    private String unit;
    private String block;
    private String floor;
    private String street;
    private String building;
    private String postalcode;
    private String wheelchair;

    public Venue() {
    }

    public Long getId() {
        return this.id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getRoomno() {
        return this.roomno;
    }

    public void setRoomno(String roomno) {
        this.roomno = roomno;
    }

    public String getUnit() {
        return this.unit;
    }

    public void setUnit(String unit) {
        this.unit = unit;
    }

    public String getBlock() {
        return this.block;
    }

    public void setBlock(String block) {
        this.block = block;
    }

    public String getFloor() {
        return this.floor;
    }

    public void setFloor(String floor) {
        this.floor = floor;
    }

    public String getStreet() {
        return this.street;
    }

    public void setStreet(String street) {
        this.street = street;
    }

    public String getBuilding() {
        return this.building;
    }

    public void setBuilding(String building) {
        this.building = building;
    }

    public String getPostalcode() {
        return this.postalcode;
    }

    public void setPostalcode(String postalcode) {
        this.postalcode = postalcode;
    }

    public String getWheelchair() {
        return this.wheelchair;
    }

    public void setWheelchair(String wheelchair) {
        this.wheelchair = wheelchair;
    }

}
