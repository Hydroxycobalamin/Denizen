package com.denizenscript.denizen.scripts.commands.world;

import com.denizenscript.denizen.utilities.debugging.Debug;
import com.denizenscript.denizen.nms.NMSHandler;
import com.denizenscript.denizen.nms.abstracts.BlockLight;
import com.denizenscript.denizen.objects.dLocation;
import com.denizenscript.denizencore.exceptions.InvalidArgumentsException;
import com.denizenscript.denizencore.objects.Argument;
import com.denizenscript.denizencore.objects.core.DurationTag;
import com.denizenscript.denizencore.objects.core.ElementTag;
import com.denizenscript.denizencore.objects.ArgumentHelper;
import com.denizenscript.denizencore.scripts.ScriptEntry;
import com.denizenscript.denizencore.scripts.commands.AbstractCommand;

public class LightCommand extends AbstractCommand {

    // <--[command]
    // @Name Light
    // @Syntax light [<location>] [<#>/reset] (duration:<duration>)
    // @Required 2
    // @Short Creates a light source at the location with a specified brightness.
    // @Group world
    //
    // @Description
    // This command can create and reset a light source at a specified location, regardless of the type
    // of block. It will be shown to all players near the location until it is reset.
    // The brightness must be between 0 and 15, inclusive.
    // Optionally, specify the amount of time the light should exist before being removed.
    // WARNING: May cause lag spikes, use carefully.
    //
    // @Tags
    // <l@location.light>
    // <l@location.light.blocks>
    //
    // @Usage
    // Use to create a bright light at a noted location.
    // - light l@MyFancyLightOfWool 15
    //
    // @Usage
    // Use to reset the brightness of the location to its original state.
    // - light l@MyFancyLightOfWool reset
    // -->

    @Override
    public void parseArgs(ScriptEntry scriptEntry) throws InvalidArgumentsException {

        for (Argument arg : ArgumentHelper.interpretArguments(scriptEntry.aHArgs)) {

            if (!scriptEntry.hasObject("location")
                    && arg.matchesArgumentType(dLocation.class)) {
                scriptEntry.addObject("location", arg.asType(dLocation.class));
            }
            else if (!scriptEntry.hasObject("light")
                    && arg.matchesPrimitive(ArgumentHelper.PrimitiveType.Integer)) {
                scriptEntry.addObject("light", arg.asElement());
            }
            else if (!scriptEntry.hasObject("reset")
                    && arg.matches("reset")) {
                scriptEntry.addObject("reset", new ElementTag(true));
            }
            else if (!scriptEntry.hasObject("duration")
                    && arg.matchesPrefix("d", "duration")
                    && arg.matchesArgumentType(DurationTag.class)) {
                scriptEntry.addObject("duration", arg.asType(DurationTag.class));
            }

        }

        if (!scriptEntry.hasObject("location") ||
                (!scriptEntry.hasObject("light") && !scriptEntry.hasObject("reset"))) {
            throw new InvalidArgumentsException("Must specify a valid location and light level.");
        }

        scriptEntry.defaultObject("reset", new ElementTag(false));
    }

    @Override
    public void execute(ScriptEntry scriptEntry) {

        dLocation location = scriptEntry.getdObject("location");
        ElementTag light = scriptEntry.getElement("light");
        ElementTag reset = scriptEntry.getElement("reset");
        DurationTag duration = scriptEntry.getdObject("duration");

        if (scriptEntry.dbCallShouldDebug()) {

            Debug.report(scriptEntry, getName(), location.debug() + reset.debug()
                    + (light != null ? light.debug() : "") + (duration != null ? duration.debug() : ""));

        }

        if (location.getY() < 0 || location.getY() > 255) {
            Debug.echoError(scriptEntry.getResidingQueue(), "Invalid light location!");
            return;
        }
        for (int x = -1; x <= 1; x++) {
            for (int z = -1; z <= 1; z++) {
                location.clone().add(x * 16, 0, z * 16).getChunk().load();
            }
        }
        if (!reset.asBoolean()) {
            int brightness = light.asInt();
            if (brightness < 0 || brightness > 15) {
                Debug.echoError("Light brightness must be between 0 and 15, inclusive!");
                return;
            }
            NMSHandler.getInstance().createBlockLight(location, brightness, duration == null ? 0 : duration.getTicks());
        }
        else {
            BlockLight.removeLight(location);
        }
    }
}
