package net.george.peony.effect;

import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.attribute.EntityAttributeModifier;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.entity.effect.StatusEffectCategory;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.random.Random;

public class BitterStatusEffect extends StatusEffect {
    public BitterStatusEffect() {
        super(StatusEffectCategory.HARMFUL, 0x84bb0f);
        this.addAttributeModifier(EntityAttributes.GENERIC_ATTACK_DAMAGE,
                Identifier.ofVanilla("effect.weakness"), -1.0, EntityAttributeModifier.Operation.ADD_VALUE);
    }

    @Override
    public boolean applyUpdateEffect(LivingEntity entity, int amplifier) {
        Random random = entity.getRandom();
        if (entity.getHealth() > 1.0F) {
            entity.damage(entity.getDamageSources().magic(), (float) random.nextBetween(10, 5) / 10);
        }
        return true;
    }

    @Override
    public boolean canApplyUpdateEffect(int duration, int amplifier) {
        int seconds = 6 - amplifier;
        return duration % ((seconds <= 0 ? 1 : seconds) * 20) == 0 && amplifier > 1;
    }
}
