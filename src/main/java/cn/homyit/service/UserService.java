package cn.homyit.service;

import cn.homyit.domain.Result;
import cn.homyit.domain.User;
import org.springframework.stereotype.Service;

@Service
public interface UserService {
    /*
    用户登录
     */
    Result login(User user);
    /*
    用户登出
     */
    Result logout(User user);



}
