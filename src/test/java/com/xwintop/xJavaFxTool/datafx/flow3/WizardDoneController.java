package com.xwintop.xJavaFxTool.datafx.flow3;

import javax.annotation.PostConstruct;

import io.datafx.controller.FXMLController;
import javafx.fxml.FXML;
import javafx.scene.control.Button;

/**
 * This is a view controller for the last steps in the wizard. The "back" and "finish" buttons of the action-bar that
 * is shown on each view of the wizard are defined in the AbstractWizardController class. So this class only needs to
 * define the "next" button. Because this is the last step in the wizard the "next" button and the "finish" button
 * should never be used. When looking in the fxml files of the wizard the action-bar that contains all buttons is
 * defined as a global fxml. by doing so a developer doesn't need to recreate it for each view. Therefore the "next" and
 * "finish" button will automatically appear on each view. Because they shouldn't be used here the will become disabled
 * by setting the disable property for both of them in the init() method. As described in tutorial 1 the init() method
 * is annotated with the  @PostConstruct annotation and therefore this method will be called once all fields of the
 * controller instance were injected. So when the view appears on screen the buttons "next" and "finish" will be
 * disabled.
 *
 * When looking at the @FXMLController annotation of the class you can find a new feature. next to the fxml files that
 * defines the view of the wizard step a "title" is added. This defines the title of the view. Because the wizard is
 * added to a Stage by using the Flow.startInStage() method the title of the flow is automatically bound to the window
 * title of the Stage. So whenever the view in the flow changes the title of the application window will change to the
 * defined title of the view. As you will learn in future tutorial you can easily change the title of a view in code.
 * In addition to the title other metadata like a icon can be defined for a view or flow.
 */
@FXMLController(value="wizardDone.fxml", title = "Wizard: Finish")
public class WizardDoneController extends AbstractWizardController {

    @FXML
    private Button nextButton;

    @PostConstruct
    public void init() {
        nextButton.setDisable(true);
        getFinishButton().setDisable(true);
    }
}
