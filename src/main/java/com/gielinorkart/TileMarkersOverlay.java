package com.gielinorkart;

import lombok.extern.slf4j.Slf4j;
import net.runelite.api.Client;
import net.runelite.api.Perspective;
import net.runelite.api.Point;
import net.runelite.api.coords.LocalPoint;
import net.runelite.api.coords.WorldPoint;
import net.runelite.client.ui.overlay.*;

import javax.inject.Inject;
import java.awt.*;
@Slf4j
public class TileMarkersOverlay extends Overlay {
    private final Client client;
    private final GielinorKartConfig config;
    private final GielinorKartPlugin plugin;

    @Inject
    private TileMarkersOverlay(Client client, GielinorKartConfig config, GielinorKartPlugin plugin) {
        this.client = client;
        this.config = config;
        this.plugin = plugin;
        setPosition(OverlayPosition.DYNAMIC);
        setLayer(OverlayLayer.ABOVE_SCENE);
        setPriority(OverlayPriority.MED);
    }

    @Override
    public Dimension render(Graphics2D graphics) {
        if (config.showLines()) {
            showRaceTiles(graphics);
        }
        return null;
    }

        private void showRaceTiles(final Graphics2D graphics) {
        for (Race r: plugin.races) {
            renderTile(graphics, LocalPoint.fromWorld(client, r.getStart()), Color.GREEN, 2.0, r.getCourseName() + " Start");
            renderTile(graphics, LocalPoint.fromWorld(client, r.getEnd()), Color.RED, 2.0, r.getCourseName() + " End");
            int counter = 1;
            for (WorldPoint w: r.getCheckpoints()) {
                renderTile(graphics, LocalPoint.fromWorld(client, w), Color.WHITE, 2.0, "Checkpoint " + counter);
                counter++;
            }
        }
    }

    public void renderTile(final Graphics2D graphics, final LocalPoint dest, final Color color, final double borderWidth, final String label)
    {
        if (dest == null)
        {
            return;
        }

        final Polygon poly = Perspective.getCanvasTilePoly(client, dest);
        if (poly == null)
        {
            return;
        }
        if (label != null) {
            Point canvasTextLocation = Perspective.getCanvasTextLocation(client, graphics, dest, label, 0);
            OverlayUtil.renderTextLocation(graphics, canvasTextLocation, label, color);
        }
        OverlayUtil.renderPolygon(graphics, poly, color, new Color(0, 0, 0, 50), new BasicStroke((float) borderWidth));
    }
}
