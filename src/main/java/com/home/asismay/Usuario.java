package com.home.asismay;

import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;

public class Usuario {
    private final SimpleIntegerProperty id;
    private final SimpleStringProperty usuario;
    private final SimpleStringProperty email;
    private final SimpleStringProperty puesto;

    public Usuario(int id, String usuario, String email, String puesto) {
        this.id = new SimpleIntegerProperty(id);
        this.usuario = new SimpleStringProperty(usuario);
        this.email = new SimpleStringProperty(email);
        this.puesto = new SimpleStringProperty(puesto);
    }

    // Getters
    public int getId() { return id.get(); }
    public String getUsuario() { return usuario.get(); }
    public String getEmail() { return email.get(); }
    public String getPuesto() { return puesto.get(); }

    // Setters
    public void setId(int id) { this.id.set(id); }
    public void setUsuario(String usuario) { this.usuario.set(usuario); }
    public void setEmail(String email) { this.email.set(email); }
    public void setPuesto(String puesto) { this.puesto.set(puesto); }

    // Propiedades para TableView
    public SimpleIntegerProperty idProperty() { return id; }
    public SimpleStringProperty usuarioProperty() { return usuario; }
    public SimpleStringProperty emailProperty() { return email; }
    public SimpleStringProperty puestoProperty() { return puesto; }
}
