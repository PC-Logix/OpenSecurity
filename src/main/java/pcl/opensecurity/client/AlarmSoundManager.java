package pcl.opensecurity.client;

import pcl.opensecurity.blockentity.AlarmBlockEntity;

import net.minecraft.client.Minecraft;

/** Client-side alarm playback lifecycle. Called from the alarm block-entity ticker. */
public final class AlarmSoundManager {
    public static void tick(AlarmBlockEntity alarm) {
        AlarmBlockEntity.PlaybackHandle current = alarm.getPlayback();

        if (!alarm.isActive() || alarm.isRemoved() || alarm.getLevel() == null) {
            if (current != null) {
                current.stopPlayback();
                alarm.setPlayback(null);
            }
            return;
        }

        if (current != null && (current.isStopped() || !current.soundId().equals(alarm.getSoundId()))) {
            current.stopPlayback();
            alarm.setPlayback(null);
            current = null;
        }

        if (current == null) {
            AlarmSoundInstance sound = new AlarmSoundInstance(alarm);
            alarm.setPlayback(sound);
            Minecraft.getInstance().getSoundManager().play(sound);
        }
    }

    private AlarmSoundManager() {}
}
