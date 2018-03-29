package org.openmore.common.utils;

import io.jsonwebtoken.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.openmore.common.exception.*;

import java.math.BigInteger;
import java.security.MessageDigest;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

/**
 * Created by michaeltang on 2018/3/22.
 */
public class CommonUtils {
    private final static String key = "ipottery2017";
    private final static int tokenExpireDays = 3650;

    public static final String SCOPE_APP = "app";
    public static final String SCOPE_BACKEND = "backend";

    private static Logger logger = LoggerFactory.getLogger(CommonUtils.class);

    /**
     * 获取现在时间
     *
     * @return 返回时间类型 yyyy-MM-dd HH:mm:ss
     */
    public static String getNowDateString(int randomSize) {
        Date currentTime = new Date();
        SimpleDateFormat formatter = new SimpleDateFormat("yyyyMMddHHmmss");
        String dateString = formatter.format(currentTime);
        if (randomSize > 0) {
            dateString += CommonUtils.randomNumber(randomSize);
        }
        return dateString;
    }

    /**
     * 对字符串md5加密
     *
     * @param str
     * @return
     */
    public static String md5(String str) throws Exception {
        try {
            // 生成一个MD5加密计算摘要
            MessageDigest md = MessageDigest.getInstance("MD5");
            // 计算md5函数
            md.update(str.getBytes());
            // digest()最后确定返回md5 hash值，返回值为8为字符串。因为md5 hash值是16位的hex值，实际上就是8位的字符
            // BigInteger函数则将8位的字符串转换成16位hex值，用字符串来表示；得到字符串形式的hash值
            return new BigInteger(1, md.digest()).toString(16);
        } catch (Exception e) {
            throw new Exception("MD5加密出现错误");
        }
    }

    /**
     * 获取指定位数的的string随机数，随机范围为A-Z 2-9
     *
     * @param length string的长度
     * @return 指定lenght的随机字符串
     */
    public static String randomString(int length) {
        // 去掉I,O, 0, 1
        String str = "ABCDEFGHJKLMNPQRSTUVWXYZ23456789";
        return randomString(str, length);
    }


    /**
     * 获取指定位数的的string随机数，随机范围为0-9
     *
     * @param length string的长度
     * @return 指定lenght的随机字符串
     */
    public static String randomNumber(int length) {
        // 去掉I,O, 0, 1
        String str = "0123456789012345678901234567890123456789";
        return randomString(str, length);
    }

    /**
     * 获得指定位数的随机数，随机数集合由string指定
     *
     * @param string
     * @param length
     * @return
     */
    public static String randomString(String string, int length) {
        Random random = new Random();
        StringBuffer buf = new StringBuffer();
        for (int i = 0; i < length; i++) {
            int num = random.nextInt(string.length());
            buf.append(string.charAt(num));
        }
        return buf.toString();
    }

    /**
     * 由userId加密成token
     *
     * @param uid
     * @param deviceToken
     * @param scope
     * @return
     */
    public static String getTokenByUserId(Integer uid, String deviceToken, String scope) {
        if (scope == null)
            scope = SCOPE_APP;

        logger.debug("deviceToken：" + deviceToken);

        Date expiredData = new Date();
        // 默认过期时间
        expiredData.setTime(System.currentTimeMillis() + tokenExpireDays * 24 * 3600 * 1000);
        Map<String, Object> claims = new HashMap<>();
        claims.put("scope", scope);
        claims.put("device_token", deviceToken);
        claims.put("uid", uid + "");
        String token = Jwts.builder()
                .setIssuedAt(new Date())
                .setClaims(claims)
                .compressWith(CompressionCodecs.DEFLATE)
                .setExpiration(expiredData)
                .signWith(SignatureAlgorithm.HS256, key)
                .compact();

        logger.debug("Token = " + token);

        return token;
    }


    /**
     * 将HttpRequest里的Bearer信息和DeviceTokne进行解析，获得userId信息
     *
     * @param authorization 授权信息
     * @param deviceToken   设备信息，没有时，为none
     * @return
     * @throws InvalidTokenException
     */
    public static int getUserIdFromAuthHeaderInfo(String authorization, String deviceToken) {
        int uid = 0;
        if (authorization != null && authorization.startsWith("Bearer ")) {
            authorization = authorization.substring("Bearer ".length());
            if (deviceToken == null || deviceToken.length() == 0) {
                deviceToken = "none";
            }
            String uidStr = CommonUtils.getUserIdFromToken(authorization, deviceToken);
            uid = uidStr == null ? 0 : Integer.valueOf(uidStr);
        }
        return uid;
    }

    /**
     * 由tokne解密为userId同时检查过期时间
     *
     * @param userToken
     * @return
     * @throws InvalidTokenException
     */
    public static String getUserIdFromToken(String userToken, String deviceToken) throws InvalidTokenException {
        try {
            Jws<Claims> jws = Jwts.parser().setSigningKey(key).parseClaimsJws(userToken);
            logger.debug("token body：" + jws.getBody());
            String decodeDevToken = (String) (jws.getBody().get("device_token"));
            if (deviceToken == null || !deviceToken.equals(decodeDevToken)) {
                throw new InvalidTokenException("无效的设备token");
            }
            return (String) jws.getBody().get("uid");
        } catch (ExpiredJwtException e) {
            logger.debug("授权已过期，请重新登录");
            throw new InvalidTokenException("授权已过期，请重新登录");
        } catch (InvalidTokenException e) {
            logger.debug("无效的设备token");
            throw e;
        } catch (Exception e) {
            logger.debug(e.getMessage());
            throw new InvalidTokenException("无效token");
        }
    }


    /**
     * 由tokne获得域
     *
     * @param userToken
     * @return
     * @throws InvalidTokenException
     */
    public static String getScopeFromToken(String userToken, String deviceToken) throws InvalidTokenException {
        try {
            Jws<Claims> jws = Jwts.parser().setSigningKey(key).parseClaimsJws(userToken);
            logger.debug("token body：" + jws.getBody());
            String decodeDevToken = (String) (jws.getBody().get("device_token"));
            if (deviceToken == null || !deviceToken.equals(decodeDevToken)) {
                throw new InvalidTokenException("无效的设备token");
            }
            String scope = (String) jws.getBody().get("scope");
            return scope;
        } catch (ExpiredJwtException e) {
            logger.debug("授权已过期，请重新登录");
            throw new InvalidTokenException("授权已过期，请重新登录");
        } catch (InvalidTokenException e) {
            logger.debug("无效的设备token");
            throw e;
        } catch (Exception e) {
            logger.debug(e.getMessage());
            throw new InvalidTokenException("无效token");
        }
    }

}
