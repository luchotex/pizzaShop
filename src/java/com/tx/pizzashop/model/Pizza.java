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
public class Pizza extends Product {

    private PizzaSizeProperty sizeProperty;
    private boolean hasDeliver;
    private PizzaIngredientData ingredientData;

    public Pizza() {
    }

    public Pizza(String type, PizzaSizeProperty sizeProperty, boolean hasDeliver, PizzaIngredientData ingredientData, Product product) {
        super(product.getProductId(), product.getType(), product.getPreparationTime());
        this.sizeProperty = sizeProperty;
        this.hasDeliver = hasDeliver;
        this.ingredientData = ingredientData;
    }

    public PizzaSizeProperty getSizeProperty() {
        return sizeProperty;
    }

    public void setSizeProperty(PizzaSizeProperty sizeProperty) {
        this.sizeProperty = sizeProperty;
    }

    public boolean isHasDeliver() {
        return hasDeliver;
    }

    public void setHasDeliver(boolean hasDeliver) {
        this.hasDeliver = hasDeliver;
    }

    public PizzaIngredientData getIngredientData() {
        return ingredientData;
    }

    public void setIngredientData(PizzaIngredientData ingredientData) {
        this.ingredientData = ingredientData;
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
        final Pizza pizzaToCompare = (Pizza) obj;

        if (this.getProductId() != pizzaToCompare.getProductId()) {
            return false;
        }

        if (this.getType() != pizzaToCompare.getType()) {
            return false;
        }

        if (this.hasDeliver != pizzaToCompare.hasDeliver) {
            return false;
        }

        if (!this.sizeProperty.equals(pizzaToCompare.sizeProperty)) {
            return false;
        }
        return this.ingredientData.equals(pizzaToCompare.ingredientData);
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 59 * hash + Objects.hashCode(this.getSizeProperty().getSize());
        hash = 59 * hash + this.getSizeProperty().hashCode();
        hash = 59 * hash + Objects.hashCode(this.sizeProperty);
        hash = 59 * hash + (this.hasDeliver ? 1 : 0);
        hash = 59 * hash + this.ingredientData.hashCode();
        return hash;
    }

    public Pizza createNewInstance() {
        Pizza result = new Pizza();
        result.setProductId(getProductId());
        result.setPreparationTime(getPreparationTime());
        result.setType(getType());
        result.setIngredientData(ingredientData != null ? ingredientData.createNewInstance() : null);
        result.setSizeProperty(sizeProperty != null ? sizeProperty.createNewInstance() : null);

        return result;

    }

}
