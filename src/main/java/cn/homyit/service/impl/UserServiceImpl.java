package cn.homyit.service.impl;

import cn.homyit.dao.UserDao;
import cn.homyit.domain.Result;
import cn.homyit.domain.User;
import cn.homyit.service.UserService;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import org.springframework.beans.factory.annotation.Autowired;

public class UserServiceImpl implements UserService {
    @Autowired
    private UserDao userDao;

    @Override
    public Result login(User user) {


        return null;
    }

    @Override
    public Result logout(User user) {
        return null;
    }
}
