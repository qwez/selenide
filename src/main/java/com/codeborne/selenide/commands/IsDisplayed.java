package com.codeborne.selenide.commands;

import com.codeborne.selenide.Command;
import com.codeborne.selenide.SelenideElement;
import com.codeborne.selenide.ex.ElementNotFound;
import com.codeborne.selenide.impl.Cleanup;
import com.codeborne.selenide.impl.WebElementSource;
import org.openqa.selenium.WebDriverException;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.remote.RemoteWebElement;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.CheckReturnValue;
import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;

@ParametersAreNonnullByDefault
public class IsDisplayed implements Command<Boolean> {
  private static final Logger logger = LoggerFactory.getLogger(IsDisplayed.class);

  @Override
  @CheckReturnValue
  public Boolean execute(SelenideElement proxy, WebElementSource locator, @Nullable Object[] args) {
    try {
      WebElement element = locator.getWebElement();
      return isDisplayedPatched(element);
    }
    catch (WebDriverException | ElementNotFound elementNotFound) {
      if (Cleanup.of.isInvalidSelectorError(elementNotFound)) {
        throw Cleanup.of.wrap(elementNotFound);
      }
      return false;
    }
    catch (IndexOutOfBoundsException invalidElementIndex) {
      return false;
    }
  }

  private boolean isDisplayedPatched(WebElement element) {
    try {
      return element.isDisplayed();
    }
    catch (NullPointerException e) {
      if (element instanceof RemoteWebElement) {
        logger.warn("NPE in RemoteWebElement.isDisplayed, see https://github.com/SeleniumHQ/selenium/issues/9266", e);
        return false;
      }
      throw e;
    }
  }
}
