package org.kreyzon.stripe.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Data
public class RegistrationAdvanceBatchDetailDTO {
    @JsonProperty("batch_id")
    private Long batch_id;

    @JsonProperty("sName")
    private String sName;

    @JsonProperty("sEmail")
    private String sEmail;

    @JsonProperty("sPhone")
    private String sPhone;

    @JsonProperty("status")
    private String status;

    @JsonProperty("registered")
    private String registered;

    @JsonProperty("sId")
    private Long sId;

//    public RegistrationAdvanceBatchDetailDTO(Long batch_id, String sName, String sEmail, String sPhone, boolean status, String registered) {
//        this.batch_id = batch_id;
//        this.sName = sName;
//        this.sEmail = sEmail;
//        this.sPhone = sPhone;
//        this.status = status;
//        this.registered = registered;
//    }


    public RegistrationAdvanceBatchDetailDTO() {

    }

//    public void setStatus(String status) {
//        this.status = status;
//    }
}