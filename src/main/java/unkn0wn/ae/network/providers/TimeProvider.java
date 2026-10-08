package unkn0wn.ae.network.providers;

import net.minecraft.nbt.NBTTagCompound;
import unkn0wn.ae.utils.ClientData;

import java.time.LocalTime;

public class TimeProvider implements IClientDataProvider {
    public NBTTagCompound getData() {
        NBTTagCompound nbtTagCompound = new NBTTagCompound();
        String time = LocalTime.now().toString();
        nbtTagCompound.setString(ClientData.TIME.toString(), time);

        return nbtTagCompound;
    }
}
