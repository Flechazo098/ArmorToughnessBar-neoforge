package net.tianben.armor_toughness_bar.config;

import me.shedaniel.autoconfig.AutoConfig;
import me.shedaniel.autoconfig.ConfigData;
import me.shedaniel.autoconfig.annotation.Config;
import me.shedaniel.autoconfig.serializer.Toml4jConfigSerializer;

@Config(name = "armor_toughness_bar")
public class ArmorToughnessBarConfig implements ConfigData {
    public boolean enabled = true; // 是否启用韧性条
    public int offsetX = 82; // 韧性条的 X 轴偏移量
    public int offsetY = -49; // 韧性条的 Y 轴偏移量
    public boolean enableColorChange = false; // 是否启用超过20点韧性值颜色变化（默认关闭）
    public boolean hasCheckedAttributeFix = false; // 是否已经检测过属性修复模组

    public static ArmorToughnessBarConfig getConfig() {
        return AutoConfig.getConfigHolder(ArmorToughnessBarConfig.class).getConfig();
    }

    public static void load() {
        // 注册配置类
        AutoConfig.register(ArmorToughnessBarConfig.class, Toml4jConfigSerializer::new);
    }
}