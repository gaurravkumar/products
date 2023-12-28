# products
Microservice to manage products. The main functions of this Service is to register, to get all products, to get a single Product and update in auction status of the product.

# Access to the service
The service can be accessed as follows:

## REGISTER: 
- URL : [REGISTER PRODUCT](http://localhost:8081/api/products/registerProduct)
- Header: The header should contain a valid token which the user will get as response from user service when they register. This token must be passed in the header like a key value pair
_token:f621f84b-d3f1-4dab-bcf5-6cbd7746a8bf_

```
Sample Request: token has to be provided in the header.
{
    "name": "Fourth Product",
    "minimumPrice": 0.9,
    "inAuction": true
}
```

## Update InAuction Status:
This method is used internally when the user stops the auction. (More on Auction can be found in auctions repository) or it can be used directly as well. Again, the header has to be present with token like _token:f621f84b-d3f1-4dab-bcf5-6cbd7746a8bf_
- URL: [UPDATE STATUS](http://localhost:8081/api/products/updateInAuctionStatus)
```
{
    "productId": 1,
    "inAuction": false
}

```
## Get All Products:
This method is used when the user want to retrieve all the products where they can bid. (More on _bid_ can be found in auctions repository). Again, the header has to be present with token like _token:f621f84b-d3f1-4dab-bcf5-6cbd7746a8bf_
- URL: [GET ALL](http://localhost:8081/api/products/getAllProducts)
```
Use the URL mentioned and token in the header will give results token:<YOUR TOKEN HERE>
```
## Get Product By ID:
This method is used when the user want to retrieve specific product. Again, the header has to be present with token like _token:f621f84b-d3f1-4dab-bcf5-6cbd7746a8bf_
- URL: [GET PRODUCT](http://localhost:8081/api/products/get/productId/{id})

```
Use the URL mentioned and token in the header will give results token:<YOUR TOKEN HERE>
Sample get URL for getting Product Id 1 : http://localhost:8081/api/products/get/productId/1

```
## Set up

- Clone the project on you local machine IDE(Eclipse or IntelliJ)
- Build the Project
- Start the project

## More on Eureka Server Project folder


