package com.danho.begone_spins;

import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.FormattedText;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.*;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.EntityEvent;
import net.minecraftforge.event.entity.living.LivingDropsEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;

import java.util.List;

import static net.minecraft.network.chat.Component.*;
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

    @SubscribeEvent
    public void onSpiderSpawn(final EntityEvent event)
    {
        Entity entity = event.getEntity();
        if (entity == null) return;

        EntityType entityType = entity.getType();
        List<EntityType> spiderTypes = List.of(EntityType.SPIDER, EntityType.CAVE_SPIDER);
        if (spiderTypes.contains(entityType))
            event.setCanceled(true);
    }

    @SubscribeEvent
    public void onZombieTypeKilled(final LivingDropsEvent event)
    {
        Entity entity = event.getEntity();
        if (entity == null) return;

        EntityType entityType = entity.getType();
        List<EntityType> zombieTypes = List.of(
                EntityType.ZOMBIE,
                EntityType.ZOMBIE_VILLAGER,
                EntityType.DROWNED,
                EntityType.HUSK,
                EntityType.ZOMBIFIED_PIGLIN);
        if (!zombieTypes.contains(entityType)) return;

        Entity entityCausedDeath = event.getSource().getEntity();
        if (!(entityCausedDeath instanceof Player player)) return;

        ItemStack mainHandItem = player.getMainHandItem();
        boolean shouldDropString = shouldDropItem(mainHandItem, 0.5); // 50% chance
        boolean shouldDropEye = shouldDropItem(mainHandItem, 0.1); // 10% chance

        if (shouldDropString) event.getDrops().add(createItemEntity(entity, Items.STRING, mainHandItem));
        if (shouldDropEye) event.getDrops().add(createItemEntity(entity, Items.SPIDER_EYE, mainHandItem));
    }

    private boolean shouldDropItem(ItemStack handItem, double chance) {
        Item item = handItem.getItem();
        if (item instanceof SwordItem || item instanceof AxeItem) {
            chance += 0.05;
        }

        int lootingLevel = handItem.getEnchantmentLevel(Enchantments.MOB_LOOTING);
        if (lootingLevel > 0) {
            chance += 0.1 * lootingLevel;
        }

        return Math.random() < chance;
    }

    private ItemEntity createItemEntity(Entity entity, Item item, ItemStack handItem) {
        int lootingLevel = handItem.getEnchantmentLevel(Enchantments.MOB_LOOTING);
        int count = 1 + (int) (Math.random() * lootingLevel);
        return new ItemEntity(entity.level(), entity.getX(), entity.getY(), entity.getZ(), new ItemStack(item, count));
    }
}
