package com.denizenscript.denizen.objects.properties.trade;

import com.denizenscript.denizen.objects.dTrade;
import com.denizenscript.denizencore.objects.core.ElementTag;
import com.denizenscript.denizencore.objects.Mechanism;
import com.denizenscript.denizencore.objects.ObjectTag;
import com.denizenscript.denizencore.objects.properties.Property;
import com.denizenscript.denizencore.tags.Attribute;

public class TradeMaxUses implements Property {

    public static boolean describes(ObjectTag recipe) {
        return recipe instanceof dTrade;
    }

    public static TradeMaxUses getFrom(ObjectTag recipe) {
        if (!describes(recipe)) {
            return null;
        }
        return new TradeMaxUses((dTrade) recipe);
    }

    public static final String[] handledTags = new String[] {
            "max_uses"
    };

    public static final String[] handledMechs = new String[] {
            "max_uses"
    };

    private dTrade recipe;

    public TradeMaxUses(dTrade recipe) {
        this.recipe = recipe;
    }

    public String getPropertyString() {
        if (recipe.getRecipe() == null) {
            return null;
        }
        return String.valueOf(recipe.getRecipe().getMaxUses());
    }

    public String getPropertyId() {
        return "max_uses";
    }

    public String getAttribute(Attribute attribute) {
        if (attribute == null) {
            return null;
        }

        // <--[tag]
        // @attribute <trade@trade.max_uses>
        // @returns ElementTag(Number)
        // @mechanism dTrade.max_uses
        // @description
        // Returns the maximum amount of times that the trade can be used.
        // -->
        if (attribute.startsWith("max_uses")) {
            return new ElementTag(recipe.getRecipe().getMaxUses()).getAttribute(attribute.fulfill(1));
        }

        return null;
    }

    public void adjust(Mechanism mechanism) {

        // <--[mechanism]
        // @object dTrade
        // @name max_uses
        // @input Element(Number)
        // @description
        // Sets the maximum amount of times that the trade can be used.
        // @tags
        // //
        // -->
        if (mechanism.matches("max_uses") && mechanism.requireInteger()) {
            recipe.getRecipe().setMaxUses(mechanism.getValue().asInt());
        }
    }
}
