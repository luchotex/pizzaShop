/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.tx.pizzashop.model;

import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

/**
 *
 * @author Luis Kupferberg
 */
public class PizzaIngredientData {

    private Set<Ingredient> mainIngredients;
    private Set<Ingredient> optionalIngredients;

    public PizzaIngredientData() {
    }

    public PizzaIngredientData(Set<Ingredient> mainIngredients, Set<Ingredient> optionalIngredients) {
        this.mainIngredients = mainIngredients;
        this.optionalIngredients = optionalIngredients;
    }

    public Set<Ingredient> getMainIngredients() {
        return mainIngredients;
    }

    public void setMainIngredients(Set<Ingredient> mainIngredients) {
        this.mainIngredients = mainIngredients;
    }

    public Set<Ingredient> getOptionalIngredients() {
        return optionalIngredients;
    }

    public void setOptionalIngredients(Set<Ingredient> optionalIngredients) {
        this.optionalIngredients = optionalIngredients;
    }
    
    public void removeMainIngredient(String ingredientId) {
        mainIngredients.remove(ingredientId);
    }

    public void removeOptionalIngredient(String ingredientId) {
        optionalIngredients.remove(ingredientId);
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
        final PizzaIngredientData ingredientToCompare = (PizzaIngredientData) obj;
        if (!Objects.equals(this.mainIngredients, ingredientToCompare.mainIngredients)) {
            return false;
        }
        if (!Objects.equals(this.optionalIngredients, ingredientToCompare.optionalIngredients)) {
            return false;
        }
        return true;
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 31 * hash + Objects.hashCode(this.mainIngredients);
        hash = 31 * hash + Objects.hashCode(this.optionalIngredients);
        return hash;
    }
    
    public PizzaIngredientData createNewInstance(){
        PizzaIngredientData result = new PizzaIngredientData();
        Set<Ingredient> main = new HashSet<>();
        Set<Ingredient> optional = new HashSet<>();
        
        mainIngredients.stream().forEach((ingredient) -> {
            main.add(ingredient);
        });
        result.setMainIngredients(main);
        result.setOptionalIngredients(optional);
        
        return result;
    } 

}
