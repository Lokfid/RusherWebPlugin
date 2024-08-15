package org.lokfid;

import com.cinemamod.mcef.MCEF;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.BufferBuilder;
import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.Tesselator;
import com.mojang.blaze3d.vertex.VertexFormat;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.network.chat.Component;
import org.rusherhack.client.api.Globals;

public class BrowserElement extends Screen implements Globals {

    private static final int BROWSER_DRAW_OFFSET = 20;
    private final BrowserPlugin plugin;

    private String previousWebpageStorage = "https://www.google.com/";

    protected BrowserElement(BrowserPlugin plugin) {
        super(Component.literal("Basic Browser"));
        this.plugin = plugin;
    }

    @Override
    protected void init() {
        super.init();
        if (plugin.getBrowser() == null) {
            String url = previousWebpageStorage;
            boolean transparent = true;
            Runtime.getRuntime().addShutdownHook(new Thread(() -> {
                if (plugin.getBrowser() != null) {
                    plugin.getBrowser().close();
                }
            }));
            plugin.setBrowser(MCEF.createBrowser(url, transparent));
            resizeBrowser();
        }
    }


    private int mouseX(double x) {
        return (int) ((x - BROWSER_DRAW_OFFSET) * mc.getWindow().getGuiScale());
    }

    private int mouseY(double y) {
        return (int) ((y - BROWSER_DRAW_OFFSET) * mc.getWindow().getGuiScale());
    }

    private int scaleX(double x) {
        return (int) ((x - BROWSER_DRAW_OFFSET * 2) * mc.getWindow().getGuiScale());
    }

    private int scaleY(double y) {
        return (int) ((y - BROWSER_DRAW_OFFSET * 2) * mc.getWindow().getGuiScale());
    }


    private void resizeBrowser() {
        if (width > 100 && height > 100) {
            plugin.getBrowser().resize(scaleX(width), scaleY(height));
        }
    }


    @Override
    public void resize(Minecraft mc, int i, int j) {
        super.resize(mc, i, j);
        resizeBrowser();
    }

    public void close() {
        plugin.getBrowser().close();

    }
    @Override
    public void render(GuiGraphics guiGraphics, int x, int y, float partialTick) {
        resizeBrowser();
        super.render(guiGraphics, x, y, partialTick);
        RenderSystem.disableDepthTest();
        RenderSystem.setShader(GameRenderer::getPositionTexColorShader);
        RenderSystem.setShaderTexture(0, plugin.getBrowser().getRenderer().getTextureID());
        Tesselator t = Tesselator.getInstance();
        BufferBuilder buffer = t.getBuilder();
        buffer.begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_TEX_COLOR);
        buffer.vertex(BROWSER_DRAW_OFFSET, height - BROWSER_DRAW_OFFSET, 0).uv(0.0f, 1.0f).color(255, 255, 255, 255).endVertex();
        buffer.vertex(width - BROWSER_DRAW_OFFSET, height - BROWSER_DRAW_OFFSET, 0).uv(1.0f, 1.0f).color(255, 255, 255, 255).endVertex();
        buffer.vertex(width - BROWSER_DRAW_OFFSET, BROWSER_DRAW_OFFSET, 0).uv(1.0f, 0.0f).color(255, 255, 255, 255).endVertex();
        buffer.vertex(BROWSER_DRAW_OFFSET, BROWSER_DRAW_OFFSET, 0).uv(0.0f, 0.0f).color(255, 255, 255, 255).endVertex();
        t.end();
        RenderSystem.setShaderTexture(0, 0);
        RenderSystem.enableDepthTest();
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        plugin.getBrowser().sendMousePress(mouseX(mouseX), mouseY(mouseY), button);
        plugin.getBrowser().setFocus(true);
        if (button == 3 && plugin.getBrowser().canGoBack()){
            plugin.getBrowser().goBack();
        }
        if (button == 4 && plugin.getBrowser().canGoForward()){
            plugin.getBrowser().goForward();
        }
        return super.mouseClicked(mouseX, mouseY, button);
    }

    @Override
    public boolean mouseReleased(double mouseX, double mouseY, int button) {
        plugin.getBrowser().sendMouseRelease(mouseX(mouseX), mouseY(mouseY), button);
        plugin.getBrowser().setFocus(true);
        return super.mouseReleased(mouseX, mouseY, button);
    }

    @Override
    public void mouseMoved(double mouseX, double mouseY) {
        plugin.getBrowser().sendMouseMove(mouseX(mouseX), mouseY(mouseY));
        super.mouseMoved(mouseX, mouseY);
    }

    @Override
    public boolean mouseDragged(double mouseX, double mouseY, int button, double dragX, double dragY) {
        return super.mouseDragged(mouseX, mouseY, button, dragX, dragY);
    }

    @Override
    public boolean mouseScrolled(double mouseX, double mouseY, double delta) {
        plugin.getBrowser().sendMouseWheel(mouseX(mouseX), mouseY(mouseY), delta, 0);
        return super.mouseScrolled(mouseX, mouseY, delta);
    }

    @Override
    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        plugin.getBrowser().sendKeyPress(keyCode, scanCode, modifiers);
        plugin.getBrowser().setFocus(true);
        return super.keyPressed(keyCode, scanCode, modifiers);
    }

    @Override
    public boolean keyReleased(int keyCode, int scanCode, int modifiers) {
        plugin.getBrowser().sendKeyRelease(keyCode, scanCode, modifiers);
        plugin.getBrowser().setFocus(true);
        return super.keyReleased(keyCode, scanCode, modifiers);
    }

    @Override
    public boolean charTyped(char codePoint, int modifiers) {
        if (codePoint == (char) 0) return false;
        plugin.getBrowser().sendKeyTyped(codePoint, modifiers);
        plugin.getBrowser().setFocus(true);
        return super.charTyped(codePoint, modifiers);
    }

}