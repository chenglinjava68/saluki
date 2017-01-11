/*
 * Copyright (c) 2017, Quancheng-ec.com All right reserved. This software is the
 * confidential and proprietary information of Quancheng-ec.com ("Confidential
 * Information"). You shall not disclose such Confidential Information and shall
 * use it only in accordance with the terms of the license agreement you entered
 * into with Quancheng-ec.com.
 */
package com.quancheng.saluki.core.grpc.router.internal;

import java.util.List;

import javax.script.Invocable;
import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.quancheng.saluki.core.common.GrpcURL;
import com.quancheng.saluki.core.grpc.router.GrpcRouter;

/**
 * @author shimingliu 2017年1月9日 下午2:24:56
 * @version ScriptRouter.java, v 0.0.1 2017年1月9日 下午2:24:56 shimingliu
 */
public class ScriptRouter extends GrpcRouter {

    private static final Logger log = LoggerFactory.getLogger(ScriptRouter.class);

    private final ScriptEngine  engine;

    public ScriptRouter(GrpcURL url, String type, String rule){
        super(url, rule);
        engine = new ScriptEngineManager().getEngineByName(type);
        if (engine == null) {
            throw new IllegalStateException(new IllegalStateException("Unsupported route rule type: " + type
                                                                      + ", rule: " + rule));
        }
    }

    @Override
    protected void parseRouter() {
        // do nothing
    }

    @Override
    public boolean match(List<GrpcURL> providerUrls) {
        String rule = super.getRule();
        try {
            engine.eval(super.getRule());
            Invocable invocable = (Invocable) engine;
            GrpcURL refUrl = super.getUrl();
            Object obj = invocable.invokeFunction("route", refUrl, providerUrls);
            if (obj instanceof Boolean) {
                return (Boolean) obj;
            } else {
                return true;
            }
        } catch (ScriptException | NoSuchMethodException e) {
            log.error("route error , rule has been ignored. rule: " + rule + ", url: " + providerUrls, e);
            return true;
        }
    }

}
