package com.schabikowski.ProductService.repository;

import com.schabikowski.ProductService.model.Product;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface ProductRepository extends MongoRepository<Product, String> {}
