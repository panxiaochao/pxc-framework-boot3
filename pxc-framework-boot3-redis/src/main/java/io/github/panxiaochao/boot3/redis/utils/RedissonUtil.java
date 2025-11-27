/*
 * Copyright © 2025-2026 Lypxc (545685602@qq.com)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package io.github.panxiaochao.boot3.redis.utils;

import io.github.panxiaochao.boot3.core.utils.CollectionUtil;
import io.github.panxiaochao.boot3.core.utils.SpringContextUtil;
import io.github.panxiaochao.boot3.core.utils.StrUtil;
import io.github.panxiaochao.boot3.core.utils.StringPools;
import org.redisson.api.*;
import org.redisson.api.geo.GeoSearchArgs;
import org.redisson.api.options.KeysScanOptions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.Duration;
import java.util.BitSet;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;

/**
 * <p>
 * Redisson 工具类
 * </p>
 *
 * @author Lypxc
 * @since 2023-06-27
 */
public class RedissonUtil {

    /**
     * LOGGER RedissonUtil.class
     */
    private static final Logger LOGGER = LoggerFactory.getLogger(RedissonUtil.class);

    /**
     * constructor private
     */
    private RedissonUtil() {
    }

    private static final RedissonClient REDISSON_CLIENT = SpringContextUtil.getBean(RedissonClient.class);

    /**
     * Obtain RedissonClient
     * @return RedissonClient
     */
    public static RedissonClient ofRedissonClient() {
        return REDISSON_CLIENT;
    }

    /**
     * Returns id of this Redisson instance ID
     * @return String
     */
    public static String getRedissonId() {
        return ofRedissonClient().getId();
    }

    // ------------------------------- Key查询类型操作 --------------------------------

    /**
     * 按匹配模式获取键值，默认10个 <pre>
     * Supported glob-style patterns:
     *  h?llo subscribes to hello, hallo and hxllo
     *  h*llo subscribes to hllo and heeeello
     *  h[ae]llo subscribes to hello and hallo, but not hillo
     * </pre>
     * @param pattern - match pattern
     * @return Set<String>
     */
    public static Set<String> getKeysByPattern(String pattern) {
        Iterable<String> iterable = getRKey().getKeys(KeysScanOptions.defaults().pattern(pattern));
        return CollectionUtil.toHashSet(iterable);
    }

    /**
     * 按匹配模式获取count个数键值 <pre>
     * Supported glob-style patterns:
     *  h?llo subscribes to hello, hallo and hxllo
     *  h*llo subscribes to hllo and heeeello
     *  h[ae]llo subscribes to hello and hallo, but not hillo
     * </pre>
     * @param pattern - match pattern
     * @param count - keys loaded per request to Redis
     * @return Set<String>
     */
    public static Set<String> getKeysByPattern(String pattern, int count) {
        Iterable<String> iterable = getRKey().getKeys(KeysScanOptions.defaults().pattern(pattern).limit(count));
        return CollectionUtil.toHashSet(iterable);
    }

    /**
     * 按匹配模式删除多个对象。
     * <p>
     * Method executes in <b>NON atomic way</b> in cluster mode due to lua script
     * limitations.
     * <p>
     * <pre>
     * Supported glob-style patterns:
     *  h?llo subscribes to hello, hallo and hxllo
     *  h*llo subscribes to hllo and heeeello
     *  h[ae]llo subscribes to hello and hallo, but not hillo
     * </pre>
     * @param pattern 表达式
     * @return number of removed keys
     */
    public static long deleteKeyByPattern(String pattern) {
        return getRKey().deleteByPattern(pattern);
    }

    /**
     * 返回当前所选数据库中的键数量
     * @return count of keys
     */
    public static long countKeys() {
        return getRKey().count();
    }

    /**
     * 检查redis中是否存在keys
     * @return true or false
     */
    public static boolean countExists(String... keys) {
        return getRKey().countExists(keys) > 0;
    }

    /**
     * 按 keys 删除多个对象
     * @param keys - object names
     */
    public static void deleteKeys(String... keys) {
        getRKey().delete(keys);
    }

    /**
     * Obtain the RKeys.
     * @return RKeys
     */
    private static RKeys getRKey() {
        return ofRedissonClient().getKeys();
    }
    // ------------------------------- Object 类型操作 --------------------------------

    /**
     * 设置值
     * @param key 缓存的键值
     * @param value 缓存的值
     */
    public static <T> void set(String key, T value) {
        set(key, value, Duration.ofMillis(-1));
    }

    /**
     * 获得key剩余存活时间
     * @param key 缓存键值
     * @return 剩余存活时间
     */
    public static <T> long getRemainTimeToLive(String key) {
        return getRBucket(key).remainTimeToLive();
    }

    /**
     * 设置过期时间
     * @param key key
     * @param duration expiration duration
     */
    public static boolean expire(String key, Duration duration) {
        return getRBucket(key).expire(duration);
    }

    /**
     * 设置过期时间，当只有key设置过过期时间才会设置
     * @param key key
     * @param duration expiration duration
     */
    public static <T> boolean expireIfSet(String key, Duration duration) {
        RBucket<T> rBucket = getRBucket(key);
        return rBucket.expireIfSet(duration);
    }

