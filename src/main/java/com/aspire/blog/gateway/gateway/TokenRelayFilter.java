package com.aspire.blog.gateway.gateway;

import java.util.Set;

import org.springframework.stereotype.Component;

import com.netflix.zuul.ZuulFilter;
import com.netflix.zuul.context.RequestContext;

@Component
public class TokenRelayFilter extends ZuulFilter {

	@Override
	public Object run() {
		RequestContext ctx = RequestContext.getCurrentContext();
		@SuppressWarnings("unchecked")
		Set<String> headers = (Set<String>) ctx.get("ignoredHeaders");
		// JWT tokens should be relayed to the resource servers
		headers.remove("authorization");
		return null;
	}

	@Override
	public boolean shouldFilter() {
		return true;
	}

	@Override
	public String filterType() {
		return "pre";
	}

	@Override
	public int filterOrder() {
		return 10000;
	}
}
