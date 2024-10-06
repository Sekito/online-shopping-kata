package com.bem.onlineshopping.service;

import com.bem.onlineshopping.dto.ProductDTO;
import com.bem.onlineshopping.exception.ResourceNotFoundException;
import com.bem.onlineshopping.mappers.ProductMapper;
import com.bem.onlineshopping.model.Product;
import com.bem.onlineshopping.repository.ProductRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class ProductService {
    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private ProductMapper productMapper;

    public List<Product> getAllProducts() {
        return productRepository.findAll();
      /*  return productRepository.findAll().stream()
                .map(productMapper::toDto)
                .collect(Collectors.toList());*/
    }

    public Product getProductById(Long id) {
        return productRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Product not found"));
    }

    public Product createProduct(ProductDTO productDTO) {
        Product product = productMapper.toEntity(productDTO);
        return productRepository.save(product);
    }

    public Product updateProduct(Long id, ProductDTO updatedProductDTO) {
        Product existingProduct = productRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Product not found"));
        existingProduct.setName(updatedProductDTO.getName());
        existingProduct.setPrice(updatedProductDTO.getPrice());
        return productRepository.save(existingProduct);
    }

    public void deleteProduct(Long id) {
        productRepository.deleteById(id);
    }

    public List<Product> getAvailableProducts() {
        return productRepository.findProductByInventoryGreaterThan(0);
    }
}