    /**
     * 设置过期时间，当只有key没有设置过期时间才会设置
     * @param key key
     * @param duration expiration duration
     */
    public static <T> boolean expireIfNotSet(String key, Duration duration) {
        RBucket<T> rBucket = getRBucket(key);
        return rBucket.expireIfNotSet(duration);
    }

    /**
     * 获取值
     * @param key key
     * @return value
     */
    public static <T> T get(String key) {
        RBucket<T> rBucket = getRBucket(key);
        return rBucket.get();
    }

    /**
     * 获取值通过批量keys
     * @param keys keys
     * @return value
     */
    public static <T> Map<String, T> get(String... keys) {
        RBuckets rBuckets = getRBuckets();
        return rBuckets.get(keys);
    }

    /**
     * 仅当对象不存在时设置具有过期持续时间的值.
     * @param key key
     * @param value value to set
     * @param duration expiration duration
     * @return {@code true} if successful, or {@code false} if element was already set
     */
    public static <T> boolean setIfAbsent(String key, T value, Duration duration) {
        return getRBucket(key).setIfAbsent(value, duration);
    }

    /**
     * 仅当对象已存在时设置具有过期持续时间的值.
     * @param value value to set
     * @param duration expiration duration
     * @return {@code true} if successful, or {@code false} if element wasn't set
     */
    public static <T> boolean setIfExists(String key, T value, Duration duration) {
        return getRBucket(key).setIfExists(value, duration);
    }

    /**
     * 设置值.
     * @param key 缓存的键值
     * @param value 缓存的值
     * @param duration 过期时间
     */
    public static <T> void set(String key, T value, Duration duration) {
        if (duration.toMillis() <= 0) {
            getRBucket(key).set(value);
        }
        else {
            RBatch batch = ofRBatch();
            RBucketAsync<T> bucket = batch.getBucket(key);
            bucket.setAsync(value);
            bucket.expireAsync(duration);
            batch.execute();
        }
    }

    /**
     * 设置值并保持之前的过期时间，需要Redis 6.X以上版本.
     * @param key 缓存的键值
     * @param value 缓存的值
     */
    public static <T> void setAndKeepTtL(String key, T value) {
        RBucket<T> bucket = getRBucket(key);
        try {
            bucket.setAndKeepTTL(value);
        }
        catch (Exception e) {
            long timeToLive = bucket.remainTimeToLive();
            if (timeToLive == -1) {
                bucket.set(value);
            }
            else {
                set(key, value, Duration.ofMillis(timeToLive));
            }
        }
    }

    /**
     * 删除值通过key.
     * @param key key
     */
    public static void delete(String key) {
        getRBucket(key).delete();
    }

    /**
     * 获取当前值后并且删除.
     * @param key key
     */
    public static <T> T getAndDelete(String key) {
        RBucket<T> rBucket = getRBucket(key);
        return rBucket.getAndDelete();
    }

    /**
     * 删除值通过批量key
     * @param collection collection
     */
    public static void delete(Collection<?> collection) {
        RBatch batch = ofRBatch();
        collection.forEach(key -> {
            batch.getBucket(key.toString()).deleteAsync();
        });
        batch.execute();
    }

    /**
     * 检查对象是否存在
     * @return <code>true</code> if object exists and <code>false</code> otherwise
     */
    public static boolean isExists(String key) {
        return getRBucket(key).isExists();
    }

    /**
     * Obtain the RBucket.
     * @param name name of object
     * @return RBucket
     */
    private static <T> RBucket<T> getRBucket(String name) {
        return ofRedissonClient().getBucket(name);
    }

    /**
     * Obtain the RBuckets.
     * @return RBuckets
     */
    private static RBuckets getRBuckets() {
        return ofRedissonClient().getBuckets();
    }

    // ------------------------------- 管道 类型操作 --------------------------------

    /**
     * Obtain the RBatch
     * @return RBatch
     */
    private static RBatch ofRBatch() {
        return ofRedissonClient().createBatch();
    }

    // ------------------------------- 限流 类型操作 --------------------------------

    /**
     * 限流.
     * @param key 限流key
     * @param rateType 限流类型
     * @param rate 速率
     * @param rateInterval 速率间隔
     * @return -1 表示失败
     */
    public static long tryRateLimiter(String key, RateType rateType, long rate, long rateInterval) {
        RRateLimiter rateLimiter = getRRateLimiter(key);
        boolean trySetRateSuccess = rateLimiter.trySetRate(rateType, rate, Duration.ofMillis(rateInterval));
        // 第一次成功 拿锁后进行设置过期时间
        if (trySetRateSuccess) {
            // 设置过期时间，和速率一样，防止缓存残留
            rateLimiter.expire(Duration.ofMillis(rateInterval));
        }
        if (rateLimiter.tryAcquire()) {
            return rateLimiter.availablePermits();
        }
        else {
            return -1L;
        }
    }

    /**
     * Obtain the RRateLimiter.
     * @param name name of object
     * @return RRateLimiter
     */
    private static RRateLimiter getRRateLimiter(String name) {
        return ofRedissonClient().getRateLimiter(name);
    }

