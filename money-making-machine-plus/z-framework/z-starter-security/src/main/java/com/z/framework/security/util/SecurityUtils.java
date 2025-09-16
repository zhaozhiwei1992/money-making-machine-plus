package com.z.framework.security.util;

import org.apache.commons.lang3.StringUtils;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;

import java.security.Principal;
import java.util.*;
import java.util.stream.Stream;


/**
 * @Title: SecurityUtil
 * @Package com/example/springbootsecurityjwt/util/SecurityUtil.java
 * @Description:
 * @author zhaozhiwei
 * @date 2021/6/4 下午10:22
 * @version V1.0
 */
public final class SecurityUtils {

    public static Authentication getAuthentication(){
        return SecurityContextHolder.getContext().getAuthentication();
    }

    public static boolean isAuthenticated() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        return authentication != null && getAuthorities(authentication).noneMatch("ROLE_ANONYMOUS"::equals);
    }

    private static Stream<String> getAuthorities(Authentication authentication) {
        return authentication.getAuthorities().stream().map(GrantedAuthority::getAuthority);
    }

    /**
     * Gets current login name.
     *
     * @return the current login name
     */
    public static String getCurrentLoginName() {
        Authentication authentication = getAuthentication();
        if(authentication!=null) {
            Object principal = authentication.getPrincipal();
            if (principal instanceof UserDetails) {
                return ((UserDetails) principal).getUsername();
            }

            if (principal instanceof Principal) {
                return ((Principal) principal).getName();
            }
            return String.valueOf(principal);
        }else{
            return "system";
        }
    }

    /**
     * 取得当前用户的登录信息
     * @return
     */
    public static Map getAuthenticationDetails(){
        Map retMap = new HashMap();
        Authentication authentication = getAuthentication();
        if(authentication!=null && authentication.getDetails() instanceof Map){
            return (Map)authentication.getDetails();
        }
        return retMap;
    }

    /**
     * 取得当前用户的tokenid
     * @return
     */
    public static String getTokenId(){
        try {
            return (String)getAuthenticationDetails().get("tokenid");
        } catch (Exception e) {
           return null;
        }
    }


    /**
     * 取得当前用户的token
     * @return
     */
    public static String getToken(){
        return (String)getAuthenticationDetails().get("token");
    }

    /**
     * 取得当前用户的ID
     * @return
     */
    public static Long getUserId(){
        return Long.parseLong(String.valueOf(getAuthenticationDetails().get("userid")));
    }

    /**
     * 取得当前用户的年度
     * @return
     */
    public static Integer getYear(){
        return Integer.parseInt(String.valueOf(getAuthenticationDetails().get("year")));
    }

    /**
     * 取得当前用户的财政
     * @return
     */
    public static String getProvince(){
        return (String)getAuthenticationDetails().get("province");
    }
    /**
     * 取得当前用户的客户端IP
     * @return
     */
    public static String getRemoteAddress(){
        return (String)getAuthenticationDetails().get("remoteAddress");
    }


    /**
     * 取得当前用户是否是系统管理员
     * @return
     */
    public static boolean isAdministrator(){
        return Boolean.parseBoolean((String)getAuthenticationDetails().get("administrator"));
    }


    /**
     * 取得当前用户的授权信息
     * @return
     */
    public static Set<String> getCurrentAuthorityUrl() {
        Set<String> path = new HashSet<>();
        Authentication authentication = getAuthentication();
        if(authentication!=null) {
            Collection<? extends GrantedAuthority> authorities = authentication.getAuthorities();
            for (final GrantedAuthority authority : authorities) {
                String url = authority.getAuthority();
                if (StringUtils.isNotEmpty(url)) {
                    path.add(url);
                }
            }
        }
        return path;
    }

    public static void clearContext(){
        SecurityContextHolder.clearContext();
    }
}