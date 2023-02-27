package com.turing.customizablebees.proxy;

import com.turing.customizablebees.CustomizableBees;

public interface ClientRunnable extends Runnable {
    ClientRunnable BLANK = new ClientRunnable() {
        @Override
        public void run() {

        }
    };

    static void safeRun(ClientRunnable runnable) {
        if (CustomizableBees.proxy.isClient()) CustomizableBees.proxy.run(runnable);
    }

    @Override
    default void run() {

    }
}
