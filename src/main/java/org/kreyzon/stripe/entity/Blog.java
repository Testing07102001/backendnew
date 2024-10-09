package org.kreyzon.stripe.entity;

import javax.persistence.*;

@Entity
public class Blog {
    @Id
    @GeneratedValue
    private Long id;


    private String title;
    private String image;



    private String date;


    public String getDate() {
        return date;
    }


    public void setDate(String date) {
        this.date = date;
    }


    private String post;


    public Long getId() {
        return id;
    }


    public void setId(Long id) {
        this.id = id;
    }


    public String getTitle() {
        return title;
    }


    public void setTitle(String title) {
        this.title = title;
    }


    public String getImage() {
        return image;
    }


    public void setImage(String image) {
        this.image = image;
    }


    public String getPost() {
        return post;
    }


    public void setPost(String post) {
        this.post = post;
    }

}
