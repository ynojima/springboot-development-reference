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
import java.util.Optional;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Options;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

@Mapper
public interface TodoRepository {

    @Select("SELECT todo_id, todo_title, finished, created_at FROM todo WHERE todo_id = #{todoId}")
    Optional<Todo> findById(Long todoId);

    @Select("SELECT todo_id, todo_title, finished, created_at FROM todo")
    Collection<Todo> findAll();

    @Insert("INSERT INTO todo(todo_title, finished, created_at) VALUES(#{todoTitle}, #{finished}, #{createdAt})")
    @Options(useGeneratedKeys = true, keyProperty = "todoId")
    void create(Todo todo);

    @Update("UPDATE todo SET finished = true WHERE todo_id = #{todoId}")
    void updateById(Long todoId);

    @Delete("DELETE FROM todo WHERE todo_id = #{todoId}")
    void deleteById(Long todoId);

    @Select("SELECT COUNT(*) FROM todo WHERE finished = #{finished}")
    long countByFinished(boolean finished);

}
