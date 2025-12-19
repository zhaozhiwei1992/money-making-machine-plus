
package com.z.module.system.service.consumer;

import com.z.module.system.domain.LoginLog;
import com.z.module.system.repository.LoginLogRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

public class LoginLogConsumerTest {

    @Mock
    private LoginLogRepository loginLogRepository;

    @InjectMocks
    private LoginLogConsumer loginLogConsumer;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    public void testOnMessage() {
        LoginLog loginLog = new LoginLog();
        loginLog.setLoginName("test_user");

        loginLogConsumer.onMessage(loginLog);

        verify(loginLogRepository, times(1)).save(loginLog);
    }
}
