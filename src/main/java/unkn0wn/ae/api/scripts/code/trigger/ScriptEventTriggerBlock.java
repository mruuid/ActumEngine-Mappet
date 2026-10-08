package unkn0wn.ae.api.scripts.code.trigger;

import mchorse.mappet.api.triggers.blocks.EventTriggerBlock;
import unkn0wn.ae.api.scripts.code.trigger.utils.ScriptDataTriggerBlock;
import unkn0wn.ae.api.scripts.user.trigger.IScriptEventTriggerBlock;

public class ScriptEventTriggerBlock extends ScriptDataTriggerBlock<EventTriggerBlock> implements IScriptEventTriggerBlock {

    public ScriptEventTriggerBlock() {
        this(new EventTriggerBlock());
    }

    public ScriptEventTriggerBlock(EventTriggerBlock triggerBlock) {
        this.triggerBlock = triggerBlock;
    }
}
