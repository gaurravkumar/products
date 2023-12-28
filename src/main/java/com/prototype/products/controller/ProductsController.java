package com.prototype.products.controller;

import com.prototype.products.dto.ProductInputDTO;
import com.prototype.products.dto.ProductOutputDTO;
import com.prototype.products.exception.ProductException;
import com.prototype.products.service.ProductsService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/products")
public class ProductsController {
    private ProductsService productsService;

    @Autowired
    public ProductsController(ProductsService productsService) {
        this.productsService = productsService;
    }

    @PostMapping("/registerProduct")
    public ResponseEntity<ProductOutputDTO> registerProduct(@RequestBody ProductInputDTO productInputDTO,
                                                            HttpServletRequest request) {
        try {
            // Register the product
            var result = productsService.registerProduct(productInputDTO, request.getHeader("token"));
            return ResponseEntity.status(HttpStatus.CREATED).body(result);
        } catch (ProductException e) {
            ProductOutputDTO errorDTO = new ProductOutputDTO(productInputDTO.productId(),
                    productInputDTO.name(), productInputDTO.minimumPrice(), productInputDTO.inAuction(),
                    productInputDTO.owner(), e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorDTO);
        }
    }

    @PostMapping("/updateInAuctionStatus")
    public ResponseEntity<ProductOutputDTO> updateInAuctionStatus(@RequestBody ProductInputDTO productInputDTO,
                                                            HttpServletRequest request) {
        try {
            var result = productsService.updateAuctionStatus(productInputDTO.inAuction(),productInputDTO.productId(), request.getHeader("token"));
            return ResponseEntity.status(HttpStatus.CREATED).body(result);
        } catch (ProductException e) {
            ProductOutputDTO errorDTO = new ProductOutputDTO(productInputDTO.productId(),
                    productInputDTO.name(), productInputDTO.minimumPrice(), productInputDTO.inAuction(),
                    productInputDTO.owner(), e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorDTO);
        }
    }

    @GetMapping("/getAllProducts")
    public ResponseEntity<List<ProductOutputDTO>> getAllProducts(HttpServletRequest request) {
        try {
            var productOutputList = productsService.getProducts(request.getHeader("token"));
            return ResponseEntity.status(HttpStatus.OK).body(productOutputList);
        } catch (ProductException e) {
            ProductOutputDTO errorDTO = new ProductOutputDTO(-1L,
                    "", -1.0F, false,
                    "", e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(List.of(errorDTO));
        }
    }

    @GetMapping(path = "/get/productId/{productId}", produces = {MediaType.APPLICATION_JSON_VALUE})
    public ResponseEntity<ProductOutputDTO> getProductById(@PathVariable Long productId,
                                                           @RequestHeader("token") String userToken) {
        try {
            var productOutputDTO = productsService.getProductById(productId, userToken);
            return ResponseEntity.status(HttpStatus.OK).body(productOutputDTO);
        } catch (ProductException e) {
            ProductOutputDTO errorDTO = new ProductOutputDTO(productId,
                    "", -1.0F, false,
                    "", e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorDTO);
        }
    }
}