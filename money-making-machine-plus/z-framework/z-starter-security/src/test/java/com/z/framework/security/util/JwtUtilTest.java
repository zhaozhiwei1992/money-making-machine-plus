
package com.z.framework.security.util;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

public class JwtUtilTest {

    @Test
    public void testGenerateAndValidateToken() {
        String userName = "testUser";
        String token = JwtUtil.generateToken(userName);

        assertNotNull(token);

        String validatedUserName = JwtUtil.validateToken(token);
        assertEquals(userName, validatedUserName);
    }
}
