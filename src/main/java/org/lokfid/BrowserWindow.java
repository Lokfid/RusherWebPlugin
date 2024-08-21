package org.lokfid;

import org.cef.network.CefCookie;
import org.cef.network.CefCookieManager;
import org.rusherhack.client.api.RusherHackAPI;
import org.rusherhack.client.api.feature.window.ResizeableWindow;
import org.rusherhack.client.api.render.graphic.IGraphic;
import org.rusherhack.client.api.render.graphic.TextureGraphic;
import org.rusherhack.client.api.ui.window.content.component.ButtonComponent;
import org.rusherhack.client.api.ui.window.view.SimpleView;
import org.rusherhack.client.api.ui.window.view.TabbedView;
import org.rusherhack.client.api.ui.window.view.WindowView;

import java.io.*;
import java.util.*;

/**
 * @author Doogie13
 * @since 18/08/2024
 */
public class BrowserWindow extends ResizeableWindow {

    private static final File COOKIES_DIR = new File("rusherhack-web-cookies/");

    private final List<BrowserWindowView> browsers = new ArrayList<>();
    private final SimpleView simpleView;
    private TabbedView tabbedView = null;

    private IGraphic[] graphics = new IGraphic[50];

    @SuppressWarnings({"unchecked", "rawtypes"})
    public BrowserWindow(BrowserPlugin plugin) {
        super("Rusher Browser", 400, 320);
        loadCookies();
        browsers.add(new BrowserWindowView(this));
        try {
            for (int i = 1; i <= 50; i++) {
                StringBuilder s = new StringBuilder(String.valueOf(i));
                while (s.length() < 4) s.insert(0, "0");
                graphics[i - 1] = new TextureGraphic(String.format("web/images/logo/%s.png", s), 64, 64);
            }
        } catch (IOException | NullPointerException e) {
            plugin.getLogger().error(e.getMessage());
            for (StackTraceElement s : e.getStackTrace())
                plugin.getLogger().error(s.toString());
            graphics = null;
        }
        ButtonComponent newTabButton = new ButtonComponent(this, "New Tab", 50, 12, () -> {
            BrowserWindowView browserWindowView = new BrowserWindowView(this);
            browsers.add(browserWindowView);
            tabbedView.setActiveTabView(browserWindowView);
        });
        ButtonComponent closeTabButton = new ButtonComponent(this, "Close Tab", 50, 12, () -> {
            BrowserWindowView browserWindowView = (BrowserWindowView) tabbedView.getActiveTabView();
            if (browsers.size() > 1) {
                int i = browsers.indexOf(browserWindowView);
                browsers.remove(browserWindowView);
                if (browserWindowView.hasBrowser())
                    browserWindowView.getBrowser().close();
                tabbedView.setActiveTabView(browsers.get(Math.max(i - 1, 0)));
            } else
                browserWindowView.resetBrowser();
        });
        ButtonComponent homeButton = new ButtonComponent(this, "Home", 50, 12,
                () -> ((BrowserWindowView) tabbedView.getActiveTabView()).resetBrowser());
        tabbedView = new TabbedView(this, (List) browsers);
        simpleView = new SimpleView(this,
                List.of(new ButtonHeader(this, newTabButton, closeTabButton, homeButton), tabbedView));
    }

    @Override
    public WindowView getRootView() {
        return simpleView;
    }

    @Override
    public boolean renderIcon(double x, double y, double width, double height) {
        if (graphics == null) return false;
        RusherHackAPI.getRenderer2D().drawGraphicRectangle(graphics[(int) ((System.currentTimeMillis() / 50) % 50)], x, y, width, height);
        return true;
    }

    @Override
    public void onClose() {
        super.onClose();
        unsubscribeAll(null);
    }

    public void shutDown() {
        onClose();
        for (BrowserWindowView browserWindowView : browsers)
            if (browserWindowView.hasBrowser())
                browserWindowView.getBrowser().close();
        CefCookieManager.getGlobalManager().visitAllCookies((cefCookie, i, i1, boolRef) -> {
            saveCookie(cefCookie);
            return true;
        });
    }

    @SuppressWarnings("ResultOfMethodCallIgnored")
    private static void saveCookie(CefCookie cefCookie) {
        File file = new File(COOKIES_DIR, cefCookie.name);
        if (!file.exists()) {
            file.getParentFile().mkdirs();
        }
        else {
            file.delete();
        }
        try (
                FileOutputStream fos = new FileOutputStream(file);
                ObjectOutputStream oos = new ObjectOutputStream(fos)
        ) {
            file.createNewFile();
            oos.writeObject(new SerialisableCefCookie(cefCookie));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }


    private static void loadCookies() {
        File[] files = COOKIES_DIR.listFiles();
        if (files == null) return;
        for (File file : files) {
            if (file.isDirectory())
                continue;
            try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(file))) {
                CefCookie cookie = ((SerialisableCefCookie) ois.readObject()).getCookie();
                CefCookieManager.getGlobalManager().setCookie(cookie.domain, cookie);
            } catch (IOException | ClassNotFoundException e) {
                throw new RuntimeException(e);
            }
        }
    }

    public void unsubscribeAll(BrowserWindowView ignored) {
        for (BrowserWindowView b : browsers) {
            if (ignored != null && b == ignored)
                continue;
            b.getBrowser().setFocus(false);
            RusherHackAPI.getEventBus().unsubscribe(b);
            b.subscribed = false;
        }
    }
}