    // ------------------------------- 二进制流 类型操作 --------------------------------

    /**
     * Obtain the RBinaryStream.
     * @param name name of object
     * @return RBinaryStream
     */
    private static RBinaryStream getRBinaryStream(String name) {
        return ofRedissonClient().getBinaryStream(name);
    }

    // ------------------------------- List 类型操作 --------------------------------

    /**
     * 按指定索引删除对象
     * @param key 缓存的键值
     * @param index 索引
     */
    public static void removeList(String key, int index) {
        getRList(key).fastRemove(index);
    }

    /**
     * 从该列表中删除指定元素
     * @param key 缓存的键值
     * @param value 需要移除的值
     * @return 缓存的对象
     */
    public static <T> boolean removeList(String key, T value) {
        RList<T> rList = getRList(key);
        return rList.remove(value);
    }

    /**
     * 获取所有List缓存
     * @param key 缓存的键值
     * @return 缓存的对象
     */
    public static <T> List<T> getList(String key) {
        RList<T> rList = getRList(key);
        return rList.readAll();
    }

    /**
     * 通过索引获取List缓存
     * @param key 缓存的键值
     * @param indexes 索引
     * @return 缓存的对象
     */
    public static <T> List<T> getList(String key, final int... indexes) {
        RList<T> rList = getRList(key);
        return rList.get(indexes);
    }

    /**
     * 通过范围获取List缓存
     * @param key 缓存的键值
     * @param form 起始下标
     * @param to 截止下标
     * @return 缓存的对象
     */
    public static <T> List<T> getListRange(String key, int form, int to) {
        RList<T> rList = getRList(key);
        return rList.range(form, to);
    }

    /**
     * 缓存List缓存
     * @param key 缓存的键值
     * @param data 缓存的数据
     * @return 缓存的对象
     */
    public static <T> boolean addList(String key, final T data) {
        RList<T> rList = getRList(key);
        return rList.add(data);
    }

    /**
     * 设置List Key过期时间
     * @param key key
     * @param duration expiration duration
     * @return true or false
     */
    public static boolean expireList(String key, Duration duration) {
        return getRList(key).expire(duration);
    }

    /**
     * 缓存List数据
     * @param key 缓存的键值
     * @param dataList 待缓存的List数据
     * @return 缓存的对象
     */
    public static <T> boolean addList(String key, final List<T> dataList) {
        RList<T> rList = getRList(key);
        return rList.addAll(dataList);
    }

    /**
     * Obtain the getRList.
     * @param name name of object
     * @return getRList
     */
    private static <T> RList<T> getRList(String name) {
        return ofRedissonClient().getList(name);
    }

    // ------------------------------- Set 类型操作 --------------------------------

    /**
     * 去除Set缓存
     * @param key 缓存的key
     * @return set对象
     */
    public static <T> boolean removeSet(String key, T value) {
        RSet<T> rSet = getRSet(key);
        return rSet.remove(value);
    }

    /**
     * 获得All Set缓存
     * @param key 缓存的key
     * @return set对象
     */
    public static <T> Set<T> getAllSet(String key) {
        RSet<T> rSet = getRSet(key);
        return rSet.readAll();
    }

    /**
     * 缓存Set
     * @param key 缓存键值
     * @param dataSet 缓存的数据
     * @return 缓存数据的对象
     */
    public static <T> boolean addSet(String key, final Set<T> dataSet) {
        RSet<T> rSet = getRSet(key);
        return rSet.addAll(dataSet);
    }

    /**
     * 缓存Set数据
     * @param key 缓存的键值
     * @param data 待缓存的数据
     * @return 缓存的对象
     */
    public static <T> boolean addSet(String key, final T data) {
        RSet<T> rSet = getRSet(key);
        return rSet.add(data);
    }

    /**
     * 设置Set Key过期时间
     * @param key key
     * @param duration expiration duration
     * @return true or false
     */
    public static boolean expireSet(String key, Duration duration) {
        return getRSet(key).expire(duration);
    }

    /**
     * Obtain the RSet.
     * @param name name of object
     * @return RSet
     */
    private static <T> RSet<T> getRSet(String name) {
        return ofRedissonClient().getSet(name);
    }

    // ------------------------------- Map 类型操作 --------------------------------

    /**
     * clear map all
     * @param name name of object
     */
    public static void clearMap(String name) {
        getRMap(name).clear();
    }

    /**
     * remove map by key
     * @param name name of object
     * @param key key
     */
    public static void removeMap(String name, String key) {
        getRMap(name).remove(key);
    }

    /**
     * remove map by key and value
     * @param name name of object
     * @param key key
     * @param <T> T Object
     */
    public static <T> boolean removeMap(String name, String key, T value) {
        return getRMap(name).remove(key, value);
    }

    /**
     * get all v from ramp
     * @param name name of object
     * @param <T> T Object
     */
    public static <T> Map<String, T> getMapAll(String name) {
        RMap<String, T> rMap = getRMap(name);
        return rMap.readAllMap();
    }

