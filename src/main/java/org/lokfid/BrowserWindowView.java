package org.lokfid;

import com.cinemamod.mcef.MCEF;
import com.cinemamod.mcef.MCEFBrowser;
import org.lwjgl.glfw.GLFW;
import org.rusherhack.client.api.Globals;
import org.rusherhack.client.api.RusherHackAPI;
import org.rusherhack.client.api.events.client.input.EventMouse;
import org.rusherhack.client.api.render.graphic.VectorGraphic;
import org.rusherhack.client.api.ui.window.content.WindowContent;
import org.rusherhack.client.api.ui.window.content.component.ButtonComponent;
import org.rusherhack.client.api.ui.window.content.component.TextFieldComponent;
import org.rusherhack.client.api.ui.window.view.SimpleView;
import org.rusherhack.client.api.ui.window.view.WindowView;
import org.rusherhack.core.event.stage.Stage;
import org.rusherhack.core.event.subscribe.Subscribe;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Doogie13
 * @since 19/08/2024
 */
public class BrowserWindowView extends SimpleView implements Globals {

    private final VectorGraphic[] icons = new VectorGraphic[3];
    private final BrowserWindow window;
    private TextFieldComponent url;

    private MCEFBrowser browser;

    boolean subscribed;

    public BrowserWindowView(BrowserWindow window) {
        super(window, List.of());
        this.window = window;
        setContentList(generateMembers());
        try {
            this.icons[0] = new VectorGraphic("web/images/larrow.svg", 726, 726);
            this.icons[1] = new VectorGraphic("web/images/rarrow.svg", 726, 726);
            this.icons[2] = new VectorGraphic("web/images/reload.svg", 800, 800);
        } catch (IOException | NullPointerException e) {
            throw new RuntimeException("Failed to load graphics", e);
        }
    }

    // Generates buttons and url query box
    private List<? extends WindowContent> generateMembers() {
        List<WindowContent> components = new ArrayList<>();
        int buttonHeight = 12;
        int buttonWidth = 12;
        components.add(new ButtonComponent(window, "", buttonWidth, buttonHeight,
                () -> getBrowser().goBack()));
        components.add(new ButtonComponent(window, "", buttonWidth, buttonHeight,
                () -> getBrowser().goForward()));
        components.add(new ButtonComponent(window, "", buttonWidth, buttonHeight,
                () -> getBrowser().reloadIgnoreCache()));
        url = new TextFieldComponent(window, getBrowser().getURL(),
                400 - buttonWidth * components.size());
        url.setReturnCallback(this::processSearchQuery);
        components.add(url);
        return components;
    }

    private void processSearchQuery(String text) {
        if (testURL(text)) {
            getBrowser().loadURL(text);
            getBrowser().setFocus(true);
        } else // we have a search query
            getBrowser().loadURL(String.format("https://duckduckgo.com/?q=%s", text));
        url.setFocused(false);
    }

    private boolean testURL(String text) {
        try {
            return !new URL(text).getAuthority().isEmpty();
        } catch (MalformedURLException e) {
            return false;
        }
    }

    @Override
    public void renderContent(double mouseX, double mouseY, WindowView parent) {

        if (!subscribed) {
            window.unsubscribeAll(this);
            RusherHackAPI.getEventBus().subscribe(this);
            System.out.println("YUPI");
            subscribed = true;
        }

        double y = getY() + 12;
        double height = getHeight() - 12;
        // todo: scale control
        getBrowser().resize((int) (getWidth()) * 2, (int) (height) * 2);

        if (!url.isFocused())
            url.setValue(getBrowser().getURL());

        RusherHackAPI.getRenderer2D()._drawTextureRectangle(
                getBrowser().getRenderer().getTextureID(),
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

    @Subscribe
    public void mouseMoved(EventMouse.Move event) {
        if (mc.screen != RusherHackAPI.getThemeManager().getWindowsScreen()) {
            window.unsubscribeAll(null);
            return;
        }
        int mouseX = (int) ((event.getMouseX()) - getX() * 2);
        int mouseY = (int) ((event.getMouseY()) - (getY() + 12) * 2);
        getBrowser().sendMouseMove(
                mouseX,
                mouseY
        );
    }

    // this shouldn't be needed... wtf?
    @Subscribe(stage = Stage.PRE)
    public void mouseClickedEvent(EventMouse.Key event) {
        if (event.getAction() != GLFW.GLFW_PRESS)
            return;
        if (mc.screen != RusherHackAPI.getThemeManager().getWindowsScreen()) {
            window.unsubscribeAll(null);
            return;
        }
        mouseClicked(event.getMouseX(), event.getMouseY(), event.getButton());
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        getBrowser().setFocus(true);
        getBrowser().sendMousePress(
                (int) ((mouseX - getX()) * 2),
                (int) ((mouseY - getY()- 12) * 2),
                button
        );
        System.out.println(
                (int) ((mouseX - getX()) * 2) + " " +
                        (int) ((mouseY - getY()- 12) * 2) + " " +
                        button
        );
        if (button == GLFW.GLFW_MOUSE_BUTTON_4 && getBrowser().canGoBack())
            getBrowser().goBack();
        else if (button == GLFW.GLFW_MOUSE_BUTTON_5 && getBrowser().canGoForward())
            getBrowser().goForward();
        return super.mouseClicked(mouseX, mouseY, button);
    }

    @Override
    public boolean mouseScrolled(double mouseX, double mouseY, double delta) {
        if (!isHovered(mouseX, mouseY)) return super.mouseScrolled(mouseX, mouseY, delta);
        getBrowser().sendMouseWheel(
                (int) ((mouseX - getX()) * 2),
                (int) ((mouseY - getY()- 12) * 2),
                delta,
                0
        );
        getBrowser().setFocus(true);
        return super.mouseScrolled(mouseX, mouseY, delta);
    }

    @Override
    public void mouseReleased(double mouseX, double mouseY, int button) {
        getBrowser().sendMouseRelease(
                (int) ((mouseX - getX()) * 2),
                (int) ((mouseY - getY()- 12) * 2),
                button
        );
        System.out.println(
                (int) ((mouseX - getX()) * 2) + " " +
                        (int) ((mouseY - getY()- 12) * 2) + " " +
                        button
        );
        getBrowser().setFocus(true);
        super.mouseReleased(mouseX, mouseY, button);
    }

    @Override
    public boolean keyTyped(int key, int scanCode, int modifiers) {
        if (!url.isFocused()) {
            getBrowser().sendKeyPress(key, scanCode, modifiers);
            getBrowser().setFocus(true);
        }
        return super.keyTyped(key, scanCode, modifiers);
    }

    @Override
    public boolean charTyped(char character) {
        if (!url.isFocused())
            getBrowser().sendKeyTyped(character, 0);
        return super.charTyped(character);
    }


    public boolean hasBrowser() {
        return browser != null;
    }

    public MCEFBrowser getBrowser() {
        if (browser == null) {
            browser = MCEF.createBrowser("https://start.duckduckgo.com/", true);
            browser.resize(400, 300);
        }
        return browser;
    }

    public void resetBrowser() {
        if (!hasBrowser())
            return;
        browser.close();
        browser = null;
        RusherHackAPI.getEventBus().unsubscribe(this);
        subscribed = false;
    }

    @Override
    public String getDisplayName() {
        try {
            return new URL(getBrowser().getURL()).getAuthority();
        } catch (MalformedURLException e) {
            return "Tab";
        }
    }
}