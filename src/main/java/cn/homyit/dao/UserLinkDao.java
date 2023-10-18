package cn.homyit.dao;

import cn.homyit.domain.UserLink;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface UserLinkDao extends BaseMapper<UserLink> {
}
