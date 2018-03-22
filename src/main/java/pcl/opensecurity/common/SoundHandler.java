package pcl.opensecurity.common;

import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundEvent;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.registry.GameRegistry;
import pcl.opensecurity.client.sounds.AlarmResource;

public class SoundHandler {
	
	public static SoundEvent klaxon1;
	public static SoundEvent turretMove;
	public static SoundEvent turretFire;
	public static SoundEvent keypad_press;
	public static SoundEvent security_door;
	public static SoundEvent scanner1;
	public static SoundEvent scanner2;
	public static SoundEvent scanner3;
	public static SoundEvent card_swipe;
	
	public static void registerSounds() {
        turretMove 	= registerSound("turretMove");
        turretFire 	= registerSound("turretFire");
        keypad_press 	= registerSound("keypad_press");
        security_door  = registerSound("security_door");
        scanner1 = registerSound("scanner1");
        scanner2 	= registerSound("scanner2");
        scanner3 	= registerSound("scanner3");
        card_swipe 	= registerSound("card_swipe");
	}
	
    /**
     * Register a {@link SoundEvent}.
     *
     * @param soundName The SoundEvent's name without the testmod3 prefix
     * @return The SoundEvent
     */
    private static SoundEvent registerSound(String soundName)
    {
        final ResourceLocation soundID = new ResourceLocation(AlarmResource.PACK_NAME, soundName);
        //return GameRegistry.register(new SoundEvent(soundID).setRegistryName(soundID));
        return new SoundEvent(soundID).setRegistryName(soundID);
    }
    
	@Mod.EventBusSubscriber
	public static class RegistrationHandler {
		@SubscribeEvent
		public static void registerSoundEvents(RegistryEvent.Register<SoundEvent> event) {
			event.getRegistry().registerAll(
					turretMove,
					turretFire,
					keypad_press,
					security_door,
					scanner1,
					scanner2,
					scanner3,
					card_swipe
			);
		}
}
}
