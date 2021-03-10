package com.ruoyi.common.utils;

import org.jasypt.encryption.pbe.StandardPBEStringEncryptor;

import java.security.Key;

public class DESUtil {
    private static Key key;
    //自己的密钥
    private static String KEY_STR = "mykey";


    public static void main(String[] args) throws Exception {


        StandardPBEStringEncryptor standardPBEStringEncryptor = new StandardPBEStringEncryptor();
        /*配置文件中配置如下的算法*/
        standardPBEStringEncryptor.setAlgorithm("PBEWithMD5AndDES");
        /*配置文件中配置的password*/
        standardPBEStringEncryptor.setPassword("EWRREWRERWECCCXC");
        /*要加密的文本*/
        String name = standardPBEStringEncryptor.encrypt("kent_admin");
        String password = standardPBEStringEncryptor.encrypt("L!f2e#e_h3CNgVD");
        /*将加密的文本写到配置文件中*/
        System.out.println("name=" + name);
        System.out.println("password=" + password);
    }
}