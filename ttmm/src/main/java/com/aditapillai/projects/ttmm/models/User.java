package com.aditapillai.projects.ttmm.models;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Builder;
import lombok.Getter;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.List;

@Document(collection = "users")
@Builder
@Getter
public class User {
    private final String id;
    private final String fullName;
    private final String firstName;
    private final String lastName;
    private final String phone;
    private final String email;

    @JsonIgnore
    private final String passwordHash;
    private final List<String> friends;

    public List<String> getFriends() {
        return this.friends;
    }
}
