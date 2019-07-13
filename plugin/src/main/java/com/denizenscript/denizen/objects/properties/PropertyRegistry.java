package com.denizenscript.denizen.objects.properties;

import com.denizenscript.denizen.objects.*;
import com.denizenscript.denizen.objects.properties.bukkit.BukkitElementProperties;
import com.denizenscript.denizen.objects.properties.bukkit.BukkitListProperties;
import com.denizenscript.denizen.objects.properties.bukkit.BukkitQueueProperties;
import com.denizenscript.denizen.objects.properties.bukkit.BukkitScriptProperties;
import com.denizenscript.denizen.objects.properties.entity.*;
import com.denizenscript.denizen.objects.properties.inventory.InventoryContents;
import com.denizenscript.denizen.objects.properties.inventory.InventoryHolder;
import com.denizenscript.denizen.objects.properties.inventory.InventorySize;
import com.denizenscript.denizen.objects.properties.inventory.InventoryTitle;
import com.denizenscript.denizen.objects.properties.item.*;
import com.denizenscript.denizen.objects.properties.material.*;
import com.denizenscript.denizen.objects.properties.trade.*;
import com.denizenscript.denizen.nms.NMSHandler;
import com.denizenscript.denizen.nms.NMSVersion;
import com.denizenscript.denizencore.objects.core.ElementTag;
import com.denizenscript.denizencore.objects.core.ListTag;
import com.denizenscript.denizencore.objects.core.QueueTag;
import com.denizenscript.denizencore.objects.core.ScriptTag;
import com.denizenscript.denizencore.objects.properties.PropertyParser;

public class PropertyRegistry {

