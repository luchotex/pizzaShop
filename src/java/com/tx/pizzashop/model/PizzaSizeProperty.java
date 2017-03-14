/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.tx.pizzashop.model;

import java.util.Objects;

/**
 *
 * @author Luis Kupferberg
 */
public class PizzaSizeProperty {

    private Integer slices;
    private String size;

    public PizzaSizeProperty() {
    }

    public PizzaSizeProperty(Integer slices, String size) {
        this.slices = slices;
        this.size = size;
    }

    public synchronized Integer getSlices() {
        return slices;
    }

    public synchronized void setSlices(Integer slices) {
        this.slices = slices;
    }

    public synchronized String getSize() {
        return size;
    }

    public synchronized void setSize(String size) {
        this.size = size;
    }

    @Override
    public synchronized boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final PizzaSizeProperty propertyToCompare = (PizzaSizeProperty) obj;
        if (!Objects.equals(this.size, propertyToCompare.size)) {
            return false;
        }
        return Objects.equals(this.slices, propertyToCompare.slices);
    }

    @Override
    public int hashCode() {
        int hash = 3;
        hash = 53 * hash + Objects.hashCode(this.slices);
        hash = 53 * hash + Objects.hashCode(this.size);
        return hash;
    }
    
    public PizzaSizeProperty createNewInstance(){
        return new PizzaSizeProperty(slices, size);
    }

}
