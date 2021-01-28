package de.bendahl;

import static org.springframework.web.bind.annotation.RequestMethod.DELETE;
import static org.springframework.web.bind.annotation.RequestMethod.GET;
import static org.springframework.web.bind.annotation.RequestMethod.POST;
import static org.springframework.web.bind.annotation.RequestMethod.PUT;

import io.micrometer.core.annotation.Timed;
import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.info.Info;
import java.util.List;
import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;
import javax.websocket.server.PathParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * This is the REST endpoint of the application. Swagger annotations are used to add some meta data
 * to the class. This is then used by Swagger to generate additional documentation.
 *
 * @author Benjamin Dahlmanns
 */
@OpenAPIDefinition(info = @Info(title = "Awesome little todo application to demonstrate Docker"))
@RestController
@RequestMapping("/todos")
public class TodoEndpoint {

  private final TodoRepository repository;

  private static final ThreadLocalRandom random = ThreadLocalRandom.current();

  @Autowired
  public TodoEndpoint(TodoRepository repository) {
    this.repository = repository;
  }

  @Operation(description = "Get a list of all available todos")
  @RequestMapping(method = GET, produces = "application/json")
  public List<Todo> findAll() throws InterruptedException {
    delay();
    error();
    return repository.findAll();
  }

  @Operation(description = "Get a specific todo identified by it's id")
  @RequestMapping(path = "/todo/{id}", method = GET, produces = "application/json")
  public Todo findOne(@PathVariable long id) {
    return repository.findById(id).orElse(null);
  }

  @Operation(description = "Create a new todo. Note that the id will be auto generated.")
  @RequestMapping(path = "/todo", method = POST, consumes = "application/json", produces = "application/json")
  public Todo addTodo(@RequestBody Todo newTodo) {
    return repository.save(newTodo);
  }

  @Operation(description = "Modify an existing todo.")
  @RequestMapping(path = "/todo/{id}", method = PUT, consumes = "application/json", produces = "application/json")
  public ResponseEntity<Todo> updateTodo(@PathVariable long id, @RequestBody Todo todo) {
    Todo result = repository.findById(id).orElse(null);
    if (result == null) {
      return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }
    result.setDone(todo.isDone());
    result.setText(todo.getText());
    return ResponseEntity.ok(repository.save(result));
  }

  @Operation(description = "Delete the todo with the given id.")
  @RequestMapping(path = "/todo/{id}", method = DELETE)
  public void deleteTodo(@PathVariable long id) {
    repository.deleteById(id);
  }

  @Operation(description = "Delete all todos with the given done status")
  @RequestMapping(method = DELETE)
  public void deleteByDone(@PathParam("done") boolean done) {
    repository.deleteByDone(done);
  }

  private void delay() throws InterruptedException {
    if (random.nextInt(10) == 1) {
      Thread.sleep(1000);
    }
  }

  private void error() {
    if (random.nextInt(10) == 9) {
      throw new RuntimeException();
    }
  }
}
