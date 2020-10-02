package com.dansoftware.libraryapp.gui.sgmdialog;

import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import org.jetbrains.annotations.NotNull;

import java.util.Iterator;
import java.util.List;
import java.util.Optional;

public abstract class SegmentSequence implements Iterable<Segment> {

    private final ObjectProperty<Segment> focusedSegment;
    private final List<Segment> segments;

    public SegmentSequence(@NotNull List<Segment> segments) {
        this.segments = segments;
        this.focusedSegment = new SimpleObjectProperty<>();
        this.segments.stream().findFirst().ifPresent(focusedSegment::set);
    }

    public SegmentSequence(Segment... segments) {
        this(List.of(segments));
    }

    public boolean isSegmentFirst(Segment segment) {
        return this.segments.stream().findFirst().equals(Optional.ofNullable(segment));
    }

    public boolean isSegmentLast(Segment newValue) {
        return this.segments.stream().reduce((segment, segment2) -> segment2).equals(Optional.ofNullable(newValue));
    }

    public Segment getFocusedSegment() {
        return focusedSegment.get();
    }

    public ObjectProperty<Segment> focusedSegmentProperty() {
        return focusedSegment;
    }

    public Segment getPrevFrom(Segment from) {
        Segment last = null;
        for (Segment segment : this) {
            if (segment.equals(from)) {
                return last;
            }
            last = segment;
        }

        return null;
    }

    public Segment getNextFrom(Segment from) {
        Segment last = null;
        for (Segment segment : this) {
            if (last != null && last.equals(from)) {
                return last;
            }
            last = segment;
        }

        return null;
    }

    public void navigateBackFrom(Segment from) {
        Segment prev = getPrevFrom(from);
        if (prev != null) {
            focusedSegment.set(prev);
        }
    }

    public void navigateNextFrom(Segment from) {
        Segment next = getNextFrom(from);
        if (next != null) {
            focusedSegment.set(next);
        }
    }

    @NotNull
    @Override
    public Iterator<Segment> iterator() {
        return segments.iterator();
    }

    protected abstract void onSegmentsFinished();
}
