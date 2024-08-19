package org.lokfid;

import com.cinemamod.mcef.MCEF;
import com.cinemamod.mcef.MCEFBrowser;
import org.rusherhack.client.api.RusherHackAPI;
import org.rusherhack.client.api.plugin.Plugin;

/**
 * Browser plugin
 * @author Lokfid
 * @author Doogie
 */
public class BrowserPlugin extends Plugin {

    private MCEFBrowser browser;

    @Override
    public void onLoad() {

        if (!MCEF.isInitialized())
            MCEF.initialize();

        //creating and registering a new module
        RusherHackAPI.getWindowManager().registerFeature(new BrowserWindow(this));

        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            if (hasBrowser()) {
                getBrowser().close();
            }
        }));

    }

    public boolean hasBrowser() {
        return browser != null;
    }

    public MCEFBrowser getBrowser() {
        if (browser == null)
            browser = MCEF.createBrowser("https://start.duckduckgo.com/", true);
        return browser;
    }

    @Override
    public void onUnload() {
        if (browser != null) {
            browser.close();
        }
    }

}