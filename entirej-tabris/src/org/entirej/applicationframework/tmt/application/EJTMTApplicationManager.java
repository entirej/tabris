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

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Shell;
import org.entirej.applicationframework.tmt.application.interfaces.EJTMTFormContainer;
import org.entirej.applicationframework.tmt.layout.EJTMTEntireJGridPane;
import org.entirej.applicationframework.tmt.renderers.form.EJTMTFormRenderer;
import org.entirej.framework.core.EJFrameworkManager;
import org.entirej.framework.core.EJManagedFrameworkConnection;
import org.entirej.framework.core.EJMessage;
import org.entirej.framework.core.EJParameterList;
import org.entirej.framework.core.EJTranslatorHelper;
import org.entirej.framework.core.data.controllers.EJApplicationLevelParameter;
import org.entirej.framework.core.data.controllers.EJEmbeddedFormController;
import org.entirej.framework.core.data.controllers.EJInternalQuestion;
import org.entirej.framework.core.data.controllers.EJPopupFormController;
import org.entirej.framework.core.data.controllers.EJQuestion;
import org.entirej.framework.core.interfaces.EJApplicationManager;
import org.entirej.framework.core.interfaces.EJMessenger;
import org.entirej.framework.core.internal.EJInternalForm;

public class EJTMTApplicationManager implements EJApplicationManager, Serializable
{
    private EJFrameworkManager        _frameworkManager;
    private EJTMTApplicationContainer _applicationContainer;

    private EJTMTMessenger            messenger;

    private Shell                     shell;

    private List<EJInternalForm>      embeddedForms = new ArrayList<EJInternalForm>();

    public EJTMTApplicationManager()
    {
        messenger = new EJTMTMessenger(this);
    }

    @Override
    public EJFrameworkManager getFrameworkManager()
    {
        return _frameworkManager;
    }

    public Shell getShell()
    {
        return shell;
    }

    public EJTMTFormContainer getFormContainer()
    {
        return _applicationContainer.getFormContainer();
    }

    public EJTMTApplicationContainer getApplicationContainer()
    {
        return _applicationContainer;
    }
    
    @Override
    public void setFrameworkManager(EJFrameworkManager manager)
    {
        _frameworkManager = manager;
    }

    @Override
    public EJMessenger getApplicationMessenger()
    {
        return messenger;
    }
    
    public void setApplicationContainer(EJTMTApplicationContainer container)
    {
        if (container == null)
        {
            throw new NullPointerException("The ApplicationContainer cannot bu null");
        }
        this._applicationContainer = container;
    }

    public void buildApplication( Shell shell)
    {
       
        this.shell = shell;
        _applicationContainer.buildApplication(this);
    }

    @Override
    public EJInternalForm getActiveForm()
    {
        if (_applicationContainer == null)
        {
            return null;
        }

        return _applicationContainer.getActiveForm();
    }

    @Override
    public EJInternalForm getForm(String formName)
    {

        EJInternalForm form = _applicationContainer.getForm(formName);
        if (form == null)
        {
            for (EJInternalForm internalForm : embeddedForms)
            {
                if (formName.equals(internalForm.getProperties().getName()))
                {
                    form = internalForm;
                    break;
                }
            }
        }
        return form;
    }
    
    

    public void openEmbeddedForm(EJEmbeddedFormController embeddedController)
    {
        embeddedController.getCallingForm().getRenderer().openEmbeddedForm(embeddedController);
    }
    
 
    public void closeEmbeddedForm(EJEmbeddedFormController embeddedController)
    {
        embeddedController.getCallingForm().getRenderer().closeEmbeddedForm(embeddedController);
    }


    public EJInternalForm createEmbeddedForm(String formName, Composite parent)
    {
        try
        {

            EJInternalForm form = getFrameworkManager().createInternalForm(formName, null);
            if (form != null)
            {

                EJTMTFormRenderer renderer = (EJTMTFormRenderer) form.getRenderer();
                renderer.createControl(parent);
                EJTMTEntireJGridPane gridPane = renderer.getGuiComponent();
                gridPane.cleanLayout();
                embeddedForms.add(form);
                return form;
            }
        }
        catch (Exception e)
        {

            getApplicationMessenger().handleException(e, true);
        }
        return null;
    }

