package com.dansoftware.libraryapp.gui.sgmdialog;

import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Iterator;
import java.util.List;
import java.util.Optional;

public abstract class SegmentSequence implements Iterable<Segment> {

    private static final Logger logger = LoggerFactory.getLogger(SegmentSequence.class);

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
        return this.segments.get(0).equals(segment);
    }

    public boolean isSegmentLast(@Nullable Segment segment) {
        return this.segments.get(segments.size() - 1).equals(segment);
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
                return segment;
            }
            last = segment;
        }

        return null;
    }

    public void navigateBack() {
        Segment prev = getPrevFrom(this.focusedSegment.get());
        if (prev != null) {
            focusedSegment.set(prev);
        }
    }

    public void navigateNext() {
        Segment next = getNextFrom(this.focusedSegment.get());
        if (next != null) {
            focusedSegment.set(next);
        } else {
            onSegmentsFinished();
        }
    }

    @NotNull
    @Override
    public Iterator<Segment> iterator() {
        return segments.iterator();
    }

    protected abstract void onSegmentsFinished();
}
