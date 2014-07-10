/*******************************************************************************
 * Copyright 2013 Mojave Innovations GmbH
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *   http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * 
 * Contributors:
 *     Mojave Innovations GmbH - initial API and implementation
 ******************************************************************************/
package org.entirej.applicationframework.tmt.application;

import java.util.ArrayList;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.dialogs.ErrorDialog;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Shell;
import org.entirej.framework.core.EJApplicationException;
import org.entirej.framework.core.EJMessage;
import org.entirej.framework.core.data.controllers.EJInternalQuestion;
import org.entirej.framework.core.data.controllers.EJQuestion;
import org.entirej.framework.core.enumerations.EJQuestionButton;
import org.entirej.framework.core.interfaces.EJMessenger;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.eclipsesource.tabris.widgets.ClientDialog;
import com.eclipsesource.tabris.widgets.ClientDialog.ButtonType;
import com.eclipsesource.tabris.widgets.ClientDialog.Severity;

public class EJTMTMessenger implements EJMessenger
{
    private final EJTMTApplicationManager manager;
    final Logger                          logger = LoggerFactory.getLogger(EJTMTMessenger.class);

    public EJTMTMessenger(EJTMTApplicationManager manager)
    {
        this.manager = manager;
    }

    @Override
    public void handleMessage(EJMessage message)
    {
        switch (message.getLevel())
        {
            case DEBUG:
                logger.debug(message.getMessage());
                break;
            case HINT:
                
                ClientDialog hintDialog = new ClientDialog();
                hintDialog.setTitle("Hint");
                hintDialog.setMessage(message.getMessage());
                hintDialog.setButton(ClientDialog.ButtonType.OK, "OK");
                hintDialog.open();
                break;
               
            case MESSAGE:
                ClientDialog msgDialog = new ClientDialog();
                msgDialog.setTitle("Message");
                msgDialog.setMessage(message.getMessage());
                msgDialog.setButton(ClientDialog.ButtonType.OK, "OK");
                msgDialog.open();
                break;
            case WARNING:

                
                ClientDialog warnDialog = new ClientDialog();
                warnDialog.setTitle("Warning");
                warnDialog.setMessage(message.getMessage());
                warnDialog.setButton(ClientDialog.ButtonType.OK, "OK");
                warnDialog.setSeverity(Severity.WARNING);
                warnDialog.open();
                break;
            case ERROR:
                ClientDialog errorDialog = new ClientDialog();
                errorDialog.setTitle("Error");
                errorDialog.setMessage(message.getMessage());
                errorDialog.setButton(ClientDialog.ButtonType.OK, "OK");
                errorDialog.setSeverity(Severity.ERROR);
                errorDialog.open();
                break;
            default:
                System.out.println(message.getMessage());
        }
    }

    /**
     * Asks the given question and after the user has made an answer, the answer
     * will be sent to the corresponding <code>IActionProcessor</code>
     * 
     * @param question
     *            The question to be asked
     */
    @Override
    public void askInternalQuestion(EJInternalQuestion question)
    {
        askQuestion(question);
        question.getForm().internalQuestionAnswered(question);
    }

    /**
     * Asks the given question and after the user has made an answer, the answer
     * will be sent to the corresponding <code>IActionProcessor</code>
     * 
     * @param question
     *            The question to be asked
     */
    @Override
    public void askQuestion(final EJQuestion question)
    {
        EJQuestionButton[] optionsButtons = getOptions(question);
        String[] options = new String[optionsButtons.length];
        for (int i = 0; i < optionsButtons.length; i++)
        {
            options[i] = question.getButtonText(optionsButtons[i]);
        }
        
        ClientDialog hintDialog = new ClientDialog();
        
        hintDialog.setTitle(question.getTitle());
        hintDialog.setMessage(question.getMessageText());
        for (int i = 0; i < optionsButtons.length; i++)
        {
            final EJQuestionButton button = optionsButtons[i];
            options[i] = question.getButtonText(button);
            ButtonType type  = ButtonType.OK;;
            switch (button)
            {
                case ONE:
                    type = ButtonType.OK;
                    break;
                case TWO:
                    type = ButtonType.NEUTRAL;
                    break;
                case THREE:
                    type = ButtonType.CANCEL;
                    break;

            }
            hintDialog.setButton(type, question.getButtonText(button), new Listener()
            {
                
                @Override
                public void handleEvent(Event event)
                {
                    question.setAnswer(button);
                    question.getActionProcessor().questionAnswered(question);
                    
                }
            });
        }
       
        hintDialog.open();
    }

    private EJQuestionButton[] getOptions(EJQuestion question)
    {
        ArrayList<EJQuestionButton> options = new ArrayList<EJQuestionButton>();

        String but1Text = question.getButtonText(EJQuestionButton.ONE);
        String but2Text = question.getButtonText(EJQuestionButton.TWO);
        String but3Text = question.getButtonText(EJQuestionButton.THREE);

        if (but1Text != null && but1Text.trim().length() > 0)
        {
            options.add(EJQuestionButton.ONE);
        }
        if (but2Text != null && but2Text.trim().length() > 0)
        {
            options.add(EJQuestionButton.TWO);
        }
        if (but3Text != null && but3Text.trim().length() > 0)
        {
            options.add(EJQuestionButton.THREE);
        }

        return options.toArray(new EJQuestionButton[0]);
    }

    @Override
    public void handleException(Exception exception, boolean showUserMessage)
    {
        if (exception instanceof EJApplicationException && showUserMessage)
        {
            // If the EJApplicationException is created with no parameters, the
            // user is using it to halt application processing, therefore there
            // is not need to handler the exception
            if (!((EJApplicationException) exception).stopProcessing())
            {
                logger.error(exception.getMessage(), exception);
                EJMessage frameworkMessage = ((EJApplicationException) exception).getFrameworkMessage();
                if(frameworkMessage.getMessage()!=null)
                {
                    handleMessage(frameworkMessage);
                }
                else
                {
                    handleMessage(new EJMessage(exception.getMessage()));
                }
                
            }
            
        }
        else if (showUserMessage)
        {
            logger.error(exception.getMessage(), exception);
            Status status = new Status(IStatus.ERROR, "tmt.ej", exception.getMessage());
            ErrorDialog.openError(manager.getShell(), "Error", "Internal Error", status);
            
            
        }
    }

    @Override
    public void handleException(Exception exception)
    {
        handleException(exception, true);
    }

   
}
