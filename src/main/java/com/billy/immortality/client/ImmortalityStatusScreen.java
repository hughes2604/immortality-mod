package com.billy.immortality.client;

import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;

public final class ImmortalityStatusScreen extends Screen {
    private boolean nullified;

    public ImmortalityStatusScreen(boolean nullified) {
        super(Text.translatable("screen.immortality.status_title"));
        this.nullified = nullified;
    }

    public void setNullified(boolean nullified) {
        this.nullified = nullified;
    }

    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float delta) {
        renderBackground(context, mouseX, mouseY, delta);

        int panelWidth = 320;
        int panelHeight = 112;
        int left = (width - panelWidth) / 2;
        int top = (height - panelHeight) / 2;
        int right = left + panelWidth;
        int bottom = top + panelHeight;

        context.fill(left, top, right, bottom, 0xE8101118);
        context.fill(left, top, right, top + 2, 0xFFFFC84A);
        context.fill(left, bottom - 2, right, bottom, 0xFF815719);
        context.fill(left, top, left + 2, bottom, 0xFF815719);
        context.fill(right - 2, top, right, bottom, 0xFF815719);

        context.drawCenteredTextWithShadow(textRenderer, title, width / 2, top + 24, 0xFFFFD76A);
        Text status = nullified
                ? Text.translatable("screen.immortality.deactivated").formatted(Formatting.RED)
                : Text.translatable("screen.immortality.active").formatted(Formatting.GOLD);
        context.drawCenteredTextWithShadow(textRenderer, status, width / 2, top + 63,
                nullified ? 0xFFFF5555 : 0xFFFFD76A);
        super.render(context, mouseX, mouseY, delta);
    }

    @Override
    public boolean shouldPause() {
        return false;
    }
}
