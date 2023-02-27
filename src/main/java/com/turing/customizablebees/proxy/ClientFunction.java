package com.turing.customizablebees.proxy;

import com.turing.customizablebees.CustomizableBees;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import java.util.function.Function;

public interface ClientFunction<T, R> extends Function<T, R> {
    @Override
    default R apply(T t) {
        return CustomizableBees.proxy.apply(this, t);
    }

    @SideOnly(Side.CLIENT)
    R applyClient(T t);

    @SideOnly(Side.SERVER)
    R applyServer(T t);

    abstract class RunnableProvider<T> implements ClientFunction<T, ClientRunnable> {
        @Override
        @SideOnly(Side.CLIENT)
        public ClientRunnable applyClient(T t) {
            return ClientRunnable.BLANK;
        }

        @Override
        @SideOnly(Side.SERVER)
        public ClientRunnable applyServer(T t) {
            return new ClientRunnable() {
                @Override
                @SideOnly(Side.SERVER)
                public void run() {
                    RunnableProvider.this.tick(t);
                }
            };
        }

        @SideOnly(Side.SERVER)
        public abstract void tick(T t);
    }
}
