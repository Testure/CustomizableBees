package com.turing.customizablebees.proxy;

import com.google.common.util.concurrent.ListenableFuture;
import com.turing.customizablebees.api.APIHelper;
import com.turing.customizablebees.bees.CustomBeeModel;
import forestry.core.config.Constants;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.block.model.ModelBakery;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.model.ModelLoader;

public class ClientProxy extends Proxy {
    @Override
    public boolean isClient() {
        return true;
    }

    @Override
    public void run(ClientRunnable runnable) {
        runnable.run();
    }

    @Override
    public <T, R> R apply(ClientFunction<T, R> function, T t) {
        return function.applyClient(t);
    }

    @Override
    public ListenableFuture<Object> addScheduledTaskClient(Runnable runnable) {
        return Minecraft.getMinecraft().addScheduledTask(runnable);
    }

    @Override
    public void preInit() {
        APIHelper.COMBS.forEach(comb -> {
            ModelResourceLocation location = new ModelResourceLocation(new ResourceLocation(Constants.MOD_ID, "bee_combs"), "inventory");
            ModelLoader.setCustomModelResourceLocation(comb, 0, location);
            ModelBakery.registerItemVariants(comb, location);
            ModelLoader.setCustomMeshDefinition(comb, stack -> location);
        });

        CustomBeeModel.SUFFIX_MAP.forEach(CustomBeeModel::registerModels);
    }

    @Override
    public void init() {
        APIHelper.COMBS.forEach(comb -> Minecraft.getMinecraft().getItemColors().registerItemColorHandler(comb.getItemColor(), comb));
    }
}
