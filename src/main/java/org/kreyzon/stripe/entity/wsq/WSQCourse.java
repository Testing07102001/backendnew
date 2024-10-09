package org.kreyzon.stripe.entity.wsq;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.util.List;

@Entity
@Getter
@Setter
public class WSQCourse {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;
    private String fee;

    @ManyToOne
    @JoinColumn(name = "category_id")
    private Category category;

    // Adding a list of languages
    @ElementCollection
    @CollectionTable(name = "course_languages", joinColumns = @JoinColumn(name = "course_id"))
    @Column(name = "language")
    private List<String> languages;
}
