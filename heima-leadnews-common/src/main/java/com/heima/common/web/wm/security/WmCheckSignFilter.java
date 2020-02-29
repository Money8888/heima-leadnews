package com.heima.common.web.wm.security;

import com.alibaba.fastjson.JSON;
import com.heima.common.common.contants.Contants;
import com.heima.model.common.dtos.ResponseResult;
import com.heima.model.common.enums.AppHttpCodeEnum;
import com.heima.utils.common.UrlSignUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.annotation.Order;
import org.springframework.web.filter.GenericFilterBean;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.HashMap;
import java.util.Map;
import java.util.SortedMap;
import java.util.TreeMap;

@Order(1)
@WebFilter(filterName = "wmCheckSignFilter" ,urlPatterns = "/*")
public class WmCheckSignFilter extends GenericFilterBean {

    Logger logger = LoggerFactory.getLogger(WmCheckSignFilter.class);

    // URL有效果的验签效果
    public final static int URL_TIMEOUT = 2 * 60 * 1000;

    public void doFilter(ServletRequest req, ServletResponse res, FilterChain chain) throws IOException, ServletException {
        HttpServletRequest request = (HttpServletRequest) req;
        ResponseResult<?> result = checkToken(request);
        String uri = request.getRequestURI();
        // 测试和开发环境不过滤
        if (true||result == null || !Contants.isProd()||uri.startsWith("/login")){
            chain.doFilter(req, res);
        } else {
            res.setCharacterEncoding(Contants.CHARTER_NAME);
            res.setContentType("application/json");
            res.getOutputStream().write(JSON.toJSONString(result).getBytes());
        }
    }

    /**
     * 判定URL的时间有效性和数据有效性
     *
     * @param request
     * @return 如果验证不通过则返回对应的错误，否则返回null继续执行
     */
    public ResponseResult checkToken(HttpServletRequest request) {
        ResponseResult<?> rr = null;
        long time = System.currentTimeMillis();
        long t = time - Long.valueOf(request.getHeader("t") == null ? "0" : request.getHeader("t"));
        if (t > 0 && t < URL_TIMEOUT) {
            String md = request.getHeader("md");
            SortedMap<String, String> params = getAllParams(request);
            params.put("t", request.getHeader("t"));
            String sign = UrlSignUtils.getUrlSignUtils.getSign(params);
            // 验签不通过
            if (!md.equalsIgnoreCase(sign)) {
                rr = ResponseResult.setAppHttpCodeEnum(AppHttpCodeEnum.SIGN_INVALID);
            }
        } else {
            rr = ResponseResult.setAppHttpCodeEnum(AppHttpCodeEnum.SIG_TIMEOUT);
        }
        return rr;
    }

    //从请求中获取所有参数
    private static SortedMap<String, String> getAllParams(HttpServletRequest request) {
        SortedMap<String, String> result = new TreeMap<>();
        Map<String, String> urlParams = getUrlParams(request);
        for (Map.Entry entry : urlParams.entrySet()) {
            result.put((String) entry.getKey(), (String) entry.getValue());
        }
        return result;
    }

    //将请求参数转换成Map
    private static Map<String, String> getUrlParams(HttpServletRequest request) {
        String param = "";
        try {
            if (request.getQueryString() != null) {
                param = URLDecoder.decode(request.getQueryString(), "utf-8");
            }
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        Map<String, String> result = new HashMap<>();
        String[] params = param.split("&");
        if (StringUtils.isNotEmpty(param)) {
            for (String s : params) {
                Integer index = s.indexOf("=");
                result.put(s.substring(0, index), s.substring(index + 1));
            }
        }
        return result;
    }
}
  