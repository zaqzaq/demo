package cn.freshz.clazz.classloader;

import java.net.URL;
import java.net.URLClassLoader;

/**
 * Created by dell on 2017/6/29.
 */
public class MyCalssLoader extends URLClassLoader {
    public MyCalssLoader(URL[] urls, ClassLoader parent) {
        super(urls, parent);
    }
}
