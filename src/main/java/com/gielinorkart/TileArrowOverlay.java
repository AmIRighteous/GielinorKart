package com.gielinorkart;

import lombok.extern.slf4j.Slf4j;
import net.runelite.api.Client;
import net.runelite.api.coords.WorldPoint;
import net.runelite.client.ui.overlay.Overlay;
import net.runelite.client.ui.overlay.OverlayLayer;
import net.runelite.client.ui.overlay.OverlayPosition;

import javax.inject.Inject;
import java.awt.*;
@Slf4j
public class TileArrowOverlay extends Overlay{
    private final GielinorKartPlugin plugin;
    private final GielinorKartConfig config;
    private final Client client;

    @Inject
    public TileArrowOverlay(Client client, GielinorKartPlugin plugin, GielinorKartConfig config) {
        this.plugin = plugin;
        this.config = config;
        this.client = client;
        setPosition(OverlayPosition.DYNAMIC);
        setLayer(OverlayLayer.ABOVE_SCENE);
    }
    @Override
    public Dimension render(Graphics2D graphics) {
        if (config.showArrow()) {
            WorldPoint nextPoint = plugin.getNextTile();
            if (nextPoint != null && plugin.getTimer().isActive()) {
                client.setHintArrow(nextPoint);
            }
        }
        return null;
    }
}
