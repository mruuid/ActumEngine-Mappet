package unkn0wn.ae.api.scripts.user.trigger;

import mchorse.mappet.api.triggers.blocks.SoundTriggerBlock;
import unkn0wn.ae.api.scripts.user.trigger.utils.IScriptStringTriggerBlock;

public interface IScriptSoundTriggerBlock extends IScriptStringTriggerBlock<SoundTriggerBlock> {

    String getTarget();
    void setTarget(String target);
}