    /**
     * get v by k from ramp
     * @param name name of object
     * @param key key
     * @param <T> T Object
     */
    public static <T> T getMap(String name, String key) {
        RMap<String, T> rMap = getRMap(name);
        return rMap.get(key);
    }

    /**
     * Stores k and v into ramp
     * @param name name of object
     * @param key key
     * @param value value
     * @param <T> T Object
     */
    public static <T> void addMap(String name, String key, T value) {
        RMap<String, T> rMap = getRMap(name);
        rMap.put(key, value);
    }

    /**
     * 设置Map Key过期时间
     * @param key key
     * @param duration expiration duration
     * @return true or false
     */
    public static boolean expireMap(String key, Duration duration) {
        return getRMap(key).expire(duration);
    }

    /**
     * Obtain the RMap.
     * @param name name of object
     * @return RMap
     */
    private static <K, V> RMap<K, V> getRMap(String name) {
        return ofRedissonClient().getMap(name);
    }

    // ------------------------------- MapCache 类型操作 --------------------------------

    /**
     * Obtain the RMapCache.
     * @param name name of object
     * @return RMapCache
     */
    private static <K, V> RMapCache<K, V> getRMapCache(String name) {
        return ofRedissonClient().getMapCache(name);
    }

    // ------------------------------- 原子Long 类型操作 --------------------------------

    /**
     * Obtain the RAtomicLong.
     * @param name name of object
     * @return RAtomicLong
     */
    private static RAtomicLong getRAtomicLong(String name) {
        return ofRedissonClient().getAtomicLong(name);
    }

    // ------------------------------- 字节 类型操作 --------------------------------

    /**
     * 返回设置为1的位数的数量.
     * @return 返回设置为1的位数的数量.
     */
    public static long getBitCardinality(String key) {
        return getRBitSet(key).cardinality();
    }

    /**
     * 返回设置的位数.
     * @return 返回设置的位数.
     */
    public static long getBitSize(String key) {
        return getRBitSet(key).size();
    }

    /**
     * 返回“逻辑大小”=最高集位的索引加一, 如果没有任何设置位，则返回零.
     * @return "logical size" = index of highest set bit plus one
     */
    public static long getBitLength(String key) {
        return getRBitSet(key).length();
    }

    /**
     * 获取key下BitSet对象
     * @return <code>BitSet</code>.
     */
    public static BitSet getBit(String key) {
        return getRBitSet(key).asBitSet();
    }

    /**
     * 获取key字节数组
     * @return <code>byte[]</code>.
     */
    public static byte[] getBitArray(String key) {
        return getRBitSet(key).toByteArray();
    }

    /**
     * 获取 bitIndex 位置的值, true or false
     * @param bitIndex - index of bit
     * @return <code>true</code> if bit set to one and <code>false</code> overwise.
     */
    public static boolean getBit(String key, long bitIndex) {
        return getRBitSet(key).get(bitIndex);
    }

    /**
     * 将 bitIndex 位置设置为0
     * @param bitIndex - index of bit
     * @return <code>true</code> - if previous value was true, <code>false</code> - if
     * previous value was false
     */
    public static boolean clearBit(String key, long bitIndex) {
        return getRBitSet(key).clear(bitIndex);
    }

    /**
     * Set all bits to zero
     */
    public static void clearBit(String key) {
        getRBitSet(key).clear();
    }

    /**
     * Set all bits to zero from <code>fromIndex</code> (inclusive) to
     * <code>toIndex</code> (exclusive)
     * @param fromIndex inclusive
     * @param toIndex exclusive
     *
     */
    public static void clearBit(String key, long fromIndex, long toIndex) {
        getRBitSet(key).clear(fromIndex, toIndex);
    }

    /**
     * 指定数组偏移量位置设置为指定值
     * @param indexArray The index array of bits that needs to be set to
     * <code>value</code>
     * @param value true = 1, false = 0
     */
    public static void addBit(String key, long[] indexArray, boolean value) {
        getRBitSet(key).set(indexArray, value);
    }

    /**
     * 指定范围偏移量位置设置为指定值
     * @param fromIndex inclusive
     * @param toIndex exclusive
     * @param value true = 1, false = 0
     */
    public static void addBit(String key, long bitIndex, long fromIndex, long toIndex, boolean value) {
        getRBitSet(key).set(fromIndex, toIndex, value);
    }

    /**
     * 指定范围偏移量位置设置为 1
     * @param fromIndex inclusive
     * @param toIndex exclusive
     */
    public static void addBit(String key, long bitIndex, long fromIndex, long toIndex) {
        getRBitSet(key).set(fromIndex, toIndex);
    }

    /**
     * 将 bitIndex 位置设置为指定值
     * @param bitIndex - index of bit
     * @param value true = 1, false = 0
     * @return <code>true</code> - if previous value was true, <code>false</code> - if
     * previous value was false
     */
    public static boolean addBit(String key, long bitIndex, boolean value) {
        return getRBitSet(key).set(bitIndex, value);
    }

    /**
     * 指定偏移量位置设置为 1
     * @param bitIndex - index of bit
     * @return <code>true</code> - if previous value was true, <code>false</code> - if
     * previous value was false
     */
    public static boolean addBit(String key, long bitIndex) {
        return getRBitSet(key).set(bitIndex);
    }

