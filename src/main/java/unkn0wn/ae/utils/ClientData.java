package unkn0wn.ae.utils;

import net.minecraft.nbt.NBTTagCompound;

public enum ClientData {
    DOWNLOAD{
        @Override
        public Object process(NBTTagCompound data) {
            return data.getCompoundTag(this.name());
        }
    },
    CHAMELEON_MODELS{
        @Override
        public Object process(NBTTagCompound data) {
            return data.getCompoundTag(this.name());
        }
    },
    TIME{
        @Override
        public Object process(NBTTagCompound data) {
            return data.getCompoundTag(this.name());
        }
    },
    SKIN{
        @Override
        public Object process(NBTTagCompound data) {
            return data.getCompoundTag(this.name());
        }
    },
    SCREEN {
        @Override
        public Object process(NBTTagCompound data) {
            return data.getString(this.name());
        }
    },
    SHADER {
        @Override
        public Object process(NBTTagCompound data) {
            return data.getString(this.name());
        }
    },
    CLIENT_MORPH {
        @Override
        public Object process(NBTTagCompound data) { return data.getTag(this.name()); }
    };

    ClientData() {

    }

    public abstract Object process(NBTTagCompound data);

    private Object converter(String setting) {
        try {
            return Integer.parseInt(setting);
        } catch (NumberFormatException e) {
            try {
                return Double.parseDouble(setting);
            } catch (NumberFormatException g) {
                try {
                    return Float.parseFloat(setting);
                } catch (NumberFormatException f) {
                    try {
                        return Boolean.parseBoolean(setting);
                    } catch (NumberFormatException x) {
                        return setting;
                    }
                }
            }
        }
    }
}
