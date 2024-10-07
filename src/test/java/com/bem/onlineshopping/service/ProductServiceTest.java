package com.bem.onlineshopping.service;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

import com.bem.onlineshopping.dto.ProductDTO;
import com.bem.onlineshopping.mappers.ProductMapper;
import com.bem.onlineshopping.model.Product;
import com.bem.onlineshopping.repository.ProductRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import com.bem.onlineshopping.exception.BadRequestException;
import com.bem.onlineshopping.exception.ResourceNotFoundException;

public class ProductServiceTest {

    @InjectMocks
    private ProductService productService;

    @Mock
    private ProductRepository productRepository;

    @Mock
    private ProductMapper productMapper;

    private Product product;
    private ProductDTO productDTO;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);

        // Initialize test objects
        product = new Product();
        product.setProductId(1L);
        product.setName("Test Product");
        product.setPrice(100.0);

        productDTO = new ProductDTO();
        productDTO.setName("Test Product");
        productDTO.setPrice(100.0);
    }

    @Test
    public void getAllProducts_ShouldReturnListOfProducts() {
        List<Product> products = new ArrayList<>();
        products.add(product);

        when(productRepository.findAll()).thenReturn(products);

        List<Product> foundProducts = productService.getAllProducts();

        assertNotNull(foundProducts);
        assertEquals(1, foundProducts.size());
        assertEquals(product, foundProducts.get(0));
    }

    @Test
    public void getProductById_ShouldReturnProduct() {
        Long productId = 1L;
        when(productRepository.findById(productId)).thenReturn(Optional.of(product));

        Product foundProduct = productService.getProductById(productId);

        assertNotNull(foundProduct);
        assertEquals(product, foundProduct);
    }

    @Test
    public void getProductById_ShouldThrowResourceNotFoundException() {
        Long productId = 1L;
        when(productRepository.findById(productId)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> {
            productService.getProductById(productId);
        });
    }

    @Test
    public void createProduct_ShouldReturnCreatedProduct() {
        when(productMapper.toEntity(productDTO)).thenReturn(product);
        when(productRepository.save(product)).thenReturn(product);

        Product createdProduct = productService.createProduct(productDTO);

        assertNotNull(createdProduct);
        assertEquals(product, createdProduct);
    }

    @Test
    public void updateProduct_ShouldReturnUpdatedProduct() {
        Long productId = 1L;
        Product updatedProduct = new Product();
        updatedProduct.setProductId(productId);
        updatedProduct.setName("Updated Product");
        updatedProduct.setPrice(150.0);

        when(productRepository.findById(productId)).thenReturn(Optional.of(product));
        when(productMapper.toEntity(productDTO)).thenReturn(updatedProduct);
        when(productRepository.save(any(Product.class))).thenReturn(updatedProduct);

        productDTO.setName("Updated Product");
        productDTO.setPrice(150.0);

        Product result = productService.updateProduct(productId, productDTO);

        assertNotNull(result);
        assertEquals(updatedProduct.getName(), result.getName());
        assertEquals(updatedProduct.getPrice(), result.getPrice());
    }

    @Test
    public void updateProduct_ShouldThrowResourceNotFoundException() {
        Long productId = 1L;
        when(productRepository.findById(productId)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> {
            productService.updateProduct(productId, productDTO);
        });
    }

    @Test
    public void deleteProduct_ShouldCallDeleteById() {
        Long productId = 1L;

        doNothing().when(productRepository).deleteById(productId);
        productService.deleteProduct(productId);

        verify(productRepository, times(1)).deleteById(productId);
    }

    @Test
    public void getAvailableProducts_ShouldReturnListOfAvailableProducts() {
        List<Product> availableProducts = new ArrayList<>();
        Product availableProduct = new Product();
        availableProduct.setProductId(2L);
        availableProduct.setInventory(10);
        availableProducts.add(availableProduct);

        when(productRepository.findProductByInventoryGreaterThan(0)).thenReturn(availableProducts);

        List<Product> foundAvailableProducts = productService.getAvailableProducts();

        assertNotNull(foundAvailableProducts);
        assertEquals(1, foundAvailableProducts.size());
        assertEquals(availableProduct, foundAvailableProducts.get(0));
    }
}
