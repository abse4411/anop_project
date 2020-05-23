package com.anop.resource;

import org.hibernate.validator.constraints.Range;

import javax.validation.constraints.NotNull;
import java.io.Serializable;

/**
 * 通知群组用户更新资源
 *
 * @author Xue_Feng
 */
public class GroupUserUpdateResource implements Serializable {
    @NotNull
    @Range(min = 0, max = 1)
    private Byte isAdmin;

    public Byte getIsAdmin() {
        return isAdmin;
    }

    public void setIsAdmin(Byte isAdmin) {
        this.isAdmin = isAdmin;
    }
}
