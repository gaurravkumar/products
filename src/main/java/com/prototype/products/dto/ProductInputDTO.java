package com.prototype.products.dto;

public record ProductInputDTO(Long productId, String name, Float minimumPrice, boolean inAuction, String owner) {

    public ProductInputDTO withUpdatedOwner(String owner){
        return new ProductInputDTO(this.productId, this.name,this.minimumPrice, this.inAuction,owner);
    }

    public ProductInputDTO withUpdatedError(String error){
        return new ProductInputDTO(this.productId, this.name,this.minimumPrice, this.inAuction,this.owner);
    }

}
