package com.danho.begone_spins;

import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.*;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraftforge.client.event.sound.PlaySoundEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.EntityEvent;
import net.minecraftforge.event.entity.living.LivingDropsEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import java.util.List;

// The value here should match an entry in the META-INF/mods.toml file
@Mod(BegoneSpins.MOD_ID)
public class BegoneSpins
{
    // Define mod id in a common place for everything to reference
    public static final String MOD_ID = "begone_spins";
    public BegoneSpins()
    {
        // Register ourselves for server and other game events we are interested in
        MinecraftForge.EVENT_BUS.register(this);
    }

    /**
     * Prevents spider sounds from playing
     * @param event Sound event
     */
    @SubscribeEvent
    public void onSoundPlayed(final PlaySoundEvent event)
    {
        // If the sound name contains "spider", cancel the event
        if (event.getName().contains("spider")) {
            event.setCanceled(true);
        }
    }

    /**
     * Prevents spiders from spawning
     * @param event Entity event
     */
    @SubscribeEvent
    public void onSpiderSpawn(final EntityEvent event)
    {
        // If no entity is being spawned, return
        Entity entity = event.getEntity();
        if (entity == null) return;

        // If the entity is a type of spider, cancel the event
        EntityType entityType = entity.getType();
        List<EntityType> spiderTypes = List.of(EntityType.SPIDER, EntityType.CAVE_SPIDER);
        if (spiderTypes.contains(entityType))
            event.setCanceled(true);
    }

    /**
     * Makes zombies drop string and spider eyes
     * @param event Living drops event
     */
    @SubscribeEvent
    public void onZombieTypeKilled(final LivingDropsEvent event)
    {
        // If no entity is being killed, return
        Entity entity = event.getEntity();
        if (entity == null) return;

        // If the entity is not a type of zombie, return
        EntityType entityType = entity.getType();
        List<EntityType> zombieTypes = List.of(
                EntityType.ZOMBIE,
                EntityType.ZOMBIE_VILLAGER,
                EntityType.DROWNED,
                EntityType.HUSK,
                EntityType.ZOMBIFIED_PIGLIN);
        if (!zombieTypes.contains(entityType)) return;

        // If the entity was not killed by a player, return
        Entity entityCausedDeath = event.getSource().getEntity();
        if (!(entityCausedDeath instanceof Player player)) return;

        // Determine if mob should drop string and/or spider eye depending on random chance and player's hand item
        ItemStack mainHandItem = player.getMainHandItem();
        boolean shouldDropString = shouldDropItem(mainHandItem, 0.5); // 50% base chance
        boolean shouldDropEye = shouldDropItem(mainHandItem, 0.1); // 10% base chance

        // Add item entities to drops, if applicable
        if (shouldDropString) event.getDrops().add(createItemEntity(entity, Items.STRING, mainHandItem));
        if (shouldDropEye) event.getDrops().add(createItemEntity(entity, Items.SPIDER_EYE, mainHandItem));
    }

    /**
     * Determines if a mob should drop an item based on the player's hand item and a random chance
     * @param handItem The player's hand item
     * @param chance The base chance of the item dropping
     * @return Whether the item should drop
     */
    private boolean shouldDropItem(ItemStack handItem, double chance) {
        // Increase chance if player is holding a sword or axe
        Item item = handItem.getItem();
        if (item instanceof SwordItem || item instanceof AxeItem) {
            chance += 0.05;
        }

        // Increase chance if player has looting enchantment
        int lootingLevel = handItem.getEnchantmentLevel(Enchantments.MOB_LOOTING);
        if (lootingLevel > 0) {
            chance += 0.1 * lootingLevel;
        }

        // Return whether the random chance was met
        return Math.random() < chance;
    }

    /**
     * Create an ItemEntity to add to the drops
     * @param entity The entity to create the item entity at
     * @param item The item to create the item entity with
     * @param handItem The player's hand item
     * @return The created item entity
     */
    private ItemEntity createItemEntity(Entity entity, Item item, ItemStack handItem) {
        // Modify item drop count based on looting enchantment
        int lootingLevel = handItem.getEnchantmentLevel(Enchantments.MOB_LOOTING);
        int count = 1 + (int) (Math.random() * lootingLevel);
        return new ItemEntity(entity.level(), entity.getX(), entity.getY(), entity.getZ(), new ItemStack(item, count));
    }
}
