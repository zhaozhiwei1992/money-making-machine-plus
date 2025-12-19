
package com.z.module.system.web.rest;

import com.z.framework.common.repository.CommonSqlRepository;
import com.z.module.system.domain.User;
import com.z.module.system.repository.*;
import com.z.module.system.service.UserService;
import com.z.module.system.web.vo.UserVO;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Collections;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

//@SpringBootTest
//@WebMvcTest(UserResource.class)
public class UserResourceTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private UserService userService;

    @MockBean
    private UserRepository userRepository;

    @MockBean
    private PasswordEncoder passwordEncoder;

    @MockBean
    private CommonSqlRepository commonSqlRepository;

    @MockBean
    private UserAuthorityRepository userAuthorityRepository;

    @MockBean
    private UserPositionRepository userPositionRepository;

    @MockBean
    private UserDepartmentRepository userDepartmentRepository;

    @MockBean
    private PositionRepository positionRepository;

    @MockBean
    private DepartmentRepository departmentRepository;

    private UserVO userVO;

    @BeforeEach
    public void setUp() {
        userVO = new UserVO();
        userVO.setLogin("test");
        userVO.setPassword("password");
        userVO.setRoleIdListStr("1");
        userVO.setPositionIdListStr("1");
        userVO.setDepartmentIdListStr("1");
    }

    @Test
    public void testCreateUser() throws Exception {
        User user = new User();
        user.setId(1L);
        user.setLogin("test");

        when(passwordEncoder.encode(anyString())).thenReturn("encoded_password");
        when(userRepository.save(any(User.class))).thenReturn(user);

        mockMvc.perform(post("/system/users")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"login\":\"test\",\"password\":\"password\",\"roleIdListStr\":\"1\",\"positionIdListStr\":\"1\",\"departmentIdListStr\":\"1\"}"))
                .andExpect(status().isOk());
    }

    @Test
    public void testGetAllUsers() throws Exception {
        Page<User> page = new PageImpl<>(Collections.singletonList(new User()));
        when(userRepository.findAll(any(PageRequest.class))).thenReturn(page);

        mockMvc.perform(get("/system/users"))
                .andExpect(status().isOk());
    }
}
