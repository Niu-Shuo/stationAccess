package org.example.utils;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @Author : Niushuo
 * @Date : 2023-09-05-10:31
 * @Description :
 */
public class JsonSerializable {
    /* 将链表序列化为字符串存入json文件中 */
    public static String serializableForList(Object objList)
            throws IOException {

        return JSON.toJSONString(objList, true);
    }

    /* 将json文件中的内容读取出来，反序列化为链表 */
    public static <T> List<T> deserializableForListFromFile(String listString2, Class<T> clazz)
            throws IOException {

        return JSON.parseArray(listString2, clazz);
    }

    /* 将HashMap序列化为字符串存入json文件中 */
    public static String serializableForMap(Object objMap)
            throws IOException {

        return JSON.toJSONString(objMap, true);
    }

    /* 将json文件中的内容读取出来，反序列化为HashMap */
    public static <T, K> HashMap<K, T> deserializableForMapFromFile(String listString2, Class<T> clazz) throws IOException {

        Map<K, T> map = JSON.parseObject(listString2, new TypeReference<Map<K, T>>() {});

        return (HashMap<K, T>) map;
    }




    //使用方法 注意Entity为随机定义，使用时用自己的类名替换下就可以用了
	/*String pathName = "src/test/java/com/...../resources/file.json";
	List<Entity> entityList = new ArrayList<Entity>();
	JsonSerilizable.serilizableForList(entityList, pathName);
	List<Entity> entityList2 = JsonSerilizable
			.deserilizableForListFromFile(pathName, Entity.class);


	HashMap<Integer, Entity> Map = new HashMap<Integer, Entity>();
	JsonSerilizable.serilizableForMap(Map, pathName);
	HashMap<Integer, Entity> Map2 = JsonSerilizable
			.deserilizableForMapFromFile(pathName, Entity.class);*/
}
