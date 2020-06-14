package com.dansoftware.libraryapp.main.init.step;

import java.util.LinkedList;
import java.util.List;

/**
 * A StepLink is a sequence of {@link Step} objects.
 */
public class StepLink {

    private final List<Step> steps;

    private StepLink(StepLinkBuilder builder) {
        this.steps = builder.steps;
    }

    public void execute() {
        steps.forEach(Step::call);
    }

    public static StepLinkBuilder builder() {
        return new StepLinkBuilder();
    }

    public static class StepLinkBuilder {
        private final List<Step> steps;

        private StepLinkBuilder() {
            steps = new LinkedList<>();
        }

        public StepLinkBuilder step(Step step) {
            steps.add(step);
            return this;
        }

        public StepLink build() {
            return new StepLink(this);
        }
    }

}
