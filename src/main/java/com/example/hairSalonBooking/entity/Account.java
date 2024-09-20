package com.example.hairSalonBooking.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.*;
import lombok.experimental.FieldDefaults;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.time.LocalDate;
import java.util.Collection;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
//@FieldDefaults(level = AccessLevel.PRIVATE)
@Entity
public class Account implements UserDetails {
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return null;
    }

    @Override
    public String getUsername() {
        return this.username; // Muon cho nguoi dung dang nhap bang cai gi thi this cai do
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    long accountid;
    @Column(unique = true)
    @NotBlank(message = "Code can not be blank!")
    String username;
    @Size(min = 6, message = "Password must be at least 6 character!")
    String password;
    @Column(unique = true)
    @Email(message = "Invalid email")
    String email;
    String fullname;
    LocalDate dob;
    int gender;
    String phone;
    String image;
    String googleid;
    String googlename;
    boolean isDelete = false;
    long roleid;
    @Column(nullable = true)
    long salonid;
    @Column(nullable = true)
    long levelid;

}
