package com.danho.begone_spins;

import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.EntityEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;

import java.util.List;
//import org.slf4j.Logger;
//import com.mojang.logging.LogUtils;

// The value here should match an entry in the META-INF/mods.toml file
@Mod(BegoneSpins.MOD_ID)
public class BegoneSpins
{
    // Define mod id in a common place for everything to reference
    public static final String MOD_ID = "begone_spins";
    // Directly reference a slf4j logger
    // private static final Logger LOGGER = LogUtils.getLogger();
    // Create a Deferred Register to hold Blocks which will all be registered under the "begone_spins" namespace
    public BegoneSpins()
    {
        IEventBus modEventBus = FMLJavaModLoadingContext.get().getModEventBus();

        // Register the commonSetup method for modloading
        modEventBus.addListener(this::commonSetup);

        // Register ourselves for server and other game events we are interested in
        MinecraftForge.EVENT_BUS.register(this);
    }

    private void commonSetup(final FMLCommonSetupEvent event)
    {

    }

    // Prevent spider spawn
    @SubscribeEvent
    public void onSpiderSpawn(final EntityEvent event)
    {
        Entity entity = event.getEntity();
        if (entity == null) return;

        EntityType entityType = entity.getType();
        List<EntityType> spiderTypes = List.of(EntityType.SPIDER, EntityType.CAVE_SPIDER);
        if (spiderTypes.contains(entityType))
        {
            event.setCanceled(true);
        }
    }
}
