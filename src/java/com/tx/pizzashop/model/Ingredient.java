/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.tx.pizzashop.model;

import java.util.Objects;
import javax.xml.bind.annotation.XmlRootElement;

/**
 *
 * @author Luis Kupferberg
 */
@XmlRootElement
public class Ingredient {

    private String ingredientId;
    private String name;
    private String type;

    public Ingredient() {
        this.ingredientId = "";
        this.type = "";
        this.name = "";
    }

    public Ingredient(String ingredientId, String name, String type) {
        this.ingredientId = ingredientId;
        this.name = name;
        this.type = type;
    }

    public String getIngredientId() {
        return ingredientId;
    }

    public void setIngredientId(String ingredientId) {
        this.ingredientId = ingredientId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final Ingredient ingredientToCompare = (Ingredient) obj;
        if (!Objects.equals(this.ingredientId, ingredientToCompare.ingredientId)) {
            return false;
        }

        if (!Objects.equals(this.name, ingredientToCompare.name)) {
            return false;
        }
        return Objects.equals(this.type, ingredientToCompare.type);
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 29 * hash + Objects.hashCode(this.ingredientId);
        hash = 29 * hash + Objects.hashCode(this.name);
        hash = 29 * hash + Objects.hashCode(this.type);
        return hash;
    }

}
