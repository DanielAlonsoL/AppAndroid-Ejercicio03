package com.danielalonso.ejercicio03.model;

import org.json.JSONObject;

import java.io.Serializable;
import java.util.Hashtable;

public class Pokemon implements Serializable {

    private int id;
    private String url;
    private String imagen_url;
    private String name;
    private int base_experience;
    private int height;
    private int weight;

    public Pokemon(int id, String url, String imagen_url, String nombre, int base_experience, int height, int weight, Hashtable<Integer, String> types) {
        this.id = id;
        this.url = url;
        this.imagen_url = imagen_url;
        this.name = nombre;
        this.base_experience = base_experience;
        this.height = height;
        this.weight = weight;
    }

    public Pokemon(int id) {
    }

    public Pokemon(int id, String name, String url) {
        this.id=id;
        this.name=name;
        this.url=url;
    }

    public Pokemon(int id, String name, String url, String imagen_url) {
        this.id=id;
        this.name=name;
        this.url=url;
        this.imagen_url=imagen_url;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String nombre) {
        this.name = nombre;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }
}
