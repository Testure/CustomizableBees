package com.turing.customizablebees.proxy;

import com.google.common.util.concurrent.ListenableFuture;

public class Proxy {
    public void preInit() {

    }

    public void init() {

    }

    public boolean isClient() {
        return false;
    }

    public void run(ClientRunnable runnable) {

    }

    public <T, R> R apply(ClientFunction<T, R> function, T t) {
        return function.applyServer(t);
    }

    public ListenableFuture<Object> addScheduledTaskClient(Runnable runnable) {
        throw new UnsupportedOperationException("This should only be run on the client");
    }
}
