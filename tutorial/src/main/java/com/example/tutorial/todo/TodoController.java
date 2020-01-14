/*
 * Copyright(c) 2019 NTT Corporation.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND,
 * either express or implied. See the License for the specific language
 * governing permissions and limitations under the License.
 */
package com.example.tutorial.todo;

import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import com.github.dozermapper.core.Mapper;

@RestController
@RequestMapping("todos")
public class TodoController {

    @Autowired
    TodoService todoService;

    @Autowired
    Mapper beanMapper;

    @GetMapping
    public List<TodoResource> getTodos() {
        Collection<Todo> todos = todoService.findAll();
        return todos.stream().map(todo -> beanMapper.map(todo, TodoResource.class))
                .collect(Collectors.toList());
    }

    @GetMapping("{todoId}")
    public TodoResource getTodo(@PathVariable("todoId") Long todoId) {
        Todo todo = todoService.findOne(todoId);
        return beanMapper.map(todo, TodoResource.class);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public TodoResource postTodo(@RequestBody @Validated TodoResource todoResource) {
        Todo createdTodo = todoService.create(beanMapper.map(todoResource, Todo.class));
        return beanMapper.map(createdTodo, TodoResource.class);
    }

    @PutMapping("{todoId}")
    public TodoResource putTodo(@PathVariable("todoId") Long todoId) {
        Todo finishedTodo = todoService.finish(todoId);
        return beanMapper.map(finishedTodo, TodoResource.class);
    }

    @DeleteMapping("{todoId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteTodo(@PathVariable("todoId") Long todoId) {
        todoService.delete(todoId);
    }

}
