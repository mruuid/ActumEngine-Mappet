package unkn0wn.ae.api.scripts.user.trigger;

import mchorse.mappet.api.triggers.blocks.ItemTriggerBlock;
import unkn0wn.ae.api.scripts.user.trigger.utils.IScriptTriggerBlock;

public interface IScriptItemTriggerBlock extends IScriptTriggerBlock<ItemTriggerBlock> {
    String getTarget();
    void setTarget(String target);

    String getMode();
    void setMode(String mode);
}
