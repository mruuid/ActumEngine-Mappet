package unkn0wn.ae.api.scripts.user.trigger.utils;

import mchorse.mappet.api.triggers.blocks.StringTriggerBlock;

public interface IScriptStringTriggerBlock<T extends StringTriggerBlock> extends IScriptTriggerBlock<T> {

    String getString();

    void setString(String string);
}
