package com.deku.darkdesolations.client.models;

import com.deku.darkdesolations.common.entity.monster.Coralfish;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.model.DefaultedEntityGeoModel;

import static com.deku.darkdesolations.Main.MOD_ID;

public class CoralfishModel extends DefaultedEntityGeoModel<Coralfish> {

    public CoralfishModel() {
        super(new ResourceLocation(MOD_ID, "monsters/coralfish"), false);

        // TODO: Maybe should just use proper organization of textures so that I don't need to override the defafult location like this
        //withAltTexture(new ResourceLocation(MOD_ID, "monsters/coralfish.png"));
    }
}
