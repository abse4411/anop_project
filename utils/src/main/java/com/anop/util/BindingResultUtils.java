package com.anop.util;

import lombok.Data;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;

import java.util.ArrayList;
import java.util.List;

/**
 * 生成绑定错误结果列表工具
 *
 * @author Xue_Feng
 */
public class BindingResultUtils {
    public static List<BindingErrorMessage> getErrorList(BindingResult bindingResult) {
        List<BindingErrorMessage> errorList = new ArrayList<>(bindingResult.getFieldErrorCount());
        for (FieldError error : bindingResult.getFieldErrors()) {
            errorList.add(new BindingErrorMessage(error.getField(), error.getDefaultMessage()));
        }
        return errorList;
    }
}

@Data
class BindingErrorMessage {
    private String field;
    private String message;

    public BindingErrorMessage(String field, String message) {
        this.field = field;
        this.message = message;
    }
}
