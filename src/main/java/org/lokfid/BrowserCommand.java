package org.lokfid;

import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import org.rusherhack.client.api.feature.command.Command;
import org.rusherhack.client.api.utils.ChatUtils;
import org.rusherhack.core.command.annotations.CommandExecutor;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class BrowserCommand extends Command {
    private static final Path LANDING_PAGE = Paths.get(mc.gameDirectory.getPath(), "rusherhack/config/landingpage");

    public BrowserCommand() {
        super("Browser", "Browser settings");
    }

    @CommandExecutor(subCommand = "landingpage")
    @CommandExecutor.Argument(value = {"url"} )
    public Component change(String string){
        //i have no idea why but it wont work with https:// or http:// xD
        if(!string.startsWith("https") || !string.startsWith("http")) {
            return Component.literal("URL must start with https:// or http://").withStyle(ChatFormatting.GOLD);
        }
        try {
            Files.write(LANDING_PAGE, string.getBytes());
        } catch (IOException e) {
            return Component.literal("ERROR");
        }
        return Component.literal("Changed to " + string).withStyle(ChatFormatting.GREEN);
    }
}
