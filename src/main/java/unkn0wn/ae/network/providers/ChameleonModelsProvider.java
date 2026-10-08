package unkn0wn.ae.network.providers;

import mchorse.chameleon.Chameleon;

public class ChameleonModelsProvider implements IClientDataProvider{
    @Override
    public void setData() {
        Chameleon.proxy.reloadModels();
    }
}