package com.deku.darkdesolations.common.entity.goals;

import com.deku.darkdesolations.common.entity.monster.Coralfish;
import net.minecraft.world.entity.ai.goal.Goal;

import java.util.EnumSet;

public class LookForDeadCoralGoal extends Goal {
    public LookForDeadCoralGoal(Coralfish coralfish) {
        setFlags(EnumSet.of(Goal.Flag.MOVE, Goal.Flag.LOOK));
    }

    @Override
    public boolean canUse() {
        return false;
    }
}
