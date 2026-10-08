package unkn0wn.ae.utils.mixins;

import zone.rong.mixinbooter.ILateMixinLoader;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class LateMixinLoader implements ILateMixinLoader {
    private static final String prefix = "late";
    @Override
    public List<String> getMixinConfigs() {
        return new ArrayList<String>(Arrays.asList(
                "mixins/mixins."+prefix+".utils.json",
                "mixins/mixins."+prefix+".scripts.json"
        ));
    }
}
