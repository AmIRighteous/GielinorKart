package com.restingbuff;

import java.awt.*;
import java.time.Duration;
import java.util.Objects;
import java.util.concurrent.TimeUnit;
import javax.inject.Inject;

import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import net.runelite.client.config.ConfigManager;
import net.runelite.client.ui.FontManager;
import net.runelite.client.ui.overlay.*;
import net.runelite.client.ui.overlay.components.LineComponent;
import net.runelite.client.ui.overlay.components.TitleComponent;

@Slf4j
class TimerOverlay extends OverlayPanel
{
    private final RestingBuffConfig config;
    private final RestingBuffPlugin plugin;
    private final LineComponent timeRemainingComponent;
    public static TitleComponent RUNNING_TITLE = TitleComponent.builder().color(Color.GREEN).text("Timer Running!").build();
    public static TitleComponent PAUSED_TITLE = TitleComponent.builder().color(Color.WHITE).text("Timer Paused").build();
    public static TitleComponent FINISHED_TITLE = TitleComponent.builder().color(Color.RED).text("FINISHED!").build();

    @Setter
    public String courseName = "";

    @Inject
    private TimerOverlay(RestingBuffConfig config, RestingBuffPlugin plugin)
    {
        super(plugin);
        setPosition(OverlayPosition.BOTTOM_LEFT);
        this.config = config;
        this.plugin = plugin;


        panelComponent.getChildren().add(PAUSED_TITLE);
        timeRemainingComponent = LineComponent.builder().left("Time:").right("").build();
        panelComponent.getChildren().add(timeRemainingComponent);

        setClearChildren(false);
    }

    @Override
    public Dimension render(Graphics2D graphics)
    {
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
        return super.render(graphics);
    }

    private static String formatTime(final long remaining)
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
}
