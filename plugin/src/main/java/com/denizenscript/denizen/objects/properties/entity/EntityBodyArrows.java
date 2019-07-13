package com.denizenscript.denizen.objects.properties.entity;

import com.denizenscript.denizen.nms.NMSHandler;
import com.denizenscript.denizen.objects.dEntity;
import com.denizenscript.denizencore.objects.core.ElementTag;
import com.denizenscript.denizencore.objects.Mechanism;
import com.denizenscript.denizencore.objects.ObjectTag;
import com.denizenscript.denizencore.objects.properties.Property;
import com.denizenscript.denizencore.tags.Attribute;

public class EntityBodyArrows implements Property {
    public static boolean describes(ObjectTag object) {
        return object instanceof dEntity && ((dEntity) object).isLivingEntity();
    }

    public static EntityBodyArrows getFrom(ObjectTag object) {
        if (!describes(object)) {
            return null;
        }
        else {
            return new EntityBodyArrows((dEntity) object);
        }
    }

    public static final String[] handledTags = new String[] {
            "body_arrows"
    };

    public static final String[] handledMechs = new String[] {
            "body_arrows", "clear_body_arrows"
    };


    ///////////////////
    // Instance Fields and Methods
    /////////////

    private EntityBodyArrows(dEntity entity) {
        this.entity = entity;
    }

    dEntity entity;

    /////////
    // Property Methods
    ///////

    private int getBodyArrows() {
        return NMSHandler.getInstance().getEntityHelper().getBodyArrows(entity.getBukkitEntity());
    }

    private void setBodyArrows(int numArrows) {
        NMSHandler.getInstance().getEntityHelper().setBodyArrows(entity.getBukkitEntity(), numArrows);
    }

    @Override
    public String getPropertyString() {
        int numArrows = getBodyArrows();
        return numArrows == 0 ? null : String.valueOf(numArrows);
    }

    @Override
    public String getPropertyId() {
        return "body_arrows";
    }

    @Override
    public String getAttribute(Attribute attribute) {

        if (attribute == null) {
            return null;
        }

        // <--[tag]
        // @attribute <e@entity.body_arrows>
        // @returns ElementTag(Number)
        // @mechanism dEntity.body_arrows
        // @group properties
        // @description
        // Returns the number of arrows stuck in the entity's body.
        // Note: Body arrows will only be visible for players or player-type npcs.
        // -->
        if (attribute.startsWith("body_arrows")) {
            return new ElementTag(getBodyArrows())
                    .getAttribute(attribute.fulfill(1));
        }

        return null;
    }

    @Override
    public void adjust(Mechanism mechanism) {

        // <--[mechanism]
        // @object dEntity
        // @name body_arrows
        // @input Element(Number)
        // @description
        // Sets the number of arrows stuck in the entity's body.
        // Note: Body arrows will only be visible for players or player-type npcs.
        // @tags
        // <e@entity.body_arrows>
        // -->
        if (mechanism.matches("body_arrows") && mechanism.requireInteger()) {
            setBodyArrows(mechanism.getValue().asInt());
        }

        // <--[mechanism]
        // @object dEntity
        // @name clear_body_arrows
        // @input None
        // @description
        // Clears all arrows stuck in the entity's body.
        // Note: Body arrows will only be visible for players or player-type npcs.
        // @tags
        // <e@entity.body_arrows>
        // -->
        if (mechanism.matches("clear_body_arrows")) {
            setBodyArrows(0);
        }
    }
}

