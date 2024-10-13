package com.z.module.system.web.rest;

import cn.hutool.core.bean.BeanUtil;
import com.z.framework.common.repository.CommonSqlRepository;
import com.z.framework.security.util.SecurityUtils;
import com.z.module.system.domain.User;
import com.z.module.system.domain.UserAuthority;
import com.z.module.system.domain.UserDepartment;
import com.z.module.system.domain.UserPosition;
import com.z.module.system.repository.*;
import com.z.module.system.service.UserService;
import com.z.module.system.web.vo.PasswordResetVO;
import com.z.module.system.web.vo.UserVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.*;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.net.URISyntaxException;
import java.util.*;
import java.util.stream.Collectors;


@Tag(name = "用户API")
@RestController
@RequestMapping("/system")
@Slf4j
public class UserResource {

    @Value("${z.app.name}")
    private String applicationName;

    private final UserService userService;

    private final UserRepository userRepository;

    private final PasswordEncoder passwordEncoder;

    private final CommonSqlRepository commonSqlRepository;

    private final UserAuthorityRepository userAuthorityRepository;

    private final UserPositionRepository userPositionRepository;

    private final UserDepartmentRepository userDepartmentRepository;

    public UserResource(UserService userService, UserRepository userRepository, PasswordEncoder passwordEncoder,
                        CommonSqlRepository commonSqlRepository, UserAuthorityRepository userAuthorityRepository,
                        UserPositionRepository userPositionRepository,
                        UserDepartmentRepository userDepartmentRepository, PositionRepository positionRepository, DepartmentRepository departmentRepository) {
        this.userService = userService;
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.commonSqlRepository = commonSqlRepository;
        this.userAuthorityRepository = userAuthorityRepository;
        this.userPositionRepository = userPositionRepository;
        this.userDepartmentRepository = userDepartmentRepository;
        this.positionRepository = positionRepository;
        this.departmentRepository = departmentRepository;
    }

    /**
     * {@code POST  /admin/users}  : Creates a new user.
     * <p>
     * Creates a new user if the login and email are not already used, and sends an
     * mail with an activation link.
     * The user needs to be activated on creation.
     *
     * @param userVO the user to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new user, or with
     * status {@code 400 (Bad Request)} if the login or email is already in use.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @Operation(description = "新增用户")
    @PostMapping("/users")
    @Transactional(rollbackFor = Exception.class)
    @PreAuthorize("hasAuthority('system:user:add')")
    public User createUser(@RequestBody UserVO userVO) throws URISyntaxException {
        log.debug("REST request to save User : {}", userVO);

        if (Objects.isNull(userVO.getPassword())) {
            userVO.setPassword("1");
        }

        if (!Objects.isNull(userVO.getId())) {
            // #bug88302
            final User user = userRepository.findById(userVO.getId()).orElse(new User());
            if (!user.getPassword().equals(userVO.getPassword())) {
                // 修改时判断是否与数据库中密码是否一致，不一致才进行加密
                String encryptedPassword = passwordEncoder.encode(userVO.getPassword());
                userVO.setPassword(encryptedPassword);
            }
        } else {
            // 新增密码全部加密
            String encryptedPassword = passwordEncoder.encode(userVO.getPassword());
            userVO.setPassword(encryptedPassword);
        }

        if (Objects.isNull(userVO.getId())) {
            userVO.setActivated(true);
        }

        final User user = new User();
        BeanUtil.copyProperties(userVO, user);

        User newUser = userRepository.save(user);

        // 保存用户角色信息
        final String roleIdListStr = userVO.getRoleIdListStr();
        final List<Long> roleIdList = Arrays.stream(roleIdListStr.split(",")).map(Long::valueOf).collect(Collectors.toList());
        final List<UserAuthority> userAuthorities = roleIdList.stream().map(roleId -> {
            final UserAuthority userAuthority = new UserAuthority();
            userAuthority.setRoleId(roleId);
            userAuthority.setUserId(newUser.getId());
            return userAuthority;
        }).collect(Collectors.toList());
        userAuthorityRepository.saveAll(userAuthorities);

        // 保存用户岗位信息
        final String positionIdListStr = userVO.getPositionIdListStr();
        final List<Long> positionIdList = Arrays.stream(positionIdListStr.split(",")).map(Long::valueOf).collect(Collectors.toList());
        final List<UserPosition> userPositionList = positionIdList.stream().map(positionId -> {
            final UserPosition userPosition = new UserPosition();
            userPosition.setPositionId(positionId);
            userPosition.setUserId(newUser.getId());
            return userPosition;
        }).collect(Collectors.toList());
        userPositionRepository.saveAll(userPositionList);

        // 保存用户部门信息
        final String departmentIdListStr = userVO.getDepartmentIdListStr();
        final List<Long> departmentIdList = Arrays.stream(departmentIdListStr.split(",")).map(Long::valueOf).collect(Collectors.toList());
        final List<UserDepartment> userDepartmentList = departmentIdList.stream().map(departmentId -> {
            final UserDepartment userDepartment = new UserDepartment();
            userDepartment.setDeptId(departmentId);
            userDepartment.setUserId(newUser.getId());
            return userDepartment;
        }).collect(Collectors.toList());
        userDepartmentRepository.saveAll(userDepartmentList);

        return newUser;
    }

    private final PositionRepository positionRepository;

    private final DepartmentRepository departmentRepository;

    /**
     * {@code GET /admin/users} : get all users with all the details - calling this are only allowed for the
     * administrators.
     *
     * @param pageable the pagination information.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body all users.
     */

