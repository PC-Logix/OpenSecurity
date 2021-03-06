package pcl.opensecurity.common.tileentity;

import li.cil.oc.api.Network;
import li.cil.oc.api.machine.Arguments;
import li.cil.oc.api.machine.Callback;
import li.cil.oc.api.machine.Context;
import li.cil.oc.api.network.EnvironmentHost;
import li.cil.oc.api.network.Visibility;
import net.minecraft.block.state.IBlockState;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTUtil;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.play.server.SPacketUpdateTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import pcl.opensecurity.common.interfaces.IPasswordProtected;
import pcl.opensecurity.lib.easing.penner.Quad;
import pcl.opensecurity.common.interfaces.IColoredTile;
import pcl.opensecurity.common.tileentity.logic.RolldoorHelper;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.HashSet;

public class TileEntityRolldoorController extends TileEntityOSCamoBase implements IPasswordProtected, IColoredTile {
    private static final double MAX_MOVE_SPEED = 3;
    private static final double MIN_MOVE_SPEED = 0.1;

    final static String NAME = "os_rolldoorcontroller";

    private String password = "";

    private int color = 0;

    public TileEntityRolldoorController(){
        super(NAME);
        node = Network.newNode(this, Visibility.Network).withComponent(getComponentName()).withConnector(32).create();
    }

    public TileEntityRolldoorController(EnvironmentHost host){
        super(NAME, host);
    }


    private AxisAlignedBB renderBoundingBox = new AxisAlignedBB(0, 0, 0, 0, 0, 0);
    private AxisAlignedBB elementsRenderBoundingBox = renderBoundingBox;

    private EnumFacing facing = EnumFacing.NORTH;

    private double currentPosition = 0;
    private double targetPosition = -1;
    private double moveSpeed = 1;

    private HashSet<WeakReference<TileEntityRolldoor>> elements = new HashSet<>();
    private HashSet<BlockPos> elementsPos = new HashSet<>();

    private boolean needsListUpdate = false;


    @Override
    public void update(){
        super.update();

        if(needsListUpdate)
            updateElementList();

        getCurrentHeight();
    }

    private void updateElementList(){
        if(getWorld() == null)
            return;

        ArrayList<BlockPos> addElements = new ArrayList<>(elementsPos);

        resetRolldoorData();

        for(BlockPos pos : addElements){
            TileEntity tile = getWorld().getTileEntity(pos);
            if(tile instanceof TileEntityRolldoor)
                addElement((TileEntityRolldoor) tile);
        }

        needsListUpdate = false;
        markDirtyClient();
    }

    // OC Callbacks
    @Callback(doc = "function():boolean; returns true if the door is opened", direct=false)
    public Object[] isOpen(Context context, Arguments args) {
        return new Object[] { isOpen() };
    }

    @Callback(doc = "function():boolean; returns true if the door is moving", direct=true)
    public Object[] isMoving(Context context, Arguments args) {
        return new Object[] { isMoving() };
    }

    @Callback(doc = "function():double; returns current position", direct=false)
    public Object[] getPosition(Context context, Arguments args){
        return new Object[]{ getCurrentHeight() };
    }

    @Callback(doc = "function(Double:position):double; sets a new position the rolldoor should move to", direct=false)
    public Object[] setPosition(Context context, Arguments args){
        double newPos = args.optDouble(0, currentPosition);
        if(!getPass().equals(args.optString(1, "")))
            return new Object[]{ false, "invalid password" };

        setTargetPosition(Math.max(0, Math.min(getHeight(), newPos)));
        return new Object[]{ targetPosition };
    }

    @Callback(doc = "function():double; toggles the rolldoor between open/close", direct=false)
    public Object[] toggle(Context context, Arguments args) {
        if(!getPass().equals(args.optString(0, "")))
            return new Object[]{ false, "invalid password" };

        if(isMoving()){
            return new Object[]{ false, "rolldoor is moving" };
        }
        else if(isOpen()) {
            setTargetPosition(getHeight());
            return new Object[]{ true, "closing door" };
        } else {
            setTargetPosition(0);
            return new Object[]{ true, "opening door" };
        }
    }

    @Callback(doc = "function():double; opens the rolldoor", direct=false)
    public Object[] open(Context context, Arguments args) {
        if(!getPass().equals(args.optString(0, "")))
            return new Object[]{ false, "invalid password" };

        setTargetPosition(0);
        return new Object[]{ true };
    }

