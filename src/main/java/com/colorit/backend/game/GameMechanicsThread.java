package com.colorit.backend.game;

import com.colorit.backend.game.mechanics.GameMechanics;
import org.springframework.stereotype.Service;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import javax.annotation.PostConstruct;
import javax.validation.constraints.NotNull;
import java.time.Clock;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

import static com.colorit.backend.game.GameConfig.ONE_TIME_STEP;

@Service
public class GameMechanicsThread implements Runnable {
    private static final @NotNull Logger LOGGER = LoggerFactory.getLogger(GameMechanicsThread.class);

    private final @NotNull GameMechanics gameMechanics;

    private final @NotNull Clock clock = Clock.systemDefaultZone();

    private final Executor tickExecutor = Executors.newSingleThreadExecutor();

    @Autowired
    public GameMechanicsThread(@NotNull GameMechanics gameMechanics) {
        this.gameMechanics = gameMechanics;
    }

    @PostConstruct
    public void initAfterStartup() {
        tickExecutor.execute(this);
    }

    @Override
    public void run() {
        try {
            mainCycle();
        } finally {
            LOGGER.warn("Game thread terminated");
        }
    }

    private void mainCycle() {
        long lastFrameMillis = ONE_TIME_STEP;
        while (true) {
            try {
                final long before = clock.millis();

                gameMechanics.gameStep(lastFrameMillis);

                final long after = clock.millis();
                try {
                    final long sleepingTime = Math.max(0, ONE_TIME_STEP - (after - before));
                    Thread.sleep(sleepingTime);
                } catch (InterruptedException e) {
                    LOGGER.error("Mechanics thread was interrupted", e);
                }

                if (Thread.currentThread().isInterrupted()) {
                    gameMechanics.reset();
                    return;
                }
                final long afterSleep = clock.millis();
                lastFrameMillis = afterSleep - before;
            } catch (RuntimeException e) {
                LOGGER.error("Mechanics executor was reseted due to exception", e);
                gameMechanics.reset();
            }
        }
    }
}