    public static void registermainProperties() {
        // register properties that add Bukkit code to core objects
        PropertyParser.registerProperty(BukkitScriptProperties.class, ScriptTag.class);
        PropertyParser.registerProperty(BukkitQueueProperties.class, QueueTag.class);
        PropertyParser.registerProperty(BukkitElementProperties.class, ElementTag.class);
        PropertyParser.registerProperty(BukkitListProperties.class, ListTag.class);

        // register core dEntity properties
        PropertyParser.registerProperty(EntityAge.class, dEntity.class);
        PropertyParser.registerProperty(EntityAI.class, dEntity.class);
        PropertyParser.registerProperty(EntityAnger.class, dEntity.class);
        PropertyParser.registerProperty(EntityAngry.class, dEntity.class);
        PropertyParser.registerProperty(EntityAreaEffectCloud.class, dEntity.class);
        PropertyParser.registerProperty(EntityArmorBonus.class, dEntity.class);
        PropertyParser.registerProperty(EntityArrowDamage.class, dEntity.class);
        PropertyParser.registerProperty(EntityInvulnerable.class, dEntity.class);
        PropertyParser.registerProperty(EntityBoatType.class, dEntity.class);
        PropertyParser.registerProperty(EntityArmorPose.class, dEntity.class);
        PropertyParser.registerProperty(EntityArms.class, dEntity.class);
        PropertyParser.registerProperty(EntityBasePlate.class, dEntity.class);
        PropertyParser.registerProperty(EntityBeamTarget.class, dEntity.class);
        PropertyParser.registerProperty(EntityBodyArrows.class, dEntity.class);
        PropertyParser.registerProperty(EntityBoundingBox.class, dEntity.class);
        PropertyParser.registerProperty(EntityChestCarrier.class, dEntity.class);
        PropertyParser.registerProperty(EntityColor.class, dEntity.class);
        PropertyParser.registerProperty(EntityCritical.class, dEntity.class);
        PropertyParser.registerProperty(EntityCustomName.class, dEntity.class);
        PropertyParser.registerProperty(EntityDisabledSlots.class, dEntity.class);
        PropertyParser.registerProperty(EntityPotionEffects.class, dEntity.class);
        PropertyParser.registerProperty(EntityElder.class, dEntity.class);
        PropertyParser.registerProperty(EntityEquipment.class, dEntity.class);
        PropertyParser.registerProperty(EntityExplosionFire.class, dEntity.class);
        PropertyParser.registerProperty(EntityExplosionRadius.class, dEntity.class);
        PropertyParser.registerProperty(EntityFirework.class, dEntity.class);
        PropertyParser.registerProperty(EntityFramed.class, dEntity.class);
        PropertyParser.registerProperty(EntityGravity.class, dEntity.class);
        PropertyParser.registerProperty(EntityHealth.class, dEntity.class);
        PropertyParser.registerProperty(EntityInfected.class, dEntity.class);
        PropertyParser.registerProperty(EntityInventory.class, dEntity.class);
        PropertyParser.registerProperty(EntityIsShowingBottom.class, dEntity.class);
        PropertyParser.registerProperty(EntityItem.class, dEntity.class);
        PropertyParser.registerProperty(EntityJumpStrength.class, dEntity.class);
        PropertyParser.registerProperty(EntityKnockback.class, dEntity.class);
        PropertyParser.registerProperty(EntityMarker.class, dEntity.class);
        PropertyParser.registerProperty(EntityMaxFuseTicks.class, dEntity.class);
        PropertyParser.registerProperty(EntityPainting.class, dEntity.class);
        PropertyParser.registerProperty(EntityPickupStatus.class, dEntity.class);
        PropertyParser.registerProperty(EntityPotion.class, dEntity.class);
        PropertyParser.registerProperty(EntityPowered.class, dEntity.class);
        PropertyParser.registerProperty(EntityProfession.class, dEntity.class);
        if (NMSHandler.getVersion().isAtLeast(NMSVersion.v1_13_R2)) {
            PropertyParser.registerProperty(EntityRiptide.class, dEntity.class);
        }
        PropertyParser.registerProperty(EntityRotation.class, dEntity.class);
        PropertyParser.registerProperty(EntitySmall.class, dEntity.class);
        PropertyParser.registerProperty(EntitySilent.class, dEntity.class);
        PropertyParser.registerProperty(EntitySitting.class, dEntity.class);
        PropertyParser.registerProperty(EntitySize.class, dEntity.class);
        PropertyParser.registerProperty(EntitySkeleton.class, dEntity.class);
        PropertyParser.registerProperty(EntitySpeed.class, dEntity.class);
        PropertyParser.registerProperty(EntitySpell.class, dEntity.class);
        PropertyParser.registerProperty(EntityTame.class, dEntity.class);
        PropertyParser.registerProperty(EntityTrades.class, dEntity.class);
        PropertyParser.registerProperty(EntityVisible.class, dEntity.class);

        // register core dInventory properties
        PropertyParser.registerProperty(InventoryHolder.class, dInventory.class); // Holder must be loaded first to initiate correctly
        PropertyParser.registerProperty(InventorySize.class, dInventory.class); // Same with size... (too small for contents)
        PropertyParser.registerProperty(InventoryContents.class, dInventory.class);
        PropertyParser.registerProperty(InventoryTitle.class, dInventory.class);

        // register core dItem properties
        PropertyParser.registerProperty(ItemApple.class, dItem.class);
        PropertyParser.registerProperty(ItemBaseColor.class, dItem.class);
        PropertyParser.registerProperty(ItemBook.class, dItem.class);
        PropertyParser.registerProperty(ItemDisplayname.class, dItem.class);
        PropertyParser.registerProperty(ItemDurability.class, dItem.class);
        PropertyParser.registerProperty(ItemCanDestroy.class, dItem.class);
        PropertyParser.registerProperty(ItemCanPlaceOn.class, dItem.class);
        PropertyParser.registerProperty(ItemColor.class, dItem.class);
        if (NMSHandler.getVersion().isAtLeast(NMSVersion.v1_14_R1)) {
            PropertyParser.registerProperty(ItemCustomModel.class, dItem.class);
            PropertyParser.registerProperty(ItemChargedProjectile.class, dItem.class);
        }
        PropertyParser.registerProperty(ItemEnchantments.class, dItem.class);
        PropertyParser.registerProperty(ItemFirework.class, dItem.class);
        PropertyParser.registerProperty(ItemFlags.class, dItem.class);
        PropertyParser.registerProperty(ItemInventory.class, dItem.class);
        PropertyParser.registerProperty(ItemLock.class, dItem.class);
        PropertyParser.registerProperty(ItemLore.class, dItem.class);
        PropertyParser.registerProperty(ItemMap.class, dItem.class);
        PropertyParser.registerProperty(ItemNBT.class, dItem.class);
        PropertyParser.registerProperty(ItemAttributeNBT.class, dItem.class);
        PropertyParser.registerProperty(ItemPatterns.class, dItem.class);
        PropertyParser.registerProperty(ItemPlantgrowth.class, dItem.class);
        PropertyParser.registerProperty(ItemPotion.class, dItem.class);
        PropertyParser.registerProperty(ItemQuantity.class, dItem.class);
        PropertyParser.registerProperty(ItemRepairCost.class, dItem.class);
        PropertyParser.registerProperty(ItemScript.class, dItem.class);
        PropertyParser.registerProperty(ItemSignContents.class, dItem.class);
        PropertyParser.registerProperty(ItemSkullskin.class, dItem.class);
        PropertyParser.registerProperty(ItemSpawnEgg.class, dItem.class);
        PropertyParser.registerProperty(ItemUnbreakable.class, dItem.class);

        // register core dMaterial properties
        if (NMSHandler.getVersion().isAtLeast(NMSVersion.v1_13_R2)) {
            PropertyParser.registerProperty(MaterialAge.class, dMaterial.class);
            PropertyParser.registerProperty(MaterialDirectional.class, dMaterial.class);
            PropertyParser.registerProperty(MaterialHalf.class, dMaterial.class);
            PropertyParser.registerProperty(MaterialLevel.class, dMaterial.class);
            PropertyParser.registerProperty(MaterialSwitchFace.class, dMaterial.class);
        }

        // register core dTrade properties
        PropertyParser.registerProperty(TradeHasXp.class, dTrade.class);
        PropertyParser.registerProperty(TradeInputs.class, dTrade.class);
        PropertyParser.registerProperty(TradeMaxUses.class, dTrade.class);
        PropertyParser.registerProperty(TradeResult.class, dTrade.class);
        PropertyParser.registerProperty(TradeUses.class, dTrade.class);
    }
}
