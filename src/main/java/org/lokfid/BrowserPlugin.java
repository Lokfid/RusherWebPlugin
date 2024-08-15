package org.lokfid;

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

        //creating and registering a new module
        RusherHackAPI.getModuleManager().registerFeature(new BrowserModule(this));
        RusherHackAPI.getHudManager().registerFeature(new BrowserHUDPin(this));

        //logger
        this.getLogger().info("Web plugin loaded!");

    }

    public MCEFBrowser getBrowser() {
        return browser;
    }

    public void setBrowser(MCEFBrowser browser) {
        this.browser = browser;
    }

    @Override
    public void onUnload() {
        if (browser != null) {
            browser.close();
        }
    }
}