package com.clotho.monolithic.inventory.service;

import com.clotho.monolithic.inventory.model.Inventory;
import com.clotho.monolithic.inventory.repository.InventoryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class InventoryService {

    private final InventoryRepository inventoryRepository;

    // Upsert inventory record
    public Inventory createOrUpdateInventory(Inventory inventory) {
        return inventoryRepository.save(inventory);
    }

    public Optional<Inventory> getInventoryBySkuCode(String skuCode) {
        return inventoryRepository.findBySkuCode(skuCode);
    }

    public void deleteInventory(Long id) {
        inventoryRepository.deleteById(id);
    }

    /**
     * Verify availability for a given SKU.
     *
     * @param skuCode the SKU code
     * @param requiredQty quantity requested in the cart
     * @throws RuntimeException if item is out of stock or insufficient quantity
     */
    public void checkAvailability(String skuCode, int requiredQty) {
        Inventory inventory = inventoryRepository.findBySkuCode(skuCode)
                .orElseThrow(() -> new RuntimeException("SKU not found: " + skuCode));

        if (inventory.getQuantity() < requiredQty) {
            throw new RuntimeException("Insufficient stock for SKU: " + skuCode);
        }
    }

    /**
     * Reduce stock for a given SKU after successful checkout.
     * Transactional to ensure data consistency especially during concurrent updates.
     *
     * @param skuCode the SKU code
     * @param reduceBy quantity to reduce
     * @throws RuntimeException if stock is insufficient
     */
    @Transactional
    public void reduceStock(String skuCode, int reduceBy) {
        Inventory inventory = inventoryRepository.findBySkuCode(skuCode)
                .orElseThrow(() -> new RuntimeException("SKU not found: " + skuCode));

        int remaining = inventory.getQuantity() - reduceBy;
        if (remaining < 0) {
            throw new RuntimeException("Insufficient stock for SKU: " + skuCode);
        }

        inventory.setQuantity(remaining);
        inventoryRepository.save(inventory);
    }
}
