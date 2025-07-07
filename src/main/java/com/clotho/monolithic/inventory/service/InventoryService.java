package com.clotho.monolithic.inventory.service;

import com.clotho.monolithic.inventory.model.Inventory;
import com.clotho.monolithic.inventory.repository.InventoryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class InventoryService {

    private final InventoryRepository inventoryRepository;

    public Inventory createOrUpdateInventory(Inventory inventory) {
        return inventoryRepository.save(inventory);
    }

    public Optional<Inventory> getInventoryBySkuCode(String skuCode) {
        return inventoryRepository.findBySkuCode(skuCode);
    }

    public void deleteInventory(Long id) {
        inventoryRepository.deleteById(id);
    }
}
