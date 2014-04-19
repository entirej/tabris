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
package org.entirej.applicationframework.tmt.pages;

import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.entirej.applicationframework.tmt.application.EJTMTApplicationManager;
import org.entirej.applicationframework.tmt.application.EJTMTImageRetriever;
import org.entirej.applicationframework.tmt.application.launcher.EJTMTContext;
import org.entirej.applicationframework.tmt.layout.EJTMTEntireJGridPane;
import org.entirej.applicationframework.tmt.pages.EJTMTFormPage.FormActionConfiguration;
import org.entirej.applicationframework.tmt.renderers.form.EJTMTFormRenderer;
import org.entirej.framework.core.EJParameterList;
import org.entirej.framework.core.EJRecord;
import org.entirej.framework.core.data.controllers.EJFormController;
import org.entirej.framework.core.enumerations.EJScreenType;
import org.entirej.framework.core.extensions.properties.EJCoreFrameworkExtensionPropertyList;
import org.entirej.framework.core.internal.EJInternalForm;
import org.entirej.framework.core.properties.EJCoreFormProperties;
import org.entirej.framework.core.properties.definitions.interfaces.EJFrameworkExtensionProperties;
import org.entirej.framework.core.properties.definitions.interfaces.EJFrameworkExtensionPropertyListEntry;

import com.eclipsesource.tabris.ui.AbstractPage;
import com.eclipsesource.tabris.ui.Action;
import com.eclipsesource.tabris.ui.Page;
import com.eclipsesource.tabris.ui.PageConfiguration;
import com.eclipsesource.tabris.ui.PageData;
import com.eclipsesource.tabris.ui.PlacementPriority;
import com.eclipsesource.tabris.ui.UI;
import com.eclipsesource.tabris.ui.UIConfiguration;

public abstract class EJTMTFormComponentPage extends AbstractPage  implements EJTMTFormAwarePage
{
    private EJInternalForm form = null;

    public EJTMTFormComponentPage(String formId)
    {

        EJTMTApplicationManager manager = EJTMTContext.getEJTMTApplicationManager();
        this.form = manager.getFrameworkManager().createInternalForm(formId, null);
    }
    public EJTMTFormComponentPage(String formId,EJParameterList list)
    {
        
        EJTMTApplicationManager manager = EJTMTContext.getEJTMTApplicationManager();
        this.form = manager.getFrameworkManager().createInternalForm(formId, list);
    }

    public EJTMTFormComponentPage(EJInternalForm form, String pageid)
    {
        this.form = form;
    }

    @Override
    public EJInternalForm getForm()
    {
        return form;
    }
    
    @Override
    public void activate()
    {
        form.getFormController().getRenderer().gainInitialFocus();
        form.focusGained();
    }
    
    
    @Override
    public void createContent(Composite parent, PageData data)
    {
        EJTMTContext.updateTabrisUIRef(getUI());
        if (form != null)
        {

            Composite composite = new Composite(parent, SWT.BORDER);
            FillLayout fillLayout = new FillLayout();
            fillLayout.marginHeight = 5;
            fillLayout.marginWidth = 5;
            composite.setLayout(fillLayout);
            EJTMTFormRenderer renderer = ((EJTMTFormRenderer) form.getRenderer());
            renderer.createControl(composite);
            EJTMTEntireJGridPane gridPane = renderer.getGuiComponent();
            gridPane.cleanLayout();
            return;

        }

    }

    public static void addFormRendererActions(String pageid, PageConfiguration pageConfig, String formId)
    {
        EJTMTApplicationManager manager = EJTMTContext.getEJTMTApplicationManager();
        addFormRendererActions(pageid,pageConfig, manager.getFrameworkManager().createInternalForm(formId, null));
    }

    public static void addFormRendererActions(String pageid, PageConfiguration pageConfig, EJInternalForm form)
    {

        final EJCoreFormProperties formProp = form.getProperties();

        PageConfiguration pageConfiguration = pageConfig;
        if (pageConfiguration != null)
        {
            // create page actions
            EJFrameworkExtensionProperties formRendererProperties = formProp.getFormRendererProperties();
            if (formRendererProperties != null)
            {
                EJCoreFrameworkExtensionPropertyList actions = formRendererProperties.getPropertyList(EJTMTFormPage.PAGE_ACTIONS);
                if (actions != null)
                {
                    for (EJFrameworkExtensionPropertyListEntry entry : actions.getAllListEntries())
                    {
                        final String action = entry.getProperty(EJTMTFormPage.PAGE_ACTION_ID);
                        if (action != null && action.length() > 0)
                        {
                            FormActionConfiguration actionConfiguration = new FormActionConfiguration(FormActionConfiguration.toActionId(pageid, action), new Action()
                            {

                                @Override
                                public void execute(UI ui)
                                {
                                    Page currentPage = ui.getPageOperator().getCurrentPage();
                                    if (currentPage instanceof EJTMTFormAwarePage)
                                    {
                                        EJInternalForm form = ((EJTMTFormAwarePage) currentPage).getForm();
                                        EJFormController formController = form.getFormController();
                                        EJRecord record = null;
                                        if (form.getFocusedBlock() != null && form.getFocusedBlock().getBlockController().getFocusedRecord() != null)
                                        {
                                            record = new EJRecord(form.getFocusedBlock().getBlockController().getFocusedRecord());
                                        }
                                        formController.getManagedActionController().executeActionCommand(formController.getEJForm(), record, action,
                                                EJScreenType.MAIN);
                                    }

                                }
                            });

                            String image = entry.getProperty(EJTMTFormPage.PAGE_ACTION_IMAGE);
                            if (image != null && image.length() > 0)
                            {
                                try
                                {
                                    actionConfiguration.setImage(EJTMTImageRetriever.class.getClassLoader().getResourceAsStream(image));
                                }
                                catch (Exception ex)
                                {
                                    form.getMessenger().handleException(ex);
                                }
                            }
                            actionConfiguration.setTitle(entry.getProperty(EJTMTFormPage.PAGE_ACTION_NAME));

                            if (Boolean.valueOf(entry.getProperty(EJTMTFormPage.PAGE_ACTION_PRIORITY)))
                            {
                                actionConfiguration.setPlacementPriority(PlacementPriority.HIGH);
                            }
                            pageConfiguration.addActionConfiguration(actionConfiguration);
                        }
                    }

                }

            }
        }
    }
    
    
   

}
