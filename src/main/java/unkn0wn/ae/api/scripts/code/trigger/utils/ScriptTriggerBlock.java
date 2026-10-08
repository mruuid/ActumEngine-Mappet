package unkn0wn.ae.api.scripts.code.trigger.utils;

import mchorse.mappet.api.triggers.blocks.AbstractTriggerBlock;
import unkn0wn.ae.api.scripts.user.trigger.utils.IScriptTriggerBlock;

public abstract class ScriptTriggerBlock<T extends AbstractTriggerBlock> implements IScriptTriggerBlock<T> {
    public T triggerBlock;

    public boolean isEmpty() {
        return this.triggerBlock.isEmpty();
    }

    @Override
    public T getTriggerBlock() {
        return triggerBlock;
    }
}
