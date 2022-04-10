package com.example.todo.api.todo;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import javax.inject.Inject;

import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.example.todo.domain.model.Todo;
import com.example.todo.domain.service.todo.TodoService;
import com.github.dozermapper.core.Mapper;

@RestController
@RequestMapping("todos")
public class TodoRestController {
	
	@Inject
	TodoService todoService;
	@Inject
	Mapper beanMapper;
	
	@GetMapping
	@ResponseStatus(HttpStatus.OK)
	public List<TodoResource> getTodos() {
		Collection<Todo> todos = todoService.findAll();
		List<TodoResource> todoResources = new ArrayList<>();
		for (Todo todo : todos) {
			todoResources.add(beanMapper.map(todo, TodoResource.class));
		}
		return todoResources; 
	}
	
	@PostMapping
	@ResponseStatus(HttpStatus.CREATED)
	public TodoResource postTodos (@RequestBody @Validated TodoResource todoResource) {
		Todo createdTodo = todoService.create(beanMapper.map(todoResource, Todo.class));
		TodoResource createdTodoResponse = beanMapper.map(createdTodo, TodoResource.class);
		return createdTodoResponse;
	}
	
	@GetMapping("{todoId}")
	@ResponseStatus(HttpStatus.OK)
	public TodoResource getTodo(@PathVariable("todoId") String todoId) {
		Todo todo = todoService.findOne(todoId);
		TodoResource todoResource = beanMapper.map(todo, TodoResource.class);		
		return todoResource;
	}
	
	@PutMapping("{todoId}")
	@ResponseStatus(HttpStatus.OK)
	public TodoResource putTodo(@PathVariable("todoId") String todoId) {
		Todo finishedTodo = todoService.finish(todoId);
		TodoResource todoResource = beanMapper.map(finishedTodo, TodoResource.class);
		return todoResource;
	}
	
	@DeleteMapping("{todoId}")
	@ResponseStatus(HttpStatus.NO_CONTENT)
	public void deleteTodo(@PathVariable("todoId") String todoId) {
		todoService.delete(todoId);		
	}

}
