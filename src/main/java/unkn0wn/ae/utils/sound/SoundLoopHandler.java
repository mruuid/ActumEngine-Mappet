package unkn0wn.ae.utils.sound;

import net.minecraft.client.Minecraft;
import net.minecraft.client.audio.ISound;
import net.minecraft.client.audio.PositionedSoundRecord;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import unkn0wn.ae.AEMod;

/**
 * Drives the looping (repeating) sounds started via {@code playLoopSound}.
 *
 * <p>When a loop sound is started, {@link unkn0wn.ae.network.packets.PacketSoundLoop}
 * plays it once and registers it in {@link AEMod#listSounds} together with its
 * repeat period ({@link AEMod#listFirstTicks}) and a live countdown
 * ({@link AEMod#listTicks}). This handler decrements the countdown every client
 * tick and, once it reaches zero, replays the sound and resets the countdown —
 * that is what makes the sound actually loop over time.</p>
 */
public class SoundLoopHandler {
    private final Minecraft mc = Minecraft.getMinecraft();

    @SubscribeEvent
    public void onClientTick(TickEvent.ClientTickEvent event) {
        if (event.phase != TickEvent.Phase.END) {
            return;
        }

        if (this.mc.player == null || this.mc.world == null) {
            return;
        }

        for (int i = 0; i < AEMod.listSounds.size(); i++) {
            int ticks = AEMod.listTicks.get(i) - 1;
            AEMod.listTicks.set(i, ticks);

            if (ticks <= 0) {
                ISound sound = AEMod.listSounds.get(i);
                ResourceLocation rl = sound.getSoundLocation();

                /* Recreate the record so the sound is genuinely restarted. */
                ISound record = new PositionedSoundRecord(rl, sound.getCategory(), sound.getVolume(), sound.getPitch(), false, 0, ISound.AttenuationType.NONE, 0.0F, 0.0F, 0.0F);

                AEMod.listSounds.set(i, record);
                this.mc.getSoundHandler().playSound(record);

                AEMod.listTicks.set(i, AEMod.listFirstTicks.get(i));
            }
        }
    }
}
