/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.tx.pizzashop.resource;

import com.tx.pizzashop.exception.InvalidRequestException;
import com.tx.pizzashop.model.Product;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
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
@Path("products")
public class ProductsResource {

    private static final Map<String, Product> PRODUCTS_MAP = new HashMap<>();

    /**
     * Creates a new instance of ProductsResource
     */
    public ProductsResource() {
    }

    /**
     * selling Returns the product for the Sale in the shop
     *
     * @param productId String
     * @return the Product
     */
    public static Product getProduct(String productId) {
        synchronized (PRODUCTS_MAP) {
            return PRODUCTS_MAP.get(productId);
        }
    }

    /**
     * Insert a product to the basis of the pizza shop
     *
     * @param productId of the pizza
     * @param type specific name of the pizza
     * @param preparationTime the time for prepare the pizza
     * @return an instance of java.lang.String
     */
    @POST
    @Produces(MediaType.APPLICATION_XML)
    public Product products(
            @QueryParam("productId") String productId,
            @QueryParam("type") String type,
            @QueryParam("preparationTime") String preparationTime) {
        System.out.println("Creating product data: productId = "
                + productId + " type =  " + type);

        Long thePreparationTime;
        try {
            thePreparationTime = Long.parseLong(preparationTime);
        } catch (NumberFormatException e) {
            // invalid preparationTime, return 400 INVALID REQUEST
            throw new InvalidRequestException("Number defined for preparationTime must be an long.");
        }

        try {
            Product newProduct = new Product(productId, type, thePreparationTime);
            synchronized (PRODUCTS_MAP) {

                if (!PRODUCTS_MAP.containsKey((productId))) {
                    PRODUCTS_MAP.put(productId, newProduct);
                }
            }

            return newProduct;
        } catch (Exception ex) {
            Logger.getLogger(ProductsResource.class.getName()).log(Level.SEVERE,
                    "Exception while creating product.", ex);
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
            synchronized (PRODUCTS_MAP) {
                if (PRODUCTS_MAP.containsKey(productId)) { // Product exists
                    PRODUCTS_MAP.remove(productId);
                    // succesfullresponse, return 204 NO CONTENT
                    response = Response.noContent().build();
                } else {
                    // inexistant product, return 404 NOT FOUND
                    response = Response.status(Response.Status.NOT_FOUND).build();
                }
            }
        } catch (Exception ex) {
            // 500 INTERNAL SERVER ERROR
            response = Response.serverError().build();
            Logger.getLogger(ProductsResource.class.getName()).log(Level.SEVERE,
                    "Exception while deleting pizza, productId = " + productId, ex);
        }

        return response;
    }

    /**
     * Returns all products.
     *
     * @return List
     */
    @GET
    @Produces(MediaType.APPLICATION_XML)
    public List<Product> products() {
        synchronized (PRODUCTS_MAP) {
            return new ArrayList<>(PRODUCTS_MAP.values());
        }
    }
}
