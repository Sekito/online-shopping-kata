package com.bem.onlineshopping.controller;

import com.bem.onlineshopping.dto.ProductDTO;
import com.bem.onlineshopping.mappers.ProductMapper;
import com.bem.onlineshopping.model.Product;
import com.bem.onlineshopping.service.ProductService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.server.mvc.WebMvcLinkBuilder;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/products")
public class ProductController {

    private final ProductService productService;
    private final ProductMapper productMapper;

    @Autowired
    public ProductController(ProductService productService,
                             ProductMapper productMapper) {
        this.productService = productService;
        this.productMapper = productMapper;
    }

    @GetMapping
    public ResponseEntity<List<EntityModel<ProductDTO>>> getAllProducts() {
        List<Product> products = productService.getAllProducts();
        List<EntityModel<ProductDTO>> productDTOS = products.stream()
                .map(product -> {
                    ProductDTO productDTO = productMapper.toDto(product);
                    EntityModel<ProductDTO> resource = EntityModel.of(productDTO);
                    resource.add(WebMvcLinkBuilder.linkTo(WebMvcLinkBuilder.methodOn(ProductController.class).getProductById(product.getProductId())).withSelfRel());
                    return resource;
                })
                .collect(Collectors.toList());
        return ResponseEntity.ok(productDTOS);
    }

    @GetMapping("/available")
    public ResponseEntity<List<EntityModel<ProductDTO>>> getAllAvailableProducts() {
        List<Product> products = productService.getAvailableProducts();
        List<EntityModel<ProductDTO>> productDTOS = products.stream()
                .map(product -> {
                    ProductDTO productDTO = productMapper.toDto(product);
                    EntityModel<ProductDTO> resource = EntityModel.of(productDTO);
                    resource.add(WebMvcLinkBuilder.linkTo(WebMvcLinkBuilder.methodOn(ProductController.class).getProductById(product.getProductId())).withSelfRel());
                    return resource;
                })
                .collect(Collectors.toList());
        return ResponseEntity.ok(productDTOS);
    }

    @GetMapping("/{id}")
    public ResponseEntity<EntityModel<ProductDTO>> getProductById(@PathVariable Long id) {
        Product product = productService.getProductById(id);
        ProductDTO productDTO = productMapper.toDto(product);

        EntityModel<ProductDTO> resource = EntityModel.of(productDTO);
        resource.add(WebMvcLinkBuilder.linkTo(WebMvcLinkBuilder.methodOn(ProductController.class).getProductById(id)).withSelfRel());
        resource.add(WebMvcLinkBuilder.linkTo(WebMvcLinkBuilder.methodOn(ProductController.class).getAllProducts()).withRel("allProducts"));
        resource.add(WebMvcLinkBuilder.linkTo(WebMvcLinkBuilder.methodOn(ProductController.class).getAllAvailableProducts()).withRel("availableProducts"));

        return ResponseEntity.ok(resource);
    }

    @PostMapping
    public ResponseEntity<EntityModel<ProductDTO>> createProduct(@Valid @RequestBody ProductDTO productDTO) {
        Product product = productService.createProduct(productDTO);
        ProductDTO productDTO2 = productMapper.toDto(product);

        EntityModel<ProductDTO> resource = EntityModel.of(productDTO2);
        resource.add(WebMvcLinkBuilder.linkTo(WebMvcLinkBuilder.methodOn(ProductController.class).getProductById(product.getProductId())).withSelfRel());
        resource.add(WebMvcLinkBuilder.linkTo(WebMvcLinkBuilder.methodOn(ProductController.class).getAllProducts()).withRel("allProducts"));

        return ResponseEntity.ok(resource);
    }

    /* Uncomment if needed
    @PutMapping("/{id}")
    public ResponseEntity<ProductDTO> updateProduct(@PathVariable Long id, @Valid @RequestBody ProductDTO productDTO) {
        Product product = productService.updateProduct(id, productDTO);
        return ResponseEntity.ok(productMapper.toDto(product));
    }
    */

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteProduct(@PathVariable Long id) {
        productService.deleteProduct(id);
        return ResponseEntity.noContent().build();
    }
}