package com.github.toy.constructor.selenium.test.steps.tests.searching.widgets.buttons;

import com.github.toy.constructor.selenium.api.widget.drafts.Button;
import org.openqa.selenium.WebElement;

public abstract class AbstractButton extends Button {
    public AbstractButton(WebElement wrappedElement) {
        super(wrappedElement);
    }
}
