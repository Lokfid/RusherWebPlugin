package org.lokfid;

import org.rusherhack.client.api.RusherHackAPI;
import org.rusherhack.client.api.plugin.Plugin;

/**
 * Example rusherhack plugin
 *
 * @author John200410
 */
public class BrowserPlugin extends Plugin {
	
	@Override
	public void onLoad() {
		
		//logger
		this.getLogger().info("Web plugin loaded!");
		
		//creating and registering a new module
		final BrowserModule exampleModule = new BrowserModule();
		RusherHackAPI.getModuleManager().registerFeature(exampleModule);
	}
	
	@Override
	public void onUnload() {
		this.getLogger().info("Web plugin unloaded!");
	}
	
}