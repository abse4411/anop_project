package com.anop.resource;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.NotNull;
import java.io.Serializable;

/**
 * 搜索分类请求体
 *
 * @author chcic
 */
@Data
public class CategorySearchResource implements Serializable {
    @NotNull
    @Length(max = 15)
    @ApiModelProperty(value = "分类名称", name = "typeName", example = "我的分类")
    private String typeName = "";
}
