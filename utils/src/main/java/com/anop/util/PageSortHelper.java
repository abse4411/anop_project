package com.anop.util;

import com.anop.resource.PageParmResource;
import com.github.pagehelper.PageHelper;

/**
 * 分页帮助类
 *
 * @author Xue_Feng
 */
public class PageSortHelper {
    public static void pageAndSort(PageParmResource pageParm, Class<?> sortClass) {
        String orderBy = pageParm.getOrderBy();
        if (!StringUtils.isNullOrWhiteSpace(orderBy)) {
            orderBy = pageParm.getOrderBy().trim();
            String[] strings = orderBy.split("\\s+");
            if (strings.length > 0) {
                if (PropertyMapperUtils.hasProperty(strings[0], sortClass)) {
                    String property = PropertyMapperUtils.getUnderscoreFormat(strings[0]);
                    if (strings.length > 1) {
                        PageHelper.startPage(pageParm.getPageNum(), pageParm.getPageSize(), property + " " + strings[1]);
                    } else {
                        PageHelper.startPage(pageParm.getPageNum(), pageParm.getPageSize(), property);
                    }
                    return;
                }
            }
        }
        PageHelper.startPage(pageParm.getPageNum(), pageParm.getPageSize());
    }
}
