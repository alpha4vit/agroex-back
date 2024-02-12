package com.vention.agroex.controller;

import com.vention.agroex.entity.ProductCategoryEntity;
import com.vention.agroex.service.ProductCategoryService;
import com.vention.agroex.util.validator.ProductCategoryValidator;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/categories")
@RequiredArgsConstructor
@Tag(name = "Product Category Controller")
public class ProductCategoryController {

    private final ProductCategoryService productCategoryService;
    private final ProductCategoryValidator productCategoryValidator;

    @PostMapping()
    public ResponseEntity<ProductCategoryEntity> save(@RequestBody ProductCategoryEntity productCategoryEntity,
                                                      BindingResult bindingResult) {
        productCategoryValidator.validate(productCategoryEntity, bindingResult);
        var savedProductCategory = productCategoryService.save(productCategoryEntity);
        return ResponseEntity.ok(savedProductCategory);
    }

    @PutMapping("/{id}")
    public ResponseEntity<ProductCategoryEntity> update(@PathVariable Long id,
                                                        @RequestBody ProductCategoryEntity productCategoryEntity,
                                                        BindingResult bindingResult) {
        productCategoryValidator.validate(productCategoryEntity, bindingResult);
        var updatedProductCategory = productCategoryService.update(id, productCategoryEntity);
        return ResponseEntity.ok(updatedProductCategory);
    }

    @GetMapping()
    public ResponseEntity<List<ProductCategoryEntity>> findAll() {
        var fetchedProductCategoryList = productCategoryService.getAll();
        return ResponseEntity.ok(fetchedProductCategoryList);
    }

    @GetMapping("/{id}")
    public ResponseEntity<List<ProductCategoryEntity>> findAllSubcategories(@PathVariable Long id) {
        var fetchedProductSubcategoryCategoryList = productCategoryService.getSubcategoriesById(id);
        return ResponseEntity.ok(fetchedProductSubcategoryCategoryList);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteById(@PathVariable Long id) {
        productCategoryService.deleteById(id);
        return ResponseEntity.noContent().build();
    }
}
