package com.clotho.monolithic.product.service;

import com.clotho.monolithic.product.model.Product;
import com.clotho.monolithic.product.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ProductService {

    private final ProductRepository productRepository;

    public Product createProduct(Product product) {
        return productRepository.save(product);
    }

    public List<Product> getAllProducts() {
        return productRepository.findAll();
    }

    public Optional<Product> getProductById(Long id) {
        return productRepository.findById(id);
    }

    public Optional<Product> getProductBySkuCode(String skuCode) {
        return productRepository.findBySkuCode(skuCode);
    }

    public void deleteProduct(Long id) {
        productRepository.deleteById(id);
    }
}
