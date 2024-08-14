package org.lokfid;

import net.minecraft.network.chat.Component;
import org.rusherhack.client.api.feature.command.ModuleCommand;
import org.rusherhack.client.api.feature.module.ModuleCategory;
import org.rusherhack.client.api.feature.module.ToggleableModule;
import org.rusherhack.core.command.annotations.CommandExecutor;

/**
 * Example rusherhack module
 *
 * @author Lokfid
 */
public class BrowserModule extends ToggleableModule {


	public BrowserModule() {
		super("Browser", "Example plugin module", ModuleCategory.CLIENT);
	}
	
	BasicBrowser basicBrowser = new BasicBrowser(Component.literal("Basic Browser"));
	@Override
	public ModuleCommand createCommand() {
		return new ModuleCommand(this) {
			@CommandExecutor(subCommand = "open")
			private String Browser() {
				mc.setScreen(basicBrowser);
				return "Opened Browser";
			}

		};
	}
}
