package com.anop.resource;

import lombok.Data;
import org.hibernate.validator.constraints.Range;

import javax.validation.constraints.NotNull;

/**
 * 通知自动待办事项更新参数
 *
 * @author Xue_Feng
 */
@Data
public class AutoTodoResource {
    @NotNull
    @Range(min = 0, max = 1)
    private byte isAuto;
}
