package com.amason.hospitalinventory.graphql;

import com.amason.hospitalinventory.model.Product;
import com.amason.hospitalinventory.repository.ProductRepository;
import com.amason.hospitalinventory.service.StockService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.graphql.data.method.annotation.QueryMapping;
import org.springframework.graphql.data.method.annotation.SchemaMapping;
import org.springframework.stereotype.Controller;
import java.util.List;

// @Controller here is GraphQL's version of @RestController - it 
// tells Spring "this class resolves GraphQL queries," not REST endpoints
@Controller
public class ProductGraphQLController {

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private StockService stockService;

    // @QueryMapping connects this method to the "products" field we 
    // defined under "type Query" in the schema - when someone asks 
    // for "products" in a GraphQL query, THIS method runs
    @QueryMapping
    public List<Product> products() {
        return productRepository.findAll();
    }

    // @SchemaMapping connects this method to the "currentStock" field 
    // specifically on the "Product" type. Spring automatically calls 
    // this ONCE PER PRODUCT in the result, passing in that exact 
    // product - this is what makes the calculated field "just work" 
    // as if it were a normal column, from the caller's perspective
    @SchemaMapping(typeName = "Product", field = "currentStock")
    public int currentStock(Product product) {
        return stockService.calculateCurrentStock(product.getId());
    }
}
