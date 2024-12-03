package com.denizenscript.denizen.objects.properties.entity;

import com.denizenscript.denizen.nms.NMSHandler;
import com.denizenscript.denizen.nms.NMSVersion;
import com.denizenscript.denizen.objects.EntityTag;
import com.denizenscript.denizen.utilities.BukkitImplDeprecations;
import com.denizenscript.denizencore.objects.Mechanism;
import com.denizenscript.denizencore.objects.core.ElementTag;
import com.denizenscript.denizencore.objects.properties.PropertyParser;
import org.bukkit.entity.*;

public class EntitySheared extends EntityProperty<ElementTag> {

    // <--[property]
    // @object EntityTag
    // @name sheared
    // @input ElementTag(Boolean)
    // @description
    // Controls whether a sheep is sheared, a bogged is harvested or a snow golem is derped, ie not wearing a pumpkin.
    // To include drops or harvesting mushroom cows consider <@link mechanism EntityTag.shear>.
    // -->

    public static boolean describes(EntityTag entity) {
        return (NMSHandler.getVersion().isAtLeast(NMSVersion.v1_21) && entity.getBukkitEntity() instanceof Shearable)
                || entity.getBukkitEntity() instanceof Snowman
                || entity.getBukkitEntity() instanceof Sheep;
    }

    @Override
    public ElementTag getPropertyValue() {
        return new ElementTag(isSheared());
    }

    @Override
    public void setPropertyValue(ElementTag param, Mechanism mechanism) {
        if (mechanism.requireBoolean()) {
            setSheared(param.asBoolean());
        }
    }

    public boolean isSheared() {
        if (NMSHandler.getVersion().isAtLeast(NMSVersion.v1_21) && getEntity() instanceof Shearable shearable) {
            return shearable.isSheared();
        } else if (getEntity() instanceof Snowman snowman) {
            return snowman.isDerp();
        } else if (getEntity() instanceof Sheep sheep) {
            return sheep.isSheared();
        }
        return false;
    }

    public void setSheared(boolean sheared) {
        if (NMSHandler.getVersion().isAtLeast(NMSVersion.v1_21) && getEntity() instanceof Shearable shearable) {
            shearable.setSheared(sheared);
        } else if (getEntity() instanceof Snowman snowman) {
            snowman.setDerp(sheared);
        } else if (getEntity() instanceof Sheep sheep) {
            sheep.setSheared(sheared);
        }
    }

    @Override
    public String getPropertyId() {
        return "sheared";
    }

    public static void register() {
        autoRegister("sheared", EntitySheared.class, ElementTag.class, false);

        // <--[tag]
        // @attribute <EntityTag.is_sheared>
        // @returns ElementTag(Boolean)
        // @group attributes
        // @deprecated use 'EntityTag.sheared'
        // @description
        // Deprecated in favor of <@link tag EntityTag.sheared>.
        // -->
        PropertyParser.registerTag(EntitySheared.class, ElementTag.class, "is_sheared", (attribute, prop) -> {
            BukkitImplDeprecations.entityIsSheared.warn(attribute.context);
            return new ElementTag(prop.isSheared());
        });
    }
}

