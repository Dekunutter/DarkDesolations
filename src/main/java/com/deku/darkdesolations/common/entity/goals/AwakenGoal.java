package com.deku.darkdesolations.common.entity.goals;

import com.deku.darkdesolations.common.entity.monster.Coralfish;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.goal.Goal;

import javax.annotation.Nullable;
import java.util.EnumSet;

public class AwakenGoal extends Goal {
    private final Coralfish coralfish;

    @Nullable
    private LivingEntity target;

    public AwakenGoal(Coralfish coralfish) {
        this.coralfish = coralfish;
        setFlags(EnumSet.of(Goal.Flag.MOVE));
    }

    @Override
    public boolean canUse() {
        LivingEntity target = coralfish.getTarget();

        System.out.println("Can use awaken goal");
        System.out.println(!coralfish.isAwake());
        System.out.println(target != null);
        System.out.println(target != null && coralfish.distanceToSqr(target) < 10.0D);

        // TODO get distance from coralfish attributes?
        return !coralfish.isAwake() && target != null && coralfish.distanceToSqr(target) < 10.0D;
    }

    public void start() {
        System.out.println("Kill navigation");
        coralfish.getNavigation().stop();
        target = coralfish.getTarget();
    }

    public void stop() {
        System.out.println("Stop awakening");
        target = null;
    }

    public boolean requiresUpdateEveryTick() {
        return true;
    }

    public void tick() {
        System.out.println("Attempt awakening");
        System.out.println(target != null);
        System.out.println(target != null && coralfish.distanceToSqr(target) > 10.0D);
        System.out.println(target != null && coralfish.getSensing().hasLineOfSight(target));
        if(target == null) {
            coralfish.sleep();
        } else if (coralfish.distanceToSqr(target) > 10.0D) {
            coralfish.sleep();
        } else if (!coralfish.getSensing().hasLineOfSight(target)) {
            coralfish.sleep();
        } else {
            System.out.println("AWAKEN!!!");
            coralfish.awaken();
        }
    }
}
