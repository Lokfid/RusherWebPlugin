package org.lokfid;

import org.rusherhack.client.api.feature.window.ResizeableWindow;
import org.rusherhack.client.api.ui.window.view.WindowView;

/**
 * @author Doogie13
 * @since 18/08/2024
 */
public class BrowserWindow extends ResizeableWindow {

    public BrowserWindow(BrowserPlugin plugin) {
        super("Rusher Browser", 400, 320);
        wv = new BrowserWindowContent(plugin, this);
    }

    private final WindowView wv;

    @Override
    public WindowView getRootView() {
        return wv;
    }
}
