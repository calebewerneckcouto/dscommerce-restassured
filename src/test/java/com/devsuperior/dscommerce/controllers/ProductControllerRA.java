package com.devsuperior.dscommerce.controllers;

import static io.restassured.RestAssured.baseURI;
import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.hasItems;
import static org.hamcrest.Matchers.is;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.devsuperior.dscommerce.tests.TokenUtil;

import io.restassured.http.ContentType;

public class ProductControllerRA {

	private Long existingProductId, nonExistingProductId;
	
	private String clientUsername,clientPassword,adminUsername,adminPassword;
	private String clientToken,adminToken,invalidToken;
	
	private String productName;
	private Map<String , Object> postProductsInstance;

	@BeforeEach
	public void setup() throws Exception {
        clientUsername = "maria@gmail.com";
        adminUsername = "alex@gmail.com";
        adminPassword = "123456";
        clientPassword = "123456";
        
        clientToken = TokenUtil.obtainAccessToken(clientUsername, clientPassword);
        adminToken =  TokenUtil.obtainAccessToken(adminUsername,adminPassword);
        invalidToken = adminToken + "xpto";// invalid token
		
		
		baseURI = "http://localhost:8080";
		 productName = "Macbook";
		 postProductsInstance = new HashMap<String, Object>();
		 postProductsInstance.put("name", "Meu produto");
		 postProductsInstance.put("description", "Lorem ipsum, dolor sit amet consectetur adipisicing elit. Qui ad, adipisci illum ipsam velit et odit eaque reprehenderit ex maxime delectus dolore labore, quisquam quae tempora natus esse aliquam veniam doloremque quam minima culpa alias maiores commodi. Perferendis enim");
		 postProductsInstance.put("imgUrl","https://raw.githubusercontent.com/devsuperior/dscatalog-resources/master/backend/img/1-big.jpg");
		 postProductsInstance.put("price", 50.0);
		 
		 List<Map<String, Object>> categories = new ArrayList<Map<String,Object>>();
		 Map<String,Object> category1 = new HashMap<String, Object>();
		 category1.put("id", 2);
		 Map<String,Object> category2 = new HashMap<String, Object>();
		 category2.put("id", 3);
		 categories.add(category1);
		 categories.add(category2);
		 postProductsInstance.put("categories", categories);
	}

	@Test
	public void findByIdSouldReturnProdcutWhenIdExists() {
		existingProductId = 2L;

		given().get("/products/{id}", existingProductId).then().statusCode(200).body("id", is(2))
				.body("name", equalTo("Smart TV"))
				.body("imgUrl", equalTo(
						"https://raw.githubusercontent.com/devsuperior/dscatalog-resources/master/backend/img/2-big.jpg"))
				.body("price", is(2190.0F))
				.body("categories.id", hasItems(2, 3))
				.body("categories.name", hasItems("Eletrônicos","Computadores"));
	}
	
	@Test
	public void findAllShouldReturnPageProductsWhenProductNameIsEmpty() {
		given().get("/products?page=0")
		.then()
		.statusCode(200)
		.body("content.name",hasItems("Macbook Pro", "PC Gamer Tera"));
	}

	@Test
	public void findAllShouldReturnPageProductsWhenProductNameIsNotEmpty() {
		given()
		.get("/products?name={productName}",productName)
		.then()
		.statusCode(200)
		.body("content.id[0]", is(3))
		.body("content.name[0]", equalTo("Macbook Pro"))
		.body("content.price[0]",is(1250.0F))
		.body("content.imgUrl[0]", equalTo("https://raw.githubusercontent.com/devsuperior/dscatalog-resources/master/backend/img/3-big.jpg"));
	}
	
	@Test
	public void findAllShouldReturnPagedProductWithPriceGreaterThan2000() {
		given().get("/products?size=25")
		.then()
		.statusCode(200)
		.body("content.findAll {it.price > 2000}.name",hasItems("Smart TV","PC Gamer Weed"));
	}
	
	@Test
	public void insertShloudReturnProductCreatedWhenAdminLogged() {
	    org.json.simple.JSONObject newProduct = new org.json.simple.JSONObject(postProductsInstance); // Criando JSON
	    System.out.println(newProduct.toJSONString()); // Log para verificar o JSON gerado

	    given()
	        .header("Content-type", "application/json")
	        .header("Authorization", "Bearer " + adminToken)
	        .body(newProduct)
	        .contentType(ContentType.JSON)
	        .accept(ContentType.JSON)
	    .when()
	        .post("/products")
	    .then()
	        .log().ifError() // Loga resposta em caso de erro
	        .statusCode(201) // Espera que seja 201
	        .body("name", equalTo("Meu produto"))
	        .body("price", is(50.0F))
	        .body("imgUrl", equalTo("https://raw.githubusercontent.com/devsuperior/dscatalog-resources/master/backend/img/1-big.jpg"))
	        .body("categories.id", hasItems(2, 3));
	}
	
	
	@Test
	public void insertShouldUnprocessableEntityWhenAdminLoggedAndInvalidName() {
		postProductsInstance.put("name", "ab");
	    org.json.simple.JSONObject newProduct = new org.json.simple.JSONObject(postProductsInstance); // Criando JSON
	    System.out.println(newProduct.toJSONString()); // Log para verificar o JSON gerado

	    given()
	        .header("Content-type", "application/json")
	        .header("Authorization", "Bearer " + adminToken)
	        .body(newProduct)
	        .contentType(ContentType.JSON)
	        .accept(ContentType.JSON)
	    .when()
	        .post("/products")
	    .then()
	        .log().ifError() // Loga resposta em caso de erro
	        .statusCode(422) // Espera que seja 422
	        .body("errors.message[0]", equalTo("Nome precisar ter de 3 a 80 caracteres"));
	       
	}
	
