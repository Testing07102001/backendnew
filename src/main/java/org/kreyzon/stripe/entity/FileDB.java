package org.kreyzon.stripe.entity;

import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;

@Entity
public class FileDB {
    @Id
    @GeneratedValue(
            generator = "uuid"
    )
    @GenericGenerator(
            name = "uuid",
            strategy = "uuid2"
    )
    private String id;
    private String name;
    private String type;
    @Lob
    private byte[] data;

    public void setId(String id) {
        this.id = id;
    }

    public FileDB() {
    }

    public FileDB(String name, String type, byte[] data) {
        this.name = name;
        this.type = type;
        this.data = data;
    }

    public String getId() {
        return this.id;
    }

    public String getName() {
        return this.name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getType() {
        return this.type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public byte[] getData() {
        return this.data;
    }

    public void setData(byte[] data) {
        this.data = data;
    }


}
