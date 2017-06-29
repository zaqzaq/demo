package cn.freshz.clazz.bean;

import java.beans.Introspector;

/**
 * Introspector 工具类 的用法 .
 *
 * @author :<a href="mailto:yingjiezhang@ebnew.com">章英杰</a>
 * @date :2017-06-29 11:35:17
 */
public class Test {
    public static void main(String[] args) {

        System.out.println(Introspector.decapitalize("SyncCompany_Update"));
        System.out.println(Introspector.decapitalize("Sync_COMPANY_UPDATE"));
    }
}
