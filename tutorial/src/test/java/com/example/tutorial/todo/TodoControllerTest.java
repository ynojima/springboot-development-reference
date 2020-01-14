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

import java.net.URI;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.Collection;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.*;
import static org.mockito.Mockito.times;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.RequestEntity;
import org.springframework.http.ResponseEntity;
import com.github.dozermapper.core.Mapper;

@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
public class TodoControllerTest {

    @Autowired
    private TestRestTemplate testRestTemplate;

    @Autowired
    private Mapper beanMapper;

    @MockBean
    private TodoService todoService;

    @Test
    @DisplayName("GET Todosが正常に動作することを確認する(Controller)")
    void testGetTodos() {
        // setup
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("uuuu/MM/dd HH:mm:ss");
        LocalDateTime createdAt1 = LocalDateTime.parse("2019/09/19 01:01:01", dtf);
        LocalDateTime createdAt2 = LocalDateTime.parse("2019/09/19 02:02:02", dtf);
        Todo expectTodo1 = new Todo(1L, "sample todo 1", false, createdAt1);
        Todo expectTodo2 = new Todo(2L, "sample todo 2", true, createdAt2);
        Collection<Todo> expectTodos = Arrays.asList(expectTodo1, expectTodo2);
        given(todoService.findAll()).willReturn(expectTodos);

        TodoResource[] expectTodoResources = expectTodos.stream()
                .map(todo -> beanMapper.map(todo, TodoResource.class)).toArray(TodoResource[]::new);

        // run
        ResponseEntity<TodoResource[]> actualResponseEntity =
                testRestTemplate.getForEntity("/todos", TodoResource[].class);

        // check
        then(todoService).should(times(1)).findAll();
        assertThat(actualResponseEntity.getBody()).isEqualTo(expectTodoResources);
        assertThat(actualResponseEntity.getStatusCode()).isEqualTo(HttpStatus.OK);
    }

    @Test
    @DisplayName("GET Todoが正常に動作することを確認する(Controller)")
    void testGetTodo() throws Exception {
        // setup
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("uuuu/MM/dd HH:mm:ss");
        LocalDateTime createdAt = LocalDateTime.parse("2019/09/19 01:01:01", dtf);
        Todo expectTodo = new Todo(1L, "sample todo 1", false, createdAt);
        TodoResource expectTodoResource = beanMapper.map(expectTodo, TodoResource.class);
        given(this.todoService.findOne(1L)).willReturn(expectTodo);

        // run
        ResponseEntity<TodoResource> actualResponseEntity =
                testRestTemplate.getForEntity("/todos/1", TodoResource.class);

        // check
        then(todoService).should(times(1)).findOne(1L);
        assertThat(actualResponseEntity.getBody()).isEqualTo(expectTodoResource);
        assertThat(actualResponseEntity.getStatusCode()).isEqualTo(HttpStatus.OK);
    }

    @Test
    @DisplayName("POST Todoが正常に動作することを確認する(Controller)")
    void testPostTodo() {
        // setup
        TodoResource inputTodoResource = new TodoResource();
        inputTodoResource.setTodoTitle("sample todo 3");
        Todo inputTodo = beanMapper.map(inputTodoResource, Todo.class);

        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("uuuu/MM/dd HH:mm:ss");
        LocalDateTime createdAt = LocalDateTime.parse("2019/09/19 03:03:03", dtf);
        Todo expectTodo = new Todo(3L, "sample todo 3", false, createdAt);
        TodoResource expectTodoResource = beanMapper.map(expectTodo, TodoResource.class);

        given(this.todoService.create(any(Todo.class))).willReturn(expectTodo);

        // run
        ResponseEntity<TodoResource> actualResponseEntity =
                testRestTemplate.postForEntity("/todos", inputTodoResource, TodoResource.class);

        // check
        then(todoService).should(times(1)).create(inputTodo);
        assertThat(actualResponseEntity.getBody()).isEqualTo(expectTodoResource);
        assertThat(actualResponseEntity.getStatusCode()).isEqualTo(HttpStatus.CREATED);
    }

    @Test
    @DisplayName("PUT Todoが正常に動作することを確認する(Controller)")
    void testPutTodo() {
        // setup
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("uuuu/MM/dd HH:mm:ss");
        LocalDateTime createdAt = LocalDateTime.parse("2019/09/19 01:01:01", dtf);
        Todo expectTodo = new Todo(1L, "sample todo 1", true, createdAt);
        TodoResource expectTodoResource = beanMapper.map(expectTodo, TodoResource.class);
        given(this.todoService.finish(1L)).willReturn(expectTodo);

        // run
        RequestEntity<String> actualRequestEntity =
                RequestEntity.put(URI.create("/todos/1")).body("");
        ResponseEntity<TodoResource> actualResponseEntity =
                testRestTemplate.exchange(actualRequestEntity, TodoResource.class);

        // check
        then(todoService).should(times(1)).finish(1L);
        assertThat(actualResponseEntity.getBody()).isEqualTo(expectTodoResource);
        assertThat(actualResponseEntity.getStatusCode()).isEqualTo(HttpStatus.OK);
    }

    @Test
    @DisplayName("DELETE Todoが正常に動作することを確認する(Controller)")
    void testDeleteTodo() {
        // run
        URI uri = URI.create("/todos/1");
        ResponseEntity<String> actualResponseEntity =
                testRestTemplate.exchange(uri, HttpMethod.DELETE, HttpEntity.EMPTY, String.class);

        // check
        then(todoService).should(times(1)).delete(1L);
        assertThat(actualResponseEntity.getBody()).isNull();
        assertThat(actualResponseEntity.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);
    }

}
