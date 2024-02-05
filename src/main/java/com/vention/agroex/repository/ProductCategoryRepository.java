package com.vention.agroex.repository;

import com.vention.agroex.entity.ProductCategory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ProductCategoryRepository extends JpaRepository<ProductCategory, Long> {
    List<ProductCategory> findProductCategoryListByParentId(Long parentId);

    void deleteByParentId(Long parentId);

    Optional<ProductCategory> findByTitle(String title);
}
