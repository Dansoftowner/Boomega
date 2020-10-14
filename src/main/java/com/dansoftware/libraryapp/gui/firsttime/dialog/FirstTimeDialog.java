package com.dansoftware.libraryapp.gui.firsttime.dialog;

import com.dansoftware.libraryapp.gui.theme.Theme;
import com.dansoftware.libraryapp.gui.theme.Themeable;
import com.dansoftware.libraryapp.locale.I18N;
import com.dansoftware.sgmdialog.SegmentDialog;

public class FirstTimeDialog extends SegmentDialog implements Themeable {

    public FirstTimeDialog() {
        super(I18N.getFirstTimeDialogValues(), new DialogSegmentSequence());
        Theme.registerThemeable(this);
    }

    @Override
    public void handleThemeApply(Theme newTheme) {
        newTheme.apply(this);
    }
}
