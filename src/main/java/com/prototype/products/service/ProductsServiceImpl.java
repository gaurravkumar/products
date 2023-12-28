package com.prototype.products.service;

import com.netflix.discovery.EurekaClient;
import com.prototype.products.dto.ProductInputDTO;
import com.prototype.products.dto.ProductOutputDTO;
import com.prototype.products.dto.UserInputDTO;
import com.prototype.products.dto.UserOutputDTO;
import com.prototype.products.entity.ProductEntity;
import com.prototype.products.exception.ProductException;
import com.prototype.products.mapper.ProductMapper;
import com.prototype.products.repository.ProductRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.List;

@Service
public class ProductsServiceImpl implements ProductsService {
    @Value("${config.userUrl.get}")
    private String userApiUrl;
    private final ProductMapper productMapper;
    private final ProductRepository productRepository;
    private final RestTemplate restTemplate;
    private final EurekaClient eurekaClient;
    @Autowired
    public ProductsServiceImpl(ProductRepository productRepository,
                               ProductMapper productMapper,
                               RestTemplate restTemplate,
                               EurekaClient eurekaClient) {

        this.productMapper = productMapper;
        this.productRepository = productRepository;
        this.restTemplate = restTemplate;
        this.eurekaClient = eurekaClient;
    }

    @Override
    public ProductOutputDTO registerProduct(final ProductInputDTO productInputDTO, final String userToken) {
        UserOutputDTO userOutputDTO = getUserFromToken(userToken);
        if (userOutputDTO == null || (userOutputDTO.error() != null)) {
            throw new ProductException("Unable to validate User");
        }
        ProductInputDTO productDTOWithUser = productInputDTO.withUpdatedOwner(userOutputDTO.token());
        ProductEntity productEntity = productMapper.productInputDTOToProductEntity(productDTOWithUser);
        var productRepoResult = productRepository.save(productEntity);
        ProductOutputDTO productDTOFromDb = productMapper.productEntityToProductOutputDTO(productRepoResult);
        return productDTOFromDb;
    }

    @Override
    public List<ProductOutputDTO> getProducts(final String userToken) {
        UserOutputDTO userOutputDTO = getUserFromToken(userToken);
        if (userOutputDTO == null || (userOutputDTO.error() != null)) {
            throw new ProductException("Unable to validate User");
        }
        var productList = productRepository.findAllByInAuction(true);
        if (productList != null && productList.size() > 0) {
            return productMapper.mapListProductEntityToListProductDTO(productList);
        }
        throw new ProductException("Products not found");
    }

    @Override
    public ProductOutputDTO getProductById(final Long productId, final String userToken) {
        UserOutputDTO userOutputDTO = getUserFromToken(userToken);
        if (userOutputDTO == null || (userOutputDTO.error() != null)) {
            throw new ProductException("Unable to validate User");
        }
        var product = productRepository.findById(productId);
        if (product.isPresent()) {
            return productMapper.productEntityToProductOutputDTO(product.get());
        }
        throw new ProductException("Products not found");
    }

    @Override
    @Transactional
    public ProductOutputDTO updateAuctionStatus(final boolean inAuctionStatus, final Long productId, final String userToken) {
        UserOutputDTO userOutputDTO = getUserFromToken(userToken);
        if (userOutputDTO == null || (userOutputDTO.error() != null)) {
            throw new ProductException("Unable to validate User");
        }
        var productRepoResult = productRepository.updateInputAuctionById(inAuctionStatus, productId);
        if (productRepoResult == 1) {
            return new ProductOutputDTO(productId, null, null, inAuctionStatus, null, null);
        }
        throw new ProductException("Unable to Update InAuction Status for: " + productId);
    }


    private UserOutputDTO getUserFromToken(final String userToken) {
        UserInputDTO userInputDTO = new UserInputDTO(userToken);
        var eurekaInstance = eurekaClient.getNextServerFromEureka("USERS", false);
        var retrievedUser = restTemplate.postForObject(eurekaInstance.getHomePageUrl()+userApiUrl, userInputDTO, UserOutputDTO.class);
        return retrievedUser;
    }
}
