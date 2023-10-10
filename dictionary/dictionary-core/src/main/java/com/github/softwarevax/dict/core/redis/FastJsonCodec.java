package com.github.softwarevax.dict.core.redis;

import com.alibaba.fastjson.JSON;
import com.github.softwarevax.dict.core.utils.StringUtils;
import io.lettuce.core.codec.RedisCodec;
import io.lettuce.core.codec.StringCodec;

import java.nio.ByteBuffer;
import java.util.List;
import java.util.Map;

public class FastJsonCodec implements RedisCodec<String, List> {

    private String prefix;

    private StringCodec codec = new StringCodec();

    public FastJsonCodec(String prefix) {
        this.prefix = prefix;
    }

    @Override
    public String decodeKey(ByteBuffer byteBuffer) {
        String key = codec.decodeKey(byteBuffer);
        return key.substring(prefix.length() + 1, key.length());
    }

    @Override
    public List<Map<String, Object>> decodeValue(ByteBuffer byteBuffer) {
        return JSON.parseObject(codec.decodeKey(byteBuffer), List.class);
    }

    @Override
    public ByteBuffer encodeKey(String k) {
        String key = StringUtils.join(":", prefix, k);
        return codec.encodeKey(key);
    }

    @Override
    public ByteBuffer encodeValue(List v) {
        return codec.encodeKey(JSON.toJSONString(v));
    }
}