    /**
     * 设置Bit Key过期时间
     * @param key key
     * @param duration expiration duration
     * @return true or false
     */
    public static boolean expireBit(String key, Duration duration) {
        return getRBitSet(key).expire(duration);
    }

    /**
     * Obtain the RBitSet.
     * @param name name of object
     * @return RBitSet
     */
    private static RBitSet getRBitSet(String name) {
        return ofRedissonClient().getBitSet(name);
    }

    // ------------------------------- 地理位置GEO 类型操作 --------------------------------

    /**
     * 将指定的地理空间位置（纬度、经度、名称）添加到指定的key中.
     * @param key 名称KEY
     * @param lng 经度
     * @param lat 纬度
     * @param member 成员名称
     * @return 添加元素个数
     */
    public static Long geoAdd(String key, double lng, double lat, Object member) {
        RGeo<Object> geo = getRGeo(key);
        return geo.add(lng, lat, member);
    }

    /**
     * 将指定的地理空间位置（纬度、经度、名称）添加到指定的key中.
     * @param key 名称KEY
     * @param entries 包含精度、维度、成员集合
     * @return 添加元素个数
     */
    public static Long geoAdd(String key, GeoEntry... entries) {
        RGeo<String> geo = getRGeo(key);
        return geo.add(entries);
    }

    /**
     * 返回成员映射的GeoHash值.
     * @param key 名称KEY
     * @param members - objects
     * @return hash mapped by object
     */
    public static Map<String, String> hash(String key, String... members) {
        RGeo<String> geo = getRGeo(key);
        return geo.hash(members);
    }

    /**
     * 返回成员的地址位置信息.
     * @param key 名称KEY
     * @param members - objects
     * @return geo position mapped by object
     */
    public static Map<String, GeoPosition> position(String key, String... members) {
        RGeo<String> geo = getRGeo(key);
        return geo.pos(members);
    }

    /**
     * 返回指定两个对象的距离，通过指定距离单位，比如：米m，千米km，英里mi，英尺ft.
     * @param key 名称KEY
     * @param firstMember - first object
     * @param secondMember - second object
     * @param geoUnit - geo unit
     * @return distance
     */
    public static Double distance(String key, String firstMember, String secondMember, GeoUnit geoUnit) {
        RGeo<String> geo = getRGeo(key);
        return geo.dist(firstMember, secondMember, geoUnit);
    }

    /**
     * 返回成员周围半径内指定搜索条件内的排序集合, 默认升序.
     * @param key 名称KEY
     * @param member 成员
     * @param radius 单位内半径
     * @param geoUnit 单位
     * @param count 返回数量
     * @return 返回集合
     */
    public static List<String> search(String key, String member, double radius, GeoUnit geoUnit, int count) {
        RGeo<String> geo = getRGeo(key);
        GeoSearchArgs geoSearchArgs = buildRadiusGeoSearchArgs(member, 0, 0, radius, geoUnit, GeoOrder.ASC, count);
        return geo.search(geoSearchArgs);
    }

    /**
     * 返回成员周围半径内指定搜索条件内的排序集合.
     * @param key 名称KEY
     * @param member 成员
     * @param radius 单位内半径
     * @param geoUnit 单位
     * @param geoOrder 排序
     * @param count 返回数量
     * @return 返回集合
     */
    public static List<String> search(String key, String member, double radius, GeoUnit geoUnit, GeoOrder geoOrder,
            int count) {
        RGeo<String> geo = getRGeo(key);
        GeoSearchArgs geoSearchArgs = buildRadiusGeoSearchArgs(member, 0, 0, radius, geoUnit, geoOrder, count);
        return geo.search(geoSearchArgs);
    }

    /**
     * 返回经纬度周围半径内指定搜索条件的排序集合, 默认升序.
     * @param key 名称KEY
     * @param lng 经度
     * @param lat 维度
     * @param radius 单位内半径
     * @param geoUnit 单位
     * @param count 返回数量
     * @return 返回集合
     */
    public static List<String> search(String key, double lng, double lat, double radius, GeoUnit geoUnit, int count) {
        RGeo<String> geo = getRGeo(key);
        GeoSearchArgs geoSearchArgs = buildRadiusGeoSearchArgs(StringPools.EMPTY, lng, lat, radius, geoUnit,
                GeoOrder.ASC, count);
        return geo.search(geoSearchArgs);
    }

    /**
     * 返回经纬度周围半径内指定搜索条件的排序集合.
     * @param key 名称KEY
     * @param lng 经度
     * @param lat 维度
     * @param radius 单位内半径
     * @param geoUnit 单位
     * @param geoOrder 排序
     * @param count 返回数量
     * @return 返回集合
     */
    public static List<String> search(String key, double lng, double lat, double radius, GeoUnit geoUnit,
            GeoOrder geoOrder, int count) {
        RGeo<String> geo = getRGeo(key);
        GeoSearchArgs geoSearchArgs = buildRadiusGeoSearchArgs(StringPools.EMPTY, lng, lat, radius, geoUnit, geoOrder,
                count);
        return geo.search(geoSearchArgs);
    }

