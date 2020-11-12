package io.jboot.components.cache.support;

import com.jfinal.token.ITokenCache;
import com.jfinal.token.Token;
import io.jboot.Jboot;

import java.util.List;

public class JbootTokenCache implements ITokenCache {

    static final String JBOOT_TOKEN = "__jboot_token";

    @Override
    public void put(Token token) {
        Jboot.getCache().put(JBOOT_TOKEN, token.getId(), token, (int) ((token.getExpirationTime() - System.currentTimeMillis()) / 1000));
    }

    @Override
    public void remove(Token token) {
        Jboot.getCache().remove(JBOOT_TOKEN, token.getId());
    }

    @Override
    public boolean contains(Token token) {
        return Jboot.getCache().get(JBOOT_TOKEN, token.getId()) != null;
    }

    @Override
    public List<Token> getAll() {
        // 此处直接 return null 即可
        // 因为 JFinal 调用此方法的目的是为了去清除过期的 Token
        // 但是，通过 Jboot 缓存，配置上过期时间时，其在过期的时候自动进行清除了，不再需要 JFinal 进行再次清除
        return null;
    }
}
