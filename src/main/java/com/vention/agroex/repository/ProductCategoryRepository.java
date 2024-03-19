package com.vention.agroex.repository;

import com.vention.agroex.entity.ProductCategoryEntity;
import org.jetbrains.annotations.NotNull;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ProductCategoryRepository extends JpaRepository<ProductCategoryEntity, Long>, JpaSpecificationExecutor<ProductCategoryEntity> {

    @Query(nativeQuery = true, value = "SELECT * from product_category where id=0")
    ProductCategoryEntity getRootCategory();

    @NotNull
    @Query(nativeQuery = true, value = "SELECT * from product_category where id!=0")
    List<ProductCategoryEntity> findAll();

    Optional<List<ProductCategoryEntity>> findProductCategoryListByParentId(Long parentId);

    void deleteByParentId(Long parentId);

    Optional<ProductCategoryEntity> findByTitle(String title);


    Optional<List<ProductCategoryEntity>> findProductCategoryListByParentIdAndLotsIsNotEmpty(Long parentId);

    @Query(value = "SELECT * FROM product_category WHERE title = :title AND (:id IS NULL OR id <> :id)", nativeQuery = true)
    Optional<ProductCategoryEntity> findByTitleAndIdNot(@Param("id") Long id, @Param("title") String title);

}
