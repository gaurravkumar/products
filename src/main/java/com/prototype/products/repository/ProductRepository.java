package com.prototype.products.repository;

import com.prototype.products.entity.ProductEntity;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProductRepository extends JpaRepository<ProductEntity, Long> {
    List<ProductEntity> findAllByInAuction(boolean inAuction);

    @Modifying
    @Transactional
    @Query("UPDATE ProductEntity p SET p.inAuction = ?1 WHERE p.productId = ?2")
    int updateInputAuctionById(boolean status, Long entityId);

}
