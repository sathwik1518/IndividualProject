package com.java.project;

import static org.junit.jupiter.api.Assertions.*;

import java.util.List;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.web.client.RestTemplate;

import com.java.project.entity.Product;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
class IndividualProjectApplicationTests {

	@Test
	void contextLoads() {
	}
	
	@LocalServerPort
	private int port;
	
	private String baseUrl = "http://localhost";
	
	private static RestTemplate restTemplate;
	
	@Autowired
	private TestH2Repository h2Repository;
	
	@BeforeAll
	public static void init() {
		restTemplate = new RestTemplate();
	}
	
	@BeforeEach
	public void setUp() {
		baseUrl = baseUrl.concat(":").concat(port+"");
	}
	
	@Test
	public void testAddProduct() {
		Product product = new Product("earphones", 7,1950);
		baseUrl = baseUrl.concat("/addProduct");
		Product response = restTemplate.postForObject(baseUrl, product, Product.class);
		assertEquals("earphones", response.getName());
	}
	
	@Test
    @Sql(statements = "INSERT INTO PRODUCT_TABLE (id,name, quantity, price) VALUES (4,'AC', 1, 34000)", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    public void testGetProducts() {
		baseUrl = baseUrl.concat("/products");
        List<Product> products = restTemplate.getForObject(baseUrl, List.class);
        assertEquals(1, products.size());
        assertEquals(1, h2Repository.findAll().size());
    }
	
	@Test
    @Sql(statements = "INSERT INTO PRODUCT_TABLE (id,name, quantity, price) VALUES (1,'CAR', 1, 334000)", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    public void testFindProductById() {
		baseUrl = baseUrl.concat("/productById/{id}");
        Product product = restTemplate.getForObject(baseUrl, Product.class, 1);
        assertAll(
                () -> assertNotNull(product),
                () -> assertEquals(1, product.getId()),
                () -> assertEquals("CAR", product.getName())
        );
    }
	
	@Test
    @Sql(statements = "INSERT INTO PRODUCT_TABLE (id,name, quantity, price) VALUES (1,'CAR', 1, 334000)", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    public void testFindProductByName() {
		baseUrl = baseUrl.concat("/productByName/{name}");
        Product product = restTemplate.getForObject(baseUrl, Product.class, "CAR");
        assertAll(
                () -> assertNotNull(product),
                () -> assertEquals(1, product.getId()),
                () -> assertEquals("CAR", product.getName())
        );
    }
	
	@Test
    @Sql(statements = "INSERT INTO PRODUCT_TABLE (id,name, quantity, price) VALUES (2,'shoes', 1, 999)", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    public void testUpdateProduct(){
        Product product = new Product("shoes", 1, 1999);
        baseUrl = baseUrl.concat("/updateProduct/{id}");
        restTemplate.put(baseUrl, product, 2);
        Product productFromDB = h2Repository.findById(2).get();
        assertAll(
                () -> assertNotNull(productFromDB),
                () -> assertEquals(1999, productFromDB.getPrice())
        );
    }
	
	@Test
    @Sql(statements = "INSERT INTO PRODUCT_TABLE (id,name, quantity, price) VALUES (8,'books', 5, 1499)", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    public void testDeleteProduct(){
		baseUrl = baseUrl.concat("/deleteProduct/{id}");
        int recordCount=h2Repository.findAll().size();
        assertEquals(1, recordCount);
        restTemplate.delete(baseUrl, 8);
        assertEquals(0, h2Repository.findAll().size());
    }

}
