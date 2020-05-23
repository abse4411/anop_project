package com.anop.resource;

import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.NotNull;

/**
 * 更新密码资源类
 *
 * @author SilverBay
 */
public class PasswordUpdateResource {

    @NotNull
    @Length(min = 1, max = 255)
    private String oldPassword;

    @NotNull
    @Length(min = 1, max = 255)
    private String newPassword;

    public String getOldPassword() {
        return oldPassword;
    }

    public void setOldPassword(String oldPassword) {
        this.oldPassword = oldPassword;
    }

    public String getNewPassword() {
        return newPassword;
    }

    public void setNewPassword(String newPassword) {
        this.newPassword = newPassword;
    }

}
