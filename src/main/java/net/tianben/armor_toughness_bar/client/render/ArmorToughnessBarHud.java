package net.tianben.armor_toughness_bar.client.render;

import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.registry.tag.FluidTags;
import net.minecraft.util.Identifier;
import net.tianben.armor_toughness_bar.config.ArmorToughnessBarConfig;

public class ArmorToughnessBarHud {
    private static final Identifier TOUGHNESS_BAR_TEXTURE = new Identifier("armor_toughness_bar", "textures/gui/armor_toughness_bar.png");

    // 硬编码的图标尺寸和间距
    private static final int ICON_WIDTH = 9; // 图标宽度
    private static final int ICON_HEIGHT = 9; // 图标高度
    private static final int ICON_SPACING = 8; // 图标间距

    public static void render(DrawContext drawContext) {
        MinecraftClient client = MinecraftClient.getInstance();

        // 检查是否启用韧性条
        if (!ArmorToughnessBarConfig.getConfig().enabled) {
            return;
        }

        // 检查是否应该渲染
        if (client.options.hudHidden
                || client.interactionManager == null
                || !client.interactionManager.hasStatusBars()
                || !(client.cameraEntity instanceof PlayerEntity player)) {
            return;
        }

        // 获取护甲韧性值
        int toughness = (int) player.getAttributeValue(EntityAttributes.GENERIC_ARMOR_TOUGHNESS);
        if (toughness <= 0) {
            return;
        }

        // 计算渲染位置
        int windowWidth = drawContext.getScaledWindowWidth();
        int windowHeight = drawContext.getScaledWindowHeight();
        int anchorX = windowWidth / 2 + ArmorToughnessBarConfig.getConfig().offsetX; // 使用配置中的 X 轴偏移量
        int anchorY = windowHeight + ArmorToughnessBarConfig.getConfig().offsetY; // 使用配置中的 Y 轴偏移量

        // 调整位置（如果玩家在水下）
        if (shouldAdjustPosition(player)) {
            anchorY -= 10;
        }

        // 渲染图标
        RenderSystem.enableBlend();
        renderIcons(drawContext, anchorX, anchorY, toughness);
        RenderSystem.disableBlend();
    }

    /**
     * 检查是否需要调整韧性条的位置。
     * 当 offsetX 和 offsetY 满足特定条件时，即使玩家在水下也不调整位置。
     */
    private static boolean shouldAdjustPosition(PlayerEntity player) {
        int offsetX = ArmorToughnessBarConfig.getConfig().offsetX;
        int offsetY = ArmorToughnessBarConfig.getConfig().offsetY;

        // 检查 offsetX 和 offsetY 是否满足条件
        boolean isOffsetXInvalid = offsetX <= 1 || offsetX >= 163;
        boolean isOffsetYInvalid = offsetY <= -58 || offsetY >= -40;

        // 如果 offsetX 或 offsetY 满足条件，则不调整位置
        if (isOffsetXInvalid || isOffsetYInvalid) {
            return false;
        }

        // 默认情况下，如果玩家在水下，则调整位置
        return player.getAir() < player.getMaxAir() || player.isSubmergedIn(FluidTags.WATER);
    }

    /**
     * 渲染韧性条图标，并根据韧性值设置颜色。
     */
    private static void renderIcons(DrawContext drawContext, int x, int y, int toughness) {
        // 计算需要渲染的图标数量
        int numIcons = 10; // 固定渲染 10 个图标，像原版护甲值一样
        for (int i = 0; i < numIcons; i++) {
            int textureU; // 纹理的 U 坐标（横向偏移）
            if (toughness >= 2) {
                textureU = 18; // 使用完整图标（最后 9 像素）
                toughness -= 2;
            } else if (toughness == 1) {
                textureU = 9; // 使用半满图标（中间 9 像素）
                toughness -= 1;
            } else {
                textureU = 0; // 使用空图标（前 9 像素）
            }

            // 设置颜色混合
            if (ArmorToughnessBarConfig.getConfig().enableColorChange) {
                setColorForToughness(toughness);
            } else {
                RenderSystem.setShaderColor(1.0f, 1.0f, 1.0f, 1.0f); // 默认颜色 (白色)
            }

            // 绘制图标
            drawContext.drawTexture(
                    TOUGHNESS_BAR_TEXTURE, // 合并后的贴图
                    x - i * ICON_SPACING, y, // 渲染位置
                    textureU, 0,           // 纹理起始坐标 (U, V)
                    ICON_WIDTH, ICON_HEIGHT, // 图标宽度和高度
                    27, 9                  // 纹理总宽度和高度
            );

            // 重置颜色混合
            RenderSystem.setShaderColor(1.0f, 1.0f, 1.0f, 1.0f);
        }
    }

    /**
     * 根据韧性值设置颜色混合。
     */
    private static void setColorForToughness(int toughness) {
        int colorHex;

        if (toughness >= 220) {
            // 超过 200 点后，每 20 点循环一次颜色
            int cycle = (toughness - 200) / 20;
            switch (cycle % 11) { // 增加到 10 种颜色
                case 0 -> colorHex = 0xFFFFFF; // 白色
                case 1 -> colorHex = 0xF06E14; // 橙色
                case 2 -> colorHex = 0xF5DC23; // 黄色
                case 3 -> colorHex = 0x2DB928; // 绿色
                case 4 -> colorHex = 0x1EAFBE; // 蓝色
                case 5 -> colorHex = 0x7346E1; // 紫色
                case 6 -> colorHex = 0xFA7DEB; // 粉色
                case 7 -> colorHex = 0xEB375A; // 浅红色
                case 8 -> colorHex = 0xFF8278; // 浅橙色
                case 9 -> colorHex = 0xAAFFFA; // 浅蓝色
                case 10 -> colorHex = 0xEBEBFF; // 浅白色
                default -> colorHex = 0xFFFFFF; // 默认白色
            }
        } else if (toughness >= 200) {
            colorHex = 0xEBEBFF; // 浅白色
        } else if (toughness >= 180) {
            colorHex = 0xAAFFFA; // 浅蓝色
        } else if (toughness >= 160) {
            colorHex = 0xFF8278; // 浅橙色
        } else if (toughness >= 140) {
            colorHex = 0xEB375A; // 浅红色
        } else if (toughness >= 120) {
            colorHex = 0xFA7DEB; // 粉色
        } else if (toughness >= 100) {
            colorHex = 0x7346E1; // 紫色
        } else if (toughness >= 80) {
            colorHex = 0x1EAFBE; // 蓝色
        } else if (toughness >= 60) {
            colorHex = 0x2DB928; // 绿色
        } else if (toughness >= 40) {
            colorHex = 0xF5DC23; // 黄色
        } else if (toughness >= 20) {
            colorHex = 0xF06E14; // 橙色
        } else {
            colorHex = 0xFFFFFF; // 默认白色
        }

        // 将十六进制颜色转换为 RGB 浮点数
        float red = ((colorHex >> 16) & 0xFF) / 255.0f;
        float green = ((colorHex >> 8) & 0xFF) / 255.0f;
        float blue = (colorHex & 0xFF) / 255.0f;
        float alpha = 1.0f; // 不透明度固定为 1.0f

        // 设置颜色
        RenderSystem.setShaderColor(red, green, blue, alpha);
    }
}