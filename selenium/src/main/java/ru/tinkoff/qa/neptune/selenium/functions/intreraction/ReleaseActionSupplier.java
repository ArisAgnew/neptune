package ru.tinkoff.qa.neptune.selenium.functions.intreraction;

import org.openqa.selenium.SearchContext;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.interactions.Actions;
import ru.tinkoff.qa.neptune.core.api.steps.StepParameter;
import ru.tinkoff.qa.neptune.selenium.api.widget.Widget;
import ru.tinkoff.qa.neptune.selenium.functions.searching.SearchSupplier;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * Builds an action that releases the pressed left mouse button.
 */
public abstract class ReleaseActionSupplier extends InteractiveAction {

    ReleaseActionSupplier() {
        super("Release left mouse button");
    }

    static final class ReleaseSimpleActionSupplier extends ReleaseActionSupplier {

        @Override
        protected void performActionOn(Actions value) {
            value.release().perform();
        }
    }

    static final class ReleaseAFoundElement extends ReleaseActionSupplier {

        @StepParameter("Element")
        private final SearchContext found;

        ReleaseAFoundElement(SearchContext found) {
            super();
            checkNotNull(found);
            this.found = found;
        }

        @Override
        protected void performActionOn(Actions value) {
            WebElement e;
            if (WebElement.class.isAssignableFrom(found.getClass())) {
                e = (WebElement) found;
            } else {
                e = ((Widget) found).getWrappedElement();
            }

            value.release(e).perform();
        }
    }

    static final class ReleaseAnElementToBeFound extends ReleaseActionSupplier {

        @StepParameter("Element")
        private final SearchSupplier<?> toFind;

        ReleaseAnElementToBeFound(SearchSupplier<?> toFind) {
            super();
            checkNotNull(toFind);
            this.toFind = toFind;
        }

        @Override
        protected void performActionOn(Actions value) {
            var found = toFind.get().apply(getDriver());

            WebElement e;
            if (WebElement.class.isAssignableFrom(found.getClass())) {
                e = (WebElement) found;
            } else {
                e = ((Widget) found).getWrappedElement();
            }

            value.release(e).perform();
        }
    }
}
