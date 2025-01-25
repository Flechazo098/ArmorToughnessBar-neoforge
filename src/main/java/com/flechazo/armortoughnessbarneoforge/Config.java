package com.flechazo.armortoughnessbarneoforge;

import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.event.config.ModConfigEvent;
import net.neoforged.neoforge.common.ModConfigSpec;

/**
 * 护甲韧性条mod配置类
 * @author Flechazo
 */
public class Config {
    private static final ModConfigSpec.Builder BUILDER = new ModConfigSpec.Builder();
    public static final ModConfigSpec SPEC;
    public static ModConfigSpec.BooleanValue ENABLED;
    public static ModConfigSpec.IntValue OFFSET_X;
    public static ModConfigSpec.IntValue OFFSET_Y;
    public static ModConfigSpec.BooleanValue ENABLE_COLOR_CHANGE;

    static {
        BUILDER.comment("护甲韧性条通用配置").push("general");

        ENABLED = BUILDER
                .comment("是否启用护甲韧性条")
                .translation(ArmorToughnessBarNeoforge.MOD_ID + ".config.enabled")
                .worldRestart ()
                .define("enabled", true);

        OFFSET_X = BUILDER
                .comment("韧性条X轴偏移量")
                .translation(ArmorToughnessBarNeoforge.MOD_ID + ".config.offset_x")
                .worldRestart ()
                .defineInRange("offset_x", 82, -200, 200);

        OFFSET_Y = BUILDER
                .comment("韧性条Y轴偏移量")
                .translation(ArmorToughnessBarNeoforge.MOD_ID + ".config.offset_y")
                .worldRestart ()
                .defineInRange("offset_y", -49, -200, 200);

        ENABLE_COLOR_CHANGE = BUILDER
                .comment("是否启用超过20点韧性值的颜色变化")
                .translation(ArmorToughnessBarNeoforge.MOD_ID + ".config.enable_color_change")
                .worldRestart ()
                .define("enable_color_change", false);

        BUILDER.pop();
        SPEC = BUILDER.build();
    }

    /**
     * 配置加载事件处理
     * @param event 配置加载事件
     */
    @SubscribeEvent
    public static void onLoad(final ModConfigEvent.Loading event) {
        if (event.getConfig().getSpec() == SPEC) {
            System.out.println ("配置已加载！");
        }
    }

    /**
     * 配置重载事件处理
     * @param event 配置重载事件
     */
    @SubscribeEvent
    public static void onReload(final ModConfigEvent.Reloading event) {
        if (event.getConfig().getSpec() == SPEC) {
            System.out.println ("配置已重载，请重启世界以应用更改！");
        }
    }

}