    @Callback(doc = "function():double; closes the rolldoor", direct=false)
    public Object[] close(Context context, Arguments args) {
        if(!getPass().equals(args.optString(0, "")))
            return new Object[]{ false, "invalid password" };

        setTargetPosition(getHeight());
        return new Object[]{ true };
    }

    @Callback(doc = "function(Double:speed):double; sets the speed of the rolldoor, returns new speed", direct=true)
    public Object[] setSpeed(Context context, Arguments args) {
        if(!getPass().equals(args.optString(1, "")))
            return new Object[]{ false, "invalid password" };

        moveSpeed = Math.max(MIN_MOVE_SPEED, Math.min(MAX_MOVE_SPEED, args.optDouble(0, moveSpeed)));
        return new Object[] { moveSpeed };
    }

    @Callback(doc = "function(String:password):integer; checks for space below the rolldoor, returns new height")
    public Object[] calibrate(Context context, Arguments args) {
        if(!getPass().equals(args.optString(0, "")))
            return new Object[]{ false, "invalid password" };

        for(WeakReference<TileEntityRolldoor> ref : elements)
            if(ref.get() != null && !ref.get().isInvalid())
                ref.get().updateHeight();

        return new Object[] { getHeight() };
    }

    @Callback(doc = "function():integer; returns height of the rolldoor", direct=true)
    public Object[] getHeight(Context context, Arguments args) {
        ArrayList<Integer> heights = new ArrayList<>();

        for(WeakReference<TileEntityRolldoor> ref : elements){
            if(ref.get() != null && !ref.get().isInvalid())
                heights.add(ref.get().height());
        }

        return new Object[]{ heights.toArray() };
    }

    @Callback(doc = "function():boolean; sets a password for controlling the door", direct=false)
    public Object[] setPassword(Context context, Arguments args){
        if(!getPass().equals(args.optString(1, "")))
            return new Object[]{ false, "old password doesnt match" };

        setPassword(args.checkString(0));

        return new Object[]{ true };
    }

    private void resetRolldoorData(){
        for(WeakReference<TileEntityRolldoor> tile : elements){
            if(tile == null || tile.get() == null || tile.get().isInvalid())
                continue;

            tile.get().setOrigin(null);
        }

        elements.clear();
        elementsPos.clear();
        elementsRenderBoundingBox = new AxisAlignedBB(0, 0, 0, 0, 0, 0).offset(getPos());
        renderBoundingBox = new AxisAlignedBB(0, 0, 0, 1, 1, 1).offset(getPos());
    }

    public void initialize(){
        resetRolldoorData();

        EnumFacing enumFacing = null;
        int yLevel = -1;

        for(TileEntityRolldoor tile : RolldoorHelper.getDoors(this).values()) {
            if(enumFacing == null)
                enumFacing = tile.getFacing();

            if (yLevel == -1)
                yLevel = tile.getPos().getY();

            if(enumFacing.equals(tile.getFacing()) && tile.getPos().getY() == yLevel)
                addElement(tile);
        }

        markDirtyClient();
    }

    public void remove(){
        HashSet<WeakReference<TileEntityRolldoor>> elementsList = new HashSet<>();
        elementsList.addAll(this.elements);

        for(WeakReference<TileEntityRolldoor> ref : elementsList)
            if(ref.get() != null && !ref.get().isInvalid())
                removeElement(ref.get());
    }

    private void addElement(TileEntityRolldoor rolldoor){
        if(rolldoor == null || rolldoor.isInvalid())
            return;

        if(rolldoor.getController() != null && !this.equals(rolldoor.getController()))
            return; //rolldoor is already assigned to a controller

        rolldoor.setOrigin(getPos());
        facing = rolldoor.getFacing();
        if(elements.size() == 0)
            elementsRenderBoundingBox = rolldoor.getElementsBoundingBox();
        else
            elementsRenderBoundingBox = elementsRenderBoundingBox.union(rolldoor.getElementsBoundingBox());

        elements.add(new WeakReference<>(rolldoor));
        elementsPos.add(rolldoor.getPos());

        renderBoundingBox = renderBoundingBox.union(elementsRenderBoundingBox);
    }

    private void removeElement(TileEntityRolldoor rolldoor){
        if(rolldoor != null)
            rolldoor.setOrigin(null);

        initialize();
    }

    @Nonnull
    @Override
    public AxisAlignedBB getRenderBoundingBox(){
        return renderBoundingBox;
    }

