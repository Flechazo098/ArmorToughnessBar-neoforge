package net.tianben.armor_toughness_bar;

import me.shedaniel.autoconfig.AutoConfig;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.loader.api.FabricLoader;
import net.tianben.armor_toughness_bar.config.ArmorToughnessBarConfig;

public class ArmorToughnessBarMod implements ClientModInitializer {
    @Override
    public void onInitializeClient() {
        // 初始化配置
        ArmorToughnessBarConfig.load();

        // 获取配置实例
        ArmorToughnessBarConfig config = ArmorToughnessBarConfig.getConfig();

        // 如果尚未检测过属性修复模组，则进行检测
        if (!config.hasCheckedAttributeFix) {
            boolean isAttributeFixLoaded = FabricLoader.getInstance().isModLoaded("attributefix");

            // 如果检测到属性修复模组，则启用颜色变化功能
            if (isAttributeFixLoaded) {
                config.enableColorChange = true;
                System.out.println("检测到 AttributeFix 模组，已自动启用颜色变化功能！");
            }

            // 标记为已检测
            config.hasCheckedAttributeFix = true;

            // 保存配置
            AutoConfig.getConfigHolder(ArmorToughnessBarConfig.class).save();
        }

        System.out.println("Armor Toughness Bar 模组已加载！");
    }
}