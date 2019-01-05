package pcl.opensecurity.common.component;

import li.cil.oc.api.Driver;
import li.cil.oc.api.driver.DriverItem;
import li.cil.oc.api.network.ManagedEnvironment;
import li.cil.oc.api.network.Node;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.items.ItemStackHandler;
import pcl.opensecurity.OpenSecurity;

public class ManagedComponent {
    private ItemStackHandler inventory = new ItemStackHandler(1);
    private ManagedEnvironment environment;
    private String boundToAddress = "";
    private ManagedComponentHost componentHost;

    public ManagedComponent(ManagedComponentHost host){
        componentHost = host;
    }

    public ItemStack getComponentItem(){
        return inventory.getStackInSlot(0);
    }

    public void update(){
        if(environment != null && environment.canUpdate())
            environment.update();
    }


    private void resetCardEnvironment(){
        boundToAddress = "";
        environment = null;

        inventory.setStackInSlot(0, ItemStack.EMPTY);
    }

    public void set(ItemStack newStack){
        if(newStack.equals(inventory.getStackInSlot(0)))
            return;

        disconnect();

        if(!newStack.isEmpty())
            connect(newStack);
    }

    private void setupEnvironment(ItemStack stack){
        DriverItem driver = Driver.driverFor(stack);

        if(driver != null)
            environment = driver.createEnvironment(stack, componentHost);
    }

    public Node node() {
        return environment != null ? environment.node() : null;
    }

    public Node getBoundMachine(){
        if(environment == null)
            return null;

        for(Node node : node().neighbors()){
            if(node.equals(componentHost.node())) //skip the host node
                continue;

            return node;
        }

        return null;
    }

    public void disconnect(){
        if(getBoundMachine() != null)
            unbindMachine(getBoundMachine());

        resetCardEnvironment();
    }

    private void connectCardToHost(){
        if(node() == null)
            return;

        componentHost.node().connect(node());
    }

    private void connect(ItemStack newStack){
        setupEnvironment(newStack);

        if(environment != null) {
            //connectCardToHost();
        }
        else
            OpenSecurity.logger.info("couldnt create environment for " + newStack.getItem().getUnlocalizedName());

        inventory.setStackInSlot(0, newStack);
    }

    public void bindMachine(Node node){
        if(node() == null)
            return;

        componentHost.node().network().connect(node, node());
        boundToAddress = node.address();
    }

    public void unbindMachine(Node node){
        boundToAddress = "";

        if(node() == null)
            return;

        componentHost.node().network().disconnect(node, node());
    }



    public void onConnect(Node arg0) {
        // reconnect the internal card component when the host connects to the carddock
        if(boundToAddress.length() > 0 && arg0.address().equals(boundToAddress))
            bindMachine(arg0);
    }



    public void readFromNBT(NBTTagCompound data) {
        inventory.deserializeNBT(data.getCompoundTag("inventory"));

        if(data.hasKey("host"))
            boundToAddress = data.getString("host");

        // 2nd restore environment for the card
        if(data.hasKey("environment")) {
            setupEnvironment(inventory.getStackInSlot(0));

            if(environment != null) {
                environment.load(data.getCompoundTag("environment"));
                // 3rd restore card data
                if (data.hasKey("card") && node() != null) {
                    node().load(data.getCompoundTag("card"));
                }
            }
        }
    }

    public NBTTagCompound writeToNBT(NBTTagCompound data) {
        data.setTag("inventory", inventory.serializeNBT());

        if(environment != null) {
            final NBTTagCompound environmentNBT = new NBTTagCompound();
            environment.save(environmentNBT);
            data.setTag("environment", environmentNBT);

            final NBTTagCompound cardNBT = new NBTTagCompound();
            node().save(cardNBT);
            data.setTag("card", cardNBT);

            if(getBoundMachine() != null){
                data.setString("host", getBoundMachine().address());
            }
        }

        return data;
    }

}
