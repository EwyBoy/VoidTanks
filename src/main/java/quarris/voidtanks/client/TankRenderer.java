package quarris.voidtanks.client;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.vertex.IVertexBuilder;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.*;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.renderer.tileentity.TileEntityRenderer;
import net.minecraft.client.renderer.tileentity.TileEntityRendererDispatcher;
import net.minecraft.fluid.Fluid;
import net.minecraft.inventory.container.PlayerContainer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.vector.Matrix3f;
import net.minecraft.util.math.vector.Matrix4f;
import net.minecraft.util.math.vector.Vector3f;
import net.minecraft.world.IBlockDisplayReader;
import net.minecraftforge.fluids.FluidStack;
import quarris.voidtanks.content.TankTile;

public class TankRenderer extends TileEntityRenderer<TankTile> {

    public TankRenderer(TileEntityRendererDispatcher rendererDispatcherIn) {
        super(rendererDispatcherIn);
    }

    @Override
    public void render(TankTile tank, float partialTicks, MatrixStack matrix, IRenderTypeBuffer buffer, int combinedLightIn, int combinedOverlayIn) {
        FluidStack fluidStack = tank.getFluid();
        if (!fluidStack.isEmpty()) {
            int amount = fluidStack.getAmount();
            int total = tank.getTank().getTankCapacity(0);
            this.renderFluidInTank(tank.getWorld(), tank.getPos(), fluidStack, matrix, buffer, amount / (float) total);
        }
    }

    private void renderFluidInTank(IBlockDisplayReader world, BlockPos pos, FluidStack fluid, MatrixStack matrix, IRenderTypeBuffer buffer, float fluidPerc) {
        matrix.push();
        matrix.translate(0.5d, 0.5d, 0.5d);
        Matrix4f matrix4f = matrix.getLast().getMatrix();
        Matrix3f matrix3f = matrix.getLast().getNormal();
        int color = fluid.getFluid().getAttributes().getColor(world, pos);
        TextureAtlasSprite sprite = this.getFluidStillSprite(fluid.getFluid());
        IVertexBuilder builder = buffer.getBuffer(RenderType.getText(sprite.getAtlasTexture().getTextureLocation()));
        for (int i = 0; i < 4; i++) {
            this.renderNorthFluidFace(sprite, matrix4f, matrix3f, builder, color, fluidPerc);
            matrix.rotate(Vector3f.YP.rotationDegrees(90));
        }
        this.renderTopFluidFace(sprite, matrix4f, matrix3f, builder, color, fluidPerc);
        matrix.pop();
    }

    private void renderTopFluidFace(TextureAtlasSprite sprite, Matrix4f matrix4f, Matrix3f normalMatrix, IVertexBuilder builder, int color, float fluidPerc) {
        float r = ((color >> 16) & 0xFF) / 255f;
        float g = ((color >> 8) & 0xFF) / 255f;
        float b = ((color) & 0xFF) / 255f;
        float a = ((color >> 24) & 0xFF) / 255f;

        float width = 10 / 16f;
        float height = 14 / 16f;

        float minU = sprite.getInterpolatedU(3);
        float maxU = sprite.getInterpolatedU(13);
        float minV = sprite.getInterpolatedV(3);
        float maxV = sprite.getInterpolatedV(13);

        builder.pos(matrix4f, -width / 2, -height / 2 + fluidPerc * height, -width / 2).color(r, g, b, a)
                .tex(minU, minV)
                .overlay(OverlayTexture.NO_OVERLAY).lightmap(15728880).normal(normalMatrix, 0, 1, 0)
                .endVertex();

        builder.pos(matrix4f, -width / 2, -height / 2 + fluidPerc * height, width / 2).color(r, g, b, a)
                .tex(minU, maxV)
                .overlay(OverlayTexture.NO_OVERLAY).lightmap(15728880).normal(normalMatrix, 0, 1, 0)
                .endVertex();

        builder.pos(matrix4f, width / 2, -height / 2 + fluidPerc * height, width / 2).color(r, g, b, a)
                .tex(maxU, maxV)
                .overlay(OverlayTexture.NO_OVERLAY).lightmap(15728880).normal(normalMatrix, 0, 1, 0)
                .endVertex();

        builder.pos(matrix4f, width / 2, -height / 2 + fluidPerc * height, -width / 2).color(r, g, b, a)
                .tex(maxU, minV)
                .overlay(OverlayTexture.NO_OVERLAY).lightmap(15728880).normal(normalMatrix, 0, 1, 0)
                .endVertex();
    }

    private void renderNorthFluidFace(TextureAtlasSprite sprite, Matrix4f matrix4f, Matrix3f normalMatrix, IVertexBuilder builder, int color, float fluidPerc) {
        float r = ((color >> 16) & 0xFF) / 255f;
        float g = ((color >> 8) & 0xFF) / 255f;
        float b = ((color) & 0xFF) / 255f;
        float a = ((color >> 24) & 0xFF) / 255f;

        float width = 10 / 16f;
        float height = 14 / 16f;

        float minU = sprite.getInterpolatedU(3);
        float maxU = sprite.getInterpolatedU(13);
        float minV = sprite.getInterpolatedV(1);
        float maxV = sprite.getInterpolatedV(15 * fluidPerc);

        builder.pos(matrix4f, -width / 2, -height / 2 + height * fluidPerc, -0.3f).color(r, g, b, a)
                .tex(minU, minV)
                .overlay(OverlayTexture.NO_OVERLAY).lightmap(15728880).normal(normalMatrix, 0, 0, 1)
                .endVertex();

        builder.pos(matrix4f, width / 2, -height / 2 + height * fluidPerc, -0.3f).color(r, g, b, a)
                .tex(maxU, minV)
                .overlay(OverlayTexture.NO_OVERLAY).lightmap(15728880).normal(normalMatrix, 0, 0, 1)
                .endVertex();

        builder.pos(matrix4f, width / 2, -height / 2, -0.3f).color(r, g, b, a)
                .tex(maxU, maxV)
                .overlay(OverlayTexture.NO_OVERLAY).lightmap(15728880).normal(normalMatrix, 0, 0, 1)
                .endVertex();

        builder.pos(matrix4f, -width / 2, -height / 2, -0.3f).color(r, g, b, a)
                .tex(minU, maxV)
                .overlay(OverlayTexture.NO_OVERLAY).lightmap(15728880).normal(normalMatrix, 0, 0, 1)
                .endVertex();
    }

    private TextureAtlasSprite getFluidStillSprite(Fluid fluid) {
        return Minecraft.getInstance()
                .getAtlasSpriteGetter(PlayerContainer.LOCATION_BLOCKS_TEXTURE)
                .apply(fluid.getAttributes().getStillTexture());
    }

    private TextureAtlasSprite getFluidFlowingSprite(Fluid fluid) {
        return Minecraft.getInstance()
                .getAtlasSpriteGetter(PlayerContainer.LOCATION_BLOCKS_TEXTURE)
                .apply(fluid.getAttributes().getFlowingTexture());
    }
}
