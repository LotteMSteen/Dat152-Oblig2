package no.hvl.dat152.rest.ws.main.test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.LocalDate;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;

import io.restassured.RestAssured;
import io.restassured.response.Response;
import no.hvl.dat152.rest.ws.exceptions.OrderNotFoundException;
import no.hvl.dat152.rest.ws.exceptions.UnauthorizedOrderActionException;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
class TestOrder {

	private String API_ROOT = "http://localhost:8090/elibrary/api/v1";

	@Value("${admin.token.test}")
	private String ADMIN_TOKEN;

	@Value("${user.token.test}")
	private String USER_TOKEN;

	@DisplayName("JUnit test for filter by Expiry @GetMapping(/orders) endpoint")
	@Test
	public void getAllOrders_thenOK() {
		String expiry = LocalDate.now().plusWeeks(4).toString();
		Response response = RestAssured.given()
				.param("expiry", expiry)
				.param("page", 0)
				.param("size", 4)
				.header("Authorization", "Bearer " + ADMIN_TOKEN)
				.get(API_ROOT + "/orders");

		assertEquals(HttpStatus.OK.value(), response.getStatusCode());
		assertTrue(response.jsonPath().getList("isbn").size() > 0);
	}

	@DisplayName("JUnit test for Paging @GetMapping(/orders) endpoint")
	@Test
	public void getAllOrdersPaginate_thenOK() {

		Response response = RestAssured.given()
				.param("page", 0)
				.param("size", 2)
				.header("Authorization", "Bearer " + ADMIN_TOKEN)
				.get(API_ROOT + "/orders");

		assertEquals(HttpStatus.OK.value(), response.getStatusCode());
		assertTrue(response.jsonPath().getList("isbn").size() == 2);
	}

	@DisplayName("JUnit test for @GetMapping(/orders) endpoint for unauthorized access role=USER")
	@Test
	public void getAllOrders_USER_ROLE_thenOK() {
		Response response = RestAssured.given()
				.header("Authorization", "Bearer " + USER_TOKEN)
				.get(API_ROOT + "/orders");

		int errorCode = response.getStatusCode() == HttpStatus.FORBIDDEN.value() ? HttpStatus.FORBIDDEN.value()
				: HttpStatus.INTERNAL_SERVER_ERROR.value();

		assertEquals(errorCode, response.getStatusCode());
	}

	@DisplayName("JUnit test for @GetMapping(/orders/{id}) endpoint")
	@Test
	public void getOrderById_thenOK() throws OrderNotFoundException, UnauthorizedOrderActionException {

		Response response = RestAssured.given()
				.header("Authorization", "Bearer " + ADMIN_TOKEN)
				.get(API_ROOT + "/orders/{id}", 2);

		assertEquals(HttpStatus.OK.value(), response.getStatusCode());
		assertEquals("qabfde1230", response.jsonPath().get("isbn"));
	}

	@DisplayName("JUnit test for @PutMapping(/orders/{id}) endpoint")
	@Test
	public void updateOrder_thenOK() throws OrderNotFoundException, UnauthorizedOrderActionException {

		String uorder = updateOrderData();

		String nexpiry = LocalDate.now().plusWeeks(4).toString();

		Response response = RestAssured.given()
				.header("Authorization", "Bearer " + ADMIN_TOKEN)
				.contentType(MediaType.APPLICATION_JSON_VALUE)
				.body(uorder)
				.put(API_ROOT + "/orders/{id}", 2);

		assertEquals(HttpStatus.OK.value(), response.getStatusCode());
		assertEquals(nexpiry, response.jsonPath().get("expiry"));
	}

	@DisplayName("JUnit test for @DeleteMapping(/orders/{id}) endpoint")
	@Test
	public void deleteOrderById_thenOK() {

		Response response = RestAssured.given()
				.header("Authorization", "Bearer " + ADMIN_TOKEN)
				.delete(API_ROOT + "/orders/{id}", 1);

		assertEquals(HttpStatus.OK.value(), response.getStatusCode());

	}

	@DisplayName("JUnit test for HATEOAS @GetMapping(/orders/{id}) endpoint")
	@Test
	public void getOrderById_HATEOAS_thenOK() {

		Response response = RestAssured.given()
				.header("Authorization", "Bearer " + ADMIN_TOKEN)
				.get(API_ROOT + "/orders/2");

		assertTrue(response.jsonPath().get("_links").toString().contains("href"));
	}

	private String updateOrderData() {
		String expiry = LocalDate.now().plusWeeks(4).toString();
		String json = "{\n"
				+ "    \"isbn\":\"ghijk1234\",\n"
				+ "    \"expiry\":\"" + expiry + "\"\n"
				+ "}";

		return json;
	}

}
