package com.java.project.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.java.project.entity.Product;

public interface ProductRepository extends JpaRepository<Product,Integer> {

	Product findByName(String name);

}
