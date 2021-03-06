package com.anop.util;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;

/**
 * 登录用户帮助工具
 *
 * @author Xue_Feng
 */
public class SecurityUtils {
    public static <T extends UserDetails> T getLoginUser(Class<T> tClass) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        Object principal = authentication.getPrincipal();
        if (principal.getClass().equals(tClass)) {
            return (T) principal;
        }
        return null;
    }
}
