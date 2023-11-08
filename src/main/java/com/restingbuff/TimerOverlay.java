package com.restingbuff;

import java.awt.*;
import java.time.Duration;
import java.util.Objects;
import java.util.concurrent.TimeUnit;
import javax.inject.Inject;

import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import net.runelite.api.Client;
import net.runelite.api.Perspective;
import net.runelite.api.Point;
import net.runelite.api.coords.LocalPoint;
import net.runelite.api.coords.WorldPoint;
import net.runelite.client.config.ConfigManager;
import net.runelite.client.ui.FontManager;
import net.runelite.client.ui.overlay.*;
import net.runelite.client.ui.overlay.components.LineComponent;
import net.runelite.client.ui.overlay.components.TitleComponent;

@Slf4j
class TimerOverlay extends OverlayPanel
{
    private final Client client;
    private final RestingBuffConfig config;
    private final RestingBuffPlugin plugin;
    private final LineComponent timeRemainingComponent;
    public static TitleComponent RUNNING_TITLE = TitleComponent.builder().color(Color.GREEN).text("Timer Running!").build();
    public static TitleComponent PAUSED_TITLE = TitleComponent.builder().color(Color.WHITE).text("Timer Paused").build();
    public static TitleComponent FINISHED_TITLE = TitleComponent.builder().color(Color.RED).text("FINISHED!").build();

    @Setter
    public String courseName = "";

    @Inject
    private TimerOverlay(RestingBuffConfig config, RestingBuffPlugin plugin, Client client)
    {
        super(plugin);
        setPosition(OverlayPosition.BOTTOM_LEFT);
        this.config = config;
        this.plugin = plugin;
        this.client = client;
        panelComponent.getChildren().add(PAUSED_TITLE);
        timeRemainingComponent = LineComponent.builder().left("Time:").right("").build();
        panelComponent.getChildren().add(timeRemainingComponent);
        setClearChildren(false);
    }
    private WorldPoint randomTile = new WorldPoint(3221, 3221, 0);

    @Override
    public Dimension render(Graphics2D graphics)
    {
        if (config.showLines()) {
            log.info("We have toggled show lines!");
            log.info("RandomTile = " + randomTile);
            renderTile(graphics, LocalPoint.fromWorld(client, randomTile), Color.YELLOW, 20, Color.YELLOW);
        }
        if (plugin.getStartToTitle().containsKey(plugin.getPlayerLocation())) {
            courseName = "The Lum Bridge";
            Duration elapsedTime = plugin.getTimer().getRealTime();
            graphics.setFont(FontManager.getRunescapeFont());

            panelComponent.getChildren().clear();
            if (!courseName.isEmpty()) {
                panelComponent.getChildren().add(TitleComponent.builder().color(Color.WHITE).text(courseName).build());
            }
            if (plugin.getTimer().isActive()) {
                panelComponent.getChildren().add(RUNNING_TITLE);
            } else if (plugin.getTimer().isCompleted()) {
                panelComponent.getChildren().add(FINISHED_TITLE);
            }
            else {
                panelComponent.getChildren().add(PAUSED_TITLE);
            }
            final Color timeColor =  Color.WHITE;
            timeRemainingComponent.setRightColor(timeColor);
            timeRemainingComponent.setRight(formatTime(elapsedTime.toSeconds()));
            panelComponent.getChildren().add(timeRemainingComponent);
        } else {
            panelComponent.getChildren().clear();
            setPriority(OverlayPriority.LOW);
        }
//        if (config.showLines()) {
//            //showRaceTiles(graphics);
//            log.info("ShowLines is True!");
//            renderTile(graphics, LocalPoint.fromWorld(client, randomTile), Color.WHITE, 2.0, Color.WHITE);
//        }
        return super.render(graphics);
    }

    public String formatTime(final long remaining)
    {
        final long hours = TimeUnit.SECONDS.toHours(remaining);
        final long minutes = TimeUnit.SECONDS.toMinutes(remaining % 3600);
        final long seconds = remaining % 60;

        if(remaining < 60) {
            return String.format("%01ds", seconds);
        }
        if(remaining < 3600) {
            return String.format("%2dm %02ds", minutes, seconds);
        }
        return String.format("%1dh %02dm", hours, minutes);
    }

    private void showRaceTiles(final Graphics2D graphics) {
        for (Race r: plugin.races) {
            renderTile(graphics, LocalPoint.fromWorld(client, r.getStart()), Color.GREEN, 2.0, Color.GREEN);
            renderTile(graphics, LocalPoint.fromWorld(client, r.getEnd()), Color.RED, 2.0, Color.RED);
            for (WorldPoint w: r.getCheckpoints()) {
                renderTile(graphics, LocalPoint.fromWorld(client, w), Color.WHITE, 2.0, Color.WHITE);
            }
        }
    }

    public void renderTile(final Graphics2D graphics, final LocalPoint dest, final Color color, final double borderWidth, final Color fillColor)
    {
        log.info("Dest = " + dest);
        if (dest == null)
        {
            return;
        }

        final Polygon poly = Perspective.getCanvasTilePoly(client, dest);
        log.info("Poly = " + poly);
        if (poly == null)
        {
            return;
        }
        Point canvasTextLocation = Perspective.getCanvasTextLocation(client, graphics, dest, "Start", 0);
        OverlayUtil.renderTextLocation(graphics, canvasTextLocation, "Start", color);
        OverlayUtil.renderPolygon(graphics, poly, color, fillColor, new BasicStroke((float) borderWidth));
    }
}
