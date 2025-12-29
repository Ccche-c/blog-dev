package com.shiyi.mapper;

import com.shiyi.entity.UserInfo;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * <p>
 *  Mapper 接口
 * </p>
 *
 * @author blue
 * @since 2021-12-25
 */
@Repository
public interface UserInfoMapper extends BaseMapper<UserInfo> {

    void deleteByUserIds(@Param("ids") List<Integer> ids);

    /**
     * 根据用户id获取用户信息
     * @param loginId 用户id
     * @return
     */
    UserInfo getByUserId(Object loginId);

}
