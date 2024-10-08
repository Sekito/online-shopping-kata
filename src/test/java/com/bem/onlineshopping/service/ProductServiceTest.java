package com.bem.onlineshopping.service;

import com.bem.onlineshopping.dto.ProductDTO;
import com.bem.onlineshopping.exception.ResourceNotFoundException;
import com.bem.onlineshopping.mappers.ProductMapper;
import com.bem.onlineshopping.model.Product;
import com.bem.onlineshopping.repository.ProductRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class ProductServiceTest {

    @InjectMocks
    private ProductService productService;

    @Mock
    private ProductRepository productRepository;

    @Mock
    private ProductMapper productMapper;

    private Product product;

    @BeforeEach
    public void setUp() {
        product = new Product();
        product.setProductId(1L);
        product.setName("Sample Product");
        product.setPrice(100.0);
        product.setInventory(10);
    }

    @Test
    public void testGetAllProducts_Success() {
        List<Product> products = new ArrayList<>();
        products.add(product);

        when(productRepository.findAll()).thenReturn(products);

        List<Product> foundProducts = productService.getAllProducts();

        assertNotNull(foundProducts);
        assertEquals(1, foundProducts.size());
        assertEquals(product.getProductId(), foundProducts.get(0).getProductId());
    }

    @Test
    public void testGetProductById_Success() {
        when(productRepository.findById(1L)).thenReturn(Optional.of(product));

        Product foundProduct = productService.getProductById(1L);

        assertNotNull(foundProduct);
        assertEquals(product.getProductId(), foundProduct.getProductId());
    }

    @Test
    public void testGetProductById_NotFound() {
        when(productRepository.findById(1L)).thenReturn(Optional.empty());

        Exception exception = assertThrows(ResourceNotFoundException.class, () -> {
            productService.getProductById(1L);
        });

        assertEquals("Product not found", exception.getMessage());
    }

    @Test
    public void testCreateProduct_Success() {
        ProductDTO productDTO = new ProductDTO();
        productDTO.setName("New Product");
        productDTO.setPrice(150.0);

        when(productMapper.toEntity(productDTO)).thenReturn(product);
        when(productRepository.save(product)).thenReturn(product);

        Product createdProduct = productService.createProduct(productDTO);

        assertNotNull(createdProduct);
        assertEquals(product.getName(), createdProduct.getName());
        verify(productMapper).toEntity(productDTO);
        verify(productRepository).save(product);
    }

    @Test
    public void testUpdateProduct_NotFound() {
        ProductDTO updatedProductDTO = new ProductDTO();
        updatedProductDTO.setName("Updated Product");
        updatedProductDTO.setPrice(120.0);

        when(productRepository.findById(1L)).thenReturn(Optional.empty());

        Exception exception = assertThrows(ResourceNotFoundException.class, () -> {
            productService.updateProduct(1L, updatedProductDTO);
        });

        assertEquals("Product not found", exception.getMessage());
    }

    @Test
    public void testDeleteProduct_Success() {
        doNothing().when(productRepository).deleteById(1L);

        productService.deleteProduct(1L);

        verify(productRepository).deleteById(1L);
    }

    @Test
    public void testGetAvailableProducts_Success() {
        List<Product> availableProducts = new ArrayList<>();
        availableProducts.add(product);

        when(productRepository.findProductByInventoryGreaterThan(0)).thenReturn(availableProducts);

        List<Product> foundProducts = productService.getAvailableProducts();

        assertNotNull(foundProducts);
        assertEquals(1, foundProducts.size());
        assertEquals(product.getProductId(), foundProducts.get(0).getProductId());
    }
}
