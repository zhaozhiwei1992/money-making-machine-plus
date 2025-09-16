package com.z.module.screen.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import java.util.Map;

/**
 * 读取项目相关配置
 * 
 * @author fuce
 */
@Configuration
@ConfigurationProperties(prefix = "z.module.screen")
@Data
public class ScreenProperties {

	private GoView goView = new GoView();

	@Data
	public static class GoView{

		private Path path = new Path();

		private Oss oss = new Oss();

		private String uploadType;

		private String httpUrl;

		@Data
		public static class Path {

			private String upload;

		}

		@Data
		public static class Oss {
//			endpoint: oss-cn-beijing.aliyuncs.com
			private String endpoint;
//			accessKey: ??
			private String accessKey;
//			secretKey: ??
			private String secretKey;
//			bucketName: goview
			private String bucketName;
//			staticDomain: ??
			private String staticDomain;
		}
	}
}
