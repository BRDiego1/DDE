package com.home.asismay;

import javafx.beans.property.SimpleStringProperty;

public class Usuario {
    private final SimpleStringProperty usuario;
    private final SimpleStringProperty email;
    private final SimpleStringProperty puesto;

    public Usuario(String usuario, String email, String puesto) {
        this.usuario = new SimpleStringProperty(usuario);
        this.email = new SimpleStringProperty(email);
        this.puesto = new SimpleStringProperty(puesto);
    }

    public String getUsuario() { return usuario.get(); }
    public String getEmail() { return email.get(); }
    public String getPuesto() { return puesto.get(); }
}
