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

import static org.assertj.core.api.Assertions.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Collection;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mybatis.spring.boot.test.autoconfigure.MybatisTest;
import org.springframework.beans.BeanWrapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.namedparam.BeanPropertySqlParameterSource;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcOperations;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.jdbc.support.rowset.SqlRowSet;

@MybatisTest
public class TodoRepositoryTest {

    @Autowired
    private TodoRepository todoRepository;

    @Autowired
    NamedParameterJdbcOperations jdbcOperations;

    private Todo getTodo(Long todoId) {

        Todo todo = new Todo(todoId, null, true, null);
        return jdbcOperations.queryForObject("SELECT * FROM todo WHERE todo_id=:todoId",
                new BeanPropertySqlParameterSource(todo),
                new BeanPropertyRowMapper<Todo>(Todo.class) {
                    @Override
                    protected void initBeanWrapper(BeanWrapper bw) {
                        super.initBeanWrapper(bw);
                        bw.setAutoGrowNestedPaths(true);
                    }
                });
    }

    private Long getTodos(Long todoId) {
        String sqlstr = "SELECT count(*) as cnt FROM todo WHERE todo_id=:todoId ";
        SqlParameterSource param = new MapSqlParameterSource().addValue("todoId", todoId);
        SqlRowSet todo = jdbcOperations.queryForRowSet(sqlstr, param);
        todo.next();
        return todo.getLong("cnt");
    }

    @Test
    @DisplayName("全Todoが取得できることを確認する(Repository)")
    void testFindAll() {
        // setup
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("uuuu/MM/dd HH:mm:ss");
        LocalDateTime createdAt1 = LocalDateTime.parse("2019/09/19 01:01:01", dtf);
        LocalDateTime createdAt2 = LocalDateTime.parse("2019/09/19 02:02:02", dtf);
        LocalDateTime createdAt3 = LocalDateTime.parse("2019/09/19 03:03:03", dtf);

        // run
        Collection<Todo> actualTodos = todoRepository.findAll();

        // check
        assertThat(actualTodos).extracting("todoId", "todoTitle", "finished", "createdAt")
                .contains(tuple(1L, "sample todo 1", false, createdAt1));
        assertThat(actualTodos).extracting("todoId", "todoTitle", "finished", "createdAt")
                .contains(tuple(2L, "sample todo 2", true, createdAt2));
        assertThat(actualTodos).extracting("todoId", "todoTitle", "finished", "createdAt")
                .contains(tuple(3L, "sample todo 3", false, createdAt3));
    }

    @Test
    @DisplayName("todoIdに対応するTodoが取得できることを確認する(Repository)")
    void testFindById() {

        // run
        Todo actualTodo = todoRepository.findById(1L).get();
        Todo todo = getTodo(1L);

        // check
        assertThat(actualTodo.getTodoId()).isEqualTo(todo.getTodoId());
        assertThat(actualTodo.getTodoTitle()).isEqualTo(todo.getTodoTitle());
        assertThat(actualTodo.isFinished()).isEqualTo(todo.isFinished());
        assertThat(actualTodo.getCreatedAt()).isEqualTo(todo.getCreatedAt());
    }

    @Test
    @DisplayName("新たなTodoが作成できることを確認する(Repository)")
    void testCreate() {
        // setup
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("uuuu/MM/dd HH:mm:ss");
        LocalDateTime createdAt = LocalDateTime.parse("2019/09/19 04:04:04", dtf);
        Todo actualTodo = new Todo(4L, "sample todo 4", false, createdAt);

        // run
        todoRepository.create(actualTodo);
        Todo todo = getTodo(4L);

        // check
        assertThat(actualTodo.getTodoTitle()).isEqualTo(todo.getTodoTitle());
    }

    @Test
    @DisplayName("finishedをfalseからtrueに変更できることを確認する(Repository)")
    void testUpdateById() {
        // run
        todoRepository.updateById(1L);
        Todo todo = getTodo(1L);

        // check
        assertThat(todo.isFinished()).isTrue();
    }

    @Test
    @DisplayName("todoId=1が削除できていることを確認する(Repository)")
    void testDeleteById() {
        // run
        todoRepository.deleteById(1L);
        Long cnt = getTodos(1L);

        // check
        assertThat(cnt).isEqualTo(0);
    }

    @Test
    @DisplayName("未完了 or 完了済のTodoの件数を取得できることを確認する(Repository)")
    void testCountByFinished() {
        // run
        long unfinishedCount = todoRepository.countByFinished(false);
        long finishedCount = todoRepository.countByFinished(true);

        // check
        assertThat(unfinishedCount).isEqualTo(2);
        assertThat(finishedCount).isEqualTo(1);
    }

}
