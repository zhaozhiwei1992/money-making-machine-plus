package com.z.module.system.web.rest;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.util.StrUtil;
import com.z.module.system.domain.Authority;
import com.z.module.system.domain.User;
import com.z.module.system.repository.UserRepository;
import com.z.framework.common.repository.CommonSqlRepository;
import com.z.module.system.service.UserService;
import com.z.framework.common.web.rest.constants.ResponseCodeEnum;
import com.z.framework.common.web.rest.vm.ResponseData;
import com.z.module.system.web.vo.UserVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.*;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.*;
import java.util.stream.Collectors;

@Tag(name = "用户API")
@RestController
@RequestMapping("/api")
@Slf4j
@Transactional(rollbackFor = Exception.class)
public class UserResource {

    @Value("${z.app.name}")
    private String applicationName;

    private final UserService userService;

    private final UserRepository userRepository;

    private final PasswordEncoder passwordEncoder;

    private final CommonSqlRepository commonSqlRepository;

    public UserResource(UserService userService, UserRepository userRepository, PasswordEncoder passwordEncoder,
                        CommonSqlRepository commonSqlRepository) {
        this.userService = userService;
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.commonSqlRepository = commonSqlRepository;
    }

    /**
     * {@code POST  /admin/users}  : Creates a new user.
     * <p>
     * Creates a new user if the login and email are not already used, and sends an
     * mail with an activation link.
     * The user needs to be activated on creation.
     *
     * @param userVO the user to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new user, or with status {@code 400 (Bad Request)} if the login or email is already in use.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @Operation(description = "新增用户")
    @PostMapping("/users")
    public ResponseEntity<User> createUser(@RequestBody UserVO userVO) throws URISyntaxException {
        log.debug("REST request to save User : {}", userVO);

        if(Objects.isNull(userVO.getPassword())){
            userVO.setPassword("1");
        }

        if(!Objects.isNull(userVO.getId())){
            // #bug88302
            final User user = userRepository.findById(userVO.getId()).orElse(new User());
            if(!user.getPassword().equals(userVO.getPassword())){
                // 修改时判断是否与数据库中密码是否一致，不一致才进行加密
                String encryptedPassword = passwordEncoder.encode(userVO.getPassword());
                userVO.setPassword(encryptedPassword);
            }
        }else{
            // 新增密码全部加密
            String encryptedPassword = passwordEncoder.encode(userVO.getPassword());
            userVO.setPassword(encryptedPassword);
        }

        if(Objects.isNull(userVO.getId())){
            userVO.setActivated(true);
        }

        // 全部增加为经办
        final Authority authority = new Authority();
        authority.setCode("ROLE_USER");
        final HashSet<Authority> authorities = new HashSet<>();
        authorities.add(authority);

        final User user = new User();
        BeanUtil.copyProperties(userVO, user);
        user.setAppid("system");

        if(Objects.isNull(user.getId())){
            user.setAuthorities(authorities);
        }

        User newUser = userRepository.save(user);

        // 用户角色丢失特殊处理, 修改时setAuthorities失效
        if(!Objects.isNull(userVO.getId())){
            final ArrayList<Map<String, Object>> userAuthorities = new ArrayList<>();
            final Map<String, Object> userAuthority = new HashMap<>();
            userAuthority.put("user_id", userVO.getId());
            userAuthority.put("authority_code", userVO.getRole());
            userAuthorities.add(userAuthority);
            commonSqlRepository.deleteBySql("t_user_authority",  " where user_id = '" + userVO.getId()+ "'");
            commonSqlRepository.insertDatas("t_user_authority", userAuthorities);
        }

        return ResponseEntity
                .created(new URI("/api/users/" + newUser.getLogin()))
                .body(newUser);
    }

    /**
     * {@code GET /admin/users} : get all users with all the details - calling this are only allowed for the administrators.
     *
     * @param pageable the pagination information.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body all users.
     */

    @Operation(description = "获取用户")
    @GetMapping("/users")
    public ResponseData<List<User>> getAllUsers(Pageable pageable, String key) {
        log.debug("REST request to get all User for an admin");

//        final List<User> all = userRepository.findAll();
        // 根据id, 升序
        Sort sort = Sort.by(Sort.Direction.ASC, "id");
        // 分页
        pageable = PageRequest.of(pageable.getPageNumber()-1, pageable.getPageSize(), sort);

        Page<User> userPage;
        // 搜索
        if(StrUtil.isNotEmpty(key)){
            final User user = new User();
            final List<String> cols = Arrays.asList("name", "login", "email");
            //      2. 将传入属性, 填充给界面显示字段
            final Map<String, String> map = cols.stream().collect(Collectors.toMap(s -> s, key2 -> key));
            //      3. 动态构建查询条件
            BeanUtil.fillBeanWithMap(map, user, true);
            log.info("填充后对象信息 {}", user);

            //创建匹配器，即如何使用查询条件
            //构建对象
            ExampleMatcher matcher = ExampleMatcher
                    .matchingAny()
                    //改变默认字符串匹配方式：模糊查询
                    .withStringMatcher(ExampleMatcher.StringMatcher.CONTAINING)
                    //改变默认大小写忽略方式：忽略大小写
                    .withIgnoreCase(true)
                    //名字采用“开始匹配”的方式查询
                    .withMatcher("name", ExampleMatcher.GenericPropertyMatchers.startsWith())
                    //忽略属性：是否关注。因为是基本类型，需要忽略掉
                    .withIgnorePaths("id");

            //创建实例
            Example<User> ex = Example.of(user, matcher);
            userPage = userRepository.findAll(ex, pageable);
        }else{
            userPage = userRepository.findAll(pageable);
        }

        final ResponseData<List<User>> listResponseData = new ResponseData<>();
        listResponseData.setData(userPage.getContent());
        listResponseData.setCode("0");
        listResponseData.setCount(Long.valueOf(userPage.getTotalElements()).intValue());
        return listResponseData;
    }

    @Operation(description = "删除用户")
    @DeleteMapping("/users")
    public ResponseData<String> deleteUser(@RequestBody List<Long> idList) {
        log.debug("REST request to delete Examples, ids: {}", idList);
        this.userRepository.deleteAllByIdIn(idList);
        final ResponseData<String> responseData = new ResponseData<>();
        responseData.setCode(ResponseCodeEnum.SUCCESS.getCode());
        responseData.setMsg(ResponseCodeEnum.SUCCESS.getMsg());
        return responseData;
    }
}
