package org.openmore.common.filter;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.util.Map;

/**
 * 对Sign的验证放到Controller，不再使用
 */
public class RequestFilter extends BaseAbstractFilter {

    private Logger logger = null;

    public void init(FilterConfig config) {
        logger = LoggerFactory.getLogger(RequestFilter.class);
    }

    public void destroy() {
        logger = null;
    }

    public static String getParamsString(Map<String, String[]> params) {
        if (params == null || params.isEmpty())
            return "";

        StringBuilder builder = new StringBuilder();
        builder.append("?");


        for (String key : params.keySet()) {
            builder.append(key).append("=").append(params.get(key)[0])
                    .append("&");

        }
        builder.deleteCharAt(builder.lastIndexOf("&"));

        return builder.toString();
    }

    @Override
    public void doFilter(HttpServletRequest request,
                         HttpServletResponse response, FilterChain chain,
                         HttpSession session, String method, String url)
            throws IOException, ServletException {
        logger.debug("doFilter");
        ServletRequest requestWrapper = null;
        // Body里的流数据一旦读取出来，后面就无法再次读取了，需要经过BodyReadHttpServletRequestWrapper保存body数据再重新新流写入
//		if (request instanceof HttpServletRequest) {
//			requestWrapper = new BodyReadHttpServletRequestWrapper(request);
//		}
        if (null == requestWrapper) {
            chain.doFilter(request, response);
        } else {
            chain.doFilter(requestWrapper, response);
        }
    }
}
