/**
 * 
 */
package no.hvl.dat152.rest.ws.controller;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

import java.time.LocalDate;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.hateoas.EntityModel;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import no.hvl.dat152.rest.ws.exceptions.OrderNotFoundException;
import no.hvl.dat152.rest.ws.model.Order;
import no.hvl.dat152.rest.ws.service.OrderService;

/**
 * @author tdoy
 */
@RestController
@RequestMapping("/elibrary/api/v1")
public class OrderController {

	@Autowired
	private OrderService orderService;

    @GetMapping("/orders")
    public ResponseEntity<List<Order>> getAllBorrowOrders(
            @RequestParam(required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate expiry,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {

        Pageable pageable = PageRequest.of(page, size);

        List<Order> orders = (expiry == null)
                ? orderService.findAllOrders(pageable) 
                : orderService.findByExpiryDate(expiry, pageable);

        return ResponseEntity.ok(orders);
    }

    // GET /orders/{id} — return HATEOAS-wrapped single order
    @GetMapping("/orders/{id}")
    public ResponseEntity<EntityModel<Order>> getBorrowOrder(@PathVariable Long id) throws OrderNotFoundException {
        Order order = orderService.findOrder(id);
        return ResponseEntity.ok(toOrderModel(order));
    }

    // PUT /orders/{id} — update and return HATEOAS-wrapped entity
    @PutMapping("/orders/{id}")
    public ResponseEntity<EntityModel<Order>> updateOrder(@RequestBody Order order, @PathVariable Long id)
            throws OrderNotFoundException {
        Order updatedOrder = orderService.updateOrder(order, id);
        return ResponseEntity.ok(toOrderModel(updatedOrder));
    }

    // DELETE /orders/{id} — tests expect 200 OK (not 204)
    @DeleteMapping("/orders/{id}")
    public ResponseEntity<Object> deleteBookOrder(@PathVariable Long id) throws OrderNotFoundException {
        orderService.deleteOrder(id);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    // POST /orders — create + HATEOAS links to other actions 
    @PostMapping("/orders")
    public ResponseEntity<EntityModel<Order>> createUserOrder(@RequestBody Order order) throws OrderNotFoundException {
        Order saved = orderService.saveOrder(order);

        EntityModel<Order> model = toOrderModel(saved)
            .add(linkTo(methodOn(OrderController.class).getAllBorrowOrders(null, 0, 10)).withRel("orders"))
            .add(linkTo(methodOn(OrderController.class).updateOrder(saved, saved.getId())).withRel("update"))
            .add(linkTo(methodOn(OrderController.class).deleteBookOrder(saved.getId())).withRel("delete"));

        return ResponseEntity
            .created(linkTo(methodOn(OrderController.class).getBorrowOrder(saved.getId())).toUri())
            .body(model);
    }

    private EntityModel<Order> toOrderModel(Order order) throws OrderNotFoundException {
        return EntityModel.of(order,
            linkTo(methodOn(OrderController.class).getBorrowOrder(order.getId())).withSelfRel(),
            linkTo(methodOn(OrderController.class).getAllBorrowOrders(null, 0, 10)).withRel("orders"));
    }
}
