package pcl.opensecurity.common.nanofog;
/**
 * @author ben_mkiv
 */
import net.minecraftforge.common.property.IUnlistedProperty;

public class FilterType implements IUnlistedProperty<FilterType.filterTypes> {
    public enum filterTypes {
        none (0),
        all (1),
        allPlayers (2),
        allHostile (4),
        allAnimals (8),
        players (16),
        hostile (32),
        animals (64);

        int value = 0;

        filterTypes(int val){
            value = val;
        }
    }

    private final filterTypes type;

    public FilterType(filterTypes type){
        this.type = type;
    }

    @Override
    public String getName(){
        return type.name();
    }

    @Override
    public boolean isValid(filterTypes value){
        return true;
    }

    @Override
    public Class<filterTypes> getType(){
        return filterTypes.class;
    }

    @Override
    public String valueToString(filterTypes value){
        return value.name();
    }

}
