package com.vention.agroex.repository;

import com.vention.agroex.entity.ProductCategoryEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ProductCategoryRepository extends JpaRepository<ProductCategoryEntity, Long>, JpaSpecificationExecutor<ProductCategoryEntity> {
    Optional<List<ProductCategoryEntity>> findProductCategoryListByParentId(Long parentId);

    void deleteByParentId(Long parentId);

    Optional<ProductCategoryEntity> findByTitle(String title);


    Optional<List<ProductCategoryEntity>> findProductCategoryListByParentIdAndLotsIsNotEmpty(Long parentId);

}
