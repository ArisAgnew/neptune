package ru.tinkoff.qa.neptune.selenium.functions.target.locator.frame.parent;

import ru.tinkoff.qa.neptune.core.api.steps.SequentialGetStepSupplier;
import ru.tinkoff.qa.neptune.selenium.SeleniumStepContext;
import ru.tinkoff.qa.neptune.selenium.functions.target.locator.TargetLocatorSupplier;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebDriverException;

import static ru.tinkoff.qa.neptune.selenium.CurrentContentFunction.currentContent;

public final class ParentFrameSupplier extends SequentialGetStepSupplier
        .GetObjectChainedStepSupplier<SeleniumStepContext, WebDriver, WebDriver, ParentFrameSupplier>
        implements TargetLocatorSupplier<WebDriver> {


    private ParentFrameSupplier() {
        super("Parent frame", driver -> {
            try {
                return driver.switchTo().parentFrame();
            }
            catch (WebDriverException e) {
                return null;
            }
        });
        throwOnEmptyResult(() -> new WebDriverException("It was impossible to switch to the parent frame for some reason"));
    }

    /**
     * Builds a function which performs the switching to the parent frame and returns it.
     * Taken from Selenium documentation:
     * <p>
     *     Change focus to the parent context. If the current context is the top level browsing context,
     *     the context remains unchanged.
     * </p>
     *
     * @return an instance of {@link ParentFrameSupplier} which wraps a function. This function
     *      performs the switching to the parent frame and returns it.
     */
    public static ParentFrameSupplier parentFrame() {
        return new ParentFrameSupplier().from(currentContent());
    }
}
