package com.turing.customizablebees.bees;

import com.turing.customizablebees.proxy.ClientRunnable;
import forestry.api.apiculture.EnumBeeType;
import forestry.api.apiculture.IAlleleBeeSpecies;
import forestry.api.apiculture.IBeeModelProvider;
import forestry.api.core.IModelManager;
import forestry.api.genetics.AlleleManager;
import forestry.core.config.Constants;
import net.minecraft.client.renderer.block.model.ModelBakery;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.item.Item;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.apache.logging.log4j.util.TriConsumer;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class CustomBeeModel implements IBeeModelProvider {
    public static final Map<String, String> SUFFIX_MAP = new HashMap<>();
    public static TriConsumer<String, String, CustomBeeModel> modelCreationHook;

    @SideOnly(Side.CLIENT)
    public static IAlleleBeeSpecies throwback;

    @SideOnly(Side.CLIENT)
    public ModelResourceLocation droneLocation;
    @SideOnly(Side.CLIENT)
    public ModelResourceLocation princessLocation;
    @SideOnly(Side.CLIENT)
    public ModelResourceLocation queenLocation;
    public final String suffix;
    public final String modid;
    public final boolean background;

    public CustomBeeModel(final String modid, final String suffix) {
        this(modid, suffix, false);
    }

    public CustomBeeModel(final String modid, final String suffix, boolean background) {
        this.modid = modid;
        this.suffix = suffix;
        this.background = background;
        SUFFIX_MAP.put(modid, suffix);

        ClientRunnable.safeRun(new ClientRunnable() {
            @Override
            @SideOnly(Side.CLIENT)
            public void run() {
                droneLocation = modelLocation(modid, "drone", suffix);
                princessLocation = modelLocation(modid, "princess", suffix);
                queenLocation = modelLocation(modid, "queen", suffix);
                registerModels(modid, suffix);
            }
        });

        if (modelCreationHook != null) modelCreationHook.accept(modid, suffix, this);
    }

    @SideOnly(Side.CLIENT)
    public static void registerModels(String modid, String suffix) {
        doRegister(modid, suffix, "drone", "bee_drone_ge");
        doRegister(modid, suffix, "princess", "bee_princess_ge");
        doRegister(modid, suffix, "queen", "bee_queen_ge");
    }

    private static void doRegister(String modid, String suffix, String queen, String beeGE) {
        ModelBakery.registerItemVariants(
                Objects.requireNonNull(Item.REGISTRY.getObject(new ResourceLocation("forestry", beeGE))),
                CustomBeeModel.resourceLocation(modid, queen, suffix)
        );
    }

    @SideOnly(Side.CLIENT)
    public static ResourceLocation resourceLocation(String modid, String name, String mining) {
        return new ResourceLocation(modid + ":bees/" + name + "_" + mining);
    }

    @SideOnly(Side.CLIENT)
    public ModelResourceLocation modelLocation(String modid, String name, String mining) {
        return new ModelResourceLocation(modid + ":bees/" + name + "_" + mining, "inventory");
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void registerModels(Item item, IModelManager manager) {

    }

    @Override
    @SideOnly(Side.CLIENT)
    public ModelResourceLocation getModel(EnumBeeType type) {
        switch (type) {
            case DRONE: return droneLocation;
            case PRINCESS: return princessLocation;
            case QUEEN: return queenLocation;
        }
        if (throwback == null) throwback = Objects.requireNonNull((IAlleleBeeSpecies) AlleleManager.alleleRegistry.getAllele(Constants.MOD_ID + ".speciesForest"));
        return throwback.getModel(type);
    }
}
