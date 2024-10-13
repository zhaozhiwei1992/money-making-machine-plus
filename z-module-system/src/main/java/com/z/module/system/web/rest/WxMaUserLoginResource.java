package com.z.module.system.web.rest;

import cn.binarywang.wx.miniapp.api.WxMaService;
import cn.binarywang.wx.miniapp.bean.WxMaJscode2SessionResult;
import cn.binarywang.wx.miniapp.bean.WxMaPhoneNumberInfo;
import cn.binarywang.wx.miniapp.bean.WxMaUserInfo;
import cn.binarywang.wx.miniapp.util.WxMaConfigHolder;
import cn.hutool.json.JSONUtil;
import com.z.framework.security.service.TokenProviderService;
import com.z.module.system.domain.User;
import com.z.module.system.domain.UserOpenId;
import com.z.module.system.repository.UserOpenIdRepository;
import com.z.module.system.repository.UserRepository;
import com.z.module.system.service.LoginService;
import lombok.extern.slf4j.Slf4j;
import me.chanjar.weixin.common.error.WxErrorException;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

/**
 * 微信小程序用户和登录接口
 *
 * @author <a href="https://github.com/binarywang">Binary Wang</a>
 */
@RestController
@Slf4j
@RequestMapping("/system/mobile/wx")
public class WxMaUserLoginResource {
	private final WxMaService wxMaService;

	@Value("${wx.miniapp.defaultappid:}")
	private String appid;

	private final LoginService loginService;
	
	private final TokenProviderService tokenProviderService;

	private final UserRepository userRepository;

	public WxMaUserLoginResource(WxMaService wxMaService, LoginService loginService, TokenProviderService tokenProviderService, UserRepository userRepository, UserOpenIdRepository userOpenIdRepository) {
		this.wxMaService = wxMaService;
		this.loginService = loginService;
		this.tokenProviderService = tokenProviderService;
		this.userRepository = userRepository;
        this.userOpenIdRepository = userOpenIdRepository;
    }

	/**
	 * 登陆接口
	 */
	@GetMapping("/login")
	public Map<String, Object> login(String code) throws Exception {
		if (StringUtils.isBlank(code)) {
			throw new RuntimeException("empty code");
		}

		if (!wxMaService.switchover(appid)) {
			throw new IllegalArgumentException(String.format("未找到对应appid=[%s]的配置，请核实！", appid));
		}

		try {
			//根据手机获取用户的信息
			WxMaPhoneNumberInfo phoneNoInfo = wxMaService.getUserService().getPhoneNoInfo(code);
			String phoneNumber = phoneNoInfo.getPhoneNumber();
			final Optional<User> oneByPhoneNumber = userRepository.findOneByPhoneNumber(phoneNumber);
			if(!oneByPhoneNumber.isPresent()) {
				throw new RuntimeException("手机号"+phoneNumber+"未注册");

			}
			Map<String, Object> map = new HashMap<>();
			//先获取openid
			WxMaJscode2SessionResult session = wxMaService.getUserService().getSessionInfo(code);
			String openid = session.getOpenid();
			final User user = oneByPhoneNumber.get();
			String token = tokenProviderService.generateToken(user.getLogin(), false);
			//记录openid和login的关联
			loginService.saveOpenidUserLogin(openid, user.getLogin());
			map.put("mobile", phoneNumber);
			map.put("openid", openid);
			map.put("token", token);
			return map;
		} catch (WxErrorException e) {
			log.error(e.getMessage(), e);
			throw new RuntimeException(e.getMessage());
		} finally {
			// 清理ThreadLocal
			WxMaConfigHolder.remove();
		}
	}

	private final UserOpenIdRepository userOpenIdRepository;

	/**
	 * 检查登陆接口
	 */
	@GetMapping("/checkLogin")
	public Map<String, Object> checkLogin(String code) throws Exception {
		Map<String, Object> map = new HashMap<>();
		if (StringUtils.isBlank(code)) {
			throw new RuntimeException("参数不全");
		}

		if (!wxMaService.switchover(appid)) {
			throw new IllegalArgumentException(String.format("未找到对应appid=[%s]的配置，请核实！", appid));
		}

		try {
			WxMaJscode2SessionResult session = wxMaService.getUserService().getSessionInfo(code);
			String openid = session.getOpenid();
			log.info("sessionKey: {}", session.getSessionKey());
			log.info("openId: {}", session.getOpenid());
			// 检查openKey是不是已经关联了用户手机号
			final Optional<UserOpenId> oneByOpenId = userOpenIdRepository.findOneByOpenId(openid);
			if(!oneByOpenId.isPresent()){
				throw new RuntimeException("没有关联手机号");
			}
			final UserOpenId userOpenId = oneByOpenId.get();
			final String login = userOpenId.getLogin();
			final Optional<User> oneByLogin = userRepository.findOneByLogin(login);
			if(oneByLogin.isPresent()){
				map.put("mobile", oneByLogin.get().getPhoneNumber());
			}else{
				throw new RuntimeException("没有关联手机号");
			}
			final String token = tokenProviderService.generateToken(login, false);
			map.put("openid", openid);
			map.put("token", token);
			return map;

		} catch (WxErrorException e) {
			log.error(e.getMessage(), e);
			throw new RuntimeException(e.getMessage());
		} finally {
			WxMaConfigHolder.remove();// 清理ThreadLocal
		}
	}

	/**
	 * <pre>
	 * 获取用户信息接口
	 * </pre>
	 */
	@GetMapping("/info")
	public String info(String sessionKey, String signature, String rawData, String encryptedData, String iv) {
		if (!wxMaService.switchover(appid)) {
			throw new IllegalArgumentException(String.format("未找到对应appid=[%s]的配置，请核实！", appid));
		}

		// 用户信息校验
		if (!wxMaService.getUserService().checkUserInfo(sessionKey, rawData, signature)) {
			WxMaConfigHolder.remove();// 清理ThreadLocal
			return "user check failed";
		}

		// 解密用户信息
		WxMaUserInfo userInfo = wxMaService.getUserService().getUserInfo(sessionKey, encryptedData, iv);
		WxMaConfigHolder.remove();// 清理ThreadLocal
		return JSONUtil.toJsonStr(userInfo);
	}

	/**
	 * <pre>
	 * 获取用户绑定手机号信息
	 * 前端通过小程序组件获取 code
	 * </pre>
	 */
	@GetMapping("/phone")
	public WxMaPhoneNumberInfo phone(String code) throws WxErrorException {
		if (!wxMaService.switchover(appid)) {
			throw new IllegalArgumentException(String.format("未找到对应appid=[%s]的配置，请核实！", appid));
		}

		if (StringUtils.isBlank(code)) {
			throw new IllegalArgumentException("code 不能为空");
		}

		// 获取手机号
		WxMaPhoneNumberInfo phoneNoInfo = wxMaService.getUserService().getPhoneNoInfo(code);
		// 清理ThreadLocal
		WxMaConfigHolder.remove();
		return phoneNoInfo;
	}
}