    /**
     * 返回经纬度周围矩形大小范围内指定搜索条件的排序集合.
     * @param key 名称KEY
     * @param member 成员
     * @param width 矩形宽度
     * @param height 矩形高度
     * @param geoUnit 单位
     * @param geoOrder 排序
     * @param count 返回数量
     * @return 返回集合
     */
    public static List<String> search(String key, String member, double width, double height, GeoUnit geoUnit,
            GeoOrder geoOrder, int count) {
        RGeo<String> geo = getRGeo(key);
        GeoSearchArgs geoSearchArgs = buildBoxGeoSearchArgs(member, 0, 0, width, height, geoUnit, geoOrder, count);
        return geo.search(geoSearchArgs);
    }

    /**
     * 返回经纬度周围矩形大小范围内指定搜索条件的排序集合.
     * @param key 名称KEY
     * @param lng 经度
     * @param lat 维度
     * @param width 矩形宽度
     * @param height 矩形高度
     * @param geoUnit 单位
     * @param geoOrder 排序
     * @param count 返回数量
     * @return 返回集合
     */
    public static List<String> search(String key, double lng, double lat, double width, double height, GeoUnit geoUnit,
            GeoOrder geoOrder, int count) {
        RGeo<String> geo = getRGeo(key);
        GeoSearchArgs geoSearchArgs = buildBoxGeoSearchArgs(StringPools.EMPTY, lng, lat, width, height, geoUnit,
                geoOrder, count);
        return geo.search(geoSearchArgs);
    }

    /**
     * 返回指定成员周围半径内指定搜索条件的元素, 并返回距离, 默认正序.
     * @param key 名称KEY
     * @param member 成员
     * @param radius 单位内半径
     * @param geoUnit 单位
     * @param count 返回数量
     * @return 返回集合
     */
    public static Map<String, Double> searchWithDistance(String key, String member, double radius, GeoUnit geoUnit,
            int count) {
        RGeo<String> geo = getRGeo(key);
        GeoSearchArgs geoSearchArgs = buildRadiusGeoSearchArgs(member, 0, 0, radius, geoUnit, GeoOrder.ASC, count);
        return geo.searchWithDistance(geoSearchArgs);
    }

    /**
     * 返回指定成员周围半径内指定搜索条件的元素，并返回距离.
     * @param key 名称KEY
     * @param member 成员
     * @param radius 单位内半径
     * @param geoUnit 单位
     * @param geoOrder 排序
     * @param count 返回数量
     * @return 返回集合
     */
    public static Map<String, Double> searchWithDistance(String key, String member, double radius, GeoUnit geoUnit,
            GeoOrder geoOrder, int count) {
        RGeo<String> geo = getRGeo(key);
        GeoSearchArgs geoSearchArgs = buildRadiusGeoSearchArgs(member, 0, 0, radius, geoUnit, geoOrder, count);
        return geo.searchWithDistance(geoSearchArgs);
    }

    /**
     * 返回经纬度周围半径内指定搜索条件的元素, 并返回距离, 默认正序.
     * @param key 名称KEY
     * @param lng 经度
     * @param lat 维度
     * @param radius 单位内半径
     * @param geoUnit 单位
     * @param count 返回数量
     * @return 返回集合
     */
    public static Map<String, Double> searchWithDistance(String key, double lng, double lat, double radius,
            GeoUnit geoUnit, int count) {
        RGeo<String> geo = getRGeo(key);
        GeoSearchArgs geoSearchArgs = buildRadiusGeoSearchArgs(StringPools.EMPTY, lng, lat, radius, geoUnit,
                GeoOrder.ASC, count);
        return geo.searchWithDistance(geoSearchArgs);
    }

    /**
     * 返回经纬度周围半径内指定搜索条件的元素，并返回距离.
     * @param key 名称KEY
     * @param lng 经度
     * @param lat 维度
     * @param radius 单位内半径
     * @param geoUnit 单位
     * @param geoOrder 排序
     * @param count 返回数量
     * @return 返回集合
     */
    public static Map<String, Double> searchWithDistance(String key, double lng, double lat, double radius,
            GeoUnit geoUnit, GeoOrder geoOrder, int count) {
        RGeo<String> geo = getRGeo(key);
        GeoSearchArgs geoSearchArgs = buildRadiusGeoSearchArgs(StringPools.EMPTY, lng, lat, radius, geoUnit, geoOrder,
                count);
        return geo.searchWithDistance(geoSearchArgs);
    }

    /**
     * 返回指定成员周围矩形大小内指定搜索条件的元素，并返回距离.
     * @param key 名称KEY
     * @param member 成员
     * @param width 矩形宽度
     * @param height 矩形高度
     * @param geoUnit 单位
     * @param geoOrder 排序
     * @param count 返回数量
     * @return 返回集合
     */
    public static Map<String, Double> searchWithDistance(String key, String member, double width, double height,
            GeoUnit geoUnit, GeoOrder geoOrder, int count) {
        RGeo<String> geo = getRGeo(key);
        GeoSearchArgs geoSearchArgs = buildBoxGeoSearchArgs(member, 0, 0, width, height, geoUnit, geoOrder, count);
        return geo.searchWithDistance(geoSearchArgs);
    }

