package unkn0wn.ae.api.scripts.code.trigger;

import mchorse.mappet.api.triggers.blocks.DialogueTriggerBlock;
import unkn0wn.ae.api.scripts.code.trigger.utils.ScriptDataTriggerBlock;
import unkn0wn.ae.api.scripts.user.trigger.IScriptDialogueTriggerBlock;

public class ScriptDialogueTriggerBlock extends ScriptDataTriggerBlock<DialogueTriggerBlock> implements IScriptDialogueTriggerBlock {

    public ScriptDialogueTriggerBlock() {
        this(new DialogueTriggerBlock());
    }

    public ScriptDialogueTriggerBlock(DialogueTriggerBlock triggerBlock) {
        this.triggerBlock = triggerBlock;
    }
}