    @Operation(description = "获取用户")
    @GetMapping("/users")
    @PreAuthorize("hasAuthority('system:user:view')")
    public HashMap<String, Object> getAllUsers(Pageable pageable, User user) {
        log.debug("REST request to get all User for an admin");

//        final List<User> all = userRepository.findAll();
        // 根据id, 升序
        Sort sort = Sort.by(Sort.Direction.ASC, "id");
        // 分页
        pageable = PageRequest.of(pageable.getPageNumber() - 1, pageable.getPageSize(), sort);

        Page<User> userPage;

        //创建匹配器，即如何使用查询条件
        //构建对象
        ExampleMatcher matcher = ExampleMatcher
                .matchingAll()
                //改变默认字符串匹配方式：模糊查询
                .withStringMatcher(ExampleMatcher.StringMatcher.CONTAINING)
                //改变默认大小写忽略方式：忽略大小写
                .withIgnoreCase(true)
                .withIgnoreNullValues()
                //名字采用“开始匹配”的方式查询
                .withMatcher("name", ExampleMatcher.GenericPropertyMatchers.startsWith())
                //忽略属性：是否关注。因为是基本类型，需要忽略掉
                .withIgnorePaths("id", "createdDate", "lastModifiedDate", "activated");

        //创建实例
        Example<User> ex = Example.of(user, matcher);
        userPage = userRepository.findAll(ex, pageable);
        final List<User> content = userPage.getContent();

        // 获取岗位信息
        List<UserPosition> userPositionList =
                userPositionRepository.findAllByUserIdIn(content.stream().map(User::getId).collect(Collectors.toList()));
        Map<Long, List<UserPosition>> userPositionGroupByUserId;
        if (!userPositionList.isEmpty()) {
            userPositionGroupByUserId =
                    userPositionList.stream().collect(Collectors.groupingBy(UserPosition::getUserId));
        } else {
            userPositionGroupByUserId = Collections.emptyMap();
        }

        // 获取部门信息
        List<UserDepartment> userDepartmentList =
                userDepartmentRepository.findAllByUserIdIn(content.stream().map(User::getId).collect(Collectors.toList()));
        Map<Long, List<UserDepartment>> userDepartmentGroupByUserId;
        if(!userDepartmentList.isEmpty()){
            userDepartmentGroupByUserId = userDepartmentList.stream().collect(Collectors.groupingBy(UserDepartment::getUserId));
        } else {
            userDepartmentGroupByUserId = Collections.emptyMap();
        }

        // 获取角色信息
        List<UserAuthority> userAuthorityList =
                userAuthorityRepository.findAllByUserIdIn(content.stream().map(User::getId).collect(Collectors.toList()));
        Map<Long, List<UserAuthority>> userAuthorityGroupByUserId;
        if(!userDepartmentList.isEmpty()){
            userAuthorityGroupByUserId = userAuthorityList.stream().collect(Collectors.groupingBy(UserAuthority::getUserId));
        } else {
            userAuthorityGroupByUserId = Collections.emptyMap();
        }

        final List<UserVO> collect = content.stream().map(u -> {
            final UserVO userVO = new UserVO();
            BeanUtils.copyProperties(u, userVO);
            if(!userPositionGroupByUserId.isEmpty() && !Objects.isNull(userPositionGroupByUserId.get(u.getId()))){
                userVO.setPositionIdListStr(userPositionGroupByUserId.get(u.getId()).stream().map(UserPosition::getPositionId).map(String::valueOf).collect(Collectors.joining(",")));
            }
            if(!userDepartmentGroupByUserId.isEmpty() && !Objects.isNull(userDepartmentGroupByUserId.get(u.getId()))){
                userVO.setDepartmentIdListStr(userDepartmentGroupByUserId.get(u.getId()).stream().map(UserDepartment::getDeptId).map(String::valueOf).collect(Collectors.joining(",")));
            }
            if(!userAuthorityGroupByUserId.isEmpty() && !Objects.isNull(userAuthorityGroupByUserId.get(u.getId()))){
                userVO.setRoleIdListStr(userAuthorityGroupByUserId.get(u.getId()).stream().map(UserAuthority::getRoleId).map(String::valueOf).collect(Collectors.joining(",")));
            }
            return userVO;
        }).collect(Collectors.toList());

        return new HashMap<String, Object>() {{
            put("list", collect);
            put("total", Long.valueOf(userPage.getTotalElements()).intValue());
        }};
    }

