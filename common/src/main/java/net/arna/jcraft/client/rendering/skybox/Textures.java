package net.arna.jcraft.client.rendering.skybox;

import com.google.common.collect.Lists;
import java.util.List;
import net.minecraft.resources.ResourceLocation;

public class Textures {
    private final List<Texture> textureList = Lists.newArrayList();

    public Textures(Texture north, Texture south, Texture east, Texture west, Texture up, Texture down) {
        this.textureList.add(down);
        this.textureList.add(north);
        this.textureList.add(south);
        this.textureList.add(up);
        this.textureList.add(east);
        this.textureList.add(west);
    }

    public Texture byId(final int i) {
        return this.textureList.get(i);
    }

    public record Texture(ResourceLocation textureId, float minU, float minV, float maxU, float maxV) {

        public Texture(ResourceLocation textureId) {
            this(textureId, 0.0F, 0.0F, 1.0F, 1.0F);
        }

        public Texture withUV(float minU, float minV, float maxU, float maxV) {
            return new Texture(this.textureId(), minU, minV, maxU, maxV);
        }
    }
}
