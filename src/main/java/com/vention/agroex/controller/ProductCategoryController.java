package com.vention.agroex.controller;

import com.vention.agroex.dto.ProductCategory;
import com.vention.agroex.service.ProductCategoryService;
import com.vention.agroex.util.mapper.ProductCategoryMapper;
import com.vention.agroex.util.validator.ProductCategoryEntityValidator;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("/categories")
@RequiredArgsConstructor
@Tag(name = "Product Category Controller")
public class ProductCategoryController {

    private final ProductCategoryService productCategoryService;
    private final ProductCategoryEntityValidator productCategoryEntityValidator;
    private final ProductCategoryMapper productCategoryMapper;

    @PostMapping()
    @PreAuthorize("@customSecurityExpression.isAdmin()")
    public ResponseEntity<ProductCategory> save(@RequestPart(value = "file", required = false) MultipartFile image,
                                                @RequestPart("data") @Valid ProductCategory productCategory,
                                                BindingResult bindingResult) {
        var productCategoryEntity = productCategoryMapper.toEntity(productCategory);
        productCategoryEntityValidator.validate(productCategoryEntity, bindingResult);
        var savedProductCategory = productCategoryService.save(productCategoryEntity, image);
        return ResponseEntity.ok(productCategoryMapper.toDTO(savedProductCategory));
    }

    @PutMapping("/{id}")
    @PreAuthorize("@customSecurityExpression.isAdmin()")
    public ResponseEntity<ProductCategory> update(@PathVariable Long id,
                                                        @RequestPart(value = "file", required = false) MultipartFile image,
                                                        @RequestPart("data") ProductCategory productCategory,
                                                        BindingResult bindingResult) {
        var productCategoryEntity = productCategoryMapper.toEntity(productCategory);
        productCategoryEntity.setId(id);
        productCategoryEntityValidator.validate(productCategoryEntity, bindingResult);
        var updatedProductCategory = productCategoryService.update(id, productCategoryEntity, image);
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

    @DeleteMapping("/{id}")
    @PreAuthorize("@customSecurityExpression.isAdmin()")
    public ResponseEntity<Long> deleteById(@PathVariable Long id) {
        productCategoryService.deleteById(id);
        return ResponseEntity.ok(id);
    }
}
