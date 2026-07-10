package de.rettichlp.ucutils.common.gui.screens;

import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.components.StringWidget;
import net.minecraft.client.gui.layouts.HeaderAndFooterLayout;
import net.minecraft.client.gui.layouts.LayoutSettings;
import net.minecraft.client.gui.layouts.LinearLayout;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import org.jspecify.annotations.NonNull;

import static de.rettichlp.ucutils.UCUtils.MOD_ID;
import static java.util.Objects.nonNull;
import static net.minecraft.client.gui.layouts.LinearLayout.vertical;
import static net.minecraft.client.renderer.RenderPipelines.GUI_TEXTURED;
import static net.minecraft.network.chat.Component.literal;
import static net.minecraft.resources.Identifier.withDefaultNamespace;

public abstract class UCUtilsScreen extends Screen {

    public final HeaderAndFooterLayout layout = new HeaderAndFooterLayout(this);

    private static final Identifier MENU_LIST_BACKGROUND = withDefaultNamespace("textures/gui/menu_list_background.png");
    private static final Identifier INWORLD_MENU_LIST_BACKGROUND = withDefaultNamespace("textures/gui/inworld_menu_list_background.png");

    private final Screen parent;

    private Component subTitle = literal("v" + getVersion());
    private boolean renderBackground = true;

    public UCUtilsScreen(Component title, Screen parent) {
        super(title);
        this.parent = parent;
    }

    public UCUtilsScreen(Component title, Component subTitle) {
        super(title);
        this.parent = null;
        this.subTitle = subTitle;
    }

    public UCUtilsScreen(Component title, Component subTitle, Screen parent) {
        super(title);
        this.parent = parent;
        this.subTitle = subTitle;
    }

    public UCUtilsScreen(Component title, Component subTitle, Screen parent, boolean renderBackground) {
        super(title);
        this.parent = parent;
        this.subTitle = subTitle;
        this.renderBackground = renderBackground;
    }

    public abstract void initBody();

    public abstract void doOnClose();

    @Override
    public void extractRenderState(@NonNull GuiGraphicsExtractor graphics, int mouseX, int mouseY, float a) {
        if (this.renderBackground) {
            extractListBackground(graphics);
        }

        super.extractRenderState(graphics, mouseX, mouseY, a);

        if (this.renderBackground) {
            extractListSeparators(graphics);
        }
    }

    @Override
    public void onClose() {
        this.minecraft.setScreen(null);
        doOnClose();
    }

    @Override
    protected void init() {
        initHeader();
        initBody();
        initFooter();
        this.layout.visitWidgets(this::addRenderableWidget);
        repositionElements();
    }

    @Override
    protected void repositionElements() {
        this.layout.arrangeElements();
    }

    public void back() {
        if (nonNull(this.parent)) {
            this.minecraft.setScreen(this.parent);
            doOnClose();
        } else {
            onClose();
        }
    }

    protected void initHeader() {
        LinearLayout linearLayout = this.layout.addToHeader(vertical().spacing(4));
        linearLayout.newCellSettings().alignHorizontallyCenter();
        linearLayout.addChild(new StringWidget(this.title, this.font), LayoutSettings::alignHorizontallyCenter);
        linearLayout.addChild(new StringWidget(this.subTitle, this.font), LayoutSettings::alignHorizontallyCenter);
    }

    protected void initFooter() {}

    /**
     * @see net.minecraft.client.gui.components.AbstractSelectionList#extractListSeparators(GuiGraphicsExtractor)
     */
    protected void extractListSeparators(@NonNull GuiGraphicsExtractor graphics) {
        Identifier headerSeparator = this.minecraft.level == null ? HEADER_SEPARATOR : INWORLD_HEADER_SEPARATOR;
        Identifier footerSeparator = this.minecraft.level == null ? FOOTER_SEPARATOR : INWORLD_FOOTER_SEPARATOR;
        graphics.blit(GUI_TEXTURED, headerSeparator, this.layout.getX(), this.layout.getHeaderHeight() - 2, 0.0F, 0.0F, this.layout.getWidth(), 2, 32, 2);
        graphics.blit(GUI_TEXTURED, footerSeparator, this.layout.getX(), this.layout.getHeight() - this.layout.getFooterHeight(), 0.0F, 0.0F, this.layout.getWidth(), 2, 32, 2);
    }

    /**
     * @see net.minecraft.client.gui.components.AbstractSelectionList#extractListBackground(GuiGraphicsExtractor)
     */
    protected void extractListBackground(@NonNull GuiGraphicsExtractor graphics) {
        Identifier menuListBackground = this.minecraft.level == null ? MENU_LIST_BACKGROUND : INWORLD_MENU_LIST_BACKGROUND;
        graphics.blit(GUI_TEXTURED, menuListBackground, this.layout.getX(), this.layout.getHeaderHeight(), 0.0F, 0.0F, this.layout.getWidth(), this.layout.getContentHeight(), 32, 32);
    }

    private String getVersion() {
        return FabricLoader.getInstance().getModContainer(MOD_ID)
                .map(modContainer -> modContainer.getMetadata().getVersion().getFriendlyString())
                .orElseThrow(() -> new NullPointerException("Cannot find version"));
    }
}
