/*
 * Copyright Vincent Blouin under the GPL License version 3
 */

package guru.bubl.service.conf;

import com.google.inject.AbstractModule;
import com.google.inject.name.Names;
import com.lambdaworks.redis.BitFieldArgs;
import com.lambdaworks.redis.RedisClient;
import com.lambdaworks.redis.RedisURI;
import com.lambdaworks.redis.SetArgs;
import com.lambdaworks.redis.api.StatefulRedisConnection;
import com.lambdaworks.redis.api.sync.RedisStringCommands;
import com.lambdaworks.redis.output.ValueStreamingChannel;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class PersistentSessionModule extends AbstractModule {

    Boolean isTesting;

    String redisUri;

    static PersistentSessionModule toTest(){
        return new PersistentSessionModule(true, null);
    }

    static PersistentSessionModule forProduction(String redisUri){
        return new PersistentSessionModule(false, redisUri);
    }
    private PersistentSessionModule(Boolean isTesting, String redisUri){
        this.isTesting = isTesting;
        this.redisUri = redisUri;
    }

    @Override
    protected void configure() {
        if(isTesting){
            bindForTesting();
        }else{
            bindNotTesting();
        }
    }

    private void bindForTesting(){
        HashMap<Object, Object> sessionHashMap = new HashMap<Object, Object>();
        bind(HashMap.class).annotatedWith(Names.named("session")).toInstance(sessionHashMap);
        bind(RedisStringCommands.class).toInstance(new RedisStringCommands() {
            @Override
            public Long append(Object o, Object o2) {
                return null;
            }

            @Override
            public Long bitcount(Object o) {
                return null;
            }

            @Override
            public Long bitcount(Object o, long l, long l1) {
                return null;
            }

            @Override
            public List<Long> bitfield(Object o, BitFieldArgs bitFieldArgs) {
                return null;
            }

            @Override
            public Long bitpos(Object o, boolean b) {
                return null;
            }

            @Override
            public Long bitpos(Object o, boolean b, long l, long l1) {
                return null;
            }

            @Override
            public Long bitopAnd(Object o, Object[] objects) {
                return null;
            }

            @Override
            public Long bitopNot(Object o, Object k1) {
                return null;
            }

            @Override
            public Long bitopOr(Object o, Object[] objects) {
                return null;
            }

            @Override
            public Long bitopXor(Object o, Object[] objects) {
                return null;
            }

            @Override
            public Long decr(Object o) {
                return null;
            }

            @Override
            public Long decrby(Object o, long l) {
                return null;
            }

            @Override
            public Object get(Object o) {
                return sessionHashMap.get(o);
            }

            @Override
            public Long getbit(Object o, long l) {
                return null;
            }

            @Override
            public Object getrange(Object o, long l, long l1) {
                return null;
            }

            @Override
            public Object getset(Object o, Object o2) {
                return null;
            }

            @Override
            public Long incr(Object o) {
                return null;
            }

            @Override
            public Long incrby(Object o, long l) {
                return null;
            }

            @Override
            public Double incrbyfloat(Object o, double v) {
                return null;
            }

            @Override
            public List mget(Object[] objects) {
                return null;
            }

            @Override
            public Long mget(ValueStreamingChannel valueStreamingChannel, Object[] objects) {
                return null;
            }

            @Override
            public String mset(Map map) {
                return null;
            }

            @Override
            public Boolean msetnx(Map map) {
                return null;
            }

            @Override
            public String set(Object o, Object o2) {
                sessionHashMap.put(
                        o,
                        o2
                );
                return "";
            }

            @Override
            public String set(Object o, Object o2, SetArgs setArgs) {
                return null;
            }

            @Override
            public Long setbit(Object o, long l, int i) {
                return null;
            }

            @Override
            public String setex(Object o, long l, Object o2) {
                return null;
            }

            @Override
            public String psetex(Object o, long l, Object o2) {
                return null;
            }

            @Override
            public Boolean setnx(Object o, Object o2) {
                return null;
            }

            @Override
            public Long setrange(Object o, long l, Object o2) {
                return null;
            }

            @Override
            public Long strlen(Object o) {
                return null;
            }
        });
    }

    private void bindNotTesting(){
        RedisClient redisClient = new RedisClient(
                RedisURI.create(redisUri)
        );
        bind(RedisClient.class).toInstance(
                redisClient
        );
        StatefulRedisConnection<String, String> connection = redisClient.connect();
        bind(RedisStringCommands.class).toInstance(
                connection.sync()
        );
    }

}
