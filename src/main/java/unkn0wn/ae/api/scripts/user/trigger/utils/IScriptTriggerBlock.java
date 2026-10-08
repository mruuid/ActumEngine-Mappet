package unkn0wn.ae.api.scripts.user.trigger.utils;

import mchorse.mappet.api.triggers.blocks.AbstractTriggerBlock;

public interface IScriptTriggerBlock<T extends AbstractTriggerBlock> {
    boolean isEmpty();

    T getTriggerBlock();
}
