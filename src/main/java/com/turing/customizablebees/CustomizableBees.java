package com.turing.customizablebees;

import com.turing.customizablebees.api.APIHelper;
import com.turing.customizablebees.api.groovyscript.GroovyscriptHelper;
import com.turing.customizablebees.bees.CustomBees;
import com.turing.customizablebees.items.CombItem;
import com.turing.customizablebees.network.Messages;
import com.turing.customizablebees.proxy.Proxy;
import forestry.api.recipes.RecipeManagers;
import net.minecraft.item.crafting.IRecipe;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.fml.common.Loader;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.SidedProxy;
import net.minecraftforge.fml.common.event.FMLConstructionEvent;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.registry.ForgeRegistries;
import net.minecraftforge.oredict.OreDictionary;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@Mod(modid = CustomizableBees.MODID, dependencies = "required-after:forestry;" + "after:crafttweaker;" + "before:groovyscript;")
public class CustomizableBees {
    public static final String MODID = "customizablebees";
    public static final Logger LOGGER = LogManager.getLogger(MODID);

    @SidedProxy(serverSide = "com.turing.customizablebees.proxy.Proxy", clientSide = "com.turing.customizablebees.proxy.ClientProxy")
    public static Proxy proxy;

    @Mod.EventHandler
    public void construction(FMLConstructionEvent event) {
        MinecraftForge.EVENT_BUS.register(this);
        if (Loader.isModLoaded("groovyscript")) GroovyscriptHelper.construction();
    }

    @Mod.EventHandler
    public void preInit(FMLPreInitializationEvent event) {
        MinecraftForge.EVENT_BUS.post(new BeeDefinitionEvent.DefineEffects());
        MinecraftForge.EVENT_BUS.post(new BeeCombEvent.Define());
        APIHelper.COMBS.forEach(comb -> {
            comb.onRegister();
            ForgeRegistries.ITEMS.register(comb);
        });
        MinecraftForge.EVENT_BUS.post(new BeeCombEvent.OnRegister());

        MinecraftForge.EVENT_BUS.post(new BeeDefinitionEvent.Pre());
        CustomBees.preInit();
        proxy.preInit();
        Messages.registerMessages(MODID);
        MinecraftForge.EVENT_BUS.post(new BeeDefinitionEvent.Post());
    }

    @SubscribeEvent
    public void recipes(RegistryEvent.Register<IRecipe> event) {
        MinecraftForge.EVENT_BUS.post(new BeeCombEvent.DefineRecipes());
        MinecraftForge.EVENT_BUS.post(new BeeCombEvent.RegisterRecipes());
        APIHelper.COMBS.forEach(comb -> {
            comb.recipes.forEach((type, products) -> RecipeManagers.centrifugeManager.addRecipe(20, comb.getStackFromType(type), products));
            comb.recipes.clear();
        });
    }

    @Mod.EventHandler
    public void init(FMLInitializationEvent event) {
        CombItem.ALL_COMB_TYPES.forEach((comb, types) -> types.forEach(type -> OreDictionary.registerOre("beeCombs", comb.getStackFromType(type))));
        if (Loader.isModLoaded("groovyscript")) GroovyscriptHelper.init();
        CustomBees.init();
        proxy.init();
    }
}
