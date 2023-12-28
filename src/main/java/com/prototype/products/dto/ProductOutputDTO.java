package com.prototype.products.dto;

public record ProductOutputDTO(Long productId, String name, Float minimumPrice, boolean inAuction, String owner, String error) {
}
