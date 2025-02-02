package com.gk.company.LoginControler;


import com.gk.commen.param.request.RequestUser;
import com.gk.commen.param.request.UserReq;
import com.gk.commen.param.result.LoginResult;
import com.gk.commen.param.result.UserResult;
import com.gk.company.organization.domain.SysUser;
import com.gk.company.organization.service.ISysUserService;
import com.gk.company.organization.service.impl.SysUserService;
import com.gk.company.utils.JwtsUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authc.AuthenticationException;
import org.apache.shiro.authc.UsernamePasswordToken;
import org.apache.shiro.session.Session;
import org.apache.shiro.subject.PrincipalCollection;
import org.apache.shiro.subject.Subject;
import org.apache.shiro.subject.support.DefaultSubjectContext;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;

/**
 * @ClassName LoginControler
 * @Deacription TODO
 * @Author
 * @Date 2021/3/31 17:34
 * @Version 1.0
 **/
@RestController
public class LoginControler {

    @Autowired
    public ISysUserService userService;

    @RequestMapping("/login")
    public LoginResult login(String userName, String password, String url) {

        LoginResult result = new LoginResult();

        UsernamePasswordToken token = new UsernamePasswordToken(userName, password);
        Subject subject = SecurityUtils.getSubject();
        try {
            subject.login(token);

            PrincipalCollection principalCollection = subject.getPrincipals();
            SysUser suser = userService.findByName(userName);
            HashMap<String, Object> map = new HashMap<String, Object>();
            Session session = subject.getSession();
            PrincipalCollection principals = (PrincipalCollection) session.getAttribute(DefaultSubjectContext.PRINCIPALS_SESSION_KEY);

            map.put("sessionId", session.getId());
            map.put("password", suser.getPassword());
            UserResult user = new UserResult();
            BeanUtils.copyProperties(suser, user);
            String jwtToken = JwtsUtils.createJWT(user, map);
            result.setCode("400");
            result.setToken(jwtToken);
            return result;
        } catch (Exception e) {
            e.printStackTrace();

            String msg = "用户或密码错误";
            if (StringUtils.isNotBlank(e.getMessage())) {
                msg = e.getMessage();
            }
            result.setCode("301");
            result.setMsg(msg);
            return result;
        }
    }

    @RequestMapping("/register")
    public LoginResult register(UserReq user) {

        LoginResult result = new LoginResult();
        try {
            try {
                userService.add(user);
            } catch (Exception e) {
                e.printStackTrace();
            }
            result.setCode("200");
            return result;
        } catch (AuthenticationException e) {

            String msg = "用户或密码错误";
            if (StringUtils.isNotBlank(e.getMessage())) {
                msg = e.getMessage();
            }
            result.setCode("301");
            return result;
        }
    }

    @RequestMapping("/logout")
    public LoginResult loginout() {

        LoginResult result = new LoginResult();
        try {
            Subject subject = SecurityUtils.getSubject();
            subject.logout();
        } catch (Exception e) {
            e.printStackTrace();
        }
        result.setCode("200");
        return result;
    }
}
