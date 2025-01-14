package com.eerussianguy.blazemap.util;

import java.io.File;
import java.util.Objects;

import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtOps;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.storage.LevelResource;

import com.eerussianguy.blazemap.BlazeMap;
import com.mojang.serialization.Codec;
import org.jetbrains.annotations.Nullable;

import static com.eerussianguy.blazemap.BlazeMap.MOD_ID;

public class Helpers {
    private static File baseDir;

    public static ResourceLocation identifier(String name) {
        return new ResourceLocation(MOD_ID, name);
    }

    public static ClientLevel levelOrThrow() {
        return Objects.requireNonNull(Minecraft.getInstance().level);
    }

    @Nullable
    public static LocalPlayer getPlayer() {
        return Minecraft.getInstance().player;
    }

    public static String getServerID() {
        Minecraft mc = Minecraft.getInstance();
        if(mc.hasSingleplayerServer()) {
            return mc.getSingleplayerServer().getWorldData().getLevelName();
        }
        else {
            return mc.getCurrentServer().ip;
        }
    }

    /**
     * @return the client-side dir to store data for the currently connected server
     */
    public static File getClientSideStorageDir() {
        Minecraft mc = Minecraft.getInstance();
        if(mc.hasSingleplayerServer()) {
            return new File(mc.getSingleplayerServer().getWorldPath(LevelResource.ROOT).toFile(), "blazemap-client");
        }
        else {
            if(baseDir == null) baseDir = new File(mc.gameDirectory, "blazemap-servers");
            return new File(baseDir, mc.getCurrentServer().ip.replace(':', '+'));
        }
    }

    public static void runOnMainThread(Runnable r) {
        Minecraft.getInstance().tell(r);
    }

    public static <T> void writeCodec(Codec<T> codec, T value, CompoundTag tag, String field) {
        tag.put(field, codec.encodeStart(NbtOps.INSTANCE, value).getOrThrow(false, BlazeMap.LOGGER::error));
    }

    public static <T> T decodeCodec(Codec<T> codec, CompoundTag tag, String field) {
        return codec.parse(NbtOps.INSTANCE, tag.get(field)).getOrThrow(false, BlazeMap.LOGGER::error);
    }

    public static TranslatableComponent translate(String key) {
        return new TranslatableComponent(key);
    }

    public static int clamp(int min, int var, int max) {
        return Math.max(min, Math.min(var, max));
    }

    public static double clamp(double min, double var, double max) {
        return Math.max(min, Math.min(var, max));
    }
}
