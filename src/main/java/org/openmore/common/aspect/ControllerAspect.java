package org.openmore.common.aspect;

import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authc.UsernamePasswordToken;
import org.apache.shiro.subject.Subject;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.openmore.common.exception.ForbiddenException;
import org.openmore.common.exception.InvalidTokenException;
import org.openmore.common.utils.CommonUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * Created by michaeltang on 2017/7/31.
 */
@Aspect
@Component
public class ControllerAspect {
    protected Logger logger = LoggerFactory.getLogger(this.getClass());

    // 匹配指定包中的所有的方法
//    @Pointcut("execution(* com.xys.service.UserService.*(..))") // 切点表达式
//    private void dataAccessOperation() {} // 切点前面

    //Controller层切点
    @Pointcut("@annotation(org.openmore.common.annotation.SignatureCheck)")
    public void controllerSignAspect() {
    }

    //Controller层切点
    @Pointcut("@annotation(org.openmore.common.annotation.TokenAuthCheck)")
    public void controllerTokenAspect() {
    }

    //    @Before("@annotation(org.openmore.common.annotation.SignatureCheck)")
    @Around("controllerTokenAspect()")
    public Object doTokenCheck(ProceedingJoinPoint joinPoint) throws Throwable {
        logger.debug(">> doTokenCheck");
        HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest();
        HttpServletResponse response = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getResponse();
        try {
            checkToken(request, response);
        } catch (ForbiddenException e) {
            sendResponse(response, e.getMsg());
            logger.error(">> ForbiddenException");
        }
        return joinPoint.proceed();
    }

    /**
     * 检查签名
     *
     * @param joinPoint
     * @return
     * @throws Throwable
     */
    //    @Before("@annotation(org.openmore.common.annotation.SignatureCheck)")
    @Around("controllerSignAspect()")
    public Object doSignCheck(ProceedingJoinPoint joinPoint) throws Throwable {
        logger.debug(">> doSignCheck");
//        System.out.println(">> " + joinPoint.getTarget().getClass().getName() + "." + joinPoint.getSignature().getName());
        HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest();
        HttpServletResponse response = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getResponse();
        try {
            checkSignature(request, response);
        } catch (ForbiddenException e) {
            sendResponse(response, e.getMsg());
            logger.error(">> ForbiddenException");
        }
        return joinPoint.proceed();
    }

    /**
     * 检查用户参数里的授权码及设备Token
     *
     * @param request
     * @param response
     * @throws InvalidTokenException
     */
    private void checkToken(HttpServletRequest request, HttpServletResponse response) throws InvalidTokenException {
        logger.debug("检查授权信息:{} {}", request.getMethod(), request.getRequestURI());

        Subject subject = SecurityUtils.getSubject();
        //如果用户已经授权，直接返回
//        if(subject.isAuthenticated()) {
//            return;
//        }else{
        String authorization = request.getHeader("Authorization");
        String deviceToken = request.getHeader("X-DEVICE_TOKEN");
        if (authorization == null || !authorization.startsWith("Bearer ")) {
            throw new InvalidTokenException("该接口需要访问授权");
        }
        if (deviceToken == null || deviceToken.length() == 0) {
            deviceToken = "none";
        }
        authorization = authorization.substring("Bearer ".length());
        logger.debug("Authorization:{}", authorization);
        logger.debug("X-DEVICE_TOKEN:{}", deviceToken);
        // 设备token相当于认证，token相当于密码
        UsernamePasswordToken token = new UsernamePasswordToken(authorization, deviceToken);
        token.setRememberMe(false);
        try {
            // 执行登录验证
            logger.debug("subject.login(token)");
            subject.login(token);
        } catch (Exception e) {
            logger.debug("Exception", e.getCause());
            throw new InvalidTokenException("无效token");
        }
        if (!subject.isAuthenticated()) {
            throw new InvalidTokenException("token登录失败");
        }
//        }
    }

    /**
     * 检查签名
     *
     * @param request
     * @param response
     * @throws ForbiddenException
     */
    private void checkSignature(HttpServletRequest request, HttpServletResponse response) throws ForbiddenException {
        logger.debug("Accept:{}", request.getHeader("Accept"));
        logger.debug("Content-Type:{}", request.getHeader("Content-Type"));
        long startTime = System.currentTimeMillis();
        logger.debug("AOP拦截到请求:{} {}", request.getMethod(), request.getRequestURI());

        // ------- 开始校验 -------
        String sign = request.getHeader("X-SIGN");
        String time = request.getHeader("X-TIMESTAMP");
        String nonce = request.getHeader("X-NONCE");
        String key = request.getHeader("X-APP_KEY");
        String encrypt = request.getHeader("X-ENCRYPT");
        String contentType = request.getHeader("Content-Type");

        String jsonBody = "";

        logger.debug(jsonBody);

        if (StringUtils.isEmpty(encrypt) || encrypt == null) {
            return;
        }

        logger.debug(jsonBody);
        logger.debug("sign = {} time = {} nonce = {} key = {} jsonBody = {} encrypt = {}", sign, time, nonce, key, jsonBody, encrypt);

        if (StringUtils.isEmpty(sign) || StringUtils.isEmpty(time)
                || StringUtils.isEmpty(nonce) || StringUtils.isEmpty(key)
                || StringUtils.isEmpty(contentType)) {
            logger.error("Header缺少参数");
            throw new ForbiddenException("Header缺少参数");
        }

        if (!request.getHeader("Content-Type").contains("application/json")) {
            logger.error("Content-Type配置不正确");
            throw new ForbiddenException("Content-Type配置不正确");
        }

        if (Math.abs(startTime / 1000 - Long.parseLong(time)) > 60) {
            logger.debug("请求时间戳超过60秒");
            throw new ForbiddenException("请求时间戳超过60秒");
        }

        String secret = "";

        if ("app_android_openmore_001".equals(key)) {
            secret = "hahahaha";
        }

        String unsignString = secret + nonce + request.getMethod().toUpperCase() + request.getRequestURI() + jsonBody + time;
        String mysign = "";
        try {
            logger.debug("unsignString = {}", unsignString);
            mysign = CommonUtils.md5(unsignString);
            logger.debug("sign = {}", mysign);
        } catch (Exception e) {
            logger.debug("md5加密失败");
            throw new ForbiddenException("签名加密失败");
        }

        if (!sign.toUpperCase().equals(mysign.toUpperCase())) {
            logger.error("signature not corrected");
            throw new ForbiddenException("签名不正确");
        }

        long endTime = System.currentTimeMillis();
        logger.debug("请求: {} {}", request.getMethod(), request.getRequestURI());
        logger.debug("花费时间：" + (endTime - startTime) + "ms");
    }


    /**
     * 异常发送响应内容
     *
     * @param response
     * @param errorMsg
     */
    private void sendResponse(HttpServletResponse response, String errorMsg) {
        ServletOutputStream out = null;
        String json = "{\"msg\": \"" + errorMsg + "\",\"errorCode\": -1}";
        try {
            response.setCharacterEncoding("UTF-8");
            response.setContentType("text/json;charset=UTF-8");
            response.setStatus(403);
            out = response.getOutputStream();
            out.write(json.getBytes("UTF-8"));
        } catch (IOException e2) {
            logger.error(">> IOException");
            e2.printStackTrace();
        } finally {
            if (null != out) {
                try {
                    out.flush();
                    out.close();
                } catch (IOException e3) {
                    logger.error(">> IOException");
                    e3.printStackTrace();
                }
            }
        }
    }

}