	@Test
	public void insertShouldUnprocessableEntityWhenAdminLoggedAndInvalidDescription() {
		postProductsInstance.put("description", "ab");
	    org.json.simple.JSONObject newProduct = new org.json.simple.JSONObject(postProductsInstance); // Criando JSON
	    System.out.println(newProduct.toJSONString()); // Log para verificar o JSON gerado

	    given()
	        .header("Content-type", "application/json")
	        .header("Authorization", "Bearer " + adminToken)
	        .body(newProduct)
	        .contentType(ContentType.JSON)
	        .accept(ContentType.JSON)
	    .when()
	        .post("/products")
	    .then()
	        .log().ifError() // Loga resposta em caso de erro
	        .statusCode(422) // Espera que seja 422
	        .body("errors.message[0]", equalTo("Descrição precisa ter no mínimo 10 caracteres"));
	       
	}
	
	@Test
	public void insertShouldUnprocessableEntityWhenAdminLoggedAndInvalidPriceIsNegative() {
		postProductsInstance.put("price", "-50");
	    org.json.simple.JSONObject newProduct = new org.json.simple.JSONObject(postProductsInstance); // Criando JSON
	    System.out.println(newProduct.toJSONString()); // Log para verificar o JSON gerado

	    given()
	        .header("Content-type", "application/json")
	        .header("Authorization", "Bearer " + adminToken)
	        .body(newProduct)
	        .contentType(ContentType.JSON)
	        .accept(ContentType.JSON)
	    .when()
	        .post("/products")
	    .then()
	        .log().ifError() // Loga resposta em caso de erro
	        .statusCode(422) // Espera que seja 422
	        .body("errors.message[0]", equalTo("O preço deve ser positivo"));
	       
	}
	
	
	@Test
	public void insertShouldUnprocessableEntityWhenAdminLoggedAndInvalidPriceIsZero() {
		postProductsInstance.put("price", "0.0");
	    org.json.simple.JSONObject newProduct = new org.json.simple.JSONObject(postProductsInstance); // Criando JSON
	    System.out.println(newProduct.toJSONString()); // Log para verificar o JSON gerado

	    given()
	        .header("Content-type", "application/json")
	        .header("Authorization", "Bearer " + adminToken)
	        .body(newProduct)
	        .contentType(ContentType.JSON)
	        .accept(ContentType.JSON)
	    .when()
	        .post("/products")
	    .then()
	        .log().ifError() // Loga resposta em caso de erro
	        .statusCode(422) // Espera que seja 422
	        .body("errors.message[0]", equalTo("O preço deve ser positivo"));
	       
	}
	
	
	@Test
	public void insertShouldUnprocessableEntityWhenAdminLoggedAndProductHasNoCategory() {
		postProductsInstance.put("categories", null);
	    org.json.simple.JSONObject newProduct = new org.json.simple.JSONObject(postProductsInstance); // Criando JSON
	    System.out.println(newProduct.toJSONString()); // Log para verificar o JSON gerado

	    given()
	        .header("Content-type", "application/json")
	        .header("Authorization", "Bearer " + adminToken)
	        .body(newProduct)
	        .contentType(ContentType.JSON)
	        .accept(ContentType.JSON)
	    .when()
	        .post("/products")
	    .then()
	        .log().ifError() // Loga resposta em caso de erro
	        .statusCode(422) // Espera que seja 422
	        .body("errors.message[0]", equalTo("Deve ter pelo menos uma categoria"));
	       
	}
	
	
	@Test
	public void  ShouldReturnForbiddenWhenClientLogged(){
		 org.json.simple.JSONObject newProduct = new org.json.simple.JSONObject(postProductsInstance); // Criando JSON
		 
		 given()
	        .header("Content-type", "application/json")
	        .header("Authorization", "Bearer " + clientToken)
	        .body(newProduct)
	        .contentType(ContentType.JSON)
	        .accept(ContentType.JSON)
	    .when()
	        .post("/products")
	    .then()
	        .log().ifError() // Loga resposta em caso de erro
	        .statusCode(403); // Espera que seja 403
	        
	}
	
	
	@Test
	public void  ShouldReturnUnauthorizednWhenInvalidToken(){
		 org.json.simple.JSONObject newProduct = new org.json.simple.JSONObject(postProductsInstance); // Criando JSON
		 
		 given()
	        .header("Content-type", "application/json")
	        .header("Authorization", "Bearer " + invalidToken)
	        .body(newProduct)
	        .contentType(ContentType.JSON)
	        .accept(ContentType.JSON)
	    .when()
	        .post("/products")
	    .then()
	        .log().ifError() // Loga resposta em caso de erro
	        .statusCode(401); // Espera que seja 401
	        
	}



}
