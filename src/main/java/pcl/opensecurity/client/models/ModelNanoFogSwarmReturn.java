package pcl.opensecurity.client.models;
/**
 * @author ben_mkiv
 */
import pcl.opensecurity.common.entity.EntityNanoFogSwarm;

public class ModelNanoFogSwarmReturn extends ModelNanoFogSwarm {

    @Override
    float getCubeScaleFactor(EntityNanoFogSwarm entity){
        if(entity.ticksExisted < EntityNanoFogSwarm.buildNotifyProgress)
            return 1f - 0.7f * interpolate(entity.ticksExisted, EntityNanoFogSwarm.buildNotifyProgress);

        return 0.3f;
    }

    @Override
    float getAlpha(EntityNanoFogSwarm entity){
        float alphaBase = 0.3f;

        if(entity.ticksExisted < EntityNanoFogSwarm.buildNotifyProgress){
            alphaBase+=0.2f * interpolate(EntityNanoFogSwarm.buildNotifyProgress-entity.ticksExisted, EntityNanoFogSwarm.buildNotifyProgress);
        }

        return alphaBase;
    }

    @Override
    float getEntityScaleFactor(EntityNanoFogSwarm entity){
        float entityScaleFactor = 1.35f;


        if (entity.ticksExisted < EntityNanoFogSwarm.buildNotifyProgress)
            entityScaleFactor -= (0.2F * (0.05 * (EntityNanoFogSwarm.buildNotifyProgress - entity.ticksExisted)));
        else if(entity.buildProgress >= 0)
            entityScaleFactor -= 0.37 * interpolate(EntityNanoFogSwarm.buildNotifyProgress - entity.ticksExisted, EntityNanoFogSwarm.buildNotifyProgress);
        else
            entityScaleFactor-=1f * interpolate(entity.ticksExisted - 10 - EntityNanoFogSwarm.buildNotifyProgress, 10);

        return entityScaleFactor / resolution;
    }

    @Override
    float getCubeOffsetFactor(EntityNanoFogSwarm entity){
        if (entity.buildProgress > EntityNanoFogSwarm.buildNotifyProgress)
            return 0;

        if (entity.buildProgress > 0)
            return (interpolate(EntityNanoFogSwarm.buildNotifyProgress - entity.buildProgress, EntityNanoFogSwarm.buildNotifyProgress));

        return 1;
    }
}
