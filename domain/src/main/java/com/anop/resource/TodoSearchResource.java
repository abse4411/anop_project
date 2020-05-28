package com.anop.resource;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.NotNull;
import java.io.Serializable;

/**
 * 搜索待办事项请求体
 *
 * @author chcic
 */
@Data
public class TodoSearchResource implements Serializable {
    @NotNull
    @Length(max = 15)
    @ApiModelProperty(value = "待办名称", name = "title", example = "我的待办")
    private String title = "";
}
