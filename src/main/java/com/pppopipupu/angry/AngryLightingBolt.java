package com.pppopipupu.angry;
import net.minecraft.util.RandomSource;
import org.joml.Vector3f;

import java.util.ArrayList;
import java.util.List;
public class AngryLightingBolt {

    public final List<Vector3f> segments = new ArrayList<>();
    public final List<AngryLightingBolt> children = new ArrayList<>();
    public final float red, green, blue;
    public final float thickness;
    public int life;

    public AngryLightingBolt(Vector3f start, Vector3f end, int segmentCount, float jitter, float thickness, int life, float r, float g, float b, int maxRecursion, RandomSource random) {
        this(start, end, segmentCount, jitter, thickness, life, r, g, b, 0, maxRecursion, random);
    }

    private AngryLightingBolt(Vector3f start, Vector3f end, int segmentCount, float jitter, float thickness, int life, float r, float g, float b, int recursionDepth, int maxRecursion, RandomSource random) {
        this.thickness = thickness;
        this.life = life;
        this.red = r;
        this.green = g;
        this.blue = b;

        generateSegments(start, end, segmentCount, jitter, random);

        if (recursionDepth < maxRecursion) {
            int forks = random.nextInt(3);
            for (int i = 0; i < forks; i++) {
                if (this.segments.size() > 2) {
                    Vector3f forkStart = this.segments.get(random.nextInt(this.segments.size() - 2) + 1);

                    Vector3f direction = end.sub(start, new Vector3f()).normalize();
                    direction.add(
                            (random.nextFloat() - 0.5f) * 1.5f,
                            (random.nextFloat() - 0.5f) * 1.5f,
                            (random.nextFloat() - 0.5f) * 1.5f
                    ).normalize();

                    float forkLength = end.distance(start) * (0.5f + random.nextFloat() * 0.3f);
                    Vector3f forkEnd = new Vector3f(direction).mul(forkLength).add(forkStart);

                    AngryLightingBolt child = new AngryLightingBolt(
                            forkStart,
                            forkEnd,
                            segmentCount / 2 + 1,
                            jitter,
                            thickness * 0.6f,
                            life / 2,
                            r, g, b,
                            recursionDepth + 1,
                            maxRecursion,
                            random
                    );
                    this.children.add(child);
                }
            }
        }
    }

    private void generateSegments(Vector3f start, Vector3f end, int segmentCount, float jitter, RandomSource random) {
        this.segments.add(start);
        Vector3f diff = end.sub(start, new Vector3f());
        float totalLength = diff.length();
        if (totalLength <= 0) {
            this.segments.add(end);
            return;
        }

        for (int i = 1; i < segmentCount; i++) {
            float progress = (float) i / segmentCount;
            Vector3f pointOnLine = new Vector3f(diff).mul(progress).add(start);
            float currentJitter = jitter * (totalLength / segmentCount);
            pointOnLine.add(
                    (random.nextFloat() - 0.5f) * 2.0f * currentJitter,
                    (random.nextFloat() - 0.5f) * 2.0f * currentJitter,
                    (random.nextFloat() - 0.5f) * 2.0f * currentJitter
            );
            this.segments.add(pointOnLine);
        }
        this.segments.add(end);
    }

    public void update() {
        if (this.life > 0) {
            this.life--;
        }
        for (AngryLightingBolt child : children) {
            child.update();
        }
    }
}