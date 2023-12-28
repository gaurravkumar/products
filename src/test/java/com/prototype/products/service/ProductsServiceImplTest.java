package com.prototype.products.service;

import com.netflix.appinfo.InstanceInfo;
import com.netflix.discovery.EurekaClient;
import com.prototype.products.dto.ProductInputDTO;
import com.prototype.products.dto.ProductOutputDTO;
import com.prototype.products.dto.UserInputDTO;
import com.prototype.products.dto.UserOutputDTO;
import com.prototype.products.entity.ProductEntity;
import com.prototype.products.exception.ProductException;
import com.prototype.products.mapper.ProductMapper;
import com.prototype.products.repository.ProductRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ProductsServiceImplTest {

    @Mock
    private ProductMapper productMapperMock;
    @Mock
    private ProductRepository productRepositoryMock;
    @Mock
    private RestTemplate restTemplateMock;
    @Mock
    EurekaClient eurekaClient;

    @InjectMocks
    ProductsServiceImpl productsService;
    ProductInputDTO productInputDTO;

    ProductOutputDTO productOutputDTO;

    UserInputDTO userInputDTO;

    UserOutputDTO userOutputDTO;

    String token = "23-45-67";

    ProductEntity productEntity;

    @BeforeEach
    void setUp() {
//        ReflectionTestUtils.setField("ProductsServiceImpl","userApiUrl", "/users");
        productInputDTO = new ProductInputDTO(1L, "name", 1.1F, true, token);
        productOutputDTO = new ProductOutputDTO(1L, "name", 1.1F, true, token, null);
        userInputDTO = new UserInputDTO(token);
        userOutputDTO = new UserOutputDTO("User", token, LocalDateTime.now(), null);

        productEntity = new ProductEntity();
        productEntity.setInAuction(productInputDTO.inAuction());
        productEntity.setOwner(token);
        productEntity.setProductId(productInputDTO.productId());
        productEntity.setName(productInputDTO.name());
        productEntity.setMinimumPrice(productInputDTO.minimumPrice());
        var instanceInfo = InstanceInfo.Builder.newBuilder().setAppName("USERS").setHostName("localhost").setPort(1234).setHomePageUrl("/users","http://localhost:1234/users").build();
        lenient().when(eurekaClient.getNextServerFromEureka("USERS", false)).thenReturn(instanceInfo);

    }
    @Test
    void registerProductWithInvalidUserThrowsException() {
        when(restTemplateMock.postForObject(eq(eurekaClient.getNextServerFromEureka("USERS", false).getHomePageUrl()+"null"), eq(userInputDTO), eq(UserOutputDTO.class))).thenReturn(null);
        assertThrows(ProductException.class, () -> productsService.registerProduct(productInputDTO, token), "Unable to validate User");
    }

    @Test
    void registerProduct() {

        ProductOutputDTO responseDTO = new ProductOutputDTO(productEntity.getProductId(), productEntity.getName(), productEntity.getMinimumPrice(), productEntity.isInAuction(), productEntity.getOwner(), null);
        when(restTemplateMock.postForObject(eq(eurekaClient.getNextServerFromEureka("USERS", false).getHomePageUrl()+"null"), eq(userInputDTO), eq(UserOutputDTO.class))).thenReturn(userOutputDTO);
        when(productMapperMock.productInputDTOToProductEntity(productInputDTO)).thenReturn(productEntity);
        when(productRepositoryMock.save(productEntity)).thenReturn(productEntity);
        when(productMapperMock.productEntityToProductOutputDTO(productEntity)).thenReturn(responseDTO);
        var response = productsService.registerProduct(productInputDTO, token);

        assertNotNull(response);
    }

    @Test
    void getProductsWithInvalidUserThrowsException() {

        when(restTemplateMock.postForObject(eq(eurekaClient.getNextServerFromEureka("USERS", false).getHomePageUrl()+"null"), eq(userInputDTO), eq(UserOutputDTO.class))).thenReturn(null);
        assertThrows(ProductException.class, () -> productsService.getProducts(token), "Unable to validate User");
    }

    @Test
    void getProductsWhenNoProductsThrowsProductsNotFoundException() {

        when(restTemplateMock.postForObject(eq(eurekaClient.getNextServerFromEureka("USERS", false).getHomePageUrl()+"null"), eq(userInputDTO), eq(UserOutputDTO.class))).thenReturn(userOutputDTO);
        when(productRepositoryMock.findAllByInAuction(true)).thenReturn(null);
        assertThrows(ProductException.class, () -> productsService.getProducts(token), "Products Not Found");

    }

    @Test
    void getProducts() {
        ProductOutputDTO responseDTO = new ProductOutputDTO(productEntity.getProductId(), productEntity.getName(), productEntity.getMinimumPrice(), productEntity.isInAuction(), productEntity.getOwner(), null);

        when(restTemplateMock.postForObject(eq(eurekaClient.getNextServerFromEureka("USERS", false).getHomePageUrl()+"null"), eq(userInputDTO), eq(UserOutputDTO.class))).thenReturn(userOutputDTO);
        when(productRepositoryMock.findAllByInAuction(true)).thenReturn(List.of(productEntity));
        when(productMapperMock.mapListProductEntityToListProductDTO(List.of(productEntity))).thenReturn(List.of(responseDTO));
        var response = productsService.getProducts(token);

        assertNotNull(response);
    }

    @Test
    void getProductByIdWithInvalidUserThrowsException() {

        when(restTemplateMock.postForObject(eq(eurekaClient.getNextServerFromEureka("USERS", false).getHomePageUrl()+"null"), eq(userInputDTO), eq(UserOutputDTO.class))).thenReturn(null);
        assertThrows(ProductException.class, () -> productsService.getProductById(1L, token), "Unable to validate User");
    }

    @Test
    void getProductByIdWhenNoProductsThrowsProductsNotFoundException() {

        when(restTemplateMock.postForObject(eq(eurekaClient.getNextServerFromEureka("USERS", false).getHomePageUrl()+"null"), eq(userInputDTO), eq(UserOutputDTO.class))).thenReturn(userOutputDTO);
        when(productRepositoryMock.findById(1L)).thenReturn(Optional.empty());
        assertThrows(ProductException.class, () -> productsService.getProductById(1L, token), "Products Not Found");

    }

    @Test
    void getProductById() {
        ProductOutputDTO responseDTO = new ProductOutputDTO(productEntity.getProductId(), productEntity.getName(), productEntity.getMinimumPrice(), productEntity.isInAuction(), productEntity.getOwner(), null);

        when(restTemplateMock.postForObject(eq(eurekaClient.getNextServerFromEureka("USERS", false).getHomePageUrl()+"null"), eq(userInputDTO), eq(UserOutputDTO.class))).thenReturn(userOutputDTO);
        when(productRepositoryMock.findById(1L)).thenReturn(Optional.of(productEntity));
        when(productMapperMock.productEntityToProductOutputDTO(productEntity)).thenReturn(responseDTO);
        var response = productsService.getProductById(1L, token);

        assertNotNull(response);
    }

    @Test
    void updateAuctionStatusWithInvalidUserThrowsException() {

        when(restTemplateMock.postForObject(eq(eurekaClient.getNextServerFromEureka("USERS", false).getHomePageUrl()+"null"), eq(userInputDTO), eq(UserOutputDTO.class))).thenReturn(null);
        assertThrows(ProductException.class, () -> productsService.updateAuctionStatus(false, 1L, token), "Unable to validate User");
    }

    @Test
    void updateAuctionStatusWhenUnableUpdateThrowsProductsNotFoundException() {

        when(restTemplateMock.postForObject(eq(eurekaClient.getNextServerFromEureka("USERS", false).getHomePageUrl()+"null"), eq(userInputDTO), eq(UserOutputDTO.class))).thenReturn(userOutputDTO);
        when(productRepositoryMock.updateInputAuctionById(false, 1L)).thenReturn(0);
        assertThrows(ProductException.class, () -> productsService.updateAuctionStatus(false, 1L, token), "Unable to Update InAuction Status for: 1");

    }

    @Test
    void updateAuctionStatus() {
        ProductOutputDTO responseDTO = new ProductOutputDTO(productEntity.getProductId(), productEntity.getName(), productEntity.getMinimumPrice(), productEntity.isInAuction(), productEntity.getOwner(), null);

        when(restTemplateMock.postForObject(eq(eurekaClient.getNextServerFromEureka("USERS", false).getHomePageUrl()+"null"), eq(userInputDTO), eq(UserOutputDTO.class))).thenReturn(userOutputDTO);
        when(productRepositoryMock.updateInputAuctionById(false, 1L)).thenReturn(1);
        var response = productsService.updateAuctionStatus(false, 1L, token);

        assertNotNull(response);
    }
}