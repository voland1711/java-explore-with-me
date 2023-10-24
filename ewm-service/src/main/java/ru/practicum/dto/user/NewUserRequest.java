package ru.practicum.dto.user;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

@Getter
@Setter
@AllArgsConstructor
public class NewUserRequest {
    @NonNull
    @NotBlank(message = "name cannot be blank or contain only spaces")
    @JsonProperty(required = true)
    @Size(min = 2, max = 250)
    private String name;

    @NonNull
    @Email
    @JsonProperty(required = true)
    @Size(min = 6, max = 254)
    private String email;

}
