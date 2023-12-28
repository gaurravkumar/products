package com.prototype.products.controller;

import com.prototype.products.dto.ProductInputDTO;
import com.prototype.products.dto.ProductOutputDTO;
import com.prototype.products.exception.ProductException;
import com.prototype.products.service.ProductsService;
import jakarta.servlet.http.HttpServletRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.resolver.MockParameterResolver;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ProductsControllerTest {

    @Mock
    private ProductsService productsServiceMock;
    @InjectMocks
    ProductsController productsController;

    @Mock
    HttpServletRequest request;
    @Mock
    BindingResult bindingResult;
    ProductInputDTO productInputDTO;
    ProductOutputDTO productOutputDTO;
    String token = "22-34-56";

    @BeforeEach
    void setUp(){
        productInputDTO = new ProductInputDTO(1L,"name", 1.1F,true,token);
        productOutputDTO = new ProductOutputDTO(1L,"name",1.1F,true,token,null);
    }

    @Test
    void registerProductFieldsEmptyWhileRegistrationErrorResponse() {
        ProductInputDTO productInputDTO = new ProductInputDTO(1L,"", -1.1F,true,token);
        FieldError fieldError = new FieldError("productInputDTO","name","Blank prohibited");
        FieldError fieldError2 = new FieldError("productInputDTO","minimumPrice","Negative prohibited");
        when(bindingResult.hasErrors()).thenReturn(true);
        when(bindingResult.getFieldErrors()).thenReturn(List.of(fieldError,fieldError2));
        ResponseEntity<ProductOutputDTO> productOutputDTOResponseEntity = productsController.registerProduct(productInputDTO, bindingResult, request);
        assertNotNull(productOutputDTOResponseEntity);
        assertTrue(productOutputDTOResponseEntity.getBody().error().contains("Blank prohibited"));
        assertTrue(productOutputDTOResponseEntity.getBody().error().contains("Negative prohibited"));
        assertTrue(productOutputDTOResponseEntity.getStatusCode().is4xxClientError());
    }
    @Test
    void registerProduct() {
        when(request.getHeader("token")).thenReturn(token);
        when(productsServiceMock.registerProduct(productInputDTO,token)).thenReturn(productOutputDTO);
        ResponseEntity<ProductOutputDTO> productOutputDTOResponseEntity = productsController.registerProduct(productInputDTO,bindingResult,request);
        assertNotNull(productOutputDTOResponseEntity);
        assertTrue(productOutputDTOResponseEntity.getStatusCode().is2xxSuccessful());
        assertEquals(1L,productOutputDTOResponseEntity.getBody().productId());
        assertEquals(1.1F,productOutputDTOResponseEntity.getBody().minimumPrice());
    }

    @Test
    void registerProductNotSuccessfulThrowsProductException() {
        when(request.getHeader("token")).thenReturn(token);
        when(productsServiceMock.registerProduct(productInputDTO,token)).thenThrow(new ProductException("Error Occurred"));
        ResponseEntity<ProductOutputDTO> productOutputDTOResponseEntity = productsController.registerProduct(productInputDTO,bindingResult,request);
        assertNotNull(productOutputDTOResponseEntity);
        assertTrue(productOutputDTOResponseEntity.getStatusCode().is4xxClientError());
        assertEquals(1L,productOutputDTOResponseEntity.getBody().productId());
        assertEquals(1.1F,productOutputDTOResponseEntity.getBody().minimumPrice());
        assertEquals("Error Occurred", productOutputDTOResponseEntity.getBody().error());
    }
    @Test
    void updateInAuctionStatus() {
        when(request.getHeader("token")).thenReturn(token);
        ProductInputDTO productInputDTOForInAuctionStatus = new ProductInputDTO(1L,"name",1.1F,false,token);
        when(productsServiceMock.updateAuctionStatus(false,productInputDTOForInAuctionStatus.productId(),token)).thenReturn(productOutputDTO);
        ResponseEntity<ProductOutputDTO> productOutputDTOResponseEntity = productsController.updateInAuctionStatus(productInputDTOForInAuctionStatus,request);
        assertNotNull(productOutputDTOResponseEntity);
        assertTrue(productOutputDTOResponseEntity.getStatusCode().is2xxSuccessful());
        assertEquals(1L,productOutputDTOResponseEntity.getBody().productId());
        assertEquals(1.1F,productOutputDTOResponseEntity.getBody().minimumPrice());
    }

    @Test
    void updateInAuctionStatusNotSuccessfulThrowsProductException() {
        when(request.getHeader("token")).thenReturn(token);
        when(productsServiceMock.updateAuctionStatus(true,productInputDTO.productId(),token)).thenThrow(new ProductException("Error Occurred"));
        ResponseEntity<ProductOutputDTO> productOutputDTOResponseEntity = productsController.updateInAuctionStatus(productInputDTO,request);
        assertNotNull(productOutputDTOResponseEntity);
        assertTrue(productOutputDTOResponseEntity.getStatusCode().is4xxClientError());
        assertEquals(1L,productOutputDTOResponseEntity.getBody().productId());
        assertEquals(1.1F,productOutputDTOResponseEntity.getBody().minimumPrice());
        assertEquals("Error Occurred", productOutputDTOResponseEntity.getBody().error());
    }

    @Test
    void getAllProducts() {
        when(request.getHeader("token")).thenReturn(token);
        when(productsServiceMock.getProducts(token)).thenReturn(List.of(productOutputDTO));
        ResponseEntity<List<ProductOutputDTO>> productOutputDTOResponseEntity = productsController.getAllProducts(request);
        assertNotNull(productOutputDTOResponseEntity);
        assertTrue(productOutputDTOResponseEntity.getStatusCode().is2xxSuccessful());
        assertEquals(1,productOutputDTOResponseEntity.getBody().size());
    }

    @Test
    void getAllProductsNotSuccessfulThrowsProductException() {
        when(request.getHeader("token")).thenReturn(token);
        when(productsServiceMock.getProducts(token)).thenThrow(new ProductException("Error Occurred"));
        ResponseEntity<List<ProductOutputDTO>> productOutputDTOResponseEntity = productsController.getAllProducts(request);
        assertNotNull(productOutputDTOResponseEntity);
        assertTrue(productOutputDTOResponseEntity.getStatusCode().is4xxClientError());
        assertEquals("Error Occurred", productOutputDTOResponseEntity.getBody().get(0).error());
    }
    @Test
    void getProductById() {
        when(productsServiceMock.getProductById(productInputDTO.productId(),token)).thenReturn(productOutputDTO);
        ResponseEntity<ProductOutputDTO> productOutputDTOResponseEntity = productsController.getProductById(productInputDTO.productId(),token);
        assertNotNull(productOutputDTOResponseEntity);
        assertTrue(productOutputDTOResponseEntity.getStatusCode().is2xxSuccessful());
        assertEquals(1L,productOutputDTOResponseEntity.getBody().productId());
        assertEquals(1.1F,productOutputDTOResponseEntity.getBody().minimumPrice());
    }

    @Test
    void getProductByIdNotSuccessfulThrowsProductException() {
        when(productsServiceMock.getProductById(productInputDTO.productId(),token)).thenThrow(new ProductException("Error Occurred"));
        ResponseEntity<ProductOutputDTO> productOutputDTOResponseEntity = productsController.getProductById(productInputDTO.productId(),token);
        assertNotNull(productOutputDTOResponseEntity);
        assertTrue(productOutputDTOResponseEntity.getStatusCode().is4xxClientError());
        assertEquals(1L,productOutputDTOResponseEntity.getBody().productId());
        assertEquals(-1F,productOutputDTOResponseEntity.getBody().minimumPrice());
        assertEquals("Error Occurred", productOutputDTOResponseEntity.getBody().error());
    }
}