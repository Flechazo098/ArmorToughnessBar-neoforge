package com.flechazo.armortoughnessbarneoforge;

import com.mojang.logging.LogUtils;
import net.minecraft.client.Minecraft;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.config.ModConfig;
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;
import net.neoforged.neoforge.client.event.RenderGuiLayerEvent;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.common.NeoForgeMod;
import net.neoforged.neoforge.fluids.FluidType;
import org.slf4j.Logger;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.neoforged.api.distmarker.OnlyIn;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.tags.FluidTags;

import java.util.function.BiPredicate;

/**
 * 护甲韧性条mod主类
 * @author Flechazo
 */
@Mod( ArmorToughnessBarNeoforge.MOD_ID)
public class ArmorToughnessBarNeoforge {
    // Define mod id in a common place for everything to reference
    public static final String MOD_ID = "armortoughnessbarneoforge";
    // Directly reference a slf4j logger
    private static final Logger LOGGER = LogUtils.getLogger ();

    private static final ResourceLocation TOUGHNESS_BAR_TEXTURE = ResourceLocation.fromNamespaceAndPath(MOD_ID, "textures/gui/armor_toughness_bar.png");
    private static final int ICON_WIDTH = 9;
    private static final int ICON_HEIGHT = 9;
    private static final int ICON_SPACING = 8;

    /**
     * 构造函数,注册事件总线和配置
     * @param eventBus mod事件总线
     */
    public ArmorToughnessBarNeoforge (IEventBus eventBus, ModContainer modContainer) {
        // 注册配置
        modContainer.registerConfig(ModConfig.Type.CLIENT, Config.SPEC);
        
        // 注册客户端事件
        eventBus.addListener(this::clientSetup);
    }

    /**
     * 客户端初始化事件处理
     * @param event 客户端设置事件
     */
    private void clientSetup(final FMLClientSetupEvent event) {
        // 注册HUD渲染事件监听器
        NeoForge.EVENT_BUS.register(this);
    }

    /**
     * HUD渲染事件处理
     * @param event GUI渲染事件
     */
    @OnlyIn(Dist.CLIENT)
    @SubscribeEvent
    public void onRenderGui(RenderGuiLayerEvent.Post event) {
        if (!Config.ENABLED.get()) return;

        Minecraft mc = Minecraft.getInstance();
        if (mc.options.hideGui || mc.gameMode == null || !mc.gameMode.canHurtPlayer() || !(mc.getCameraEntity() instanceof Player player)) {
            return;
        }

        // 获取护甲韧性值
        int toughness = (int) player.getAttributeValue(Attributes.ARMOR_TOUGHNESS);
        if (toughness <= 0) return;

        // 计算渲染位置
        int windowWidth = mc.getWindow().getGuiScaledWidth();
        int windowHeight = mc.getWindow().getGuiScaledHeight();
        int anchorX = windowWidth / 2 + Config.OFFSET_X.get();
        int anchorY = windowHeight + Config.OFFSET_Y.get();

        // 调整水下位置
        if (shouldAdjustPosition(player)) {
            anchorY -= 10;
        }

        // 渲染图标
        RenderSystem.enableBlend();
        renderIcons(event.getGuiGraphics(), anchorX, anchorY, toughness);
        RenderSystem.disableBlend();
    }

