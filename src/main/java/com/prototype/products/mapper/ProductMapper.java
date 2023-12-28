package com.prototype.products.mapper;

import com.prototype.products.dto.ProductInputDTO;
import com.prototype.products.dto.ProductOutputDTO;
import com.prototype.products.entity.ProductEntity;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper(componentModel = "spring")
public interface ProductMapper {

    ProductEntity productInputDTOToProductEntity(ProductInputDTO productDTO);

    ProductOutputDTO productEntityToProductOutputDTO(ProductEntity productEntity);

    List<ProductOutputDTO> mapListProductEntityToListProductDTO(List<ProductEntity> sourceItemList);
}
