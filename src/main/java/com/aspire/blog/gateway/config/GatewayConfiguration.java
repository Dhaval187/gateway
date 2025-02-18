package com.aspire.blog.gateway.config;

import org.springframework.cloud.netflix.zuul.filters.RouteLocator;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.aspire.blog.gateway.gateway.accesscontrol.AccessControlFilter;
import com.aspire.blog.gateway.gateway.responserewriting.SwaggerBasePathRewritingFilter;

import io.github.jhipster.config.JHipsterProperties;

@Configuration
public class GatewayConfiguration {

	@Configuration
	public static class SwaggerBasePathRewritingConfiguration {

		@Bean
		public SwaggerBasePathRewritingFilter swaggerBasePathRewritingFilter() {
			return new SwaggerBasePathRewritingFilter();
		}
	}

	@Configuration
	public static class AccessControlFilterConfiguration {

		@Bean
		public AccessControlFilter accessControlFilter(RouteLocator routeLocator,
				JHipsterProperties jHipsterProperties) {
			return new AccessControlFilter(routeLocator, jHipsterProperties);
		}
	}

}
