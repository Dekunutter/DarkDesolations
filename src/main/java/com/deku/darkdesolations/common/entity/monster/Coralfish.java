package com.deku.darkdesolations.common.entity.monster;

import com.deku.darkdesolations.common.entity.goals.AwakenGoal;
import com.deku.darkdesolations.common.entity.goals.LookForDeadCoralGoal;
import com.deku.darkdesolations.common.entity.goals.SleepGoal;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.MeleeAttackGoal;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import software.bernie.geckolib.animatable.GeoEntity;
import software.bernie.geckolib.constant.DefaultAnimations;
import software.bernie.geckolib.core.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.core.animation.AnimatableManager;
import software.bernie.geckolib.core.animation.AnimationController;
import software.bernie.geckolib.core.animation.RawAnimation;
import software.bernie.geckolib.util.ClientUtils;
import software.bernie.geckolib.util.GeckoLibUtil;

// TODO: Needs some more ambient goals so it doesnt just freeze there. Though thats good for hiding it
// TODO: Need animations
// TODO: Needs to be able to climb dead coral blocks. Maybe check how spider does this but then constrain it to only certain blocks
public class Coralfish extends Monster implements GeoEntity {
    private final AnimatableInstanceCache cache = GeckoLibUtil.createInstanceCache(this);
    private static final RawAnimation WAKE_ANIM = RawAnimation.begin().thenPlay("misc.wake");
    private static final RawAnimation SLEEP_ANIM = RawAnimation.begin().thenPlay("misc.sleep");

    private boolean isAwake = false;

    public Coralfish(EntityType<? extends Monster> entityType, Level level) {
        super(entityType, level);
    }

    protected void registerGoals() {
        this.goalSelector.addGoal(1, new AwakenGoal(this));
        this.goalSelector.addGoal(2, new MeleeAttackGoal(this, 1.0D, false));
        this.goalSelector.addGoal(3, new SleepGoal(this));
        this.goalSelector.addGoal(4, new LookForDeadCoralGoal(this));

        this.targetSelector.addGoal(1, new NearestAttackableTargetGoal<>(this, Player.class, true));
        super.registerGoals();
    }

    public static AttributeSupplier.Builder createAttributes() {
        return Monster.createMonsterAttributes()
            .add(Attributes.MAX_HEALTH, 8.0D)
            .add(Attributes.MOVEMENT_SPEED, 0.25D)
            .add(Attributes.ATTACK_DAMAGE, 3.0D)
            .add(Attributes.FOLLOW_RANGE, 10.0D);
    }

    protected float getStandingEyeHeight(Pose pose, EntityDimensions dimensions) {
        return 0.75F;
    }

    protected SoundEvent getAmbientSound() {
        return SoundEvents.SILVERFISH_AMBIENT;
    }

    protected SoundEvent getHurtSound(DamageSource source) {
        return SoundEvents.SILVERFISH_HURT;
    }

    protected SoundEvent getDeathSound() {
        return SoundEvents.SILVERFISH_DEATH;
    }

    protected void playStepSound(BlockPos position, BlockState blockState) {
        this.playSound(SoundEvents.SILVERFISH_STEP, 0.15F, 1.0F);
    }

    public MobType getMobType() {
        return MobType.ARTHROPOD;
    }

    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controllers) {
        controllers.add(
            new AnimationController<>(this, "awakenController", 10, state -> {
                System.out.println("zzzzzzTEST OUTPUT");
                System.out.println(getTarget());
                System.out.println(isAwake);
                // TODO: Looks like target never gets set.... Must be some other way to see if they are eyeing a target...
                //  Also gonna need some offset to ensure the animation plays before they begin walking. Probably just a boolean to trigger when the waking animation ends
                if (getTarget() != null && !isAwake) {
                    return state.setAndContinue(SLEEP_ANIM);
                } else {
                    return state.setAndContinue(DefaultAnimations.IDLE);
                }
            })
            .setCustomInstructionKeyframeHandler(state -> {
                Player player = ClientUtils.getClientPlayer();
                if (player != null) {
                    player.displayClientMessage(Component.literal("KeyFraming"), true);
                }
            }),
            new AnimationController<>(this, "sleepController", 10, state -> {
                if (getTarget() == null && isAwake) {
                    return state.setAndContinue(SLEEP_ANIM);
                } else {
                    return state.setAndContinue(DefaultAnimations.IDLE);
                }
            })
            .setCustomInstructionKeyframeHandler(state -> {
                Player player = ClientUtils.getClientPlayer();
                if (player != null) {
                    player.displayClientMessage(Component.literal("KeyFraming"), true);
                }
            }),
            DefaultAnimations.genericLivingController(this),
            DefaultAnimations.genericWalkIdleController(this)
        );

        controllers.add(
//            new AnimationController<>(this, 10, state -> state.setAndContinue(this.isAwake ? DefaultAnimations.WALK : DefaultAnimations.IDLE))
//                .setCustomInstructionKeyframeHandler(state -> {
//                    Player player = ClientUtils.getClientPlayer();
//                    if (player != null) {
//                        player.displayClientMessage(Component.literal("KeyFraming"), true);
//                    }
//                }),
        );
    }

    @Override
    public AnimatableInstanceCache getAnimatableInstanceCache() {
        return this.cache;
    }

    public boolean isAwake() {
        return isAwake;
    }

    public void awaken() {
        isAwake = true;
    }

    public void sleep() {
        isAwake = false;
    }
}
