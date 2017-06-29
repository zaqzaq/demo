package cn.freshz.clazz.classloader;

import java.net.URL;

/**
 * 证明不同的类加载器 加载相同的 类，会各自加载.
 *
 * @author :<a href="mailto:yingjiezhang@ebnew.com">章英杰</a>
 * @date :2017-06-29 11:31:15
 */
public class Test {
    public static void main(String[] args) throws ClassNotFoundException {
        Class<?> aClass = Class.forName("cn.freshz.clazz.MyObject");

        System.out.println(aClass.getClassLoader());
        System.out.println(aClass.getClassLoader().getParent());

        URL[]  urls=new URL[1];
        urls[0]=aClass.getResource("/");

        System.out.println(urls[0]);
        Class<?> bClass = Class.forName("cn.freshz.clazz.MyObject",false,new MyCalssLoader(urls,aClass.getClassLoader().getParent()));

        System.out.println(bClass.getClassLoader());

        System.out.println(aClass==bClass);
    }
}
