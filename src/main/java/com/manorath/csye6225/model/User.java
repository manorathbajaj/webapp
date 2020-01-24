package com.manorath.csye6225.model;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import java.util.Date;


@Entity
@Table(name = "USERS")
public class User {

    public User(String email,String password,String firstName,String lastName) {
        this.setPassword(password);
        this.setEmail(email);
        this.setFirstName(firstName);
        this.setLastName(lastName);
    }
    public User () {

    }

    @Id
    @Column(name = "id",unique=true,columnDefinition="VARCHAR(200)")
    @Getter
    @Setter
    private String id;

    @Getter
    @Setter
    @Column(unique=true)
    @Email(regexp="^([a-zA-Z0-9_\\-\\.]+)@([a-zA-Z0-9_\\-\\.]+)\\.([a-zA-Z]{2,5})$", message="Enter a valid email address")
    private String email;

    @Getter
    @Setter
    @Column
    private String password;

    @Getter
    @Setter
    @Column(name = "first_name")
    @NotBlank(message = "First Name should not be empty")
    private String firstName;

    @Getter
    @Setter
    @Column(name = "last_name")
    @NotBlank(message = "Last Name should not be empty")
    private String lastName;

    @Getter
    @Setter
    @Column(name = "account_created")
    private Date accountCreated;

    @Getter
    @Setter
    @Column(name = "account_updated")
    private Date accountUpdated;

    @Override
    public String toString() {
        return "User [id:  " +id +" Name: " +firstName +" " +lastName + "email: " + email +"]";
    }
}
