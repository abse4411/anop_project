package com.anop.service.impl;

import com.anop.mapper.CategoryMapper;
import com.anop.mapper.CustomCategoryMapper;
import com.anop.mapper.TodoMapper;
import com.anop.pojo.Category;
import com.anop.pojo.Todo;
import com.anop.pojo.example.TodoExample;
import com.anop.pojo.security.User;
import com.anop.resource.*;
import com.anop.service.CategoryService;
import com.anop.util.PageSortHelper;
import com.anop.util.PropertyMapperUtils;
import com.anop.util.SecurityUtils;
import com.github.pagehelper.PageInfo;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.List;

/**
 * @author chcic
 */
@Service
@Transactional(rollbackFor = Throwable.class)
public class CategoryServiceImpl implements CategoryService {

    @Resource
    CategoryMapper categoryMapper;

    @Resource
    CustomCategoryMapper customCategoryMapper;

    @Resource
    TodoMapper todoMapper;

    @Override
    public Category addCategory(CategoryAddResource resource) {
        Category category = PropertyMapperUtils.map(resource, Category.class);
        category.setUserId(SecurityUtils.getLoginUser(User.class).getId());
        categoryMapper.insert(category);
        return category;
    }

    @Override
    public PageInfo<List<CategoryListResource>> listCategories(PageParmResource page) {
        PageSortHelper.pageAndSort(page, CategoryListResource.class);
        List<CategoryListResource> categories = customCategoryMapper.listCategories(
                SecurityUtils.getLoginUser(User.class).getId()
        );
        return new PageInfo(categories);
    }

    @Override
    public Category getCategory(Integer id) {
        return categoryMapper.selectByPrimaryKey(id);
    }

    @Override
    public int updateCategory(Category oldCategory, CategoryUpdateResource resource) {

        if (!oldCategory.getUserId().equals(SecurityUtils.getLoginUser(User.class).getId())) {
            return -1;
        }

        Category newCategory = PropertyMapperUtils.map(resource, Category.class);
        newCategory.setId(oldCategory.getId());
        return categoryMapper.updateByPrimaryKeySelective(newCategory);
    }

    @Override
    public int deleteCategory(Integer id) {

        Category category = categoryMapper.selectByPrimaryKey(id);

        if (!category.getUserId().equals(SecurityUtils.getLoginUser(User.class).getId())) {
            return -1;
        }

        return categoryMapper.deleteByPrimaryKey(id);
    }

    @Override
    public List<CategoryListResource> listAllCategories() {
        int userId = SecurityUtils.getLoginUser(User.class).getId();
        List<CategoryListResource> categories = customCategoryMapper.listCategories(userId);
        return categories;
    }

    @Override
    public PageInfo<List<TodoResource>> listTodoByCategoryId(Integer categoryId, PageParmResource page) {
        TodoExample todoExample = new TodoExample();

        TodoExample.Criteria criteria1 = todoExample.createCriteria();
        criteria1.andUserIdEqualTo(SecurityUtils.getLoginUser(User.class).getId())
                .andCategoryIdEqualTo(categoryId);

        PageSortHelper.pageAndSort(page, TodoResource.class);
        List<Todo> todos = todoMapper.selectByExample(todoExample);
        return new PageInfo(todos);
    }
}
