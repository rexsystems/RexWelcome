package cc.rexsystems.rexwelcome.utils;

import cc.rexsystems.rexwelcome.RexWelcome;
import org.bukkit.entity.Player;

public final class SchedulerUtils {

    private SchedulerUtils() {
    }

    public static void runDelayed(RexWelcome plugin, Player player, Runnable task, long delayMs) {
        if (delayMs <= 0) {
            task.run();
            return;
        }

        long delayTicks = (delayMs + 49L) / 50L;
        player.getScheduler().runDelayed(plugin, scheduledTask -> task.run(), null, delayTicks);
    }
}
