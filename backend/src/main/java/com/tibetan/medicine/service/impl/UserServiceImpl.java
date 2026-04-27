package com.tibetan.medicine.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.tibetan.medicine.entity.User;
import com.tibetan.medicine.mapper.UserMapper;
import com.tibetan.medicine.service.UserService;
import com.tibetan.medicine.util.JwtUtil;
import org.springframework.stereotype.Service;

/**
 * 用户服务实现类
 */
@Service
public class UserServiceImpl extends ServiceImpl<UserMapper, User> implements UserService {
    
    @Override
    public User getUserByOpenId(String openId) {
        LambdaQueryWrapper<User> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(User::getOpenid, openId);
        return this.getOne(wrapper);
    }
    
    @Override
    public String login(String openId) {
        // 查询用户是否存在（openId 由调用方提供，普通登录传微信openId，测试登录传固定值）
        User user = getUserByOpenId(openId);
        if (user == null) {
            // 新用户，创建账号
            user = new User();
            user.setOpenid(openId);
            user.setNickname("新用户");
            user.setStatus(0); // 0=正常
            this.save(user);
            // save 后 MyBatis Plus 会回填 id，重新查询确保 id 不为 null
            if (user.getId() == null) {
                user = getUserByOpenId(openId);
            }
        }
        if (user == null || user.getId() == null) {
            throw new RuntimeException("用户创建失败，请重试");
        }
        // 生成 token
        return JwtUtil.generateToken(user.getId());
    }
    
    @Override
    public User getUserInfo(Long userId) {
        return this.getById(userId);
    }
    
    @Override
    public void updateUserInfo(Long userId, String nickname, String phone, String avatar) {
        User user = this.getById(userId);
        if (user != null) {
            if (nickname != null && !nickname.isEmpty()) {
                user.setNickname(nickname);
            }
            if (phone != null && !phone.isEmpty()) {
                user.setPhone(phone);
            }
            if (avatar != null && !avatar.isEmpty()) {
                user.setAvatar(avatar);
            }
            this.updateById(user);
        }
    }
}
