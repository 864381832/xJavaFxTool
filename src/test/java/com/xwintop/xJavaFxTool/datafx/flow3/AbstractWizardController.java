package com.xwintop.xJavaFxTool.datafx.flow3;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import io.datafx.controller.flow.action.BackAction;
import io.datafx.controller.flow.action.LinkAction;

/**
 * This is the base class for all view controllers in the wizard. Each view has a action-bar with an "back", "next" and
 * "finish" button. Because "back" and "finish" will always trigger the same actions in all views these buttons are
 * defined in this abstract class.
 * The "finish" button will always navigate to the last view of the wizard. This view is defined by the
 * WizardDoneController class. Therefore the @LinkAction annotation that was introduced in tutorial 2 can be used here.
 * The "back" button should always navigate to the last view. DataFX stores the view history of a flow internally and so
 * a back action can be supported. To add a back action to a view DataFX provided the @BackAction annotation. This
 * annotation can easily be added to any Node object that is injected by @FXML. Every time the node triggers an action
 * (pressing on a button for example) DataFX will execute a back action on the flow and will navigate to the last view.
 * This annotation is used for the "back" button of the action-bar.
 *
 * About the action-bar:
 * The action-bar is defined in the actionBar.fxml file. When looking at the fxml files of the wizard views
 * (wizardStart.fxml, wizard1.fxml, ...) you will see that the fxml of the action-bar is included in each of them. If
 * you open the fxml files with Scene Builder you can see the action-bar included in the view.
 */
public class AbstractWizardController {

    @FXML
    @BackAction
    private Button backButton;

    @FXML
    @LinkAction(WizardDoneController.class)
    private Button finishButton;

    public Button getBackButton() {
        return backButton;
    }

    public Button getFinishButton() {
        return finishButton;
    }
}
