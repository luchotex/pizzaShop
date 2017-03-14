/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.tx.pizzashop.resource;

import com.tx.pizzashop.exception.InvalidRequestException;
import com.tx.pizzashop.model.Product;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.ws.rs.Produces;
import javax.ws.rs.InternalServerErrorException;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;

/**
 * REST Web Service
 *
 * @author Luis Kupferberg
 */
@Path("productShops")
public class ProductShopsResource {
    

    /**
     * Creates a new instance of ProductShopsResource
     */
    public ProductShopsResource() {
    }

    
    /**
     * Insert a product to for the sell of the client     
     *
     * @param clientId of the sale fixed for either client
     * @param productId of the product
     
     * @return an instance of Product
     */
    @POST
    @Produces(MediaType.APPLICATION_XML)
    public Product productShops(
            @QueryParam("clientId") String clientId,
            @QueryParam("productId") String productId) {
        System.out.println("Creating the product selling for the clientid= " + clientId
                + " productId= " + productId);
        
        Product product = ProductsResource.getProduct(productId);
        
        if (product == null ){
            // Product not existent not permited the sell, return 400 BAD REQUEST
            throw new InvalidRequestException("The productId not correspond to none product");
        }

        Product productToSell = product.createNewInstance();
        
        try {
            synchronized (PizzaShopsResource.getClientProductMap()) {
                if (!PizzaShopsResource.getClientProductMap().containsKey((clientId))) {
                    List<Product> productList = new ArrayList<>();
                    productList.add(productToSell);
                    PizzaShopsResource.getClientProductMap().put(clientId, productList);
                } else {
                    List<Product> productList = PizzaShopsResource.getClientProductMap().get(clientId);
                    productList.add(productToSell);
                }
            }
            return productToSell;
        } catch (Exception ex) {
            Logger.getLogger(ProductShopsResource.class.getName()).log(Level.SEVERE,
                    "Exception while selling product.", ex);
            throw new InternalServerErrorException();
        }
    }
}
