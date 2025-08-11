package net.george.peony.block.entity;

import net.minecraft.util.math.Direction;

@FunctionalInterface
public interface DirectionProvider {
    Direction getDirection();
}