    @Override
    public void removeFormFromContainer(EJInternalForm form)
    {
        if (_applicationContainer == null)
        {
            return;
        }

        _applicationContainer.remove(form);
    }

    @Override
    public int getOpenedFormCount()
    {
        if (_applicationContainer == null)
        {
            return 0;
        }

        return _applicationContainer.getOpenFormCount();
    }

    @Override
    public boolean isFormOpened(String formName)
    {
        if (_applicationContainer == null)
        {
            return false;
        }

        return _applicationContainer.isFormOpened(formName);
    }

    @Override
    public void addFormToContainer(EJInternalForm form, boolean blocking)
    {
        if (_applicationContainer == null)
        {
            throw new IllegalStateException("Unable to open a form until the application has been built");
        }
        _applicationContainer.add(form);
    }

    /**
     * Used to open a specific form as a popup
     * <p>
     * A popup form is a normal form that will be opened in a modal window or as
     * part of the current form. The modal form normally has a direct connection
     * to this form and may receive or return values to or from the calling form
     * 
     * @param popupController
     *            The controller holding all required values to open the popup
     *            form
     */
    @Override
    public void openPopupForm(EJPopupFormController popupController)
    {
        if (_applicationContainer.getFormContainer() != null)
        {
            _applicationContainer.getFormContainer().openPopupForm(popupController);
        }
    }

    @Override
    public void popupFormClosed()
    {
        if (_applicationContainer.getFormContainer() != null)
        {
            _applicationContainer.getFormContainer().popupFormClosed();
        }
    }

    @Override
    public EJInternalForm switchToForm(String key)
    {

        return _applicationContainer.switchToForm(key);
    }

    @Override
    public EJManagedFrameworkConnection getConnection()
    {
        return _frameworkManager.getConnection();
    }

    @Override
    public EJApplicationLevelParameter getApplicationLevelParameter(String valueName)
    {
        return _frameworkManager.getApplicationLevelParameter(valueName);
    }

    @Override
    public void setApplicationLevelParameter(String valueName, Object value)
    {
        _frameworkManager.setApplicationLevelParameter(valueName, value);
    }

    /**
     * Used to set the current locale of the application
     * <p>
     * EntireJ stores a locale that is used by various item renderers for
     * example the NumberItemRenderer. It is used for the formatting of the
     * number etc. The default for the locale is {@link Locale.ENGLISH} but can
     * be changed via this method
     * 
     * @param locale
     *            The locale to use for this application
     */
    @Override
    public void changeLocale(Locale locale)
    {
        _frameworkManager.changeLocale(locale);
    }

    @Override
    public Locale getCurrentLocale()
    {
        return _frameworkManager.getCurrentLocale();
    }

    @Override
    public EJTranslatorHelper getTranslatorHelper()
    {
        return _frameworkManager.getTranslatorHelper();
    }

    @Override
    public void handleMessage(EJMessage message)
    {
        messenger.handleMessage(message);
    }

    @Override
    public void handleException(Exception exception)
    {
        messenger.handleException(exception);
    }

    @Override
    public void handleException(Exception exception, boolean showUserMessage)
    {
        messenger.handleException(exception, showUserMessage);
    }

    @Override
    public void askQuestion(EJQuestion question)
    {
        messenger.askQuestion(question);
    }

    @Override
    public void askInternalQuestion(EJInternalQuestion question)
    {
        messenger.askInternalQuestion(question);
    }

    @Override
    public void openForm(String formName, EJParameterList parameterList, boolean blocking)
    {
        _frameworkManager.openForm(formName, parameterList, blocking);
        
    }

    @Override
    public void openForm(String formName, EJParameterList parameterList)
    {
        _frameworkManager.openForm(formName, parameterList);
        
    }

    @Override
    public void openForm(String formName)
    {
        _frameworkManager.openForm(formName);
        
    }
    
    @Override
    public void runReport(String reportName)
    {
        throw new RuntimeException("not supported yet");
        
    }
    
    public void runReport(String reportName, EJParameterList parameterList) {
        
        throw new RuntimeException("not supported yet");
        
    };
}
