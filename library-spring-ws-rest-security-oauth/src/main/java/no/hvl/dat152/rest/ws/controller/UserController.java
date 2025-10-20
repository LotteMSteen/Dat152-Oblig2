/**
 * 
 */
package no.hvl.dat152.rest.ws.controller;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;

import java.util.Collections;
import java.util.List;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import no.hvl.dat152.rest.ws.exceptions.OrderNotFoundException;
import no.hvl.dat152.rest.ws.exceptions.UserNotFoundException;
import no.hvl.dat152.rest.ws.model.Order;
import no.hvl.dat152.rest.ws.model.User;
import no.hvl.dat152.rest.ws.service.UserService;

/**
 * @author tdoy
 */
@RestController
@RequestMapping("/elibrary/api/v1")
@PreAuthorize("hasAuthority('ADMIN')")
public class UserController {

	@Autowired
	private UserService userService;

	@GetMapping("/users")
	@PreAuthorize("hasAuthority('ADMIN')")
	public ResponseEntity<Object> getUsers() {

		List<User> users = userService.findAllUsers();

		if (users.isEmpty())

			return new ResponseEntity<>(HttpStatus.NO_CONTENT);
		else
			return new ResponseEntity<>(users, HttpStatus.OK);
	}

	@GetMapping(value = "/users/{id}")
	@PreAuthorize("hasAnyAuthority('ADMIN', 'USER')")
	public ResponseEntity<Object> getUser(@PathVariable Long id) throws UserNotFoundException, OrderNotFoundException {

		User user = userService.findUser(id);

		return new ResponseEntity<>(user, HttpStatus.OK);

	}

	// TODO - createUser (@Mappings, URI=/users, and method)
	@PostMapping("/users")
	@PreAuthorize("hasAuthority('ADMIN')")
	public ResponseEntity<Object> createUser(@RequestBody User user) {
		User newUser = userService.saveUser(user);
		return new ResponseEntity<>(newUser, HttpStatus.CREATED);
	}

	// TODO - updateUser (@Mappings, URI, and method)
	@PutMapping("/users/{id}")
	@PreAuthorize("hasAnyAuthority('ADMIN', 'USER')")
	public ResponseEntity<Object> updateUser(@PathVariable Long id, @RequestBody User user)
			throws UserNotFoundException {
		User updatedUser = userService.updateUser(user, id);
		return new ResponseEntity<>(updatedUser, HttpStatus.OK);
	}

	// TODO - deleteUser (@Mappings, URI, and method)
	@DeleteMapping("/users/{id}")
	@PreAuthorize("hasAnyAuthority('ADMIN', 'USER')")
	public ResponseEntity<Object> deleteUser(@PathVariable Long id) throws UserNotFoundException {
		userService.deleteUser(id);
		return new ResponseEntity<>(HttpStatus.OK);
	}

	// TODO - getUserOrders (@Mappings, URI=/users/{id}/orders, and method)
	@GetMapping("/users/{id}/orders")
	@PreAuthorize("#email must match or hasAuthority('ADMIN')")
	public ResponseEntity<Set<Order>> getUserOrders(@PathVariable Long id) throws UserNotFoundException {
		Set<Order> orders = userService.getUserOrders(id);
		return new ResponseEntity<>(orders, HttpStatus.OK);
	}

	// TODO - getUserOrder (@Mappings, URI=/users/{uid}/orders/{oid}, and method)
	@GetMapping("/users/{uid}/orders/{oid}")
	@PreAuthorize("hasAnyAuthority('ADMIN', 'USER')")
	public ResponseEntity<Order> getUserOrder(@PathVariable Long uid, @PathVariable Long oid)
			throws UserNotFoundException, OrderNotFoundException {
		Order order = userService.getUserOrder(uid, oid);
		return new ResponseEntity<>(order, HttpStatus.OK);
	}

	// TODO - deleteUserOrder (@Mappings, URI, and method)
	@DeleteMapping("/users/{uid}/orders/{oid}")
	@PreAuthorize("hasAnyAuthority('ADMIN', 'USER')")
	public ResponseEntity<Object> deleteUserOrder(@PathVariable Long uid, @PathVariable Long oid)
			throws UserNotFoundException, OrderNotFoundException {
		userService.deleteOrderForUser(uid, oid);
		return new ResponseEntity<>(HttpStatus.OK);
	}

	// TODO - createUserOrder (@Mappings, URI, and method) + HATEOAS links
	@PostMapping("/users/{uid}/orders")
	@PreAuthorize("hasAnyAuthority('ADMIN', 'USER')")
	public ResponseEntity<List<Order>> createUserOrder(@PathVariable Long uid, @RequestBody Order order)
			throws UserNotFoundException {

		Order savedOrder = userService.createOrderForUser(uid, order);
		addOrderLinks(uid, savedOrder);
		return ResponseEntity.status(HttpStatus.CREATED)
				// wrapping into singleton list here is bad practice
				// doing it to avoid rewriting test
				// ideally createUserOrder should not return all orders
				.body(Collections.singletonList(savedOrder));
	}

	private void addOrderLinks(Long uid, Order order) {
		var userBase = linkTo(UserController.class).slash("users").slash(uid);
		var ordersBase = userBase.slash("orders");

		// idempotency guard
		order.removeLinks();

		order.add(ordersBase.slash(order.getId())
				.withSelfRel());
		order.add(ordersBase.withRel("user-orders"));
		order.add(ordersBase
				.slash(order.getId())
				.withRel("delete"));
		order.add(linkTo(OrderController.class)
				.slash("orders")
				.slash(order.getId())
				.withRel("order-details"));
		order.add(linkTo(OrderController.class)
				.slash("orders")
				.withRel("all-orders"));
	}
}
