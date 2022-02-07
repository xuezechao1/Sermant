package com.huawei.dubbo.common.api;


import com.huawei.dubbo.common.domain.User;

public interface GoodByeService {
    String sayBye(String name);

    User sayByeUser(User user);

    double count(double count);
}
