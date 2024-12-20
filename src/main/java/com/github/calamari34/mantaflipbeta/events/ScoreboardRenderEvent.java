package com.github.calamari34.mantaflipbeta.events;

import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.scoreboard.ScoreObjective;
import net.minecraftforge.fml.common.eventhandler.Cancelable;
import net.minecraftforge.fml.common.eventhandler.Event;

// TODO: Implement Lombok Library for clean code
@Cancelable
public class ScoreboardRenderEvent extends Event {
    public final ScoreObjective objective;
    public final ScaledResolution resolution;

    public ScoreboardRenderEvent(ScoreObjective objective, ScaledResolution resolution) {
        this.objective = objective;
        this.resolution = resolution;
    }
}