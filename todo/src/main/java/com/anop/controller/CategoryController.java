package com.anop.controller;

import com.anop.pojo.Category;
import com.anop.pojo.security.User;
import com.anop.resource.*;
import com.anop.service.CategoryService;
import com.anop.util.JsonResult;
import com.anop.util.Message;
import com.anop.util.SecurityUtils;
import com.github.pagehelper.PageInfo;
import io.swagger.annotations.*;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.validation.Valid;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;

/**
 * @author chcic
 */
@Api(tags = "待办事项分类")
@RestController
@RequestMapping("/categories")
public class CategoryController {

    @Resource(name = "categoryServiceImpl")
    CategoryService categoryService;

    @ApiOperation(value = "添加待办事项分类", notes = "添加待办事项分类")
    @ApiResponses({
            @ApiResponse(code = 201, message = "成功创建", response = Category.class),
            @ApiResponse(code = 422, message = "参数未通过验证", response = Message.class)
    })
    @PostMapping()
    public Object addCateGory(
            @RequestBody @Valid CategoryAddResource resource) throws URISyntaxException {
        Category category = categoryService.addCategory(resource);
        return JsonResult.created(new URI("http://localhost:8080/v1/categories/" + category.getId())).body(category);
    }

    @ApiOperation(value = "获取当前用户的分类列表", notes = "获取当前用户的分类列表")
    @ApiResponses({
            @ApiResponse(code = 200, message = "成功获取", response = PageInfo.class),
            @ApiResponse(code = 422, message = "分页参数验证错误", response = Message.class)
    })
    @GetMapping()
    public Object listCategories(@Valid CategorySearchResource searchResource, @Valid PageParmResource page) {

        return JsonResult.ok(categoryService.listCategories(page, searchResource));
    }

    @ApiOperation(value = "获取当前用户的所有分类", notes = "获取当前用户的所有分类")
    @ApiResponses({
            @ApiResponse(code = 200, message = "成功获取", response = List.class),
            @ApiResponse(code = 422, message = "参数未通过验证", response = Message.class)
    })
    @GetMapping("/all")
    public Object listAllCategories() {
        return JsonResult.ok(categoryService.listAllCategories());
    }

    @ApiOperation(value = "获取指定id的分类的基本信息", notes = "获取指定id的分类的基本信息")
    @ApiResponses({
            @ApiResponse(code = 200, message = "成功获取", response = Category.class),
            @ApiResponse(code = 404, message = "未找到指定id的分类", response = Message.class),
            @ApiResponse(code = 403, message = "没有此分类的访问权限", response = Message.class)
    })
    @GetMapping("/{id}")
    public Object getCategories(@PathVariable int id) {
        Category category = categoryService.getCategory(id);
        if (category == null) {
            return JsonResult.notFound("category was not found", null);
        }
        if (!category.getUserId().equals(SecurityUtils.getLoginUser(User.class).getId())) {
            return JsonResult.forbidden("you have no permission to get this category", null);
        }
        return JsonResult.ok(category);
    }

    @ApiOperation(value = "更新指定id的分类", notes = "更新指定id的分类")
    @ApiResponses({
            @ApiResponse(code = 201, message = "更新成功"),
            @ApiResponse(code = 404, message = "未找到指定id的分类", response = Message.class),
            @ApiResponse(code = 403, message = "没有此分类的访问权限", response = Message.class),
            @ApiResponse(code = 422, message = "参数未通过验证", response = Message.class)
    })
    @PutMapping("/{id}")
    public Object updateCategories(
            @PathVariable int id,
            @RequestBody @Valid CategoryUpdateResource resource) {
        Category category = categoryService.getCategory(id);
        if (category == null) {
            return JsonResult.notFound("category was not found", null);
        }
        int result = categoryService.updateCategory(category, resource);
        if (result == -1) {
            return JsonResult.forbidden("you have no permission to modify this category", null);
        }
        return JsonResult.noContent().build();
    }

    @ApiOperation(value = "删除指定id的分类", notes = "删除操作同时会将分类所属所有的待办事项删除！！")
    @ApiResponses({
            @ApiResponse(code = 204, message = "删除成功"),
            @ApiResponse(code = 404, message = "未找到指定id的分类", response = Message.class),
            @ApiResponse(code = 403, message = "没有此分类的访问权限", response = Message.class)
    })
    @DeleteMapping("/{id}")
    public Object deleteCategories(@PathVariable int id) {
        Category category = categoryService.getCategory(id);
        if (category == null) {
            return JsonResult.notFound("category was not found", null);
        }
        int result = categoryService.deleteCategory(id);
        if (result == -1) {
            return JsonResult.forbidden("you have no permission to delete this category", null);
        }
        return JsonResult.noContent().build();
    }

    @ApiOperation(value = "获取指定id的分类下的所有待办事项", notes = "获取指定id的分类下的所有待办事项")
    @ApiImplicitParams(
            @ApiImplicitParam(name = "categoryId", value = "分类id", dataType = "int")
    )
    @ApiResponses({
            @ApiResponse(code = 200, message = "成功获取", response = PageInfo.class),
            @ApiResponse(code = 422, message = "分页参数验证错误", response = Message.class),
            @ApiResponse(code = 404, message = "未找到指定id的分类", response = Message.class),
            @ApiResponse(code = 403, message = "没有此分类的访问权限", response = Message.class)
    })
    @GetMapping("/list/{categoryId}")
    public Object getTodoByCategoryId(@Valid TodoSearchResource searchResource, @PathVariable int categoryId, @Valid PageParmResource page) {
        Category category = categoryService.getCategory(categoryId);
        if (category == null) {
            return JsonResult.notFound("category was not found", null);
        }
        if (!category.getUserId().equals(SecurityUtils.getLoginUser(User.class).getId())) {
            return JsonResult.forbidden("you have no permission to get the todos of this category", null);
        }
        return JsonResult.ok(categoryService.listTodoByCategoryId(categoryId, page, searchResource));
    }
}
