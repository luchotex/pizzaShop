/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.tx.pizzashop.resource;

import com.tx.pizzashop.exception.InvalidRequestException;
import com.tx.pizzashop.model.Ingredient;
import com.tx.pizzashop.model.Pizza;
import com.tx.pizzashop.model.Product;
import static com.tx.pizzashop.resource.PizzasResource.LARGE_SIZE;
import static com.tx.pizzashop.resource.PizzasResource.MEDIUM_SIZE;
import static com.tx.pizzashop.resource.PizzasResource.PERSONAL_SIZE;
import static com.tx.pizzashop.resource.PizzasResource.SMALL_SIZE;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.ejb.Singleton;
import javax.ws.rs.GET;
import javax.ws.rs.Produces;
import javax.ws.rs.InternalServerErrorException;
import javax.ws.rs.NotFoundException;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;

/**
 * REST Web Service
 *
 * @author Luis Kupferberg
 */
@Singleton
@Path("pizzaShops")
public class PizzaShopsResource {

    private static final Map<String, List<Product>> CLIENT_PRODUCT_MAP = new HashMap<>();

    /**
     * Creates a new instance of PizzaShopResource
     */
    public PizzaShopsResource() {
    }

    /**
     * Gets the all the clientProductMap
     *
     * @return Map
     */
    public static Map<String, List<Product>> getClientProductMap() {
        return CLIENT_PRODUCT_MAP;

    }

    /**
     * Insert a pizza to for the sell of the client
     *
     * @param clientId of the sale fixed for either client
     * @param productId of the pizza
     * @param numberOfSlices that is defined by the client
     * @param size size for the pizza
     * @param optionalIngredientsId that is defined by the client
     * @return an instance of Product
     */
    @POST
    @Produces(MediaType.APPLICATION_XML)
    public Pizza pizzaShops(
            @QueryParam("clientId") String clientId,
            @QueryParam("productId") String productId,
            @QueryParam("type") String numberOfSlices,
            @QueryParam("size") String size,
            @QueryParam("optionalIngredientsId") List<String> optionalIngredientsId) {
        System.out.println("Creating the pizza selling for the clientid= " + clientId
                + " productId= " + productId + " numberOfSlices=  " + numberOfSlices
                + " size = " + size);

        Integer theNumberOfSlices = validateNewPizzaInputs(size, numberOfSlices);

        if (theNumberOfSlices == 0) {
            theNumberOfSlices = PizzasResource.SIZE_PROPERTY_MAP.get(size).getSlices();
        }

        Pizza pizza = PizzasResource.getPizza(productId);

        if (pizza == null) {
            // Pizza not existent not permited the sell, return 400 BAD REQUEST
            throw new InvalidRequestException("The productId not correspond to none pizza");
        }

        Pizza pizzaToSell = pizza.createNewInstance();
        pizzaToSell.getSizeProperty().setSlices(theNumberOfSlices);

        addOptionalIngredients(pizzaToSell, optionalIngredientsId);

        try {
            synchronized (CLIENT_PRODUCT_MAP) {
                if (!CLIENT_PRODUCT_MAP.containsKey((clientId))) {
                    List<Product> productList = new ArrayList<>();
                    productList.add(pizzaToSell);
                    CLIENT_PRODUCT_MAP.put(clientId, productList);
                } else {
                    List<Product> productList = CLIENT_PRODUCT_MAP.get(clientId);
                    productList.add(pizzaToSell);
                }
            }

            return pizzaToSell;
        } catch (Exception ex) {
            Logger.getLogger(PizzaShopsResource.class.getName()).log(Level.SEVERE,
                    "Exception while selling pizza.", ex);
            throw new InternalServerErrorException();
        }
    }

    private Integer validateNewPizzaInputs(String size, String numberOfSlices) throws InvalidRequestException {
        if (!size.contains(PERSONAL_SIZE)
                && !size.contains(SMALL_SIZE)
                && !size.contains(MEDIUM_SIZE)
                && !size.contains(LARGE_SIZE)) {
            // ingredient not permitted to delete, return 400 BAD REQUEST
            throw new InvalidRequestException("The size entered is not Personal, Small, medium or Large");
        }
        Integer theNumberOfSlices;
        try {
            if (numberOfSlices.isEmpty()) {
                theNumberOfSlices = 0;
            } else {
                theNumberOfSlices = Integer.valueOf(numberOfSlices);
            }
        } catch (NumberFormatException e) {
            // invalid productId, return 400 INVALID REQUEST
            throw new InvalidRequestException("Number defined for numberOfSlices must be an Integer.");
        }
        if (!size.contains(PERSONAL_SIZE)
                && !size.contains(SMALL_SIZE)
                && !size.contains(MEDIUM_SIZE)
                && !size.contains(LARGE_SIZE)) {
            // ingredient not permitted to delete, return 400 BAD REQUEST
            throw new InvalidRequestException("The size entered is not Personal, Small, medium or Large");
        }
        return theNumberOfSlices;
    }

    private void addOptionalIngredients(Pizza pizzaToSell, List<String> optionalIngredientsId) throws InvalidRequestException {
        pizzaToSell.getIngredientData().setOptionalIngredients(new HashSet<>());

        for (String ingredientId : optionalIngredientsId) {
            Ingredient ingredient = IngredientsResource.getIngredient(ingredientId);

            if (ingredient == null) {
                // Pizza not existent not permited the sell, return 400 BAD REQUEST
                throw new InvalidRequestException("The optional ingredientId not exist");
            }

            pizzaToSell.getIngredientData().getOptionalIngredients().add(ingredient);

        }
    }

    /**
     * Returns all products bought by a client.
     *
     * @param clientId the id of the client
     * @return List
     */
    @GET
    @Path("{clientId}")
    @Produces(MediaType.APPLICATION_XML)
    public List<Product> pizzaShops(@PathParam("clientId") String clientId) {
        List<Product> result;
        try {
            synchronized (CLIENT_PRODUCT_MAP) {
                result = CLIENT_PRODUCT_MAP.get(clientId);
            }

            if (result == null) {
                throw new NotFoundException("Client with clientId = "
                        + clientId + " not found.");
            }
        } catch (Exception e) {
            Logger.getLogger(PizzaShopsResource.class.getName()).log(Level.SEVERE,
                    "Exception retrieving client product data", e);
            throw new InternalServerErrorException();

        }
        return result;
    }

    /**
     * Returns all the clientsId's.
     *
     * @return List
     */
    @GET
    @Produces(MediaType.APPLICATION_XML)
    public List<String> pizzaShops() {
        synchronized (CLIENT_PRODUCT_MAP) {
            List<String> result = new ArrayList<>();
            CLIENT_PRODUCT_MAP.entrySet().stream().forEach((element) -> {
                result.add(element.getKey());
            });
            return result;
        }
    }
}
