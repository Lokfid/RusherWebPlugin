package org.lokfid;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.BufferBuilder;
import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.Tesselator;
import com.mojang.blaze3d.vertex.VertexFormat;
import net.minecraft.client.renderer.GameRenderer;
import org.rusherhack.client.api.RusherHackAPI;
import org.rusherhack.client.api.feature.hud.ResizeableHudElement;
import org.rusherhack.client.api.render.RenderContext;
import org.rusherhack.core.setting.NumberSetting;
import org.rusherhack.core.setting.Setting;

/**
 * @author Doogie13
 * @since 15/08/2024
 */
public class BrowserHUDPin extends ResizeableHudElement {

    private final BrowserPlugin plugin;
    private int headerHeight = 0;

    final Setting<Float> engineScale = new NumberSetting<>("Engine Scale",
            "The scale of the browser", 1.0f, 0.1f, 5.0f);

    final Setting<Float> aspectRatioH = new NumberSetting<>("Aspect Ratio H",
            "The Y element of the aspect ratio", 4.0f, 0.1f, 48f);

    final Setting<Float> aspectRatioV = new NumberSetting<>("Aspect Ratio V",
            "The X element of the aspect ratio", 3.0f, 0.1f, 48f);

    final Setting<Integer> openGLSlot = new NumberSetting<>("OpenGL Slot",
            "The slot to use for the browser", 1, 0, 64);

    public BrowserHUDPin(BrowserPlugin plugin) {
        super("Browser");
        setDescription("Shows the browser window in the GUI");

        this.plugin = plugin;

        registerSettings(
                engineScale,
                aspectRatioH,
                aspectRatioV,
                openGLSlot
        );
    }

    @Override
    public void renderContent(RenderContext context, double mouseX, double mouseY) {
        headerHeight = (int) Math.round(RusherHackAPI.getRenderer2D().getFontRenderer().getFontHeight());

        RusherHackAPI.getRenderer2D().drawRectangle(
                0,
                0,
                getWidth(),
                headerHeight,
                RusherHackAPI.getThemeManager().getHudTheme().getPrimaryColor().getRGB()
        );

        RusherHackAPI.getRenderer2D().scissorBox(
                0,
                0,
                getWidth(),
                headerHeight
        );

        RusherHackAPI.getRenderer2D().beginScissor();

        RusherHackAPI.getRenderer2D().getFontRenderer().drawString(
                plugin.getBrowser() == null || mc.screen instanceof BrowserElement ? "Browser" : plugin.getBrowser().getURL(),
                2,
                ((double) headerHeight / 2) - RusherHackAPI.getRenderer2D().getFontRenderer().getFontHeight() / 2,
                0xFFFFFFFF,
                true
        );

        RusherHackAPI.getRenderer2D().endScissor();

   }

   @Override
   public void postRender(RenderContext context, double mouseX, double mouseY) {
        super.postRender(context, mouseX, mouseY);

        if (plugin.getBrowser() == null || mc.screen instanceof BrowserElement)
            return;

        double mult = mc.getWindow().getGuiScale() * (1 / engineScale.getValue());

       int width = (int) (getScaledWidth() * mult);
       int header = (int) (headerHeight * getScale());
       int height = (int) ((getScaledHeight() - header) * mult);

        plugin.getBrowser().resize(
                width,
                height
        );

        //RenderSystem.disableDepthTest();
        RenderSystem.setShader(GameRenderer::getPositionTexColorShader);
        RenderSystem.setShaderTexture(openGLSlot.getValue(), plugin.getBrowser().getRenderer().getTextureID());

        Tesselator t = Tesselator.getInstance();
        BufferBuilder buffer = t.getBuilder();

        buffer.begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_TEX_COLOR);
        buffer.vertex(getX(), getY() + getScaledHeight() + header, 0).uv(0.0f, 1.0f).color(255, 255, 255, 255).endVertex();
        buffer.vertex(getX() + getScaledWidth(), getY() + getScaledHeight() + header, 0).uv(1.0f, 1.0f).color(255, 255, 255, 255).endVertex();
        buffer.vertex(getX() + getScaledWidth(), getY() + header, 0).uv(1.0f, 0.0f).color(255, 255, 255, 255).endVertex();
        buffer.vertex(getX(), getY() + header, 0).uv(0.0f, 0.0f).color(255, 255, 255, 255).endVertex();

        t.end();

        RenderSystem.setShaderTexture(0, 0);
        //RenderSystem.enableDepthTest();

/*
        try {
            RusherHackAPI.getRenderer2D().drawRectangle(
                    getX(),
                    getY(),
                    getWidth(),
                    HEADER_HEIGHT,
                    Color.BLACK.getRGB()
            );
            double offset = RusherHackAPI.getRenderer2D().getFontRenderer().getFontHeight();
            RusherHackAPI.getRenderer2D().getFontRenderer().drawString(
                    plugin.getBrowser().getURL(),
                    getX() + 2,
                    getY() + ((double) HEADER_HEIGHT / 2) - offset/2,
                    Color.WHITE.getRGB()
            );
        } catch (Exception e) {
            System.out.println(e.getMessage());
            e.printStackTrace();
        }*/

    }

    @Override
    public boolean shouldUpdateAlignment() {
        return false;
    }

    @Override
    public double getWidth() {
        return 200;
    }

    @Override
    public double getHeight() {
        return headerHeight + 200 * (aspectRatioV.getValue() / aspectRatioH.getValue());
    }

}
