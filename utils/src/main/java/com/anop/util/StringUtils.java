package com.anop.util;

public class StringUtils {
    public static boolean isNullOrWhiteSpace(String str){
        if(str==null){
            return true;
        }
        str=str.trim();
        return str.length()==0;
    }
}