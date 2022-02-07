package org.apache.dubbo.demo.provider;

import com.huawei.dubbo.common.domain.User;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Random;

/**
 * @author luanwenfei
 * @version 0.0.1
 * @since 2021-05-13
 */
public class Custom {
    public List<Object> createList(User user){
        List<Object> userList = new ArrayList<>();
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
        userList.add(new User(getRandomString(6),"18", simpleDateFormat.format(new Date())));
        userList.add(new User(getRandomString(6),"18", simpleDateFormat.format(new Date())));
        userList.add(new User(getRandomString(6),"18",simpleDateFormat.format(new Date())));
        return userList;
    }

    public String getRandomString(int length){
        String str="abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
        Random random=new Random();
        StringBuffer sb=new StringBuffer();
        for(int i=0;i<length;i++){
            int number=random.nextInt(62);
            sb.append(str.charAt(number));
        }
        return sb.toString();
    }
}
