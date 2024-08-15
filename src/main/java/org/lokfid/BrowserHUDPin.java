package org.lokfid;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.BufferBuilder;
import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.Tesselator;
import com.mojang.blaze3d.vertex.VertexFormat;
import net.minecraft.client.renderer.GameRenderer;
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
    private static final int HEADER_HEIGHT = 15;

    final Setting<Float> engineScale = new NumberSetting<>("Engine Scale",
            "The scale of the browser", 1.0f, 0.1f, 2.0f);

    final Setting<Float> aspectRatio = new NumberSetting<>("Aspect Ratio H",
            "The X element of the aspect ratio", 4.0f, 0.1f, 48f);

    final Setting<Float> aspectRatioY = new NumberSetting<>("Aspect Ratio V",
            "The Y element of the aspect ratio", 3.0f, 0.1f, 48f);

    public BrowserHUDPin(BrowserPlugin plugin) {
        super("Browser");
        setDescription("Shows the browser window in the GUI");

        this.plugin = plugin;

        registerSettings(
                engineScale,
                aspectRatio,
                aspectRatioY
        );
    }

    @Override
    public void renderContent(RenderContext context, double mouseX, double mouseY) {

        if (plugin.getBrowser() == null || mc.screen instanceof BrowserElement)
            return;

        double mult = mc.getWindow().getGuiScale() * (1 / engineScale.getValue());
        plugin.getBrowser().resize(
                (int) (getWidth() * mult),
                (int) ((getHeight() - HEADER_HEIGHT) * mult)
        );

        RenderSystem.disableDepthTest();
        RenderSystem.setShader(GameRenderer::getPositionTexColorShader);
        RenderSystem.setShaderTexture(0, plugin.getBrowser().getRenderer().getTextureID());

        Tesselator t = Tesselator.getInstance();
        BufferBuilder buffer = t.getBuilder();

        buffer.begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_TEX_COLOR);
        buffer.vertex(getX(), getY() + getHeight() + HEADER_HEIGHT, 0).uv(0.0f, 1.0f).color(255, 255, 255, 255).endVertex();
        buffer.vertex(getX() + getWidth(), getY() + getHeight() + HEADER_HEIGHT, 0).uv(1.0f, 1.0f).color(255, 255, 255, 255).endVertex();
        buffer.vertex(getX() + getWidth(), getY() + HEADER_HEIGHT, 0).uv(1.0f, 0.0f).color(255, 255, 255, 255).endVertex();
        buffer.vertex(getX(), getY() + HEADER_HEIGHT, 0).uv(0.0f, 0.0f).color(255, 255, 255, 255).endVertex();

        t.end();

        RenderSystem.setShaderTexture(0, 0);
        RenderSystem.enableDepthTest();

/*        try {
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
    public double getWidth() {
        return 200;
    }

    @Override
    public double getHeight() {
        return HEADER_HEIGHT + 200 * (aspectRatio.getValue() / aspectRatioY.getValue());
    }

}
