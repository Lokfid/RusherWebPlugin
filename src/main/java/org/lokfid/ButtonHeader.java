package org.lokfid;

import org.rusherhack.client.api.feature.window.Window;
import org.rusherhack.client.api.ui.window.content.WindowContent;
import org.rusherhack.client.api.ui.window.view.SimpleView;
import org.rusherhack.client.api.ui.window.view.WindowView;

import java.util.List;

/**
 * @author Doogie13
 * @since 21/08/2024
 */
public class ButtonHeader extends SimpleView {

    private final WindowContent[] content;

    public ButtonHeader(Window window, WindowContent... contentList) {
        super(window, List.of());
        content = contentList;
    }

    @Override
    public void renderViewContent(double mouseX, double mouseY) {
        super.renderViewContent(mouseX, mouseY);
    }

    @Override
    public void renderContent(double mouseX, double mouseY, WindowView parent) {
        double x = getX();
        double y = getY();
        for (WindowContent content : content) {
            content.setX(x);
            content.setY(y);
            getHandler().getContentHandler().handleRenderContent(content, mouseX, mouseY, this);
            x += content.getWidth();
        }
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        for (WindowContent content : content)
            content.mouseClicked(mouseX, mouseY, button);
        return super.mouseClicked(mouseX, mouseY, button);
    }

    @Override
    public void mouseReleased(double mouseX, double mouseY, int button) {
        for (WindowContent content : content)
            content.mouseReleased(mouseX, mouseY, button);
        super.mouseReleased(mouseX, mouseY, button);
    }

    @Override
    public boolean mouseScrolled(double mouseX, double mouseY, double delta) {
        for (WindowContent content : content)
            content.mouseScrolled(mouseX, mouseY, delta);
        return super.mouseScrolled(mouseX, mouseY, delta);
    }

    @Override
    public double getHeight() {
        return 12;
    }
}
