package com.estudo.aula.dto;

import com.estudo.aula.entities.User;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

import java.util.ArrayList;
import java.util.List;

public class UserDTO {

    private Long id;

    @NotBlank(message = "Campo obrigatorio.")
    private String firstName;
    private String lastName;

    @Email(message = "Inserir email valido.")
    private String email;

    private List<RoleDTO> roles = new ArrayList<>();


    public UserDTO(){}

    public UserDTO(Long id, String firstName , String lastName, String email ) {
        this.id = id;
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;

    }

    public UserDTO(User entity){
        id = entity.getId();
        firstName = entity.getFirstName();
        lastName = entity.getLastName();
        email = entity.getEmail();

        for (var role : entity.getRoles()) {
            roles.add(new RoleDTO(role));
        }

    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public List<RoleDTO> getRoles() {
        return roles;
    }

    public void setRoles(List<RoleDTO> roles) {
        this.roles = roles;
    }
}
