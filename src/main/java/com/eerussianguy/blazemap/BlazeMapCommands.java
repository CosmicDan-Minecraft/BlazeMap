package com.eerussianguy.blazemap;


import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraftforge.server.command.EnumArgument;

import com.eerussianguy.blazemap.feature.Overlays;
import com.eerussianguy.blazemap.feature.maps.MinimapSize;
import com.mojang.brigadier.Command;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;

public class BlazeMapCommands {
    private static final EnumArgument<MinimapSize> MINIMAP_SIZE = EnumArgument.enumArgument(MinimapSize.class);

    public static LiteralArgumentBuilder<CommandSourceStack> create() {
        return Commands.literal("blazemap")
            .then(createDebug())
            .then(createMinimap());
    }

    private static LiteralArgumentBuilder<CommandSourceStack> createDebug() {
        return Commands.literal("debug")
            .then(Commands.literal("on").executes($ -> {
                BlazeMapConfig.CLIENT.enableDebug.set(true);
                Overlays.reload();
                return Command.SINGLE_SUCCESS;
            }))
            .then(Commands.literal("off").executes($ -> {
                BlazeMapConfig.CLIENT.enableDebug.set(false);
                Overlays.reload();
                return Command.SINGLE_SUCCESS;
            }));
    }

    private static LiteralArgumentBuilder<CommandSourceStack> createMinimap() {
        return Commands.literal("minimap")
            .then(minimapSize())
            .then(Commands.literal("on").executes($ -> {
                BlazeMapConfig.CLIENT.minimap.enabled.set(true);
                Overlays.reload();
                return Command.SINGLE_SUCCESS;
            }))
            .then(Commands.literal("off").executes($ -> {
                BlazeMapConfig.CLIENT.minimap.enabled.set(false);
                Overlays.reload();
                return Command.SINGLE_SUCCESS;
            }));
    }

    private static LiteralArgumentBuilder<CommandSourceStack> minimapSize() {
        return Commands.literal("size")
            .then(Commands.argument("value", MINIMAP_SIZE)
                .executes(cmd -> {
                    MinimapSize size = cmd.getArgument("value", MinimapSize.class);
                    BlazeMapConfig.CLIENT.minimap.overlaySize.set(size);
                    return Command.SINGLE_SUCCESS;
                })
            );
    }
}
