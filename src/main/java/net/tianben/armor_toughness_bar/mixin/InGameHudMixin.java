package net.tianben.armor_toughness_bar.mixin;

import net.tianben.armor_toughness_bar.client.render.ArmorToughnessBarHud;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.hud.InGameHud;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(InGameHud.class)
public class InGameHudMixin {
    @Inject(method = "render", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/hud/InGameHud;renderStatusBars(Lnet/minecraft/client/gui/DrawContext;)V", shift = At.Shift.AFTER))
    private void onRenderStatusBars(DrawContext drawContext, float tickDelta, CallbackInfo ci) {
        // 调用自定义 HUD 渲染逻辑
        ArmorToughnessBarHud.render(drawContext);
    }
}