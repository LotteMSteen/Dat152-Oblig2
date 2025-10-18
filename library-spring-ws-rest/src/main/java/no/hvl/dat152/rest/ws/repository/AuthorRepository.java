/**
 * 
 */
package no.hvl.dat152.rest.ws.repository;


import org.springframework.data.repository.CrudRepository;

import no.hvl.dat152.rest.ws.model.Author;

/**
 * @author tdoy
 */
public interface AuthorRepository extends CrudRepository<Author, Integer> {
	

}
