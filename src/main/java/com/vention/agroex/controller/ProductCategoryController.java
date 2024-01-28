package com.vention.agroex.controller;

import com.vention.agroex.entity.ProductCategory;
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
    public ResponseEntity<ProductCategory> save(@RequestBody ProductCategory productCategory,
                                                BindingResult bindingResult) {
        productCategoryValidator.validate(productCategory, bindingResult);
        var savedProductCategory = productCategoryService.save(productCategory);
        return ResponseEntity.ok(savedProductCategory);
    }

    @PutMapping("/{id}")
    public ResponseEntity<ProductCategory> update(@PathVariable Long id,
                                                  @RequestBody ProductCategory productCategory,
                                                  BindingResult bindingResult) {
        productCategoryValidator.validate(productCategory, bindingResult);
        var updatedProductCategory = productCategoryService.update(id, productCategory);
        return ResponseEntity.ok(updatedProductCategory);
    }

    @GetMapping()
    public ResponseEntity<List<ProductCategory>> findAll() {
        var fetchedProductCategoryList = productCategoryService.getAll();
        return ResponseEntity.ok(fetchedProductCategoryList);
    }

    @GetMapping("/{id}")
    public ResponseEntity<List<ProductCategory>> findAllSubcategories(@PathVariable Long id) {
        var fetchedProductSubcategoryCategoryList = productCategoryService.getSubcategoriesById(id);
        return ResponseEntity.ok(fetchedProductSubcategoryCategoryList);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteById(@PathVariable Long id) {
        productCategoryService.deleteById(id);
        return ResponseEntity.noContent().build();
    }
}
