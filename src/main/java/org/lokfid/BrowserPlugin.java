package org.lokfid;

import com.cinemamod.mcef.MCEF;
import org.rusherhack.client.api.RusherHackAPI;
import org.rusherhack.client.api.plugin.Plugin;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import static org.rusherhack.client.api.Globals.mc;


/**
 * Browser plugin
 * @author Lokfid
 * @author Doogie
 */
public class BrowserPlugin extends Plugin {
    BrowserWindow browser;


    @Override
    public void onLoad() {
        //We create a file that will contain the landingpage's URL and add duckduckgo as default
        Path dir = Paths.get(mc.gameDirectory.getPath(), "rusherhack/config/landingpage");
        if (!Files.exists(dir)){
            try{
                Files.createFile(dir);
                Files.write(dir, "https://start.duckduckgo.com".getBytes());
            }
            catch (IOException e ){
                return;
            }

        }

        if (!MCEF.isInitialized())
            MCEF.initialize();

        browser = new BrowserWindow(this);

        //creating and registering a new module
        RusherHackAPI.getWindowManager().registerFeature(browser);

        Runtime.getRuntime().addShutdownHook(new Thread(() -> browser.shutDown()));

        final BrowserCommand browserCommand = new BrowserCommand();
        RusherHackAPI.getCommandManager().registerFeature(browserCommand);

    }

    @Override
    public void onUnload() {
        if (browser != null)
            browser.shutDown();
    }

}