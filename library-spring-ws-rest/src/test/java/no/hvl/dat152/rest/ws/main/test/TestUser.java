package no.hvl.dat152.rest.ws.main.test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;

import io.restassured.RestAssured;
import io.restassured.response.Response;
import no.hvl.dat152.rest.ws.exceptions.UserNotFoundException;
import no.hvl.dat152.rest.ws.model.User;
import no.hvl.dat152.rest.ws.service.UserService;


@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
class TestUser {
	
	@Autowired
	private UserService userService;
	
	private String API_ROOT = "http://localhost:8090/elibrary/api/v1";
	
	@DisplayName("JUnit test for @GetMapping(/users) endpoint")
	@Test
	public void getAllUsers_thenOK() {
		Response response = RestAssured.get(API_ROOT+"/users");
		assertEquals(HttpStatus.OK.value(), response.getStatusCode());
		assertTrue(response.jsonPath().getList("userid").size() > 0);
	}
	
	@DisplayName("JUnit test for @GetMapping(/users/{id}) endpoint")
	@Test
	public void getUserById_thenOK() {

	    Response response = RestAssured.get(API_ROOT+"/users/1");
	    
	    assertEquals(HttpStatus.OK.value(), response.getStatusCode());
	    assertEquals("1", response.jsonPath().get("userid").toString());

	}
	
	@DisplayName("JUnit test for @PostMapping(/users) endpoint")
	@Test
	public void createUser_thenOK() {
		
		User user1 = new User("Test1", "User1");
		
		Response response = RestAssured.given()
				.contentType(MediaType.APPLICATION_JSON_VALUE)
				.body(user1)
				.post(API_ROOT+"/users");
	    
	    assertEquals(HttpStatus.CREATED.value(), response.getStatusCode());
	    assertEquals("Test1", response.jsonPath().get("firstname"));

	}
	
	@DisplayName("JUnit test for @GetMapping(/users/{id}/orders) endpoint")
	@Test
	public void getOrdersOfUser_thenOK() {
		
		Response response = RestAssured.get(API_ROOT+"/users/1/orders");
	    
	    assertEquals(HttpStatus.OK.value(), response.getStatusCode());
	    assertTrue(response.jsonPath().getList("isbn").size() > 0);
	}
	
	@DisplayName("JUnit test for @GetMapping(/users/{uid}/orders/{oid}) endpoint")
	@Test
	public void getUserOrder_thenOK() {
		
		Response response = RestAssured.get(API_ROOT+"/users/1/orders/1");
	    
	    assertEquals(HttpStatus.OK.value(), response.getStatusCode());
	    assertEquals("1", response.jsonPath().get("id").toString());
	}
	
	@DisplayName("JUnit test for @DeleteMapping(/users/{uid}/orders/{oid}) endpoint")
	@Test
	public void deleteUserOrder_thenOK() {
		
		Response response = RestAssured.delete(API_ROOT+"/users/2/orders/2");
	    
	    assertEquals(HttpStatus.OK.value(), response.getStatusCode());

	}
	
	@DisplayName("JUnit test for @PutMapping(/users/{id}) endpoint")
	@Test
	public void updateUser_thenOK() {
		User user = new User("Test1", "User1");
		user = userService.saveUser(user);
		
		user.setFirstname("Test_101");	// update firstname
		
		Response response = RestAssured.given()
				.contentType(MediaType.APPLICATION_JSON_VALUE)
				.body(user)
				.put(API_ROOT+"/users/{id}", user.getUserid());
	    
	    assertEquals(HttpStatus.OK.value(), response.getStatusCode());
	    assertEquals("Test_101", response.jsonPath().get("firstname"));
	}

	@DisplayName("JUnit test for @PostMapping(/users/{id}/orders) endpoint + HATEOAS")
	@Test
	public void createOrderForUser_thenOK() throws UserNotFoundException {

		// new order
		String order = orderData();
		
		Response response = RestAssured.given()
				.contentType(MediaType.APPLICATION_JSON_VALUE)
				.body(order)
				.post(API_ROOT+"/users/{id}/orders", "2");

		List<Object> isbns = response.jsonPath().getList("isbn");
		List<Object> hrefs = response.jsonPath().getList("links");
		
	    assertEquals(HttpStatus.CREATED.value(), response.getStatusCode());
	    assertTrue(isbns.contains("rstuv1540"));
	    assertTrue(hrefs.get(0).toString().contains("href"));

	}
	
	@DisplayName("JUnit test for @DeleteMapping(/users/{id}) endpoint")
	@Test
	public void deleteUserById_thenOK() {

	    Response response = RestAssured.delete(API_ROOT+"/users/delete/1");
	    
	    assertEquals(HttpStatus.OK.value(), response.getStatusCode());
	    
	    // attempt to access the same resource again
	    Response resp = RestAssured.get(API_ROOT+"/users/1");
	    
		int errorCode = resp.getStatusCode()== HttpStatus.NOT_FOUND.value() ? 
				HttpStatus.NOT_FOUND.value() : HttpStatus.INTERNAL_SERVER_ERROR.value();
		
	    assertEquals(errorCode, resp.getStatusCode());

	}
	
	private String orderData() {
		
		String json = "{\n"
				+ "    \"isbn\":\"rstuv1540\",\n"
				+ "    \"expiry\":\"2024-10-30\"\n"
				+ "}";
		
		return json;
	}

}