    public AxisAlignedBB getElementsRenderBoundingBox(){
        return elementsRenderBoundingBox.offset(-getPos().getX(), -getPos().getY() - 1, -getPos().getZ());
    }

    private long animationStart = 0;
    private long animationEnd = 0;
    private long animationDuration = 0;
    private double animationDistance = 0;
    private double animationStartPosition = 0;
    private double animationTargetPosition = -1;

    private void startAnimationTo(double newPosition){
        newPosition = Math.max(0, Math.min(newPosition, getHeight()));
        animationDistance = newPosition - currentPosition;
        animationStartPosition = currentPosition;
        animationTargetPosition = targetPosition;
        animationDuration = Math.round(Math.abs(animationDistance / moveSpeed) * 1000);

        animationStart = System.currentTimeMillis();
        animationEnd = animationStart + animationDuration;
    }

    private void endAnimation(){
        currentPosition = targetPosition;

        animationStart = animationEnd = animationDuration = 0;
        animationStartPosition = animationDistance = 0;
        targetPosition = -1;
    }

    public double getCurrentHeight(){
        if(!isMoving())
            return currentPosition;

        if(animationEnd == 0 || animationTargetPosition != targetPosition) {
            startAnimationTo(targetPosition);
        }

        long elapsed = System.currentTimeMillis() - animationStart;

        if(elapsed >= animationDuration) {
            endAnimation();
            return currentPosition;
        }

        currentPosition = animationStartPosition + animationDistance * Math.abs(Quad.easeInOut(elapsed, 0, 1, animationDuration));

        return currentPosition;
    }

    private void setTargetPosition(double targetPosition) {
        this.targetPosition = targetPosition;
        markDirtyClient();
    }

    private boolean isOpen(){
        return getCurrentHeight() <= 0;
    }

    private boolean isMoving(){
        return targetPosition != -1;
    }

    private int getHeight(){
        int height = 0;
        for(WeakReference<TileEntityRolldoor> ref : elements){
            if(ref.get() != null && !ref.get().isInvalid())
                height = Math.max(ref.get().height(), height);
        }

        return height;
    }

    public int getWidth(){
        return elements.size();
    }

    public EnumFacing rolldoorFacing(){
        return facing; // facing of the rolldoor, not of the controller tile
    }

    @Override
    public void readFromNBT(NBTTagCompound nbt){
        super.readFromNBT(nbt);

        elementsPos.clear();
        for(int i=0; nbt.hasKey("el"+i); i++)
            elementsPos.add(NBTUtil.getPosFromTag(nbt.getCompoundTag("el"+i)));

        facing = EnumFacing.values()[nbt.getInteger("rolldoorFacing")];
        color = nbt.getInteger("color");
        moveSpeed = nbt.getDouble("moveSpeed");
        currentPosition = nbt.getDouble("position");
        targetPosition = nbt.getDouble("targetPos");
        password = nbt.getString("password");
        needsListUpdate = true;
    }

    @Override
    public NBTTagCompound writeToNBT(NBTTagCompound nbt){
        int i=0;
        for(BlockPos pos : elementsPos){
            nbt.setTag("el" + i, NBTUtil.createPosTag(pos));
            i++;
        }

        nbt.setInteger("color", color);
        nbt.setInteger("rolldoorFacing", facing.ordinal());
        nbt.setDouble("moveSpeed", this.moveSpeed);
        nbt.setDouble("position", this.currentPosition);
        nbt.setDouble("targetPos", this.targetPosition);
        nbt.setString("password", password);
        return super.writeToNBT(nbt);
    }

    @Override
    public String getPass(){
        return password;
    }

    @Override
    public void setPassword(String newPassword){
        password = newPassword;
    }

    @Override
    public int getColor(){
        return color;
    }

    @Override
    public void setColor(int color){
        if(this.color != color) {
            this.color = color;
            onColorChanged();
        }
    }

    @Override
    public void onColorChanged(){
        markDirtyClient();
    }

    @Override
    public NBTTagCompound getUpdateTag() {
        return writeToNBT(super.getUpdateTag());
    }

    @Nullable
    @Override
    public SPacketUpdateTileEntity getUpdatePacket() {
        return new SPacketUpdateTileEntity(getPos(), 1, getUpdateTag());
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void onDataPacket(NetworkManager net, SPacketUpdateTileEntity packet) {
        readFromNBT(packet.getNbtCompound());
    }

}