    @Operation(description = "删除用户")
    @DeleteMapping("/users")
    @Transactional(rollbackFor = Exception.class)
    @PreAuthorize("hasAuthority('system:user:delete')")
    public String deleteUser(@RequestBody List<Long> idList) {
        log.debug("REST request to delete Examples, ids: {}", idList);
        this.userRepository.deleteAllByIdIn(idList);
        return "success";
    }

    @Operation(description = "获取所有用户信息")
    @GetMapping("/users/all")
    @PreAuthorize("hasAuthority('system:user:view')")
    public List<Map<String, Object>> getAllDictList() {
        final List<User> all = userRepository.findAll();
        final List<Map<String, Object>> resultMap = all.stream().map(m -> {
            Map<String, Object> map = new HashMap<>();
            map.put("id", m.getId());
            map.put("username", m.getLogin());
            map.put("nickname", m.getName());
            return map;
        }).collect(Collectors.toList());
        return resultMap;
    }

    @Operation(description = "重置密码")
    @PostMapping("/users/resetpass")
    @Transactional(rollbackFor = Exception.class)
    public User resetPassword(@RequestBody PasswordResetVO passwordResetVO) throws URISyntaxException {
        log.debug("REST request to save User : {}", passwordResetVO);

        // 1. 获取用户
        final Optional<User> oneByLogin = userRepository.findOneByLogin(SecurityUtils.getCurrentLoginName());
        // 2. 校验密码
        final User user = oneByLogin.get();
        if (!passwordEncoder.matches(passwordResetVO.getOldPassword(), user.getPassword())) {
            throw new RuntimeException("旧密码不正确");
        }
        // 3. 填充用户并保存
        final String newPassword = passwordResetVO.getNewPassword();
        final String encode = passwordEncoder.encode(newPassword);
        user.setPassword(encode);
        User newUser = userRepository.save(user);
        return newUser;
    }

