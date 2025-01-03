package com.purwandi.app.gateway.handler;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.purwandi.app.gateway.model.ProductEntity;

@RestController
@RequestMapping("/v1/product")
public class ProductHandler {

    @GetMapping("/")
    public ResponseEntity<ProductEntity> GetProduct() {
        ProductEntity product = new ProductEntity();
        product.setId(12);
        product.setName("Sepatu");

        return ResponseEntity.ok().body(product);
    }
}
