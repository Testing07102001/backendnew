package org.kreyzon.stripe.entity;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;

@Entity
@Getter
@Setter
public class Course {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)

    private Long id;
    private String category;
    private Long categoryId;
    private String name;
    private String reference;
    //    private String duration;
//    private String fee;
//    private String idi;
//    private String clustercode;
//    private String qualificationcode;
    @Lob
    private String description;
    @Lob
    private String outcome;
    @Lob
    private String requirement;
    private String url;
    //    private String daysAndTime;
//    private String processingFeeForAll;
    private Long courseFee;
    private Long registrationFee;
//    @Lob

//    private String entryRequirement;
//    @Lob
//    private String graduationRequirement;

    private String type;
    private String courseType;

}
