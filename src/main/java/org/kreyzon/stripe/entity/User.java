package org.kreyzon.stripe.entity;

import lombok.*;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import javax.persistence.*;
import java.time.LocalDate;
import java.util.Collection;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
@Entity
@Table(name = "user_table")
public class User implements UserDetails {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true)
    private String userId;

    private String name;
    private String email;
    private String password;
    private String about;
    private String status;
    private long sfid;
    private String profile_Url;
    private int usertype;
    private LocalDate startDate;
    private LocalDate endDate;



    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        // TODO Auto-generated method stub
        return null;
    }
    @Override
    public String getUsername() {
        // TODO Auto-generated method stub
        return this.email;
    }
    public String getIdentity() {
        // TODO Auto-generated method stub
        return this.userId;
    }
    @Override
    public boolean isAccountNonExpired() {
        // TODO Auto-generated method stub
        return true;

    }
    @Override
    public boolean isAccountNonLocked() {
        // TODO Auto-generated method stub
        return true;

    }
    @Override
    public boolean isCredentialsNonExpired() {
        // TODO Auto-generated method stub
        return true;

    }
    @Override
    public boolean isEnabled() {
        // TODO Auto-generated method stub
        return true;

    }



}
