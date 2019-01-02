package pcl.opensecurity.common.nanofog;
/**
 * @author ben_mkiv
 */
import net.minecraft.nbt.NBTTagCompound;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;

public class EntityFilter {
    private HashMap<String, HashSet<String>> list = new HashMap<>();

    public void add(Class entityClass, String entityName) {
        String className = entityClass.getSimpleName();

        list.putIfAbsent(className, new HashSet<>());
        if(entityName.length() > 0) list.get(className).add(entityName.toLowerCase());
    }

    public void remove(Class entityClass, String entityName) {
        String className = entityClass.getSimpleName();

        if(!list.containsKey(className))
            return;

        if(entityName.length() > 0)
            list.get(className).remove(entityName.toLowerCase());
        else if(list.get(className).size() == 0)
            list.remove(className);

    }

    public boolean contains(Class entityClass, String entityName){
        String className = entityClass.getSimpleName();

        switch(className){
            case "EntityPlayerSP":
            case "EntityPlayerMP":
                className = "EntityPlayer";
                break;
            default:
                break;
        }

        if(!list.containsKey(className))
            return false;

        HashSet<String> subList = list.get(className);
        return subList == null || subList.size() == 0 || subList.contains(entityName.toLowerCase());
    }

    public HashMap<String, HashSet<String>> getList(){
        return list;
    }

    public NBTTagCompound writeToNBT(NBTTagCompound nbt){
        int cI = 0;
        for(Map.Entry<String, HashSet<String>> entry : list.entrySet()){
            NBTTagCompound tag = new NBTTagCompound();
            tag.setString("class", entry.getKey());

            int nI = 0;
            for(String name : entry.getValue())
                tag.setString("n"+nI++, name);

            nbt.setTag("c"+cI++, tag);
        }

        return nbt;
    }

    public void readFromNBT(NBTTagCompound nbt){
        list.clear();

        for(int cI = 0; nbt.hasKey("c"+cI); cI++){
            NBTTagCompound tag = nbt.getCompoundTag("c"+cI);
            HashSet<String> names = new HashSet<>();
            for(int nI = 0; tag.hasKey("n"+nI); nI++)
                names.add(tag.getString("n"+nI));

            list.put(tag.getString("class"), names);
        }
    }

}