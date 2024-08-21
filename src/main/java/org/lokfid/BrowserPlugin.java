package org.lokfid;

import com.cinemamod.mcef.MCEF;
import org.rusherhack.client.api.RusherHackAPI;
import org.rusherhack.client.api.plugin.Plugin;

/**
 * Browser plugin
 * @author Lokfid
 * @author Doogie
 */
public class BrowserPlugin extends Plugin {

    BrowserWindow browser;

    @Override
    public void onLoad() {

        if (!MCEF.isInitialized())
            MCEF.initialize();

        browser = new BrowserWindow(this);

        //creating and registering a new module
        RusherHackAPI.getWindowManager().registerFeature(browser);

        Runtime.getRuntime().addShutdownHook(new Thread(() -> browser.shutDown()));

    }

    @Override
    public void onUnload() {
        if (browser != null)
            browser.shutDown();
    }

}