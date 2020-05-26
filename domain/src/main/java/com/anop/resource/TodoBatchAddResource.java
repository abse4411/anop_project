package com.anop.resource;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.NotNull;
import java.util.List;

/**
 * @author chocic
 */
@Data
@ApiModel(value = "批量添加待办事项请求体")
public class TodoBatchAddResource {

    @ApiModelProperty(value = "用户id列表", name = "userIds", example = "{1,2,3}")
    List<Integer> userIds;

    @NotNull
    @Length(min = 1, max = 15)
    @ApiModelProperty(value = "待办名称", name = "title", example = "我的待办")
    private String title;

    @NotNull
    @Length(max = 200)
    @ApiModelProperty(value = "待办内容", name = "content", example = "阿道夫就安分的链接克隆。。。。")
    private String content;
}