    @Operation(description = "获取用户")
    @GetMapping("/users/detail/login")
    @PreAuthorize("hasAuthority('system:user:view')")
    public UserVO getUserDetailByLogin(String login) {
        log.debug("REST request to get all User for an admin");

        final Optional<User> oneByLogin = userRepository.findOneByLogin(login);

        if(oneByLogin.isPresent()){
            final User user = oneByLogin.get();
            // 获取岗位信息
            List<UserPosition> userPositionList =
                    userPositionRepository.findAllByUserIdIn(Arrays.asList(user.getId()));
            Map<Long, List<UserPosition>> userPositionGroupByUserId;
            if(!userPositionList.isEmpty()){
                userPositionGroupByUserId = userPositionList.stream().collect(Collectors.groupingBy(UserPosition::getUserId));
            }else {
                userPositionGroupByUserId = Collections.emptyMap();
            }

            // 获取部门信息
            List<UserDepartment> userDepartmentList =
                    userDepartmentRepository.findAllByUserIdIn(Arrays.asList(user.getId()));
            Map<Long, List<UserDepartment>> userDepartmentGroupByUserId;
            if(!userDepartmentList.isEmpty()){
                userDepartmentGroupByUserId = userDepartmentList.stream().collect(Collectors.groupingBy(UserDepartment::getUserId));
            } else {
                userDepartmentGroupByUserId = Collections.emptyMap();
            }

            // 获取角色信息
            List<UserAuthority> userAuthorityList =
                    userAuthorityRepository.findAllByUserIdIn(Arrays.asList(user.getId()));
            Map<Long, List<UserAuthority>> userAuthorityGroupByUserId;
            if(!userDepartmentList.isEmpty()){
                userAuthorityGroupByUserId = userAuthorityList.stream().collect(Collectors.groupingBy(UserAuthority::getUserId));
            } else {
                userAuthorityGroupByUserId = Collections.emptyMap();
            }

            final UserVO userVO = new UserVO();
            BeanUtils.copyProperties(user, userVO);
            if(!userPositionGroupByUserId.isEmpty() && !Objects.isNull(userPositionGroupByUserId.get(user.getId()))){
                userVO.setPositionIdListStr(userPositionGroupByUserId.get(user.getId()).stream().map(UserPosition::getPositionId).map(String::valueOf).collect(Collectors.joining(",")));
            }
            if(!userDepartmentGroupByUserId.isEmpty() && !Objects.isNull(userDepartmentGroupByUserId.get(user.getId()))){
                userVO.setDepartmentIdListStr(userDepartmentGroupByUserId.get(user.getId()).stream().map(UserDepartment::getDeptId).map(String::valueOf).collect(Collectors.joining(",")));
            }
            if(!userAuthorityGroupByUserId.isEmpty() && !Objects.isNull(userAuthorityGroupByUserId.get(user.getId()))){
                userVO.setRoleIdListStr(userAuthorityGroupByUserId.get(user.getId()).stream().map(UserAuthority::getRoleId).map(String::valueOf).collect(Collectors.joining(",")));
            }
            return userVO;
        }

        throw new RuntimeException("找不到用户: " + login);
    }

    @Operation(description = "个人信息修改")
    @PostMapping("/users/personal/mod")
    @Transactional(rollbackFor = Exception.class)
    public User modUser(@RequestBody UserVO userVO) throws URISyntaxException {
        log.debug("REST request to mod User : {}", userVO);

        final Optional<User> oneByLogin = userRepository.findOneByLogin(SecurityUtils.getCurrentLoginName());
        final User user = oneByLogin.get();
        user.setAvatar(userVO.getAvatar());
        user.setName(userVO.getName());
        user.setEmail(userVO.getEmail());
        user.setPhoneNumber(userVO.getPhoneNumber());
        return userRepository.save(user);
    }


}
