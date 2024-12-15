package com.devsuperior.dscommerce.controllers;

import static io.restassured.RestAssured.baseURI;
import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.hasItems;
import static org.hamcrest.Matchers.is;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.devsuperior.dscommerce.tests.TokenUtil;

import io.restassured.http.ContentType;

public class OrderControllerRA {
	
	private String clientUsername,clientPassword,adminUsername,adminPassword;
	private String clientToken,adminToken,invalidToken;
	private Long existingOrderId,nonExistingOrderId;
	
	@BeforeEach
	public void setup() throws Exception{
		
		existingOrderId = 1L;
		nonExistingOrderId = 100L;
		
         baseURI = "http://localhost:8080";
         
         clientUsername = "maria@gmail.com";
         clientPassword= "123456";
         adminUsername="alex@gmail.com";
         adminPassword="123456";
         
         clientToken = TokenUtil.obtainAccessToken(clientUsername, clientPassword);
         adminToken = TokenUtil.obtainAccessToken(adminUsername,adminPassword);
         invalidToken = adminToken + "gpto";//invalid token
	}
	
	@Test
	public void findByIdShouldReturnOrderWhenIdExistsAndAdminLogged() {
		 given()
	        .header("Content-type", "application/json")
	        .header("Authorization", "Bearer " + adminToken)
	        .accept(ContentType.JSON)
	    .when()
	        .get("/orders/{id}",existingOrderId)
	    .then()
	        .log().ifError() // Loga resposta em caso de erro
	        .statusCode(200) // Espera que seja 200
	        .body("id", is(1))
	        .body("moment", equalTo("2022-07-25T13:00:00Z"))
	        .body("status", equalTo("PAID"))
	        .body("client.name", equalTo("Maria Brown"))
	        .body("payment.moment", equalTo("2022-07-25T15:00:00Z"))
	        .body("items.name",hasItems("The Lord of the Rings","Macbook Pro"))
	        .body("total",is(1431.0F));
	        

	}
	
	
	@Test
	public void findByIdShouldReturnOrderWhenIdExistsAndClientLogged() {
		 given()
	        .header("Content-type", "application/json")
	        .header("Authorization", "Bearer " + clientToken)
	        .accept(ContentType.JSON)
	    .when()
	        .get("/orders/{id}",existingOrderId)
	    .then()
	        .log().ifError() // Loga resposta em caso de erro
	        .statusCode(200) // Espera que seja 200
	        .body("id", is(1))
	        .body("moment", equalTo("2022-07-25T13:00:00Z"))
	        .body("status", equalTo("PAID"))
	        .body("client.name", equalTo("Maria Brown"))
	        .body("payment.moment", equalTo("2022-07-25T15:00:00Z"))
	        .body("items.name",hasItems("The Lord of the Rings","Macbook Pro"))
	        .body("total",is(1431.0F));
	        

	}
	
	
	@Test
	public void findByIdShouldReturnForbiddenWhenIdExistsAndClientLoggedAndOrderDoesNotBelongUser() {
		Long otherOrderId = 2L;
		
		 given()
	        .header("Content-type", "application/json")
	        .header("Authorization", "Bearer " + clientToken)
	        .accept(ContentType.JSON)
	    .when()
	        .get("/orders/{id}",otherOrderId)
	    .then()
	        .log().ifError() // Loga resposta em caso de erro
	        .statusCode(403); // Espera que seja 403
	      
	        

	}
	@Test
	public void findByIdShouldReturnNotFoundWhenIdDoesNotExistsAndClientLogged() {
		 given()
	        .header("Content-type", "application/json")
	        .header("Authorization", "Bearer " + adminToken)
	        .accept(ContentType.JSON)
	    .when()
	        .get("/orders/{id}",nonExistingOrderId)
	    .then()
	        .log().ifError() // Loga resposta em caso de erro
	        .statusCode(404); // Espera que seja 404
	        
	        

	}
	
	
	@Test
	public void findByIdShouldReturnUnauthorizedWhenIdExistsAndInvalidToken() {
		 given()
	        .header("Content-type", "application/json")
	        .header("Authorization", "Bearer " + invalidToken)
	        .accept(ContentType.JSON)
	    .when()
	        .get("/orders/{id}",existingOrderId)
	    .then()
	        .log().ifError() // Loga resposta em caso de erro
	        .statusCode(401); // Espera que seja 401
	        
	        

	}
	

}
