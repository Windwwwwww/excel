package cn.homyit.utils;


import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtBuilder;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;

import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.util.Base64;
import java.util.Date;
import java.util.UUID;

/**
 * JWT工具类
 */
public class JwtUtil {

    //有效期为
    public static final Long JWT_TTL = 60 * 60 *1000L;// 60 * 60 *1000  一个小时
    //设置秘钥明文
    public static final String JWT_KEY = "answer20222620";

    public static String getUUID(){
        String token = UUID.randomUUID().toString().replaceAll("-", "");
        return token;
    }

    /**
     * 生成jtw
     * @param subject token中要存放的数据（json格式）
     * @return
     */
    public static String createJWT(String subject) {
        JwtBuilder builder = getJwtBuilder(subject, null, getUUID());// 设置过期时间
        return builder.compact();
    }

    /**
     * 生成jtw
     * @param subject token中要存放的数据（json格式）
     * @param ttlMillis token超时时间
     * @return
     */
    public static String createJWT(String subject, Long ttlMillis) {
        JwtBuilder builder = getJwtBuilder(subject, ttlMillis, getUUID());// 设置过期时间
        return builder.compact();
    }

    private static JwtBuilder getJwtBuilder(String subject, Long ttlMillis, String uuid) {
        SignatureAlgorithm signatureAlgorithm = SignatureAlgorithm.HS256;
        SecretKey secretKey = generalKey();
        long nowMillis = System.currentTimeMillis();
        Date now = new Date(nowMillis);
        if(ttlMillis==null){
            ttlMillis=JwtUtil.JWT_TTL;
        }
        long expMillis = nowMillis + ttlMillis;
        Date expDate = new Date(expMillis);
        return Jwts.builder()
                .setId(uuid)              //唯一的ID
                .setSubject(subject)   // 主题  可以是JSON数据
                .setIssuer("sg")     // 签发者
                .setIssuedAt(now)      // 签发时间
                .signWith(signatureAlgorithm, secretKey) //使用HS256对称加密算法签名, 第二个参数为秘钥
                .setExpiration(expDate);
    }

    /**
     * 创建token
     * @param id
     * @param subject
     * @param ttlMillis
     * @return
     */
    public static String createJWT(String id, String subject, Long ttlMillis) {
        JwtBuilder builder = getJwtBuilder(subject, ttlMillis, id);// 设置过期时间
        return builder.compact();
    }

    public static void main(String[] args) throws Exception {
        String token = "eyJhbGciOiJIUzI1NiJ9.eyJqdGkiOiJjYWM2ZDVhZi1mNjVlLTQ0MDAtYjcxMi0zYWEwOGIyOTIwYjQiLCJzdWIiOiJzZyIsImlzcyI6InNnIiwiaWF0IjoxNjM4MTA2NzEyLCJleHAiOjE2MzgxMTAzMTJ9.JVsSbkP94wuczb4QryQbAke3ysBDIL5ou8fWsbt_ebg";
        Claims claims = parseJWT(token);
        System.out.println(claims);
    }

    /**
     * 生成加密后的秘钥 secretKey
     * @return
     */
    public static SecretKey generalKey() {
        byte[] encodedKey = Base64.getDecoder().decode(JwtUtil.JWT_KEY);
        SecretKey key = new SecretKeySpec(encodedKey, 0, encodedKey.length, "AES");
        return key;
    }


    /**
     * 解析
     *
     * @param jwt
     * @return
     * @throws Exception
     */
    public static Claims parseJWT(String jwt) throws Exception {
        SecretKey secretKey = generalKey();
        return Jwts.parser()
                .setSigningKey(secretKey)
                .parseClaimsJws(jwt)
                .getBody();
    }

}


//@Component
//public class JwtUtil {
//
//    // 有效期
//    private static final long JWT_EXPIRE = 30*60*1000L;  //半小时
//    // 令牌MIYAO
//    private static final String JWT_KEY = "123456";
//
//    public  String createToken(Object data){
//        // 当前时间
//        long currentTime = System.currentTimeMillis();
//        // 过期时间
//        long expTime = currentTime+JWT_EXPIRE;
//        // 构建jwt
//        JwtBuilder builder = Jwts.builder()
//                .setId(UUID.randomUUID()+"")
//                .setSubject(JSON.toJSONString(data))
//                .setIssuer("system")
//                .setIssuedAt(new Date(currentTime))
//                .signWith(SignatureAlgorithm.HS256, encodeSecret(JWT_KEY))
//                .setExpiration(new Date(expTime));
//        return builder.compact();
//    }
//
//    private SecretKey encodeSecret(String key){
//        byte[] encode = Base64.getEncoder().encode(key.getBytes());
//        SecretKeySpec aes = new SecretKeySpec(encode, 0, encode.length, "AES");
//        return  aes;
//    }
//
//    public Claims parseToken(String token){
//        Claims body = Jwts.parser()
//                .setSigningKey(encodeSecret(JWT_KEY))
//                .parseClaimsJws(token)
//                .getBody();
//        return body;
//    }
//
//    public <T> T parseToken(String token,Class<T> clazz){
//        Claims body = Jwts.parser()
//                .setSigningKey(encodeSecret(JWT_KEY))
//                .parseClaimsJws(token)
//                .getBody();
//        return JSON.parseObject(body.getSubject(),clazz);
//    }
//}


