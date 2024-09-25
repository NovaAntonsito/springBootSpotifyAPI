package com.springbackend.training.Controladores.DTO;

import com.springbackend.training.Entidades.UserDB;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import lombok.Value;

import java.io.Serializable;

/**
 * DTO for {@link com.springbackend.training.Entidades.UserDB}
 */
@Value
public class UserDto implements Serializable {
    @NotEmpty
    @NotBlank
    String usuarioNombre;
    @NotEmpty
    @NotBlank
    String password;

    public static UserDB toEntity(UserDto userDto) {
        UserDB userDB = new UserDB();
        userDB.setUsuarioNombre(userDto.getUsuarioNombre());
        userDB.setPassword(userDto.getPassword());
        return userDB;
    }
}