package com.z.module.system.service;

import com.z.module.system.domain.User;
import com.z.module.system.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @author zhaozhiwei
 * @version V1.0
 * @Title: null.java
 * @Package com.example.service
 * @Description:
 * jpa接口增加cacheable未生效, 所以增加UserService, 减少查库次数
 * @date 2022/3/24 下午4:45
 */
@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

    @Cacheable(cacheNames = "users")
    public User findById(long id) {
        return userRepository.findById(id).orElse(new User());
    }

    public User save(User userDTO) {
        return userRepository.save(userDTO);
    }

    public List<User> findAll() {
        return userRepository.findAll();
    }

    /**
     * @data: 2022/9/27-上午11:53
     * @User: zhaozhiwei
     * @method: findByLogin
      * @param username :
     * @return: com.longtu.domain.User
     * @Description: 根据登录名获取用户
     */
    public User findByLogin(String username) {
        return userRepository.findOneByLogin(username).orElse(new User());
    }
}
