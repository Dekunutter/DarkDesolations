package com.deku.darkdesolations.common.entity.goals;

import com.deku.darkdesolations.common.entity.monster.Coralfish;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.goal.Goal;

import java.util.EnumSet;

public class SleepGoal extends Goal {
    private final Coralfish coralfish;

    public SleepGoal(Coralfish coralfish) {
        this.coralfish = coralfish;
        setFlags(EnumSet.of(Goal.Flag.MOVE, Goal.Flag.LOOK));
    }

    @Override
    public boolean canUse() {
        LivingEntity target = coralfish.getTarget();
        return coralfish.isAwake() && target == null;
    }

    public void start() {
        coralfish.sleep();
    }

    public boolean requiresUpdateEveryTick() {
        return false;
    }
}
