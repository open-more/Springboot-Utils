package org.openmore.common.utils;

/**
 * Created by michaeltang on 2018/3/22.
 */

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;

import java.util.Map;
import java.util.Set;
import java.util.concurrent.TimeUnit;

public class RedisOps {
    private Logger logger = LoggerFactory.getLogger(RedisOps.class);

    @Autowired
    private StringRedisTemplate redisTemplate;

    /**
     * 设置key的值为value，超时时间为expireTime，单位秒
     * @param key
     * @param value
     * @param expireTime
     * @return
     */
    public void set(String key, String value, long expireTime){
        logger.debug("set");
        this.redisTemplate.opsForValue().set(key, value, expireTime, TimeUnit.SECONDS);
    }

    public String get(String key){
        logger.debug("get");
        return this.redisTemplate.opsForValue().get(key);
    }



    //Key（键），简单的key-value操作

    /**
     * 实现命令：TTL key，以秒为单位，返回给定 key的剩余生存时间(TTL, time to live)。
     * @param key
     * @return
     */
    public long ttl(String key) {
        logger.debug("ttl");
        return this.redisTemplate.getExpire(key);
    }

    /**
     * 实现命令：KEYS pattern，查找所有符合给定模式 pattern的 key
     */
    public Set<String> keys(String pattern){
        logger.debug("keys");
        return this.redisTemplate.keys(pattern);
    }

    /**
     * 实现命令：DEL key，删除一个key
     * @param key
     */
    public void del(String key){
        logger.debug("del");
        this.redisTemplate.delete(key);
    }

    //Hash（哈希表）

    /**
     * 实现命令：HSET key field value，将哈希表 key中的域 field的值设为 value
     * @param key
     * @param field
     * @param value
     */
    public void hSet(String key, String field, Object value) {
        logger.debug("hSet");
        this.redisTemplate.opsForHash().put(key, field, value);
    }

    /**
     * 实现命令：HGET key field，返回哈希表 key中给定域 field的值
     * @param key
     * @param field
     * @return
     */
    public String hGet(String key, String field) {
        logger.debug("hGet");
        return (String) this.redisTemplate.opsForHash().get(key, field);
    }

    /**
     * 实现命令：HDEL key field [field ...]，删除哈希表 key 中的一个或多个指定域，不存在的域将被忽略。
     * @param key
     * @param fields
     */
    public void hDel(String key, Object... fields) {
        logger.debug("hDel");
        this.redisTemplate.opsForHash().delete(key, fields);
    }

    /**
     * 实现命令：HGETALL key，返回哈希表 key中，所有的域和值。
     * @param key
     * @return
     */
    public Map<Object, Object> hGetAll(String key) {
        logger.debug("hGetAll");
        return this.redisTemplate.opsForHash().entries(key);
    }

    //List（列表）

    /**
     * 实现命令：LPUSH key value，将一个值 value插入到列表 key的表头
     * @param key
     * @param value
     * @return 执行 LPUSH命令后，列表的长度。
     */
    public long lPush(String key, String value) {
        logger.debug("lPush");
        return this.redisTemplate.opsForList().leftPush(key, value);
    }

    /**
     * 实现命令：LPOP key，移除并返回列表 key的头元素。
     * @param key
     * @return 列表key的头元素。
     */
    public String lPop(String key) {
        logger.debug("lPop");
        return this.redisTemplate.opsForList().leftPop(key);
    }

    /**
     * 实现命令：RPUSH key value，将一个值 value插入到列表 key的表尾(最右边)。
     * @param key
     * @param value
     * @return 执行 LPUSH命令后，列表的长度。
     */
    public long rPush(String key, String value) {
        logger.debug("rPush");
        return this.redisTemplate.opsForList().rightPush(key, value);
    }

    /**
     * 实现命令：RPOP key，移除并返回列表 key的尾元素。
     * @param key
     * @return 列表key的头元素。
     */
    public String rPop(String key) {
        logger.debug("rPop");
        return this.redisTemplate.opsForList().rightPop(key);
    }

    //Set（集合）
    /**
     * 实现命令：SADD key member，将一个 member元素加入到集合 key当中，已经存在于集合的 member元素将被忽略。
     * @param key
     * @param member
     */
    public void sAdd(String key, String member) {
        logger.debug("sAdd");
        this.redisTemplate.opsForSet().add(key, member);
    }

    /**
     * 实现命令：SMEMBERS key，返回集合 key 中的所有成员。
     * @param key
     * @return
     */
    public Set<String> sMembers(String key) {
        logger.debug("sMembers");
        return this.redisTemplate.opsForSet().members(key);
    }

    //SortedSet（有序集合）
    /**
     * 实现命令：ZADD key score member，将一个 member元素及其 score值加入到有序集 key当中。
     * @param key
     * @param score
     * @param member
     */
    public void zAdd(String key, double score, String member) {
        logger.debug("zAdd");
        this.redisTemplate.opsForZSet().add(key, member, score);
    }

    /**
     * 实现命令：ZRANGE key start stop，返回有序集 key中，指定区间内的成员。
     * @param key
     * @param start
     * @param stop
     * @return
     */
    public Set<String> zRange(String key, double start, double stop) {
        logger.debug("zRange");
        return this.redisTemplate.opsForZSet().rangeByScore(key, start, stop);
    }
}
