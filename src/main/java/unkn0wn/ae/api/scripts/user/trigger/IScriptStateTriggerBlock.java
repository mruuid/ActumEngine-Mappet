package unkn0wn.ae.api.scripts.user.trigger;

import mchorse.mappet.api.triggers.blocks.StateTriggerBlock;
import unkn0wn.ae.api.scripts.user.trigger.utils.IScriptStringTriggerBlock;

public interface IScriptStateTriggerBlock extends IScriptStringTriggerBlock<StateTriggerBlock> {
    String getTarget();
    void setTarget(String target);

    Object getValue();
    void setValue(Object value);

    String getMode();
    void setMode(String mode);
}
