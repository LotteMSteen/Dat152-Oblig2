/**
 * 
 */
package no.hvl.dat152.rest.ws.service;

import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import no.hvl.dat152.rest.ws.exceptions.OrderNotFoundException;
import no.hvl.dat152.rest.ws.exceptions.UserNotFoundException;
import no.hvl.dat152.rest.ws.model.Order;
import no.hvl.dat152.rest.ws.model.User;
import no.hvl.dat152.rest.ws.repository.UserRepository;

/**
 * @author tdoy
 */
@Service
public class UserService {

	@Autowired
	private UserRepository userRepository;

	
	public List<User> findAllUsers(){
		
		List<User> allUsers = (List<User>) userRepository.findAll();
		
		return allUsers;
	}
	
	public User findUser(Long userid) throws UserNotFoundException {
		
		User user = userRepository.findById(userid)
				.orElseThrow(()-> new UserNotFoundException("User with id: "+userid+" not found"));
		
		return user;
	}
	
	
	public User saveUser(User user){
		
		return userRepository.save(user);
	}
	
	 public void deleteUser(Long id) throws UserNotFoundException {
		 
		 User user = userRepository.findById(id)
					.orElseThrow(()-> new UserNotFoundException("User with id: "+id+" not found"));
		 
		 userRepository.delete(user);
	 }
	
	 public User updateUser(User user, Long id) throws UserNotFoundException{
		
		User existingUser = userRepository.findById(id)
				.orElseThrow(()-> new UserNotFoundException("User with id: "+id+" not found"));

				existingUser.setFirstname(user.getFirstname());
				existingUser.setLastname(user.getLastname());
				return userRepository.save(existingUser);
	 }

	
	public Set<Order> getUserOrders(Long userid) throws UserNotFoundException {
		
		User user = userRepository.findById(userid)
				.orElseThrow(()-> new UserNotFoundException("User with id: "+userid+" not found"));
		
		return user.getOrders();
	}
	
	public Order getUserOrder(Long userid, Long oid) throws OrderNotFoundException, UserNotFoundException{
		
		User user = userRepository.findById(userid)
				.orElseThrow(()-> new UserNotFoundException("User with id: "+userid+" not found"));
		
		Set<Order> orders = user.getOrders();
		
		Iterator<Order> iterator = orders.iterator();
		
		while(iterator.hasNext()) {
			Order order = iterator.next();
			if(order.getId().equals(oid)) {
				return order;
			}
		}
		
		throw new OrderNotFoundException("Order with id: "+oid+" not found for user with id: "+userid);
	}
	
 public void deleteOrderForUser(Long userid, Long oid) throws UserNotFoundException, OrderNotFoundException{
		
		User user = userRepository.findById(userid)
				.orElseThrow(()-> new UserNotFoundException("User with id: "+userid+" not found"));
		
		Set<Order> orders = user.getOrders();
		
		Iterator<Order> iterator = orders.iterator();
		
		while(iterator.hasNext()) {
			Order order = iterator.next();
			if(order.getId().equals(oid)) {
				iterator.remove();
				userRepository.save(user);
				return;
			}
		}
		
		throw new OrderNotFoundException("Order with id: "+oid+" not found for user with id: "+userid);
	}

	
	public User createOrdersForUser(Long userid, Order order) throws UserNotFoundException{
		
		User user = userRepository.findById(userid)
				.orElseThrow(()-> new UserNotFoundException("User with id: "+userid+" not found"));
		
		Set<Order> orders = user.getOrders();
		orders.add(order);
		user.setOrders(orders);
		
		return userRepository.save(user);
	}
}
