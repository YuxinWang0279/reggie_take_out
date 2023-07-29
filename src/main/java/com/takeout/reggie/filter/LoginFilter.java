package com.takeout.reggie.filter;

import com.alibaba.fastjson.JSON;
import com.takeout.reggie.common.BaseContext;
import com.takeout.reggie.common.R;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.netty.util.NetUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.AntPathMatcher;

import javax.servlet.*;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@Slf4j
@WebFilter(urlPatterns = "/*")
public class LoginFilter implements Filter {
    //路径匹配器
    public static final AntPathMatcher PATH_MATCHER = new AntPathMatcher();
    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
        HttpServletRequest request = (HttpServletRequest) servletRequest;
        HttpServletResponse response = (HttpServletResponse) servletResponse;
        //1.获取本次请求的url
        String requestURL = request.getRequestURI();
        log.info("拦截到请求{}",requestURL);
        //2.判断本次请求是否需要处理
        String[] urls = new String[]{
                "/employee/login",
                "/employee/logout",
                "/backend/**",
                "/front/**",
                "/common/**",
                "/user/sendMsg",
                "/user/login",
                "/doc.html",
                "/webjars/**",
                "/swagger-resources",
                "/v2/api-docs"

        };
        //3.如果不需要处理则直接放行
        if(check(requestURL,urls)){
            filterChain.doFilter(servletRequest,servletResponse);
            log.info("本次请求{}不需要处理",requestURL);
            return;
        }

        //4-2 user login
        if(request.getSession().getAttribute("User")!=null){
            //request.getSession().getId(): session id 不是userId
            log.info("用户已登陆,用户id{}",request.getSession().getAttribute("User"));
            BaseContext.setCurrentId((Long) request.getSession().getAttribute("User"));
            filterChain.doFilter(servletRequest,servletResponse);
            return;
        }
        //4-1.判断登陆状态，如果已经登陆则直接放行
        String jwt = "";
        Cookie[] cookies = request.getCookies();
        if(cookies!=null){
            for(Cookie cookie:cookies){
                if(cookie.getName().equals("JWT"))
                    jwt = cookie.getValue();
            }
            try{
                Claims claims = Jwts.parser()
                        .setSigningKey("secret")
                        .parseClaimsJws(jwt)
                        .getBody();
                log.info("用户已登陆,用户id{}",claims.get("Employee"));
                Number id = (Number)claims.get("Employee");
                BaseContext.setCurrentId(id.longValue());
                filterChain.doFilter(servletRequest,servletResponse);
                return;
            }
            catch (Exception ex){
                log.info(ex.toString());
            }
        }

         //request.getSession().getAttribute("Employee")!=null
            //request.getSession().getId(): session id 不是userId



        //5.如果未登陆则返回对应登陆结果,通过输出流向client输出数据
        log.info("用户未登陆");
        response.getWriter().write(JSON.toJSONString(R.error("NOTLOGIN")));
    }
    public boolean check(String url,String[] urls){
        for(String u:urls){
            if(PATH_MATCHER.match(u,url))
                return true;
        }
        return false;
    }
}
