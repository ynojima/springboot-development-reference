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

import java.time.LocalDateTime;
import java.util.Collection;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.example.tutorial.common.exception.BusinessException;
import com.example.tutorial.common.exception.ResourceNotFoundException;

@Service
@Transactional
public class TodoServiceImpl implements TodoService {

    private static final long MAX_UNFINISHED_COUNT = 5;

    @Autowired
    TodoRepository todoRepository;

    @Override
    public Todo findOne(Long todoId) {
        return todoRepository.findById(todoId).orElseThrow(() -> new ResourceNotFoundException(
                "The requested Todo is not found. (id=" + todoId + ")"));
    }

    @Override
    public Collection<Todo> findAll() {
        return todoRepository.findAll();
    }

    @Override
    public Todo create(Todo todo) {
        long unfinishedCount = todoRepository.countByFinished(false);
        if (unfinishedCount >= MAX_UNFINISHED_COUNT) {
            throw new BusinessException(
                    "The count of un-finished Todo must not be over " + MAX_UNFINISHED_COUNT + ".");
        }

        LocalDateTime createdAt = LocalDateTime.now();
        todo.setCreatedAt(createdAt);
        todo.setFinished(false);

        todoRepository.create(todo);
        return todo;
    }

    @Override
    public Todo finish(Long todoId) {
        Todo todo = findOne(todoId);
        if (todo.isFinished()) {
            throw new BusinessException(
                    "The requested Todo is already finished. (id=" + todoId + ")");
        }
        todo.setFinished(true);
        todoRepository.updateById(todoId);
        return todo;
    }

    @Override
    public void delete(Long todoId) {
        findOne(todoId);
        todoRepository.deleteById(todoId);
    }

}
