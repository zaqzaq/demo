package cn.freshz.demo.structure;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * HASH MAP 结构 测试
 *
 * @author :<a href="mailto:yingjiezhang@ebnew.com">章英杰</a>
 * @date :2016-09-01 14:30:22
 */
public class HashMapTest {
    public static void main(String[] args) {
        HashMap<String,String> hashMap=new HashMap<>();

        hashMap.put("1","a");
        hashMap.put("2","b");
        hashMap.put("3","c");
        hashMap.put("4","d");

        Set<Map.Entry<String, String>> entries = hashMap.entrySet();

        for(Map.Entry<String, String> me:entries){

            System.out.println(me.getKey()+":"+me.getValue());

        }

    }
}
