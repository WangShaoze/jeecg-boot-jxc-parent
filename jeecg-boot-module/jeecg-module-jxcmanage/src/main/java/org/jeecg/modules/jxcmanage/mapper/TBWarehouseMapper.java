package org.jeecg.modules.jxcmanage.mapper;

import java.util.Date;
import java.util.List;

import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Param;
import org.jeecg.modules.jxcmanage.entity.TBWarehouse;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;

/**
 * @Description: 仓库管理
 * @Author: jeecg-boot
 * @Date: 2025-09-15
 * @Version: V1.0
 */
public interface TBWarehouseMapper extends BaseMapper<TBWarehouse> {

    @Insert("INSERT INTO sys_user (id, username, realname, phone, `password`, salt, create_by, create_time, del_flag, status, activiti_sync, user_identity, login_tenant_id)\n" +
            "VALUES(#{id}, #{username}, #{realname},#{phone}, #{password}, #{salt}, #{createBy}, #{createTime}, 0, 1, 1, 1, 0);")
    int registerSysUser(
            @Param("id") String id,
            @Param("username") String username,
            @Param("realname") String realname,
            @Param("phone") String phone,
            @Param("password") String password,
            @Param("salt") String salt,
            @Param("createBy") String createBy,
            @Param("createTime") Date createTime
    );


    @Insert("INSERT INTO `sys_user_role` (`id`, `user_id`, `role_id`, `tenant_id`) " +
            "VALUES (#{id}, #{userId}, #{roleId}, 0);")
    int registerUserRole(
            @Param("id") String id,
            @Param("userId") String userId,
            @Param("roleId") String roleId
    );

}
