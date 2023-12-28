package com.prototype.products.service;

import com.prototype.products.dto.ProductInputDTO;
import com.prototype.products.dto.ProductOutputDTO;

import java.util.List;

public interface ProductsService {
    ProductOutputDTO registerProduct(ProductInputDTO productInputDTO, String userToken);

    List<ProductOutputDTO> getProducts(String userToken);

    ProductOutputDTO getProductById(Long productId, String userToken);

    ProductOutputDTO updateAuctionStatus(boolean inAuctionStatus, Long productId, String userToken);
}
