package com.clotho.monolithic.product.controller;

import com.clotho.monolithic.product.model.Product;
import com.clotho.monolithic.product.service.ProductService;
import com.clotho.monolithic.product.service.SupabaseStorageService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("/products")
@RequiredArgsConstructor
public class ProductController {

    private final ProductService productService;
    private final SupabaseStorageService supabaseStorageService;

    @PostMapping
    public ResponseEntity<Product> createProduct(@RequestBody Product product) {
        return ResponseEntity.ok(productService.createProduct(product));
    }

    @GetMapping
    public ResponseEntity<List<Product>> getAllProducts() {
        return ResponseEntity.ok(productService.getAllProducts());
    }

    @GetMapping("/sku/{skuCode}")
    public ResponseEntity<Product> getProductBySkuCode(@PathVariable String skuCode) {
        return productService.getProductBySkuCode(skuCode)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/{id}")
    public ResponseEntity<Product> getProductById(@PathVariable Long id) {
        return productService.getProductById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PutMapping("/{id}")
    public ResponseEntity<Product> updateProduct(
            @PathVariable Long id,
            @RequestPart("product") Product updatedProduct,
            @RequestPart(value = "file", required = false) MultipartFile file) {

        return productService.getProductById(id).map(existingProduct -> {
            // Update fields
            existingProduct.setName(updatedProduct.getName());
            existingProduct.setDescription(updatedProduct.getDescription());
            existingProduct.setPrice(updatedProduct.getPrice());
            existingProduct.setCategory(updatedProduct.getCategory());
            existingProduct.setSkuCode(updatedProduct.getSkuCode());

            // Handle image upload if a new file is provided
            if (file != null && !file.isEmpty()) {
                String fileUrl = supabaseStorageService.uploadFile(file);
                existingProduct.setImageUrl(fileUrl);
            }

            Product savedProduct = productService.createProduct(existingProduct);
            return ResponseEntity.ok(savedProduct);
        }).orElse(ResponseEntity.notFound().build());
    }


    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteProduct(@PathVariable Long id) {
        productService.deleteProduct(id);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/upload/{productId}")
    public ResponseEntity<Product> uploadProductImage(
            @PathVariable Long productId,
            @RequestParam("file") MultipartFile file) {

        return productService.getProductById(productId).map(product -> {
            String fileUrl = supabaseStorageService.uploadFile(file);
            product.setImageUrl(fileUrl);
            Product updated = productService.createProduct(product);
            return ResponseEntity.ok(updated);
        }).orElse(ResponseEntity.notFound().build());
    }
}
