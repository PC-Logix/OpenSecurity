package pcl.opensecurity.util;

import java.util.ArrayList;
import java.util.List;

import pcl.opensecurity.OpenSecurity;
import net.minecraft.item.Item;

/*
 * Credit to <a href="https://github.com/McJty">McJty</a> and <a href="https://github.com/planetguy32">planetguy32</a> 
 * 
 * Source: https://github.com/McJty/McJtyLib/blob/master/src/main/java/mcjty/varia/WrenchChecker.java
 * Original: https://github.com/McJty/RFTools/issues/13
 */
public class WrenchChecker {

    private static List<Class> wrenchClasses=new ArrayList<Class>();

    public static void init() {
        for (String className : new String[] {
                /*
                 * Can add or remove class names here
                 * Use API interface names where possible, in case of refactoring
                 * note that many mods implement BC wrench API iff BC is installed
                 * and include no wrench API of their own - we use implementation
                 * classes here to catch these cases.
                */
                "buildcraft.api.tools.IToolWrench",             //Buildcraft
                "resonant.core.content.ItemScrewdriver",        //Resonant Induction
                "ic2.core.item.tool.ItemToolWrench",            //IC2
                "ic2.core.item.tool.ItemToolWrenchElectric",    //IC2 (more)
                "mrtjp.projectred.api.IScrewdriver",            //Project Red
                "mods.railcraft.api.core.items.IToolCrowbar",   //Railcraft
                "com.bluepowermod.items.ItemScrewdriver",       //BluePower
                "cofh.api.item.IToolHammer",                    //Thermal Expansion and compatible
                "appeng.items.tools.quartz.ToolQuartzWrench",   //Applied Energistics
                "crazypants.enderio.api.tool.ITool",            //Ender IO
                "mekanism.api.IMekWrench",                      //Mekanism
                "mcjty.rftools.items.smartwrench",              //RFTools
                "pneumaticCraft.common.item.ItemPneumaticWrench",
                "powercrystals.minefactoryreloaded.api.IToolHammer"

        }) {
            try {
                wrenchClasses.add(Class.forName(className));
            } catch (ClassNotFoundException e) {
                OpenSecurity.logger.info("Failed to load wrench class " + className + " (this is not an error)");
            }
        }
    }

    public static boolean isAWrench(Item item) {
        for (Class c : wrenchClasses) {
            if (c.isAssignableFrom(item.getClass())) {
                return true;
            }
        }
        return false;
    }

}