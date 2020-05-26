package com.anop.Service.impl;

import com.anop.mapper.CustomTodoMapper;
import com.anop.mapper.TodoMapper;
import com.anop.pojo.Todo;
import com.anop.pojo.example.TodoExample;
import com.anop.pojo.security.User;
import com.anop.resource.*;
import com.anop.service.TodoService;
import com.anop.util.PageSortHelper;
import com.anop.util.PropertyMapperUtils;
import com.anop.util.SecurityUtils;
import com.github.pagehelper.PageInfo;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.Date;
import java.util.List;

/**
 * @author chcic
 */
@Service
@Transactional(rollbackFor = Throwable.class)
public class TodoServiceImpl implements TodoService {

    private static final byte UN_COMPLETED = 0;

    private static final byte UN_CHECKED = 0;

    private static final byte CHECKED = 1;

    private static final byte COMPLETE = 0;

    private static final byte IMPORTANT = 1;

    private static final byte FAVORITE = 2;

    @Resource
    TodoMapper todoMapper;

    @Resource
    CustomTodoMapper customTodoMapper;

    @Override
    public Todo addTodo(TodoAddResource resource) {
        Todo todo = PropertyMapperUtils.map(resource, Todo.class);
        todo.setIsCompleted(UN_COMPLETED);
        todo.setUserId(SecurityUtils.getLoginUser(User.class).getId());
        todoMapper.insert(todo);
        return todo;
    }

    @Override
    public int deleteTodo(Todo todo) {

        if (!todo.getUserId().equals(SecurityUtils.getLoginUser(User.class).getId())) {
            return -1;
        }

        return todoMapper.deleteByPrimaryKey(todo.getId());
    }

    @Override
    public int updateTodo(Todo oldTodo, TodoUpdateResource resource) {

        if (!oldTodo.getUserId().equals(SecurityUtils.getLoginUser(User.class).getId())) {
            return -1;
        }

        TodoExample example = new TodoExample();
        TodoExample.Criteria criteria = example.createCriteria();
        criteria.andIdEqualTo(oldTodo.getId());
        Todo newTodo = PropertyMapperUtils.map(resource, Todo.class);
        newTodo.setId(oldTodo.getId());
        newTodo.setUserId(oldTodo.getUserId());
        newTodo.setIsCompleted(oldTodo.getIsCompleted());

        return todoMapper.updateByExample(newTodo, example);
    }

    @Override
    public int checkTodo(Todo todo, TodoFlagResource resource) {

        if (!todo.getUserId().equals(SecurityUtils.getLoginUser(User.class).getId())) {
            return -1;
        }

        TodoExample example = new TodoExample();
        TodoExample.Criteria criteria = example.createCriteria();
        criteria.andIdEqualTo(todo.getId());

        if (resource.getFlag() == IMPORTANT) {
            todo.setIsImportant((todo.getIsImportant() == CHECKED) ? UN_CHECKED : CHECKED);
        }
        else if (resource.getFlag() == FAVORITE) {
            todo.setIsFavorite((todo.getIsFavorite() == CHECKED) ? UN_CHECKED : CHECKED);
        }
        else if (resource.getFlag() == COMPLETE) {
            todo.setIsCompleted((todo.getIsCompleted() == CHECKED) ? UN_CHECKED : CHECKED);
        }
        return todoMapper.updateByExampleSelective(todo, example);
    }

    @Override
    public Todo getTodo(int todoId) {
        return todoMapper.selectByPrimaryKey(todoId);
    }

    @Override
    public PageInfo<List<Todo>> listUserTodo(PageParmResource page, TodoFlagResource flagResource) {
        TodoExample todoExample = new TodoExample();

        TodoExample.Criteria criteria1 = todoExample.createCriteria();
        criteria1.andUserIdEqualTo(SecurityUtils.getLoginUser(User.class).getId())
                .andIsCompletedEqualTo((byte) 0)
                .andEndDateGreaterThan(new Date());

        if (flagResource.getFlag() == IMPORTANT) {
            criteria1.andIsImportantEqualTo((byte) 1);
        }
        else if (flagResource.getFlag() == FAVORITE) {
            criteria1.andIsFavoriteEqualTo((byte) 1);
        }

        TodoExample.Criteria criteria2 = todoExample.createCriteria();
        criteria2.andUserIdEqualTo(SecurityUtils.getLoginUser(User.class).getId())
                .andIsCompletedEqualTo((byte) 0)
                .andEndDateIsNull();

        if (flagResource.getFlag() == IMPORTANT) {
            criteria2.andIsImportantEqualTo((byte) 1);
        }
        else if (flagResource.getFlag() == FAVORITE) {
            criteria2.andIsFavoriteEqualTo((byte) 1);
        }

        todoExample.or(criteria2);

        PageSortHelper.pageAndSort(page, TodoResource.class);
        List<Todo> todos = todoMapper.selectByExample(todoExample);
        return new PageInfo(todos);
    }

    @Override
    public PageInfo<List<Todo>> listHistoryTodo(PageParmResource page) {
        TodoExample todoExample = new TodoExample();
        TodoExample.Criteria criteria = todoExample.createCriteria();
        criteria.andUserIdEqualTo(SecurityUtils.getLoginUser(User.class).getId());
        PageSortHelper.pageAndSort(page, TodoResource.class);
        List<Todo> todos = todoMapper.selectByExample(todoExample);
        return new PageInfo(todos);
    }

    @Override
    public int addTodos(TodoBatchAddResource resource) {
        Todo newTodo = new Todo();
        newTodo.setTitle(resource.getTitle());
        newTodo.setContent(resource.getContent());
        return customTodoMapper.insertBatch(resource.getUserIds(), newTodo);
    }

    @Override
    public PageInfo<List<Todo>> searchTodosLike(String title, PageParmResource page) {
        TodoExample todoExample = new TodoExample();
        TodoExample.Criteria criteria = todoExample.createCriteria();
        criteria.andUserIdEqualTo(SecurityUtils.getLoginUser(User.class).getId())
                .andTitleLike("%" + title + "%");

        PageSortHelper.pageAndSort(page, TodoResource.class);
        List<Todo> todos = todoMapper.selectByExample(todoExample);
        return new PageInfo(todos);
    }
}
