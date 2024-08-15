package org.lokfid;

import org.rusherhack.client.api.feature.module.ModuleCategory;
import org.rusherhack.client.api.feature.module.ToggleableModule;
import org.rusherhack.core.setting.BooleanSetting;
import org.rusherhack.core.setting.Setting;
import org.rusherhack.core.setting.StringSetting;

/**
 * Example rusherhack module
 *
 * @author Lokfid
 */
public class BrowserModule extends ToggleableModule {

    private final Setting<Boolean> savePriorWebpageSetting = new BooleanSetting("Open to previous webpage",
            "Saves the previously opened webpage and opens it next session", false);

    private final Setting<String> homepageSetting = new StringSetting("Home Page",
            "Dictates the page the browser opens to", "https://www.google.com/");

    private final BrowserPlugin plugin;
    private BrowserElement basicBrowser;

    public BrowserModule(BrowserPlugin plugin) {
        super("Browser", "Allows interaction with a browser", ModuleCategory.CLIENT);
        this.plugin = plugin;

        registerSettings(
                savePriorWebpageSetting,
                homepageSetting
        );
    }


    @Override
    public void onEnable() {
        if (basicBrowser == null)
            basicBrowser = new BrowserElement(plugin);
        mc.setScreen(basicBrowser);
        super.toggle();
    }

    @Override
    public void onDisable() {
        storeDetails();
        super.onDisable();
    }

    public void storeDetails() {

    }

    public String getHomepage() {
        return homepageSetting.getValue();
    }

    public Boolean shouldSavePriorWebpage() {
        return savePriorWebpageSetting.getValue();
    }

}