    /**
     * 返回经纬度周围矩形大小内指定搜索条件的元素，并返回距离.
     * @param key 名称KEY
     * @param lng 经度
     * @param lat 维度
     * @param width 矩形宽度
     * @param height 矩形高度
     * @param geoUnit 单位
     * @param geoOrder 排序
     * @param count 返回数量
     * @return 返回集合
     */
    public static Map<String, Double> searchWithDistance(String key, double lng, double lat, double width,
            double height, GeoUnit geoUnit, GeoOrder geoOrder, int count) {
        RGeo<String> geo = getRGeo(key);
        GeoSearchArgs geoSearchArgs = buildBoxGeoSearchArgs(StringPools.EMPTY, lng, lat, width, height, geoUnit,
                geoOrder, count);
        return geo.searchWithDistance(geoSearchArgs);
    }

    /**
     * 返回指定成员周围半径内指定搜索条件的元素，并返回经纬度，默认正序.
     * @param key 名称KEY
     * @param member 成员
     * @param radius 单位内半径
     * @param geoUnit 单位
     * @param count 返回数量
     * @return 返回集合
     */
    public static Map<String, GeoPosition> searchWithPosition(String key, String member, double radius, GeoUnit geoUnit,
            int count) {
        RGeo<String> geo = getRGeo(key);
        GeoSearchArgs geoSearchArgs = buildRadiusGeoSearchArgs(member, 0, 0, radius, geoUnit, GeoOrder.ASC, count);
        return geo.searchWithPosition(geoSearchArgs);
    }

    /**
     * 返回指定成员周围半径内指定搜索条件的元素，并返回经纬度.
     * @param key 名称KEY
     * @param member 成员
     * @param radius 单位内半径
     * @param geoUnit 单位
     * @param geoOrder 排序
     * @param count 返回数量
     * @return 返回集合
     */
    public static Map<String, GeoPosition> searchWithPosition(String key, String member, double radius, GeoUnit geoUnit,
            GeoOrder geoOrder, int count) {
        RGeo<String> geo = getRGeo(key);
        GeoSearchArgs geoSearchArgs = buildRadiusGeoSearchArgs(member, 0, 0, radius, geoUnit, geoOrder, count);
        return geo.searchWithPosition(geoSearchArgs);
    }

    /**
     * 返回经纬度周围半径内指定搜索条件的元素，并返回经纬度，默认正序.
     * @param key 名称KEY
     * @param lng 经度
     * @param lat 维度
     * @param radius 单位内半径
     * @param geoUnit 单位
     * @param count 返回数量
     * @return 返回集合
     */
    public static Map<String, GeoPosition> searchWithPosition(String key, double lng, double lat, double radius,
            GeoUnit geoUnit, int count) {
        RGeo<String> geo = getRGeo(key);
        GeoSearchArgs geoSearchArgs = buildRadiusGeoSearchArgs(StringPools.EMPTY, lng, lat, radius, geoUnit,
                GeoOrder.ASC, count);
        return geo.searchWithPosition(geoSearchArgs);
    }

    /**
     * 返回经纬度周围半径内指定搜索条件的元素，并返回经纬度.
     * @param key 名称KEY
     * @param lng 经度
     * @param lat 维度
     * @param radius 单位内半径
     * @param geoUnit 单位
     * @param geoOrder 排序
     * @param count 返回数量
     * @return 返回集合
     */
    public static Map<String, GeoPosition> searchWithPosition(String key, double lng, double lat, double radius,
            GeoUnit geoUnit, GeoOrder geoOrder, int count) {
        RGeo<String> geo = getRGeo(key);
        GeoSearchArgs geoSearchArgs = buildRadiusGeoSearchArgs(StringPools.EMPTY, lng, lat, radius, geoUnit, geoOrder,
                count);
        return geo.searchWithPosition(geoSearchArgs);
    }

    /**
     * 返回指定成员周围矩形大小内指定搜索条件的元素，并返回经纬度.
     * @param key 名称KEY
     * @param member 成员
     * @param width 矩形宽度
     * @param height 矩形高度
     * @param geoUnit 单位
     * @param geoOrder 排序
     * @param count 返回数量
     * @return 返回集合
     */
    public static Map<String, GeoPosition> searchWithPosition(String key, String member, double width, double height,
            GeoUnit geoUnit, GeoOrder geoOrder, int count) {
        RGeo<String> geo = getRGeo(key);
        GeoSearchArgs geoSearchArgs = buildBoxGeoSearchArgs(member, 0, 0, width, height, geoUnit, geoOrder, count);
        return geo.searchWithPosition(geoSearchArgs);
    }

