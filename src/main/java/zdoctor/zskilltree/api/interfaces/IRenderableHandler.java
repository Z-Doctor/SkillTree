package zdoctor.zskilltree.api.interfaces;

import com.mojang.blaze3d.matrix.MatrixStack;

public interface IRenderableHandler {
    /**
     * @return returns if rendering should continue
     */
    boolean preRender(MatrixStack matrixStack, int mouseX, int mouseY, float partialTicks);

}
