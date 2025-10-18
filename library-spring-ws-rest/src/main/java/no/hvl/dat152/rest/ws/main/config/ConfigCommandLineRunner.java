package no.hvl.dat152.rest.ws.main.config;


import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import no.hvl.dat152.rest.ws.exceptions.AuthorNotFoundException;
import no.hvl.dat152.rest.ws.model.Author;
import no.hvl.dat152.rest.ws.model.Book;
import no.hvl.dat152.rest.ws.model.Order;
import no.hvl.dat152.rest.ws.model.User;
import no.hvl.dat152.rest.ws.repository.AuthorRepository;
import no.hvl.dat152.rest.ws.repository.BookRepository;
import no.hvl.dat152.rest.ws.repository.UserRepository;
import no.hvl.dat152.rest.ws.service.AuthorService;

@Component
class ConfigCommandLineRunner implements CommandLineRunner  {

  private static final Logger log = LoggerFactory.getLogger(ConfigCommandLineRunner.class);

  @Autowired
  AuthorService authorService;
  
  @Autowired
  BookRepository bookRepository;
  
  @Autowired
  AuthorRepository authorRepository;
  
  @Autowired
  UserRepository userRepository;
  
  @Override
  public void run(String... args) throws Exception {
	  
	  authorRepository.saveAll(createDefaultAuthors());
	  bookRepository.saveAll(creatDefaultBooks());
	  userRepository.saveAll(createDefaultUsersPlusOrders());
   
  }
  
	private Iterable<Author> createDefaultAuthors() {
		
		List<Author> authors = new ArrayList<Author>();
		
		authors.add(new Author("Shari", "Pfleeger"));
		authors.add(new Author("Jim", "Cooling"));
		authors.add(new Author("James", "Kurose"));
		authors.add(new Author("Keith", "Ross"));
		authors.add(new Author("Martin", "Kleppmann"));
		authors.add(new Author("Cormen", "Leiserson"));
		
		return authors;
	}
	
	private Iterable<Book> creatDefaultBooks() throws AuthorNotFoundException {
		
		Author author1 = authorService.findById(1);
		Author author2 = authorService.findById(2);
		Author author3 = authorService.findById(3);
		Author author4 = authorService.findById(4);
		Author author5 = authorService.findById(5);
		Author author6 = authorService.findById(6);
		
		Set<Author> authors = new HashSet<Author>();
		Book book1 = new Book();
		book1.setIsbn("abcde1234");
		book1.setTitle("Software Engineering and System");
		authors.add(author1);
		book1.setAuthors(authors);
		
		Set<Author> authors2 = new HashSet<Author>();
		Book book2 = new Book();
		book2.setIsbn("ghijk1234");
		book2.setTitle("Computer Network");
		authors2.add(author3);
		authors2.add(author4);
		book2.setAuthors(authors2);
		
		Set<Author> authors3 = new HashSet<Author>();
		Book book3 = new Book();
		book3.setIsbn("qabfde1230");
		book3.setTitle("Real-Time Operating System");
		authors3.add(author2);
		book3.setAuthors(authors3);
		
		Set<Author> authors4 = new HashSet<Author>();
		Book book4 = new Book();
		book4.setIsbn("rstuv1540");
		book4.setTitle("Introduction To Algorithms");
		authors4.add(author6);
		book4.setAuthors(authors4);
		
		Set<Author> authors5 = new HashSet<Author>();
		Book book5 = new Book();
		book5.setIsbn("cfewxy2651");
		book5.setTitle("Designing Data-Intensive Applications");
		authors5.add(author5);
		book5.setAuthors(authors5);
		
		
		List<Book> books =  new ArrayList<Book>();		
		books.add(book1);
		books.add(book2);
		books.add(book3);
		books.add(book4);
		books.add(book5);
		
		return books;
	}
	
	private Iterable<User> createDefaultUsersPlusOrders(){
		List<User> users = new ArrayList<>();
		
		// user1
		User user1 = new User("Robert", "Isaac");
		// orders
		Order order1 = new Order("ghijk1234", LocalDate.now().plusWeeks(2));		
		user1.addOrder(order1);
		
		//user2
		User user2 = new User("Kristin", "Solberg");
		Order order2_1 = new Order("abcde1234", LocalDate.now().plusWeeks(3));
		Order order2_2 = new Order("qabfde1230", LocalDate.now().plusWeeks(3));
		user2.addOrder(order2_1);
		user2.addOrder(order2_2);
		
		users.add(user1);
		users.add(user2);
		
		return users;
		
	}

}
