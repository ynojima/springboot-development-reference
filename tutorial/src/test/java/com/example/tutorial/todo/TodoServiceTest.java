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

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import static org.assertj.core.api.Assertions.*;
import static org.mockito.BDDMockito.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.Collection;
import java.util.Optional;

@SpringBootTest(classes = TodoServiceImpl.class)
public class TodoServiceTest {

    @MockBean
    private TodoRepository todoRepository;

    @Autowired
    private TodoService todoService;

    @Test
    @DisplayName("todoIdに対応するTodoが取得できることを確認する(Service)")
    void testFindOne() {
        // setup
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("uuuu/MM/dd HH:mm:ss");
        LocalDateTime createdAt = LocalDateTime.parse("2019/09/19 01:01:01", dtf);
        Todo expectTodo = new Todo(1L, "sample todo 1", false, createdAt);
        Optional<Todo> expectOptionalTodo = Optional.of(expectTodo);
        given(todoRepository.findById(1L)).willReturn(expectOptionalTodo);

        // run
        Todo actualTodo = todoService.findOne(1L);

        // check
        then(todoRepository).should(times(1)).findById(1L);
        assertThat(actualTodo.getTodoId()).isEqualTo(1);
        assertThat(actualTodo.getTodoTitle()).isEqualTo("sample todo 1");
        assertThat(actualTodo.isFinished()).isEqualTo(false);
        assertThat(actualTodo.getCreatedAt()).isEqualTo(createdAt);
    }

    @Test
    @DisplayName("全Todoが取得できることを確認する(service)")
    void testFindAll() {
        // setup
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("uuuu/MM/dd HH:mm:ss");
        LocalDateTime createdAt1 = LocalDateTime.parse("2019/09/19 01:01:01", dtf);
        LocalDateTime createdAt2 = LocalDateTime.parse("2019/09/19 02:02:02", dtf);
        Todo expectTodo1 = new Todo(1L, "sample todo 1", false, createdAt1);
        Todo expectTodo2 = new Todo(2L, "sample todo 2", true, createdAt2);

        Collection<Todo> expectTodos = Arrays.asList(expectTodo1, expectTodo2);
        given(todoRepository.findAll()).willReturn(expectTodos);

        // run
        Collection<Todo> actualTodos = todoService.findAll();

        // check
        then(todoRepository).should(times(1)).findAll();
        assertThat(actualTodos).extracting("todoId", "todoTitle", "finished", "createdAt")
                .contains(tuple(1L, "sample todo 1", false, createdAt1));
        assertThat(actualTodos).extracting("todoId", "todoTitle", "finished", "createdAt")
                .contains(tuple(2L, "sample todo 2", true, createdAt2));
    }

    @Test
    @DisplayName("新たなTodoが作成できることを確認する(service)")
    void testCreate() {
        // setup
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("uuuu/MM/dd HH:mm:ss");
        LocalDateTime createdAt = LocalDateTime.parse("2019/09/19 03:03:03", dtf);
        Todo expectTodo = new Todo(3L, "sample todo 3", false, createdAt);

        doNothing().when(todoRepository).create(expectTodo);

        // run
        todoService.create(expectTodo);

        // check
        then(todoRepository).should(times(1)).create(expectTodo);
    }

    @Test
    @DisplayName("todoId=1のfinishedがtrueになることを確認する(service)")
    void testFinish() {
        // setup
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("uuuu/MM/dd HH:mm:ss");
        LocalDateTime createdAt = LocalDateTime.parse("2019/09/19 01:01:01", dtf);
        Todo expectTodo = new Todo(1L, "sample todo 1", false, createdAt);
        Optional<Todo> expectOptionalTodo = Optional.of(expectTodo);
        given(todoRepository.findById(1L)).willReturn(expectOptionalTodo);

        doNothing().when(todoRepository).updateById(1L);

        // run
        todoService.finish(1L);

        // check
        then(todoRepository).should(times(1)).findById(1L);
        then(todoRepository).should(times(1)).updateById(1L);
    }

    @Test
    @DisplayName("todoId=1がDeleteによって削除されることを確認する(service)")
    void testDelete() {
        // setup
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("uuuu/MM/dd HH:mm:ss");
        LocalDateTime createdAt = LocalDateTime.parse("2019/09/19 01:01:01", dtf);
        Todo expectTodo = new Todo(1L, "sample todo 1", false, createdAt);
        Optional<Todo> expectOptionalTodo = Optional.of(expectTodo);
        given(todoRepository.findById(1L)).willReturn(expectOptionalTodo);

        doNothing().when(todoRepository).deleteById(1L);

        // run
        todoService.delete(1L);

        // check
        then(todoRepository).should(times(1)).findById(1L);
        then(todoRepository).should(times(1)).deleteById(1L);
    }
}
