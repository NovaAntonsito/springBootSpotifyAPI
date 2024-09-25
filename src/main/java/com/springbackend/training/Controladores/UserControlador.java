package com.springbackend.training.Controladores;

import com.springbackend.training.Controladores.Base.ControladorBase;
import com.springbackend.training.Controladores.DTO.UserDto;
import com.springbackend.training.Entidades.UserDB;
import com.springbackend.training.Servicios.UserServicio;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@CrossOrigin(origins = "*")
@RequestMapping(path = "api/v1/user")
@RequiredArgsConstructor
@Slf4j
public class UserControlador extends ControladorBase<UserDB, UserServicio> {

        private final UserServicio userServicio;


        @PostMapping("register")
        public ResponseEntity<?> registerUser(@RequestBody UserDto userDto) {

                UserDB user = UserDto.toEntity(userDto);
             userServicio.crearUsuarioLocal(user);
             return ResponseEntity.status(HttpStatus.OK).body(Map.of("success", true , "body" , "El usuario fue guardado con exito"));
        }

}
