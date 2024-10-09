package org.kreyzon.stripe.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UserDTO {
    private Long id;

    public UserDTO(Long id, String name, String email,String profile_Url) {
        this.id = id;
        this.name = name;
        this.email = email;
        this.profile_Url = profile_Url;
    }

    private String name;
    private String email;
    private String profile_Url;
}
