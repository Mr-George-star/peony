package net.george.peony.advancement.criterion;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.george.peony.advancement.PeonyCriteria;
import net.minecraft.advancement.AdvancementCriterion;
import net.minecraft.advancement.criterion.AbstractCriterion;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.predicate.entity.EntityPredicate;
import net.minecraft.predicate.entity.LootContextPredicate;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.Identifier;
import net.minecraft.util.StringIdentifiable;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;

public class CookingFinishedCriterion extends AbstractCriterion<CookingFinishedCriterion.Conditions> {
    @Override
    public Codec<Conditions> getConditionsCodec() {
        return Conditions.CODEC;
    }

    public void trigger(ServerPlayerEntity player, BlockEntityType<?> entityType, Conditions.FinishingType finishingType) {
        @Nullable Identifier typeId = BlockEntityType.getId(entityType);
        if (typeId != null) {
            trigger(player, typeId, finishingType);
        }
    }

    public void trigger(ServerPlayerEntity player, Identifier typeId, Conditions.FinishingType finishingType) {
        this.trigger(player, conditions ->
                conditions.matches(typeId, finishingType));
    }

    public record Conditions(Optional<LootContextPredicate> player, Identifier typeId, FinishingType finishingType) implements AbstractCriterion.Conditions {
        public static final Codec<Conditions> CODEC = RecordCodecBuilder.create(instance -> instance.group(
                EntityPredicate.LOOT_CONTEXT_PREDICATE_CODEC.optionalFieldOf("player").forGetter(Conditions::player),
                Identifier.CODEC.fieldOf("type_id").forGetter(Conditions::typeId),
                FinishingType.CODEC.fieldOf("finishing_type").forGetter(Conditions::finishingType)
        ).apply(instance, Conditions::new));

        public static AdvancementCriterion<Conditions> create(BlockEntityType<?> type, FinishingType finishingType) {
            return create(BlockEntityType.getId(type), finishingType);
        }

        public static AdvancementCriterion<Conditions> create(Identifier typeId, FinishingType finishingType) {
            return PeonyCriteria.COOKING_FINISHED.create(new Conditions(Optional.empty(), typeId, finishingType));
        }

        @Override
        public Optional<LootContextPredicate> player() {
            return this.player;
        }

        public boolean matches(Identifier typeId, FinishingType finishingType) {
            return this.typeId.equals(typeId) && this.finishingType.equals(finishingType);
        }

        public enum FinishingType implements StringIdentifiable {
            SUCCESS("success"),
            FAILED("failed");

            public static final Codec<FinishingType> CODEC = StringIdentifiable.createCodec(FinishingType::values);
            private final String name;

            FinishingType(String name) {
                this.name = name;
            }

            @Override
            public String asString() {
                return this.name;
            }

            @Override
            public String toString() {
                return this.name;
            }
        }
    }
}
