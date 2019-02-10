package pcl.opensecurity.client.jei;


import mezz.jei.api.IJeiRuntime;
import mezz.jei.api.IModPlugin;
import mezz.jei.api.IModRegistry;
import mezz.jei.api.ISubtypeRegistry;
import mezz.jei.api.ingredients.IIngredientBlacklist;
import mezz.jei.api.ingredients.IModIngredientRegistration;
import mezz.jei.api.recipe.IRecipeCategoryRegistration;
import net.minecraft.item.ItemStack;
import pcl.opensecurity.common.ContentRegistry;

@mezz.jei.api.JEIPlugin
public class JEI_Plugin implements IModPlugin {
    public @interface JEIPlugin {}

    public JEI_Plugin(){}


    @Override
    public void registerItemSubtypes(ISubtypeRegistry subtypeRegistry) {

    }

    /**
     * Register special ingredients, beyond the basic ItemStack and FluidStack.
     *
     * @since JEI 3.11.0
     */
    @Override
    public void registerIngredients(IModIngredientRegistration registry) {

    }

    /**
     * Register the categories handled by this plugin.
     * These are registered before recipes so they can be checked for validity.
     *
     * @since JEI 4.5.0
     */
    @Override
    public void registerCategories(IRecipeCategoryRegistration registry) {
    }

    /**
     * Register this mod plugin with the mod registry.
     */
    @Override
    public void register(IModRegistry registry) {
        IIngredientBlacklist blacklist = registry.getJeiHelpers().getIngredientBlacklist();
        blacklist.addIngredientToBlacklist(new ItemStack(ContentRegistry.nanoFog));
        blacklist.addIngredientToBlacklist(new ItemStack(ContentRegistry.rolldoorElement));
    }

    /**
     * Called when jei's runtime features are available, after all mods have registered.
     *
     * @since JEI 2.23.0
     */
    @Override
    public void onRuntimeAvailable(IJeiRuntime jeiRuntime) {

    }
}

