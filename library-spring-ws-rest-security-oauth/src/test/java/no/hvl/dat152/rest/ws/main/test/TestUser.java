package no.hvl.dat152.rest.ws.main.test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;

import io.restassured.RestAssured;
import io.restassured.response.Response;
import no.hvl.dat152.rest.ws.exceptions.UserNotFoundException;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
class TestUser {

	private static final String API_ROOT = "http://localhost:8090/elibrary/api/v1";

	@Value("${admin.token.test}")
	private String ADMIN_TOKEN; // user 2 has ADMIN role

	@Value("${user.token.test}")
	private String USER_TOKEN; // user 1 has USER role

	@Value("${user3.token.test}")
	private String USER3_TOKEN; // user 3 has USER role

	@DisplayName("JUnit test for @GetMapping(/users) endpoint")
	@Test
	public void getAllUsers_thenOK() {
		Response response = RestAssured.given()
				.header("Authorization", "Bearer " + ADMIN_TOKEN)
				.get(API_ROOT + "/users");
		assertEquals(HttpStatus.OK.value(), response.getStatusCode());
		assertTrue(response.jsonPath().getList("userid").size() > 0);
	}

	@DisplayName("JUnit test for @GetMapping(/users) endpoint for unauthorized user role=USER")
	@Test
	public void getAllUsers_USER_ROLE_thenOK() {
		Response response = RestAssured.given()
				.header("Authorization", "Bearer " + USER_TOKEN)
				.get(API_ROOT + "/users");

		int errorCode = response.getStatusCode() == HttpStatus.FORBIDDEN.value() ? HttpStatus.FORBIDDEN.value()
				: HttpStatus.INTERNAL_SERVER_ERROR.value();

		assertEquals(errorCode, response.getStatusCode());
	}

	@DisplayName("JUnit test for @GetMapping(/users/{id}) endpoint")
	@Test
	public void getUserById_thenOK() {

		Response response = RestAssured.given()
				.header("Authorization", "Bearer " + USER_TOKEN)
				.get(API_ROOT + "/users/1");

		assertEquals(HttpStatus.OK.value(), response.getStatusCode());
		assertEquals("1", response.jsonPath().get("userid").toString());

	}

	@DisplayName("JUnit test for @GetMapping(/users/{id}/orders) endpoint")
	@Test
	public void getOrdersOfUser_thenOK() {

		Response response = RestAssured.given()
				.header("Authorization", "Bearer " + USER_TOKEN)
				.get(API_ROOT + "/users/1/orders");

		assertEquals(HttpStatus.OK.value(), response.getStatusCode());
		assertTrue(response.jsonPath().getList("isbn").size() > 0);
	}

	@DisplayName("JUnit test for @GetMapping(/users/{id}/orders) endpoint for unauthorized user")
	@Test
	public void getOrdersOfUnauthorizedUser_thenOK() {

		Response response = RestAssured.given()
				.header("Authorization", "Bearer " + USER3_TOKEN) // user 3 token trying to access user 1 orders
				.get(API_ROOT + "/users/1/orders");

		int errorCode = response.getStatusCode() == HttpStatus.FORBIDDEN.value() ? HttpStatus.FORBIDDEN.value()
				: HttpStatus.INTERNAL_SERVER_ERROR.value();

		assertEquals(errorCode, response.getStatusCode());
	}

	@DisplayName("JUnit test for @PostMapping(/users/{id}/orders) endpoint")
	@Test
	public void createOrderForUser_thenOK() throws UserNotFoundException {

		// new order
		String order = orderData();

		Response response = RestAssured.given()
				.header("Authorization", "Bearer " + USER_TOKEN)
				.contentType(MediaType.APPLICATION_JSON_VALUE)
				.body(order)
				.post(API_ROOT + "/users/{id}/orders", 1); // post order for user 1

		List<Object> isbns = response.jsonPath().getList("isbn");

		assertEquals(HttpStatus.CREATED.value(), response.getStatusCode());
		assertTrue(isbns.contains("rstuv1540"));

	}

	@DisplayName("JUnit test for @DeleteMapping(/users/{id}) endpoint")
	@Test
	public void deleteUserById_thenOK() {

		Response response = RestAssured.given()
				.header("Authorization", "Bearer " + ADMIN_TOKEN)
				.delete(API_ROOT + "/users/3");

		assertEquals(HttpStatus.OK.value(), response.getStatusCode());

		// attempt to access resource again
		Response resp = RestAssured.given()
				.header("Authorization", "Bearer " + ADMIN_TOKEN)
				.get(API_ROOT + "/users/3");

		int errorCode = response.getStatusCode() == HttpStatus.FORBIDDEN.value() ? HttpStatus.FORBIDDEN.value()
				: HttpStatus.INTERNAL_SERVER_ERROR.value();

		assertEquals(errorCode, resp.getStatusCode());

	}

	@DisplayName("JUnit test for @GetMapping(/users/{uid}/orders/{oid}) endpoint")
	@Test
	public void getUserOrder_thenOK() {

		Response response = RestAssured.given()
				.header("Authorization", "Bearer " + USER_TOKEN)
				.get(API_ROOT + "/users/1/orders/1");

		assertEquals(HttpStatus.OK.value(), response.getStatusCode());
		assertEquals("1", response.jsonPath().get("id").toString());
	}

	@DisplayName("JUnit test for @DeleteMapping(/users/{uid}/orders/{oid}) endpoint")
	@Test
	public void deleteUserOrder_thenOK() {

		Response response = RestAssured.given()
				.header("Authorization", "Bearer " + USER_TOKEN)
				.delete(API_ROOT + "/users/1/orders/2");

		assertEquals(HttpStatus.OK.value(), response.getStatusCode());

	}

	private String orderData() {

		String json = "{\n"
				+ "    \"isbn\":\"rstuv1540\",\n"
				+ "    \"expiry\":\"2024-10-30\"\n"
				+ "}";

		return json;
	}

}
