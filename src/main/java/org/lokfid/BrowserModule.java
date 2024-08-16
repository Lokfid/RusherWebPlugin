package org.lokfid;

import com.cinemamod.mcef.MCEF;
import org.rusherhack.client.api.feature.command.ModuleCommand;
import org.rusherhack.client.api.feature.module.ModuleCategory;
import org.rusherhack.client.api.feature.module.ToggleableModule;
import org.rusherhack.core.command.annotations.CommandExecutor;
import org.rusherhack.core.setting.BooleanSetting;
import org.rusherhack.core.setting.Setting;

/**
 * Example rusherhack module
 *
 * @author Lokfid
 */
public class BrowserModule extends ToggleableModule {

    private final Setting<Boolean> savePriorWebpageSetting = new BooleanSetting("Open to previous webpage",
            "Saves the previously opened webpage and opens it next session", false);

    private final BrowserPlugin plugin;
    private final BrowserElement basicBrowser;

    public BrowserModule(BrowserPlugin plugin) {
        super("Browser", "Allows interaction with a browser", ModuleCategory.CLIENT);
        this.plugin = plugin;
        registerSettings(
                savePriorWebpageSetting
        );
        basicBrowser = new BrowserElement(plugin);
    }

    @Override
    public ModuleCommand createCommand() {
        return new ModuleCommand(this) {
            @CommandExecutor(subCommand = "browse")
            @CommandExecutor.Argument("URL")
            private String browse(String url) {
                plugin.getBrowser().close();
                plugin.setBrowser(MCEF.createBrowser(url, true));
                return "Browsed to URL: " + url;
            }
        };
    }

    @Override
    public void onEnable() {
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

    public Boolean shouldSavePriorWebpage() {
        return savePriorWebpageSetting.getValue();
    }

}
