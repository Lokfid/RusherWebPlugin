package org.lokfid;

import org.lwjgl.glfw.GLFW;
import org.rusherhack.client.api.Globals;
import org.rusherhack.client.api.RusherHackAPI;
import org.rusherhack.client.api.feature.window.Window;
import org.rusherhack.client.api.render.graphic.VectorGraphic;
import org.rusherhack.client.api.ui.window.content.WindowContent;
import org.rusherhack.client.api.ui.window.content.component.ButtonComponent;
import org.rusherhack.client.api.ui.window.content.component.TextFieldComponent;
import org.rusherhack.client.api.ui.window.view.SimpleView;
import org.rusherhack.client.api.ui.window.view.WindowView;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Doogie13
 * @since 19/08/2024
 */
public class BrowserWindowContent extends SimpleView implements Globals {

    private final BrowserPlugin plugin;
    private final VectorGraphic[] icons = new VectorGraphic[3];
    private TextFieldComponent url;

    public BrowserWindowContent(BrowserPlugin plugin, Window window) {
        super(window, List.of());
        setContentList(generateMembers(plugin, window));
        this.plugin = plugin;
        try {
            this.icons[0] = new VectorGraphic("web/images/larrow.svg", 726, 726);
            this.icons[1] = new VectorGraphic("web/images/rarrow.svg", 726, 726);
            this.icons[2] = new VectorGraphic("web/images/reload.svg", 800, 800);
        } catch (IOException | NullPointerException e) {
            throw new RuntimeException("Failed to load graphics", e);
        }
    }

    private List<? extends WindowContent> generateMembers(BrowserPlugin plugin, Window window) {
        List<WindowContent> components = new ArrayList<>();
        int buttonHeight = 12;
        int buttonWidth = 12;
        components.add(new ButtonComponent(window, "", buttonWidth, buttonHeight,
                () -> plugin.getBrowser().goBack()));
        components.add(new ButtonComponent(window, "", buttonWidth, buttonHeight,
                () -> plugin.getBrowser().goForward()));
        components.add(new ButtonComponent(window, "", buttonWidth, buttonHeight,
                () -> plugin.getBrowser().reloadIgnoreCache()));
        url = new TextFieldComponent(window, plugin.getBrowser().getURL(),
                400 - buttonWidth * components.size());
        url.setReturnCallback(text -> {
            plugin.getBrowser().loadURL(text);
            plugin.getBrowser().setFocus(true);
            url.setFocused(false);
        });
        components.add(url);
        return components;
    }

    @Override
    public void renderContent(double mouseX, double mouseY, WindowView parent) {
        double guiScale = mc.getWindow().getGuiScale();

        double y = getY() + 12;
        plugin.getBrowser().sendMouseMove((int) ((mouseX - getX()) * guiScale), (int) ((mouseY - y) * guiScale));
        double height = getHeight() - 12;
        plugin.getBrowser().resize((int) (getWidth() * guiScale), (int) (height * guiScale));

        if (!url.isFocused())
            url.setValue(plugin.getBrowser().getURL());

        RusherHackAPI.getRenderer2D()._drawTextureRectangle(
                plugin.getBrowser().getRenderer().getTextureID(),
                (int) getWidth(),
                (int) height,
                getX(),
                y,
                getWidth(),
                height,
                0
        );
        url.setWidth(getWidth() - 12 * (getContent().size() - 1));
        double x = getX();
        y = getY();
        List<WindowContent> windowContents = getContent();
        for (int i = 0; i < windowContents.size(); i++) {
            WindowContent content = windowContents.get(i);
            content.setX(x);
            content.setY(y);
            getHandler().getContentHandler().handleRenderContent(content, mouseX, mouseY, this);
            if (i < icons.length) {
                RusherHackAPI.getRenderer2D().drawGraphicRectangle(icons[i],
                        x + 1, y + 1, content.getWidth() - 2, content.getHeight() - 2);
            }
            x += content.getWidth();
        }
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        double guiScale = mc.getWindow().getGuiScale();
        plugin.getBrowser().sendMousePress(
                (int) ((mouseX - getX()) * guiScale),
                (int) ((mouseY - getY()) * guiScale),
                button
        );
        if (button == GLFW.GLFW_MOUSE_BUTTON_4 && plugin.getBrowser().canGoBack())
            plugin.getBrowser().goBack();
        else if (button == GLFW.GLFW_MOUSE_BUTTON_5 && plugin.getBrowser().canGoForward())
            plugin.getBrowser().goForward();
        plugin.getBrowser().setFocus(true);
        return super.mouseClicked(mouseX, mouseY, button);
    }

    @Override
    public boolean mouseScrolled(double mouseX, double mouseY, double delta) {
        if (!isHovered(mouseX, mouseY)) return super.mouseScrolled(mouseX, mouseY, delta);
        double guiScale = mc.getWindow().getGuiScale();
        plugin.getBrowser().sendMouseWheel(
                (int) ((mouseX - getX()) * guiScale),
                (int) ((mouseY - getY()) * guiScale),
                delta,
                0
        );
        plugin.getBrowser().setFocus(true);
        return super.mouseScrolled(mouseX, mouseY, delta);
    }

    @Override
    public void mouseReleased(double mouseX, double mouseY, int button) {
        double guiScale = mc.getWindow().getGuiScale();
        plugin.getBrowser().sendMouseRelease(
                (int) ((mouseX - getX()) * guiScale),
                (int) ((mouseY - getY()) * guiScale),
                button
        );
        plugin.getBrowser().setFocus(true);
        super.mouseReleased(mouseX, mouseY, button);
    }

    @Override
    public boolean keyTyped(int key, int scanCode, int modifiers) {
        if (!url.isFocused()) {
            plugin.getBrowser().sendKeyPress(key, scanCode, modifiers);
            plugin.getBrowser().setFocus(true);
        }
        return super.keyTyped(key, scanCode, modifiers);
    }

    @Override
    public boolean charTyped(char character) {
        if (!url.isFocused())
            plugin.getBrowser().sendKeyTyped(character, 0);
        return super.charTyped(character);
    }
}