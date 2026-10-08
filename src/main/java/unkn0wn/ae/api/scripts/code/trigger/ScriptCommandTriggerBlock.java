package unkn0wn.ae.api.scripts.code.trigger;

import mchorse.mappet.api.triggers.blocks.CommandTriggerBlock;
import unkn0wn.ae.api.scripts.code.trigger.utils.ScriptStringTriggerBlock;
import unkn0wn.ae.api.scripts.user.trigger.IScriptCommandTriggerBlock;

public class ScriptCommandTriggerBlock extends ScriptStringTriggerBlock<CommandTriggerBlock> implements IScriptCommandTriggerBlock {

    public ScriptCommandTriggerBlock() {
        this(new CommandTriggerBlock());
    }

    public ScriptCommandTriggerBlock(CommandTriggerBlock triggerBlock) {
        this.triggerBlock = triggerBlock;
    }
}
