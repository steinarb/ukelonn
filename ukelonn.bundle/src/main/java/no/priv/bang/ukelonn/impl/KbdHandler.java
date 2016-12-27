package no.priv.bang.ukelonn.impl;

import com.vaadin.event.Action;
import com.vaadin.event.Action.Handler;
import com.vaadin.event.ShortcutAction;

public abstract class KbdHandler implements Handler {
    private static final long serialVersionUID = -6270960109506089229L;
    Action tab_next = new ShortcutAction("Tab", ShortcutAction.KeyCode.TAB, null);
    Action tab_prev = new ShortcutAction("Shift+Tab", ShortcutAction.KeyCode.TAB, new int[] {ShortcutAction.ModifierKey.SHIFT});
    Action cur_down = new ShortcutAction("Down", ShortcutAction.KeyCode.ARROW_DOWN, null);
    Action cur_up   = new ShortcutAction("Up", ShortcutAction.KeyCode.ARROW_UP,   null);
    Action enter   = new ShortcutAction("Enter", ShortcutAction.KeyCode.ENTER,      null);

    @Override
    public Action[] getActions(Object target, Object sender) {
        return new Action[] {tab_next, tab_prev, cur_down, cur_up, enter};
    }

}
