package pcl.opensecurity.util;


import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityList;
import net.minecraft.entity.item.EntityBoat;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.ResourceLocation;
import pcl.opensecurity.OpenSecurity;

import java.util.HashSet;
import java.util.logging.Logger;

public class ClassHelper {
    static public HashSet<Class<? extends Entity>> getEntityList(){
        HashSet<Class<? extends Entity>> list = new HashSet<>();

        String skipedClasses = "";
        for (ResourceLocation name : EntityList.getEntityNameList()){
            Class eClazz = EntityList.getClass(name);
            if(eClazz == null)
                continue;

            try {
                list.add(EntityList.getClass(name));
            } catch (Exception e) { skipedClasses+=", " + name.toString(); }
        }

        if(skipedClasses.length() > 0)
            Logger.getLogger(OpenSecurity.MODID).info("skipped not supported entity classes: " + skipedClasses.substring(2));

        // this isnt part of the EntityList
        list.add(EntityPlayer.class);
        // this isn't exposed to EntityList.getEntityNameList()
        list.add(EntityBoat.class);

        return list;
    }

}
