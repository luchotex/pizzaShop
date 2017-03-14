/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.tx.pizzashop.resource;

import com.tx.pizzashop.exception.InvalidRequestException;
import com.tx.pizzashop.model.Ingredient;
import com.tx.pizzashop.model.Pizza;
import com.tx.pizzashop.model.PizzaIngredientData;
import com.tx.pizzashop.model.PizzaSizeProperty;
import com.tx.pizzashop.model.Product;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.ejb.Singleton;
import javax.ws.rs.Produces;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.InternalServerErrorException;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

/**
 * REST Web Service
 *
 * @author Luis Kupferberg
 */
@Singleton
@Path("pizzas")
public class PizzasResource {

    public static final String PERSONAL_SIZE = "Personal";
    public static final String SMALL_SIZE = "Small";
    public static final String MEDIUM_SIZE = "Medium";
    public static final String LARGE_SIZE = "Large";

    private static final Map<String, Pizza> PIZZAS_MAP = new HashMap<>();
    public static final Map<String, PizzaSizeProperty> SIZE_PROPERTY_MAP = new HashMap<>();

    static {
        SIZE_PROPERTY_MAP.put(PERSONAL_SIZE, new PizzaSizeProperty(1, PERSONAL_SIZE));
        SIZE_PROPERTY_MAP.put(SMALL_SIZE, new PizzaSizeProperty(2, SMALL_SIZE));
        SIZE_PROPERTY_MAP.put(MEDIUM_SIZE, new PizzaSizeProperty(4, MEDIUM_SIZE));
        SIZE_PROPERTY_MAP.put(LARGE_SIZE, new PizzaSizeProperty(8, LARGE_SIZE));
    }

    /**
     * Creates a new instance of PizzaResource
     */
    public PizzasResource() {
    }

    /**
     * selling Returns the pizza for the Sale in the shop
     *
     * @param productId String
     * @return the Pizza
     */
    public static Pizza getPizza(String productId) {
        synchronized (PIZZAS_MAP) {
            return PIZZAS_MAP.get(productId);
        }
    }

    /**
     * Remove the Ingredient for all the pizzas
     *
     * @param ingredientId of the ingredient to be removed
     */
    public static void removeIngredient(String ingredientId) {
        synchronized (PIZZAS_MAP) {
            PIZZAS_MAP.entrySet().stream().map((pizzaElement) -> pizzaElement.getValue()).map((pizza) -> pizza.getIngredientData()).map((ingredientData) -> {
                ingredientData.removeMainIngredient(ingredientId);
                return ingredientData;
            }).forEach((ingredientData) -> {
                ingredientData.removeOptionalIngredient(ingredientId);
            });
        }
    }

    /**
     * Insert a pizza to the basis of the pizza shop with all the properties
     * like size, main ingredients and optional ingredients
     *
     * @param productId of the pizza
     * @param type specific name of the pizza
     * @param size size to be added for the pizza type
     * @param preparationTime the time to prepare the pizza
     * @param mainIngredientsIdList id's from the main ingredients like cheesem
     * sauce, crust
     * @param optionalIngredientsIdList the ids for the extra ingredients
     * @return an instance of java.lang.String
     */
    @POST
    @Produces(MediaType.APPLICATION_XML)
    public Pizza pizzas(
            @QueryParam("productId") String productId,
            @QueryParam("type") String type,
            @QueryParam("size") String size,
            @QueryParam("preparationTime") String preparationTime,
            @QueryParam("mainIngredientsIdList") List<String> mainIngredientsIdList,
            @QueryParam("optionalIngredientsIdList") List<String> optionalIngredientsIdList) {
        System.out.println("Creating pizza data: productId = "
                + productId + " type =  " + type + " size = " + size);

        Long thePreparationTime;
        try {
            thePreparationTime = Long.parseLong(preparationTime);
        } catch (NumberFormatException e) {
            // invalid preparationTime, return 400 INVALID REQUEST
            throw new InvalidRequestException("Number defined for preparationTime must be an long.");
        }

        if (!size.contains(PERSONAL_SIZE)
                && !size.contains(SMALL_SIZE)
                && !size.contains(MEDIUM_SIZE)
                && !size.contains(LARGE_SIZE)) {
            // size not existent, return 400 BAD REQUEST
            throw new InvalidRequestException("The size entered is not Personal, Small, medium or Large");
        }

        Integer thePizzaSlices = SIZE_PROPERTY_MAP.get(size).getSlices();

        try {
            PizzaSizeProperty property = new PizzaSizeProperty(thePizzaSlices, size);

            Set<Ingredient> mainIngredients = new HashSet<>();
            mainIngredientsIdList.stream().map((ingredientId) -> IngredientsResource.getIngredient(ingredientId)).forEach((ingredientToAdd) -> {
                mainIngredients.add(ingredientToAdd);
            });
            Set<Ingredient> optionalIngredients = new HashSet<>();
            optionalIngredientsIdList.stream().map((ingredientId) -> IngredientsResource.getIngredient(ingredientId)).forEach((ingredientToAdd) -> {
                optionalIngredients.add(ingredientToAdd);
            });

            PizzaIngredientData ingredientData = new PizzaIngredientData(mainIngredients, optionalIngredients);
            Product product = new Product(productId, type, thePreparationTime);
            Pizza newPizza = new Pizza(type, property, false, ingredientData, product);
            synchronized (PIZZAS_MAP) {

                if (!PIZZAS_MAP.containsKey((productId))) {
                    PIZZAS_MAP.put(productId, newPizza);
                }
            }

            return newPizza;
        } catch (Exception ex) {
            Logger.getLogger(PizzasResource.class.getName()).log(Level.SEVERE,
                    "Exception while creating pizza.", ex);
            throw new InternalServerErrorException();
        }
    }

    /**
     * Deletes a pizza based on the productId.
     *
     * @param productId of the pizza to be deleted
     * @return Response
     */
    @DELETE
    @Produces(MediaType.APPLICATION_XML)
    public Response pizzas(@QueryParam("name") String productId) {
        Response response;
        try {
            synchronized (PIZZAS_MAP) {
                if (PIZZAS_MAP.containsKey(productId)) { // Pizza exists
                    PIZZAS_MAP.remove(productId);
                    // succesfullresponse, return 204 NO CONTENT
                    response = Response.noContent().build();
                } else {
                    // inexistant pizza, return 404 NOT FOUND
                    response = Response.status(Response.Status.NOT_FOUND).build();
                }
            }
        } catch (Exception ex) {
            // 500 INTERNAL SERVER ERROR
            response = Response.serverError().build();
            Logger.getLogger(PizzasResource.class.getName()).log(Level.SEVERE,
                    "Exception while deleting pizza, productId = " + productId, ex);
        }

        return response;
    }

    /**
     * Returns all pizzas.
     *
     * @return List
     */
    @GET
    @Produces(MediaType.APPLICATION_XML)
    public List<Pizza> pizzas() {
        synchronized (PIZZAS_MAP) {
            return new ArrayList<>(PIZZAS_MAP.values());
        }
    }
}
