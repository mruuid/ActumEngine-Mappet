package unkn0wn.ae.api.scripts.code.trigger;

import mchorse.mappet.api.triggers.blocks.SoundTriggerBlock;
import mchorse.mappet.api.utils.TargetMode;
import unkn0wn.ae.api.scripts.code.trigger.utils.ScriptStringTriggerBlock;
import unkn0wn.ae.api.scripts.user.trigger.IScriptSoundTriggerBlock;

public class ScriptSoundTriggerBlock extends ScriptStringTriggerBlock<SoundTriggerBlock> implements IScriptSoundTriggerBlock {

    public String getTarget() {
        return this.getTriggerBlock().target.name();
    }
    public void setTarget(String target) {
        this.getTriggerBlock().target = TargetMode.valueOf(target.toUpperCase());
    }

    public ScriptSoundTriggerBlock() {
        this(new SoundTriggerBlock());
    }

    public ScriptSoundTriggerBlock(SoundTriggerBlock triggerBlock) {
        this.triggerBlock = triggerBlock;
    }
}
