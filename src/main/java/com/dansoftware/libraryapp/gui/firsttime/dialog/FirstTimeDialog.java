package com.dansoftware.libraryapp.gui.firsttime.dialog;

import com.dansoftware.libraryapp.gui.firsttime.dialog.DialogSegmentSequence;
import com.dansoftware.libraryapp.locale.I18N;
import com.dansoftware.sgmdialog.SegmentDialog;

public class FirstTimeDialog extends SegmentDialog {

    public FirstTimeDialog() {
        super(I18N.getFirstTimeDialogValues(), new DialogSegmentSequence());
    }
}
