package com.prototype.products.dto;

import jakarta.validation.constraints.NegativeOrZero;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;

public record ProductInputDTO(Long productId,
                              @NotBlank(message = "Name cannot be blank") String name,
                              @Positive(message = "Minimum Price cannot be zero or negative") Float minimumPrice,
                              boolean inAuction,
                              String owner) {

    public ProductInputDTO withUpdatedOwner(String owner){
        return new ProductInputDTO(this.productId, this.name,this.minimumPrice, this.inAuction,owner);
    }

    public ProductInputDTO withUpdatedError(String error){
        return new ProductInputDTO(this.productId, this.name,this.minimumPrice, this.inAuction,this.owner);
    }

}
