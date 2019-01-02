package pcl.opensecurity.client.models;
/**
 * @author ben_mkiv
 */
import pcl.opensecurity.common.entity.EntityNanoFogSwarm;

public class ModelNanoFogSwarmBuild extends ModelNanoFogSwarm {
    @Override
    float getCubeScaleFactor(EntityNanoFogSwarm entity){
        return 0.3f + 0.7f * interpolate(entity.buildProgress, EntityNanoFogSwarm.buildNotifyProgress);
    }

    @Override
    float getAlpha(EntityNanoFogSwarm entity){
        float alphaBase = 0.3f;

        if(entity.targetReached && entity.buildProgress > 0){
            alphaBase+=0.2f * interpolate(entity.buildProgress, EntityNanoFogSwarm.buildNotifyProgress);
        }

        return alphaBase;
    }

    @Override
    float getEntityScaleFactor(EntityNanoFogSwarm entity){
        float entityScaleFactor = 1.35f;

        if (entity.ticksExisted <= 20)
            entityScaleFactor *= interpolate(entity.ticksExisted, 20);
        else if (entity.buildProgress > 0)
            entityScaleFactor -= 0.2F * (0.05 * entity.buildProgress);

        return entityScaleFactor / resolution;
    }

    @Override
    float getCubeOffsetFactor(EntityNanoFogSwarm entity){
        if(entity.blockJobDone != 0)
            return 0;

        if (entity.targetReached)
            return interpolate(EntityNanoFogSwarm.buildNotifyProgress - entity.buildProgress, EntityNanoFogSwarm.buildNotifyProgress);

        return 1f;
    }

}
