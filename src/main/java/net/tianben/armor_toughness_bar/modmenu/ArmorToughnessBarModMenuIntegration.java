package net.tianben.armor_toughness_bar.modmenu;

import com.terraformersmc.modmenu.api.ConfigScreenFactory;
import com.terraformersmc.modmenu.api.ModMenuApi;
import me.shedaniel.autoconfig.AutoConfig;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.tianben.armor_toughness_bar.config.ArmorToughnessBarConfig;

@Environment(EnvType.CLIENT)
public class ArmorToughnessBarModMenuIntegration implements ModMenuApi {
    @Override
    public ConfigScreenFactory<?> getModConfigScreenFactory() {
        return parent -> AutoConfig.getConfigScreen(ArmorToughnessBarConfig.class, parent).get();
    }
}