    /**
     * 检查是否需要调整韧性条位置
     * @param player 玩家实体
     * @return 是否需要调整位置
     */
    private boolean shouldAdjustPosition(Player player) {
        int offsetX = Config.OFFSET_X.get();
        int offsetY = Config.OFFSET_Y.get();

        boolean isOffsetXInvalid = offsetX <= 1 || offsetX >= 163;
        boolean isOffsetYInvalid = offsetY <= -58 || offsetY >= -40;

        if (isOffsetXInvalid || isOffsetYInvalid) {
            return false;
        }

        // 检查 WATER_TYPE 是否已经绑定
        if (!NeoForgeMod.WATER_TYPE.isBound ()){
            System.err.println ("WATER_TYPE holder is not bound to any FluidType instance.");
            return false;
        }

        // 获取水的FluidType实例
        FluidType waterType = NeoForgeMod.WATER_TYPE.value ();

        BiPredicate< FluidType, Double > isInWaterPredicate = (fluidType, height) ->
                fluidType == waterType;

        // 使用 isInFluidType 方法并传入谓词来检查玩家是否在水中
        boolean isInWater = player.isInFluidType (isInWaterPredicate, false);

        // 默认情况下，如果玩家在水下，则调整位置
        return player.getAirSupply() < player.getMaxAirSupply() || isInWater ;
    }

    /**
     * 渲染韧性条图标
     * @param graphics GUI渲染上下文
     * @param x X坐标
     * @param y Y坐标
     * @param toughness 韧性值
     */
    private void renderIcons(GuiGraphics graphics, int x, int y, int toughness) {
        int numIcons = 10;
        for (int i = 0; i < numIcons; i++) {
            int textureU = 0;
            if (toughness >= 2) {
                textureU = 18;
                toughness -= 2;
            } else if (toughness == 1) {
                textureU = 9;
                toughness -= 1;
            }

            if (Config.ENABLE_COLOR_CHANGE.get()) {
                setColorForToughness(toughness);
            } else {
                RenderSystem.setShaderColor(1.0f, 1.0f, 1.0f, 1.0f);
            }

            graphics.blit(
                TOUGHNESS_BAR_TEXTURE,
                x - i * ICON_SPACING, y,
                textureU, 0,
                ICON_WIDTH, ICON_HEIGHT,
                27, 9
            );

            RenderSystem.setShaderColor(1.0f, 1.0f, 1.0f, 1.0f);
        }
    }

    /**
     * 根据韧性值设置渲染颜色
     * @param toughness 韧性值
     */
    private void setColorForToughness(int toughness) {
        int colorHex;

        if (toughness >= 220) {
            int cycle = (toughness - 200) / 20;
            colorHex = switch (cycle % 11) {
                case 0 -> 0xFFFFFF; // 白色
                case 1 -> 0xF06E14; // 橙色
                case 2 -> 0xF5DC23; // 黄色
                case 3 -> 0x2DB928; // 绿色
                case 4 -> 0x1EAFBE; // 蓝色
                case 5 -> 0x7346E1; // 紫色
                case 6 -> 0xFA7DEB; // 粉色
                case 7 -> 0xEB375A; // 浅红色
                case 8 -> 0xFF8278; // 浅橙色
                case 9 -> 0xAAFFFA; // 浅蓝色
                case 10 -> 0xEBEBFF; // 浅白色
                default -> 0xFFFFFF;
            };
        } else if (toughness >= 200) {
            colorHex = 0xEBEBFF;
        } else if (toughness >= 180) {
            colorHex = 0xAAFFFA;
        } else if (toughness >= 160) {
            colorHex = 0xFF8278;
        } else if (toughness >= 140) {
            colorHex = 0xEB375A;
        } else if (toughness >= 120) {
            colorHex = 0xFA7DEB;
        } else if (toughness >= 100) {
            colorHex = 0x7346E1;
        } else if (toughness >= 80) {
            colorHex = 0x1EAFBE;
        } else if (toughness >= 60) {
            colorHex = 0x2DB928;
        } else if (toughness >= 40) {
            colorHex = 0xF5DC23;
        } else if (toughness >= 20) {
            colorHex = 0xF06E14;
        } else {
            colorHex = 0xFFFFFF;
        }

        float red = ((colorHex >> 16) & 0xFF) / 255.0f;
        float green = ((colorHex >> 8) & 0xFF) / 255.0f;
        float blue = (colorHex & 0xFF) / 255.0f;
        RenderSystem.setShaderColor(red, green, blue, 1.0f);
    }
}
