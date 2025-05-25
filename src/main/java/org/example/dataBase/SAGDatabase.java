package org.example.dataBase;

import org.example.secureAccessGateway.SecureAccessGateway;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @Author : Niushuo
 * @Date : 2023-09-03-22:02
 * @Description :
 */
public class SAGDatabase {
    //接入网关名，接入网关作用域
    public Map<String, List<String>> SAGDatabase_ID_Scope = new HashMap<>();
    public Map<String, SecureAccessGateway> SAGDatabase_ID_SAG = new HashMap<>();
    public List<String> MTDatabase = Arrays.asList("NO1001","NO1002");
    public Map<String,List<String>> MTDatabase_ID_Scope = new HashMap<>();

}
