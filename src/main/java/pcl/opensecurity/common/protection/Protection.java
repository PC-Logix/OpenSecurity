package pcl.opensecurity.common.protection;

import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.world.World;
import net.minecraft.world.storage.MapStorage;
import net.minecraft.world.storage.WorldSavedData;
import pcl.opensecurity.OpenSecurity;
import pcl.opensecurity.networking.PacketProtectionAdd;
import pcl.opensecurity.networking.PacketProtectionRemove;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import static pcl.opensecurity.OpenSecurity.MODID;

public class Protection extends WorldSavedData {
    private static final String DATA_NAME = MODID + "_ProtectionData";
    HashMap<ChunkPos, ArrayList<ProtectionAreaChunk>> chunkAreas = new HashMap<>();

    public enum UserAction { mine, explode, place, interactTE, interactHostile, interactAnimal }

    static Protection clientInstance = new Protection();

    public int clientInstanceWorld = Integer.MAX_VALUE;

    public Protection(){
        super(DATA_NAME);
    }

    public Protection(String s){
        super(s);
    }

    public static Protection get(World world) {
        if(!world.isRemote) {
            MapStorage storage = world.getPerWorldStorage();
            Protection instance = (Protection) storage.getOrLoadData(Protection.class, DATA_NAME);

            if (instance == null) {
                instance = new Protection();
                storage.setData(DATA_NAME, instance);
            }

            return instance;
        }
        else {
            if(clientInstance.clientInstanceWorld != world.provider.getDimension()){
                clientInstance = new Protection();
                clientInstance.clientInstanceWorld = world.provider.getDimension();
            }
            return clientInstance;
        }
    }

    // add protection
    public static boolean addArea(World world, AxisAlignedBB area, BlockPos controller){
        if(get(world).isProtectedArea(area))
            return false;



        for(ChunkPos chunk : getChunks(area)) {
            AxisAlignedBB newArea = area.intersect(new AxisAlignedBB(chunk.getXStart(), 0, chunk.getZStart(), chunk.getXEnd() + 1, 256, chunk.getZEnd() + 1));
            get(world).addAreaToChunk(chunk, new ProtectionAreaChunk(newArea, controller));
        }

        get(world).markDirty();

        if(!world.isRemote) {
            PacketProtectionAdd packet = new PacketProtectionAdd(world, controller, area);
            OpenSecurity.network.sendToDimension(packet, world.provider.getDimension());
        }

        return true;
    }

    private void addAreaToChunk(ChunkPos chunkPos, ProtectionAreaChunk pac){
        if(!chunkAreas.containsKey(chunkPos))
            chunkAreas.put(chunkPos, new ArrayList<>());

        chunkAreas.get(chunkPos).add(pac);
    }


    // remove protection
    public static void removeArea(World world, BlockPos controller){
        for(ArrayList<ProtectionAreaChunk> areas : get(world).chunkAreas.values())
            for(ProtectionAreaChunk area : areas)
                if(area.getControllerPosition().equals(controller)){
                    areas.remove(area);
                    return;
                }


        get(world).markDirty();

        if(!world.isRemote) {
            PacketProtectionRemove packet = new PacketProtectionRemove(world, controller);
            OpenSecurity.network.sendToDimension(packet, world.provider.getDimension());
        }
    }

    private void removeAreaFromChunk(ChunkPos chunkPos, AxisAlignedBB area){
        if(!chunkAreas.containsKey(chunkPos))
            return;

        chunkAreas.get(chunkPos).remove(getProtection(area));
    }

    // update protection
    public static boolean updateArea(World world, BlockPos controller, AxisAlignedBB newArea){
        removeArea(world, controller);

        return addArea(world, newArea, controller);
    }

    public void clear(){
        chunkAreas = new HashMap<>();
        markDirty();
    }


    // WorldSavedData methods
    @Override
    public void readFromNBT(NBTTagCompound nbt){
        chunkAreas = new HashMap<>();

        for(int iC = 0; nbt.hasKey("chunk" + iC); iC++){
            ArrayList<ProtectionAreaChunk> areaList = new ArrayList<>();
            NBTTagCompound chunkTag = nbt.getCompoundTag("chunk" + iC);
            for(int iA = 0; chunkTag.hasKey("area" + iA); iA++)
                areaList.add(new ProtectionAreaChunk(chunkTag.getCompoundTag("area" + iA)));


            chunkAreas.put(new ChunkPos(nbt.getInteger("x"), nbt.getInteger("z")), areaList);
        }
    }

    @Override
    public NBTTagCompound writeToNBT(NBTTagCompound compound){
        int iC = 0;
        for(Map.Entry<ChunkPos, ArrayList<ProtectionAreaChunk>> chunkData : chunkAreas.entrySet()){
            NBTTagCompound chunkTag = new NBTTagCompound();
            chunkTag.setInteger("x", chunkData.getKey().x);
            chunkTag.setInteger("z", chunkData.getKey().z);

            int iA = 0;
            for(ProtectionAreaChunk area : chunkData.getValue())
                chunkTag.setTag("area" + iA++, area.writeToNBT(new NBTTagCompound()));

            if(iA > 0)
                compound.setTag("chunk" + iC++, chunkTag);
        }

        return compound;
    }

    private static ArrayList<ChunkPos> getChunks(AxisAlignedBB area){
        ArrayList<ChunkPos> chunks = new ArrayList<>();

        for(int cX = (int) area.minX >> 4; cX <= (int) area.maxX >> 4; cX++)
            for(int cZ = (int) area.minZ >> 4; cZ <= (int) area.maxZ >> 4; cZ++)
                chunks.add(new ChunkPos(cX, cZ));

        return chunks;
    }

    private ProtectionAreaChunk getProtection(BlockPos blockPos){
        ChunkPos chunkPos = new ChunkPos(blockPos);

        if(!chunkAreas.containsKey(chunkPos))
            return null;

        for(ProtectionAreaChunk protectedArea : chunkAreas.get(chunkPos))
            if(protectedArea.intersects(blockPos))
                return protectedArea;

        return null;
    }

    private ProtectionAreaChunk getProtection(AxisAlignedBB areaIn){
        for(ChunkPos chunkPos : getChunks(areaIn))
            if(chunkAreas.containsKey(chunkPos))
                for(ProtectionAreaChunk protectedArea : chunkAreas.get(chunkPos))
                    if(protectedArea.intersects(areaIn))
                        return protectedArea;

        return null;
    }

    public static boolean isProtected(Entity entityIn, UserAction action, BlockPos blockPos){
        if(entityIn instanceof EntityPlayer && ((EntityPlayer) entityIn).isCreative())
            return false;

        ProtectionAreaChunk protection = get(entityIn.world).getProtection(blockPos);

        if(protection == null)
            return false;

        TileEntity controller = entityIn.world.getTileEntity(protection.getControllerPosition());

        return controller != null
                && controller instanceof IProtection
                && ((IProtection) controller).isProtected(entityIn, action);
    }

    public static boolean isProtected(World world, UserAction action, BlockPos blockPos){
        ProtectionAreaChunk protection = get(world).getProtection(blockPos);

        if(protection == null)
            return false;

        TileEntity controller = world.getTileEntity(protection.getControllerPosition());

        return controller != null
                && controller instanceof IProtection
                && ((IProtection) controller).isProtected(null, action);
    }

    // helper
    private boolean isProtectedArea(AxisAlignedBB areaIn){
        return getProtection(areaIn) != null;
    }


}

