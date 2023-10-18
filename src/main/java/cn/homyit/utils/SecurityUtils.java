package cn.homyit.utils;

import cn.homyit.domain.LoginUser;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

public class SecurityUtils
{

    /**
     * 获取用户
     **/
    public static LoginUser getLoginUser()
    {
        return (LoginUser) getAuthentication().getPrincipal();
    }

    /**
     * 获取Authentication
     */
    public static Authentication getAuthentication() {
        return SecurityContextHolder.getContext().getAuthentication();
    }

    public static Boolean isAdmin(){
        Integer id = getLoginUser().getUser().getId();
        Integer type=getLoginUser().getUser().getType();
        return id != null && type==0;
    }

    public static Integer getUserId() {
        return getLoginUser().getUser().getId();
    }
}

