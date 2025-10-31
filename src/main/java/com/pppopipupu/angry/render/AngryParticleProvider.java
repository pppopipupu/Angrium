package com.pppopipupu.angry.render;

import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.particle.ParticleProvider;
import net.minecraft.client.particle.SpriteSet;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.core.particles.SimpleParticleType;

public class AngryParticleProvider implements ParticleProvider<SimpleParticleType> {
    public AngryParticleProvider(SpriteSet spriteSet) {
    }

    @Override
    public Particle createParticle(SimpleParticleType type, ClientLevel level,
                                   double x, double y, double z,
                                   double xd, double yd, double zd) {
        return new AngryParticle(level, x, y, z, xd, yd, zd);
    }
}