    /**
     * 返回经纬度周围矩形大小内指定搜索条件的元素，并返回经纬度.
     * @param key 名称KEY
     * @param lng 经度
     * @param lat 维度
     * @param width 矩形宽度
     * @param height 矩形高度
     * @param geoUnit 单位
     * @param geoOrder 排序
     * @param count 返回数量
     * @return 返回集合
     */
    public static Map<String, GeoPosition> searchWithPosition(String key, double lng, double lat, double width,
            double height, GeoUnit geoUnit, GeoOrder geoOrder, int count) {
        RGeo<String> geo = getRGeo(key);
        GeoSearchArgs geoSearchArgs = buildBoxGeoSearchArgs(StringPools.EMPTY, lng, lat, width, height, geoUnit,
                geoOrder, count);
        return geo.searchWithPosition(geoSearchArgs);
    }

    /**
     * 构造以成员或者经纬度周围半径大小查为询条件.
     * @param member 成员
     * @param lng 经度
     * @param lat 维度
     * @param radius 单位内半径
     * @param geoUnit 单位
     * @param geoOrder 排序
     * @param count 返回数量
     * @return 返回查询对象
     */
    private static GeoSearchArgs buildRadiusGeoSearchArgs(String member, double lng, double lat, double radius,
            GeoUnit geoUnit, GeoOrder geoOrder, int count) {
        if (StrUtil.isNotBlank(member)) {
            return GeoSearchArgs.from(member).radius(radius, geoUnit).order(geoOrder).count(count);
        }
        return GeoSearchArgs.from(lng, lat).radius(radius, geoUnit).order(geoOrder).count(count);
    }

    /**
     * 构造以成员或者经纬度周围矩形大小查为询条件.
     * @param member 成员
     * @param lng 经度
     * @param lat 维度
     * @param width 矩形宽度
     * @param height 矩形高度
     * @param geoUnit 单位
     * @param geoOrder 排序
     * @param count 返回数量
     * @return 返回查询对象
     */
    private static GeoSearchArgs buildBoxGeoSearchArgs(String member, double lng, double lat, double width,
            double height, GeoUnit geoUnit, GeoOrder geoOrder, int count) {
        if (StrUtil.isNotBlank(member)) {
            return GeoSearchArgs.from(member).box(width, height, geoUnit).order(geoOrder).count(count);
        }
        return GeoSearchArgs.from(lng, lat).box(width, height, geoUnit).order(geoOrder).count(count);
    }

    /**
     * Obtain the RGeo.
     * @param name name of object
     * @return RGeo
     */
    private static <T> RGeo<T> getRGeo(String name) {
        return ofRedissonClient().getGeo(name);
    }

    // ------------------------------- 可重入锁 类型操作 --------------------------------

    /**
     * tryLock by RLock.
     * @param lock the RLock object
     * @param waitTime the maximum time to acquire the lock
     * @param leaseTime lease time
     * @param unit time unit
     * @return <code>true</code> if lock is successfully acquired, otherwise
     * <code>false</code> if lock is already set.
     */
    public static boolean tryLock(RLock lock, long waitTime, long leaseTime, TimeUnit unit) {
        boolean tryLockSuccess = false;
        try {
            tryLockSuccess = lock.tryLock(waitTime, leaseTime, unit);
        }
        catch (InterruptedException e) {
            LOGGER.error("Exception tryLock", e);
        }
        return tryLockSuccess;
    }

    /**
     * Releases the lock.
     * @param lock the RLock Object
     */
    public static void unLock(RLock lock) {
        // 是否上锁 && 是否同一个线程
        if (lock.isLocked() && lock.isHeldByCurrentThread()) {
            lock.unlock();
        }
    }

    /**
     * Obtain RLock.
     * @param lockName the lock name
     * @return RLock object
     */
    public static RLock rLock(String lockName) {
        return ofRedissonClient().getLock(lockName);
    }

    // ------------------------------- 发布/订阅 类型操作 --------------------------------

    /**
     * 发布通道消息.
     * @param channelKey 通道key
     * @param msg 发送数据
     */
    public static <T> void publish(String channelKey, T msg) {
        RTopic topic = getRTopic(channelKey);
        topic.publish(msg);
    }

    /**
     * 发布通道消息.
     * @param channelKey 通道key
     * @param msg 发送数据
     * @param consumer 自定义处理
     */
    public static <T> void publish(String channelKey, T msg, Consumer<T> consumer) {
        RTopic topic = getRTopic(channelKey);
        topic.publish(msg);
        consumer.accept(msg);
    }

    /**
     * 订阅通道接收消息 - key 监听器需开启 `notify-keyspace-events` 等 redis 相关配置.
     * @param channelKey 通道key
     * @param clazz 消息类型
     * @param consumer 自定义处理
     * @return locally unique listener id
     */
    public static <T> int subscribe(String channelKey, Class<T> clazz, Consumer<T> consumer) {
        RTopic topic = getRTopic(channelKey);
        return topic.addListener(clazz, (channel, msg) -> consumer.accept(msg));
    }

    /**
     * Removes the listener by <code>id</code> for listening this topic.
     * @param channelKey 通道key
     * @param listenerIds - listener ids
     */
    public static void removeListener(String channelKey, Integer... listenerIds) {
        RTopic topic = getRTopic(channelKey);
        topic.removeListener(listenerIds);
    }

    /**
     * Obtain the RTopic.
     * @param name name of object
     * @return RTopic
     */
    private static RTopic getRTopic(String name) {
        return ofRedissonClient().getTopic(name);
    }

}
