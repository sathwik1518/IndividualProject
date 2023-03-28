package com.java.project;

import org.springframework.data.jpa.repository.JpaRepository;

import com.java.project.entity.Product;

public interface TestH2Repository extends JpaRepository<Product , Integer> {

}
