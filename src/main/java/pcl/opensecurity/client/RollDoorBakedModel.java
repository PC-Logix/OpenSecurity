package pcl.opensecurity.client;

import pcl.opensecurity.blockentity.RollDoorBlockEntity;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.core.Direction;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.client.model.BakedModelWrapper;
import net.neoforged.neoforge.client.model.IDynamicBakedModel;
import net.neoforged.neoforge.client.ChunkRenderTypeSet;
import net.neoforged.neoforge.client.model.data.ModelData;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.ArrayList;

public final class RollDoorBakedModel extends BakedModelWrapper<BakedModel> implements IDynamicBakedModel {
    public RollDoorBakedModel(BakedModel originalModel) {
        super(originalModel);
    }

    @Override
    public List<BakedQuad> getQuads(@Nullable BlockState state, @Nullable Direction side, RandomSource random,
                                   ModelData data, @Nullable RenderType renderType) {
        BlockState camouflage = data.get(RollDoorBlockEntity.CAMOUFLAGE);
        if (camouflage == null) {
            return originalModel.getQuads(state, side, random, data, renderType);
        }
        BakedModel camouflageModel = Minecraft.getInstance().getBlockRenderer().getBlockModel(camouflage);
        List<BakedQuad> originalQuads = originalModel.getQuads(state, side, random, data, renderType);
        if (originalQuads.isEmpty()) return originalQuads;
        List<BakedQuad> camouflageQuads = allFacadeQuads(camouflageModel, camouflage, renderType);
        if (camouflageQuads.isEmpty()) return originalQuads;

        List<BakedQuad> result = new ArrayList<>(originalQuads.size());
        for (BakedQuad original : originalQuads) {
            List<BakedQuad> facadeFaces = matchingTextureQuads(camouflageQuads, original.getDirection());
            for (BakedQuad facade : facadeFaces) result.add(remapTexture(original, facade));
        }
        return result;
    }

    @Override
    public ChunkRenderTypeSet getRenderTypes(BlockState state, RandomSource random, ModelData data) {
        BlockState camouflage = data.get(RollDoorBlockEntity.CAMOUFLAGE);
        if (camouflage == null) return originalModel.getRenderTypes(state, random, data);
        BakedModel camouflageModel = Minecraft.getInstance().getBlockRenderer().getBlockModel(camouflage);
        return camouflageModel.getRenderTypes(camouflage, random, ModelData.EMPTY);
    }

    private static List<BakedQuad> matchingTextureQuads(List<BakedQuad> quads, Direction direction) {
        List<BakedQuad> matches = new ArrayList<>();
        for (BakedQuad quad : quads) {
            if (quad.getDirection() == direction) matches.add(quad);
        }
        if (matches.isEmpty()) matches.add(quads.get(0));
        return matches;
    }

    private static List<BakedQuad> allFacadeQuads(BakedModel model, BlockState state, @Nullable RenderType renderType) {
        RandomSource random = RandomSource.create(0L);
        List<BakedQuad> quads = new ArrayList<>(model.getQuads(state, null, random, ModelData.EMPTY, renderType));
        for (Direction direction : Direction.values()) {
            quads.addAll(model.getQuads(state, direction, random, ModelData.EMPTY, renderType));
        }
        return quads;
    }

    private static BakedQuad remapTexture(BakedQuad original, BakedQuad facade) {
        TextureAtlasSprite replacement = facade.getSprite();
        TextureAtlasSprite source = original.getSprite();
        int[] vertices = original.getVertices().clone();
        if (!mapFacadeUvs(vertices, facade)) {
            for (int vertex = 0; vertex < 4; vertex++) {
                int offset = vertex * 8;
                float u = Float.intBitsToFloat(vertices[offset + 4]);
                float v = Float.intBitsToFloat(vertices[offset + 5]);
                float normalizedU = (u - source.getU0()) / (source.getU1() - source.getU0());
                float normalizedV = (v - source.getV0()) / (source.getV1() - source.getV0());
                vertices[offset + 4] = Float.floatToRawIntBits(replacement.getU(normalizedU));
                vertices[offset + 5] = Float.floatToRawIntBits(replacement.getV(normalizedV));
            }
        }
        return new BakedQuad(vertices, facade.getTintIndex(), original.getDirection(), replacement,
                original.isShade(), original.hasAmbientOcclusion());
    }

    private static boolean mapFacadeUvs(int[] target, BakedQuad facade) {
        int[] source = facade.getVertices();
        int[] axes = axesFor(facade.getDirection());
        for (int first = 0; first < 4; first++) {
            for (int second = first + 1; second < 4; second++) {
                for (int third = second + 1; third < 4; third++) {
                    float x0 = component(source, first, axes[0]);
                    float y0 = component(source, first, axes[1]);
                    float dx1 = component(source, second, axes[0]) - x0;
                    float dy1 = component(source, second, axes[1]) - y0;
                    float dx2 = component(source, third, axes[0]) - x0;
                    float dy2 = component(source, third, axes[1]) - y0;
                    float determinant = dx1 * dy2 - dy1 * dx2;
                    if (Math.abs(determinant) < 0.000001F) continue;

                    for (int vertex = 0; vertex < 4; vertex++) {
                        float dx = component(target, vertex, axes[0]) - x0;
                        float dy = component(target, vertex, axes[1]) - y0;
                        float secondWeight = (dx1 * dy - dy1 * dx) / determinant;
                        float firstWeight = (dx * dy2 - dy * dx2) / determinant;
                        int offset = vertex * 8;
                        float u = uv(source, first, 0) + firstWeight * (uv(source, second, 0) - uv(source, first, 0))
                                + secondWeight * (uv(source, third, 0) - uv(source, first, 0));
                        float v = uv(source, first, 1) + firstWeight * (uv(source, second, 1) - uv(source, first, 1))
                                + secondWeight * (uv(source, third, 1) - uv(source, first, 1));
                        target[offset + 4] = Float.floatToRawIntBits(u);
                        target[offset + 5] = Float.floatToRawIntBits(v);
                    }
                    return true;
                }
            }
        }
        return false;
    }

    private static int[] axesFor(Direction direction) {
        return switch (direction.getAxis()) {
            case X -> new int[]{2, 1};
            case Y -> new int[]{0, 2};
            case Z -> new int[]{0, 1};
        };
    }

    private static float component(int[] vertices, int vertex, int component) {
        return Float.intBitsToFloat(vertices[vertex * 8 + component]);
    }

    private static float uv(int[] vertices, int vertex, int component) {
        return Float.intBitsToFloat(vertices[vertex * 8 + 4 + component]);
    }
}
