package cn.homyit.service.impl;

import cn.homyit.utils.SecurityUtils;
import org.springframework.stereotype.Service;

@Service
public class AdminService {

    public boolean isAdmin(){
        return SecurityUtils.isAdmin();
    }
}
