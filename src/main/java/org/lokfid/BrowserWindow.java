package org.lokfid;

import org.rusherhack.client.api.RusherHackAPI;
import org.rusherhack.client.api.feature.window.ResizeableWindow;
import org.rusherhack.client.api.render.graphic.IGraphic;
import org.rusherhack.client.api.render.graphic.TextureGraphic;
import org.rusherhack.client.api.ui.window.view.WindowView;

import java.io.IOException;

/**
 * @author Doogie13
 * @since 18/08/2024
 */
public class BrowserWindow extends ResizeableWindow {

    private IGraphic[] graphics = new IGraphic[50];

    public BrowserWindow(BrowserPlugin plugin) {
        super("Rusher Browser", 400, 320);
        wv = new BrowserWindowContent(plugin, this);
        try {
            for (int i = 1; i <= 50; i++) {
                StringBuilder s = new StringBuilder(String.valueOf(i));
                while (s.length() < 4) s.insert(0, "0");
                graphics[i - 1] = new TextureGraphic(String.format("web/images/logo/%s.png", s), 64, 64);
            }
        } catch (IOException | NullPointerException e) {
            plugin.getLogger().error(e.getMessage());
            for (StackTraceElement s : e.getStackTrace())
                plugin.getLogger().error(s.toString());
            graphics = null;
        }
    }

    private final BrowserWindowContent wv;

    @Override
    public WindowView getRootView() {
        return wv;
    }

    @Override
    public boolean renderIcon(double x, double y, double width, double height) {
        if (graphics == null) return false;
        RusherHackAPI.getRenderer2D().drawGraphicRectangle(graphics[(int) ((System.currentTimeMillis() / 50) % 50)], x, y, width, height);
        return true;
    }

    @Override
    public void onClose() {
        super.onClose();
        RusherHackAPI.getEventBus().unsubscribe(wv);
        wv.subscribed = true;
    }
}
