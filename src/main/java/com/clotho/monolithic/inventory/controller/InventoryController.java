package com.clotho.monolithic.inventory.controller;

import com.clotho.monolithic.inventory.model.Inventory;
import com.clotho.monolithic.inventory.service.InventoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/inventory")
@RequiredArgsConstructor
public class InventoryController {

    private final InventoryService inventoryService;

    @PostMapping
    public ResponseEntity<Inventory> upsertInventory(@RequestBody Inventory inventory) {
        return ResponseEntity.ok(inventoryService.createOrUpdateInventory(inventory));
    }

    @GetMapping("/sku/{skuCode}")
    public ResponseEntity<Inventory> getInventoryBySkuCode(@PathVariable String skuCode) {
        return inventoryService.getInventoryBySkuCode(skuCode)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteInventory(@PathVariable Long id) {
        inventoryService.deleteInventory(id);
        return ResponseEntity.noContent().build();
    }
}
