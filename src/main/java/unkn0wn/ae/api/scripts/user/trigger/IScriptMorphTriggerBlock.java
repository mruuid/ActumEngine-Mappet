package unkn0wn.ae.api.scripts.user.trigger;

import mchorse.mappet.api.triggers.blocks.MorphTriggerBlock;
import mchorse.metamorph.api.morphs.AbstractMorph;
import unkn0wn.ae.api.scripts.user.trigger.utils.IScriptTriggerBlock;

public interface IScriptMorphTriggerBlock extends IScriptTriggerBlock<MorphTriggerBlock> {

    AbstractMorph getMorph();

    void setMorph(AbstractMorph morph);

    String getTarget();
    void setTarget(String target);

    String getSelector();
    void setSelector(String selector);
}
