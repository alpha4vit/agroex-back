package com.vention.agroex.controller;

import com.vention.agroex.dto.ProductCategory;
import com.vention.agroex.entity.ProductCategoryEntity;
import com.vention.agroex.service.ProductCategoryService;
import com.vention.agroex.util.mapper.ProductCategoryMapper;
import com.vention.agroex.util.validator.ProductCategoryValidator;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
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
    private final ProductCategoryMapper productCategoryMapper;

    @PreAuthorize("@customSecurityExpression.isAdmin()")
    @PostMapping()
    public ResponseEntity<ProductCategory> save(@RequestBody ProductCategoryEntity productCategoryEntity,
                                                BindingResult bindingResult) {
        productCategoryValidator.validate(productCategoryEntity, bindingResult);
        var savedProductCategory = productCategoryService.save(productCategoryEntity);
        return ResponseEntity.ok(productCategoryMapper.toDTO(savedProductCategory));
    }

    @PreAuthorize("@customSecurityExpression.isAdmin()")
    @PutMapping("/{id}")
    public ResponseEntity<ProductCategory> update(@PathVariable Long id,
                                                        @RequestBody ProductCategoryEntity productCategoryEntity,
                                                        BindingResult bindingResult) {
        productCategoryValidator.validate(productCategoryEntity, bindingResult);
        var updatedProductCategory = productCategoryService.update(id, productCategoryEntity);
        return ResponseEntity.ok(productCategoryMapper.toDTO(updatedProductCategory));
    }

    @GetMapping("/all")
    public ResponseEntity<List<ProductCategory>> findAll() {
        var fetchedProductCategories = productCategoryService.getAll();
        return ResponseEntity.ok(productCategoryMapper.toDTOs(fetchedProductCategories));
    }

    @GetMapping("/{id}")
    public ResponseEntity<List<ProductCategory>> findAllSubcategories(@PathVariable Long id,
                                                                      @RequestParam(value = "lot_existed", required = false, defaultValue = "true") Boolean lotExisted) {
        var fetchedProductCategories = productCategoryService.getSubcategoriesById(id, lotExisted);
        return ResponseEntity.ok(productCategoryMapper.toDTOs(fetchedProductCategories));
    }

    @GetMapping()
    public ResponseEntity<List<ProductCategory>> findAllMainCategories(@RequestParam(value = "lot_existed", required = false, defaultValue = "true") Boolean lotExisted) {
        var fetchedProductCategories = productCategoryService.getSubcategoriesById(0L, lotExisted);
        return ResponseEntity.ok(productCategoryMapper.toDTOs(fetchedProductCategories));
    }

    @PreAuthorize("@customSecurityExpression.isAdmin()")
    @DeleteMapping("/{id}")
    public ResponseEntity<Long> deleteById(@PathVariable Long id) {
        productCategoryService.deleteById(id);
        return ResponseEntity.ok(id);
    }
}
