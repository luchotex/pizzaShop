/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.tx.pizzashop.resource;

import com.tx.pizzashop.model.Ingredient;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.ejb.Singleton;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.Produces;
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
@Path("ingredients")
public class IngredientsResource {
    
    public static final String CHEESE_TYPE = "cheese";
    public static final String SAUCE_TYPE = "sauce";
    public static final String CRUST_TYPE = "crust";

    private static final Map<String, Ingredient> INGREDIENT_MAP = new HashMap<>();

    static {
        INGREDIENT_MAP.put("cheese1", new Ingredient("cheese1", CHEESE_TYPE, "mozzarella"));
        INGREDIENT_MAP.put("cheese2", new Ingredient("cheese2", CHEESE_TYPE, "creolle"));
        INGREDIENT_MAP.put("cheese3", new Ingredient("cheese3", CHEESE_TYPE, "parmeesan"));

        INGREDIENT_MAP.put("sauce1", new Ingredient("sauce1", SAUCE_TYPE, "bolognese"));
        INGREDIENT_MAP.put("sauce2", new Ingredient("sauce2", SAUCE_TYPE, "cheese"));
        INGREDIENT_MAP.put("sauce3", new Ingredient("sauce3", SAUCE_TYPE, "bechamel"));
        INGREDIENT_MAP.put("sauce4", new Ingredient("sauce4", SAUCE_TYPE, "green"));

        INGREDIENT_MAP.put("crust1", new Ingredient("crust1", CRUST_TYPE, "thin"));
        INGREDIENT_MAP.put("crust2", new Ingredient("crust2", CRUST_TYPE, "thick"));
        INGREDIENT_MAP.put("crust3", new Ingredient("crust3", CRUST_TYPE, "filled with cheese"));

    }

    /**
     * Creates a new instance of IngredientsResource
     */
    public IngredientsResource() {

    }

    /**
     *Gets the ingredient used to know the properties of this using the ingredientId
     * 
     * @param ingredientId the id of the ingredient
     * @return Ingredient the ingredient retrieved
     */
    public static Ingredient getIngredient(String ingredientId) {
        synchronized (INGREDIENT_MAP) {
            return INGREDIENT_MAP.get(ingredientId);
        }

    }

    /**
     * Insert a ingredient to the basis of the pizza shop
     *
     * @param ingredientId the identifier for the ingredient
     * @param name the specific name of the ingredient
     * @param type the type of ingredient
     * @return an instance of java.lang.String
     */
    @POST
    @Produces(MediaType.APPLICATION_XML)
    public Ingredient ingredients(
            @QueryParam("ingredientId") String ingredientId,
            @QueryParam("name") String name,
            @QueryParam("type") String type) {
        System.out.println("Creating Ingredient data: ingredientId = "
                + ingredientId + " name =  " + name
                + " type = " + type);
        try {
            Ingredient newIngredient = new Ingredient(ingredientId, name, type);

            synchronized (INGREDIENT_MAP) {
                if (!INGREDIENT_MAP.containsKey((ingredientId))) {
                    INGREDIENT_MAP.put(ingredientId, newIngredient);
                }
            }

            return newIngredient;
        } catch (Exception ex) {
            Logger.getLogger(IngredientsResource.class.getName()).log(Level.SEVERE,
                    "Exception while creating ingredient.", ex);
            throw new InternalServerErrorException();
        }
    }

    /**
     * Deletes a Ingredient based on the ingredientId.
     *
     * @param ingredientId the identifier for the ingredient
     * @return Response
     */
    @DELETE
    @Produces(MediaType.APPLICATION_XML)
    public Response ingredients(@QueryParam("name") String ingredientId) {
        Response response;
        try {
            synchronized (INGREDIENT_MAP) {
                if (ingredientId.contains(CHEESE_TYPE)
                        || ingredientId.contains(SAUCE_TYPE)
                        || ingredientId.contains(CRUST_TYPE)) {
                    // ingredient not permitted to delete, return 400 BAD REQUEST
                    return Response.status(Response.Status.BAD_REQUEST).build();
                }
                
                
                if (INGREDIENT_MAP.containsKey(ingredientId)) { // ingredient exists
                    // Remove the ingredients in all pizzas
                    PizzasResource.removeIngredient(ingredientId);
                    INGREDIENT_MAP.remove(ingredientId);
                    // succesfullresponse, return 204 NO CONTENT
                    response = Response.noContent().build();
                } else {
                    // inexistant ingredient, return 404 NOT FOUND
                    response = Response.status(Response.Status.NOT_FOUND).build();
                }
            }
        } catch (Exception ex) {
            // 500 INTERNAL SERVER ERROR
            response = Response.serverError().build();
            Logger.getLogger(IngredientsResource.class.getName()).log(Level.SEVERE,
                    "Exception while deleting ingredient, ingredientId = " + ingredientId, ex);
        }

        return response;
    }

    /**
     * Returns all ingredients.
     *
     * @return List
     */
    @GET
    @Produces(MediaType.APPLICATION_XML)
    public List<Ingredient> ingredients() {
        synchronized (INGREDIENT_MAP) {
            return new ArrayList<>(INGREDIENT_MAP.values());
        }
    }
}
