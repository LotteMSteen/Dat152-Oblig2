/**
 * 
 */
package no.hvl.dat152.rest.ws.service;

import java.util.List;

import java.time.LocalDate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import no.hvl.dat152.rest.ws.exceptions.OrderNotFoundException;
import no.hvl.dat152.rest.ws.model.Order;
import no.hvl.dat152.rest.ws.repository.OrderRepository;

/**
 * @author tdoy
 */
@Service
public class OrderService {

    @Autowired
    private OrderRepository orderRepository;

    public Order saveOrder(Order order) {
        return orderRepository.save(order);
    }

    public Order findOrder(Long id) throws OrderNotFoundException {
        return orderRepository.findById(id)
                .orElseThrow(
                        () -> new OrderNotFoundException("Order with id: " + id + " not found in the order list!"));
    }

    public void deleteOrder(Long id) throws OrderNotFoundException {
        Order order = orderRepository.findById(id)
                .orElseThrow(
                        () -> new OrderNotFoundException("Order with id: " + id + " not found in the order list!"));
        orderRepository.delete(order);
    }

    public List<Order> findAllOrders() {
        return (List<Order>) orderRepository.findAll();
    }

    public List<Order> findAllOrders(Pageable pageable) {
        Page<Order> page = orderRepository.findAll(pageable);
        return page.getContent();
    }

    /** Task 2: filter by expiry (paged) */
    public List<Order> findByExpiryDate(LocalDate expiry, Pageable page) {
        Page<Order> orders = orderRepository.findByExpiryBefore(expiry, page);
        return orders.getContent();
    }

    public Order updateOrder(Order order, Long id) throws OrderNotFoundException {
        return orderRepository.findById(id)
                .map(existingOrder -> {
                    existingOrder.setExpiry(order.getExpiry());
                    return orderRepository.save(existingOrder);
                })
                .orElseThrow(
                        () -> new OrderNotFoundException("Order with id: " + id + " not found in the order list!"));
    }

}
