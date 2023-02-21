package com.deku.darkdesolations.client.renderers;

import com.deku.darkdesolations.client.models.CoralfishModel;
import com.deku.darkdesolations.common.entity.monster.Coralfish;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import software.bernie.geckolib.renderer.GeoEntityRenderer;

public class CoralfishRenderer extends GeoEntityRenderer<Coralfish> {

    public CoralfishRenderer(EntityRendererProvider.Context context) {
        super(context, new CoralfishModel());
    }
}