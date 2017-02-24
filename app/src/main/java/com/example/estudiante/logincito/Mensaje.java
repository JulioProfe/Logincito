package com.example.estudiante.logincito;

/**
 * Created by estudiante on 24/02/17.
 */

import java.io.Serializable;

/**
 * Created by estudiante on 24/02/17.
 */

public class Mensaje implements Serializable {
    private String correo, contraseña, nickname;

    public Mensaje(String corre, String contraseña, String nickname){
        this.correo = corre;
        this.contraseña = contraseña;
        this.nickname = nickname;
    }

    public String getCorreo(){
        return correo;
    }

    public String getContraseña(){
        return contraseña;
    }

    public String getNickname(){
        return nickname;
    }
}
