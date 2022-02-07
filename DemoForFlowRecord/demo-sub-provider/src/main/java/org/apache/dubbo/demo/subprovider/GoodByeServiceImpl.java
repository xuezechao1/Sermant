package org.apache.dubbo.demo.subprovider;

import com.huawei.dubbo.common.api.GoodByeService;
import com.huawei.dubbo.common.domain.User;

/**
 * @author luanwenfei
 * @version 0.0.1
 * @since 2021-04-16
 */
public class GoodByeServiceImpl implements GoodByeService {
    @Override
    public String sayBye(String name) {
        System.out.println(name + ":bye!");
        return name + ":bye!";
    }

    @Override
    public User sayByeUser(User user) {
        user.setName(user.getName() + ":bye!");
        System.out.println(user);
        return user;
    }

    @Override
    public double count(double count) {
        count += Math.random();
        System.out.println(count);
        return count;
    }
}
