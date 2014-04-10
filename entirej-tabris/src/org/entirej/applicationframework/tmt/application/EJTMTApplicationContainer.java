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
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import org.eclipse.swt.custom.CTabItem;
import org.eclipse.swt.widgets.Control;
import org.entirej.applicationframework.tmt.application.interfaces.EJTMTAppComponentRenderer;
import org.entirej.applicationframework.tmt.application.interfaces.EJTMTFormChosenEvent;
import org.entirej.applicationframework.tmt.application.interfaces.EJTMTFormChosenListener;
import org.entirej.applicationframework.tmt.application.interfaces.EJTMTFormClosedListener;
import org.entirej.applicationframework.tmt.application.interfaces.EJTMTFormContainer;
import org.entirej.applicationframework.tmt.application.interfaces.EJTMTFormOpenedListener;
import org.entirej.applicationframework.tmt.application.interfaces.EJTMTFormSelectedListener;
import org.entirej.applicationframework.tmt.application.launcher.EJTMTContext;
import org.entirej.applicationframework.tmt.pages.EJTMTFormPage;
import org.entirej.applicationframework.tmt.pages.EJTMTFormPage.FormActionConfiguration;
import org.entirej.framework.core.EJForm;
import org.entirej.framework.core.EJRecord;
import org.entirej.framework.core.data.controllers.EJFormController;
import org.entirej.framework.core.data.controllers.EJPopupFormController;
import org.entirej.framework.core.enumerations.EJScreenType;
import org.entirej.framework.core.extensions.properties.EJCoreFrameworkExtensionPropertyList;
import org.entirej.framework.core.internal.EJInternalForm;
import org.entirej.framework.core.properties.EJCoreFormProperties;
import org.entirej.framework.core.properties.EJCoreLayoutContainer;
import org.entirej.framework.core.properties.EJCoreLayoutItem;
import org.entirej.framework.core.properties.EJCoreLayoutItem.LayoutComponent;
import org.entirej.framework.core.properties.EJCoreProperties;
import org.entirej.framework.core.properties.definitions.interfaces.EJFrameworkExtensionProperties;
import org.entirej.framework.core.properties.definitions.interfaces.EJFrameworkExtensionPropertyListEntry;
import org.entirej.framework.core.renderers.interfaces.EJApplicationComponentRenderer;
import org.entirej.framework.core.renderers.registry.EJRendererFactory;

import com.eclipsesource.tabris.ui.Action;
import com.eclipsesource.tabris.ui.Page;
import com.eclipsesource.tabris.ui.PageConfiguration;
import com.eclipsesource.tabris.ui.PageData;
import com.eclipsesource.tabris.ui.PageStyle;
import com.eclipsesource.tabris.ui.PlacementPriority;
import com.eclipsesource.tabris.ui.UI;
import com.eclipsesource.tabris.ui.UIConfiguration;

public class EJTMTApplicationContainer implements Serializable, EJTMTFormOpenedListener, EJTMTFormClosedListener, EJTMTFormSelectedListener,
        EJTMTFormChosenListener
{
    protected EJTMTFormContainer              _formContainer;
    protected EJTMTApplicationManager         _applicationManager;

    protected List<EJTMTAppComponentRenderer> components = new ArrayList<EJTMTAppComponentRenderer>();

    public List<EJTMTAppComponentRenderer> getComponents()
    {
        return components;
    }
    public EJTMTAppComponentRenderer getComponentByPage(String id)
    {
        for (EJTMTAppComponentRenderer renderer : components)
        {
            if(id.equals(renderer.getPageId()))
            {
                return renderer;
            }
        }
        
        return null;
    }

    /**
     * Returns the {@link EJTMTFormContainer} used within this application
     * 
     * @return This applications {@link EJTMTFormContainer}
     */
    public EJTMTFormContainer getFormContainer()
    {
        return _formContainer;
    }

    void buildApplication(EJTMTApplicationManager applicationManager)
    {
        _applicationManager = applicationManager;

        // build tabris page base form container
        final UIConfiguration configuration = EJTMTContext.getUiConfiguration();
        final UI ui = EJTMTContext.getTabrisUI();
        if (ui != null && configuration != null)
        {
            _formContainer = new EJTMTFormContainer()
            {

                @Override
                public EJInternalForm switchToForm(String key)
                {
                    // not supported
                    return null;
                }

                @Override
                public void removeFormSelectedListener(EJTMTFormSelectedListener selectionListener)
                {
                    // ignore

                }

                @Override
                public void popupFormClosed()
                {
                    PageData currentPageData = ui.getPageOperator().getCurrentPageData();
                    if (currentPageData != null && currentPageData.get("POPUP", Boolean.class))
                    {
                        Page currentPage = ui.getPageOperator().getCurrentPage();
                        closeForm(((EJTMTFormPage) currentPage).getForm());
                    }

                }

                @Override
                public void openPopupForm(EJPopupFormController popupController)
                {
                    EJInternalForm form = popupController.getPopupForm();

                    final EJCoreFormProperties formProp = form.getProperties();
                    String pageID = EJTMTFormPage.toFormPageID(formProp.getName());
                    if (configuration.getPageConfiguration(pageID) == null)
                    {
                        PageConfiguration pageConfiguration = new PageConfiguration(pageID, EJTMTFormPage.class).setTitle(formProp.getTitle());
                        pageConfiguration.setStyle(PageStyle.FULLSCREEN);
                        configuration.addPageConfiguration(pageConfiguration);

                    }

                    PageData pageData = EJTMTFormPage.createPageData(form);
                    pageData.set("POPUP", Boolean.TRUE);
                    ui.getPageOperator().openPage(pageID, pageData);

                }

                @Override
                public Collection<EJInternalForm> getAllForms()
                {
                    return Collections.emptyList();
                }

                @Override
                public EJInternalForm getActiveForm()
                {
                    Page currentPage = ui.getPageOperator().getCurrentPage();
                    return currentPage instanceof EJTMTFormPage ? ((EJTMTFormPage) currentPage).getForm() : null;
                }

                @Override
                public boolean containsForm(String formName)
                {
                    return false;
                }

                @Override
                public void closeForm(EJInternalForm form)
                {

                    String pageId = EJTMTFormPage.toFormPageID(form.getProperties().getName());
                    if (pageId.equals(ui.getPageOperator().getCurrentPageId()))
                    {
                        ui.getPageOperator().closeCurrentPage();
                    }
                    // switch to page page and close;
                    PageConfiguration pageConfiguration = configuration.getPageConfiguration(pageId);
                    if (pageConfiguration != null)
                    {
                        configuration.removePageConfiguration(pageId);
                    }
                }

                @Override
                public void addFormSelectedListener(EJTMTFormSelectedListener selectionListener)
                {
                    // ignore

                }

                @Override
                public EJInternalForm addForm( EJInternalForm form)
                {
                     EJCoreFormProperties formProp = form.getProperties();
                    final String pageID = EJTMTFormPage.toFormPageID(formProp.getName());
                    if (configuration.getPageConfiguration(pageID) == null)
                    {
                        PageConfiguration pageConfiguration = new PageConfiguration(pageID, EJTMTFormPage.class).setTitle(formProp.getTitle());
                        pageConfiguration.setStyle(PageStyle.DEFAULT);

                        addFormActions(form, formProp, pageID, pageConfiguration);
                        configuration.addPageConfiguration(pageConfiguration);

                    }

                    PageData pageData = EJTMTFormPage.createPageData(form);
                    pageData.set("POPUP", Boolean.FALSE);
                    ui.getPageOperator().openPage(pageID, pageData);
                    return form;
                }

                

            };
        }

    }

    
    public static void addFormActions(EJInternalForm form, EJCoreFormProperties formProp, final String pageID, PageConfiguration pageConfiguration)
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
                        FormActionConfiguration actionConfiguration = new FormActionConfiguration(FormActionConfiguration.toActionId(pageID,
                                action), new Action()
                        {

                            @Override
                            public void execute(UI ui)
                            {
                                
                                EJInternalForm form = EJTMTFormPage.getFormByPageData(ui.getPageOperator().getCurrentPageData());
                                if(form==null)
                                {
                                    System.err.println(ui.getPageOperator().getCurrentPageData());
                                    System.err.println(ui.getPageOperator().getCurrentPageId());
                                    return;
                                }
                                EJFormController formController = form.getFormController();
                                EJRecord record = null;
                                if (form.getFocusedBlock() != null && form.getFocusedBlock().getBlockController().getFocusedRecord() != null)
                                {
                                    record = new EJRecord(form.getFocusedBlock().getBlockController().getFocusedRecord());
                                }
                                formController.getManagedActionController().executeActionCommand(formController.getEJForm(), record, action,
                                        EJScreenType.MAIN);

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
    
    public void buildComponents()
    {
        // build application components
        EJCoreLayoutContainer layoutContainer = EJCoreProperties.getInstance().getLayoutContainer();
        List<EJCoreLayoutItem> items = layoutContainer.getItems();
        for (EJCoreLayoutItem item : items)
        {
            switch (item.getType())
            {

                case COMPONENT:
                {

                    try
                    {
                        LayoutComponent layoutComponent = (EJCoreLayoutItem.LayoutComponent) item;
                        EJApplicationComponentRenderer applicationComponentRenderer = EJRendererFactory.getInstance().getApplicationComponentRenderer(
                                layoutComponent.getRenderer());

                        EJTMTAppComponentRenderer renderer = (EJTMTAppComponentRenderer) applicationComponentRenderer;
                        renderer.init(layoutComponent.getRendereProperties());
                        components.add(renderer);

                        
                    }
                    catch (Exception e)
                    {
                        throw new RuntimeException(e);
                    }
                }
                    break;

            }
        }
    }

    /**
     * Returns the currently active form
     * 
     * @return The currently active form or <code>null</code> if there is
     *         currently no active form
     */
    public EJInternalForm getActiveForm()
    {
        return _formContainer != null ? _formContainer.getActiveForm() : null;
    }

    /**
     * Returns the amount of forms currently opened and stored within the form
     * container
     * 
     * @return The amount of forms currently opened
     */
    public int getOpenFormCount()
    {
        return _formContainer != null ? _formContainer.getAllForms().size() : 0;
    }

    /**
     * Instructs the form container to close the given form
     * 
     * @param form
     *            The form to close
     */
    public void remove(EJInternalForm form)
    {
        if (_formContainer != null)
        {
            _formContainer.closeForm(form);
        }

        // Inform the listeners that the form has been closed
        fireFormClosed(form);
    }

    /**
     * Opens a new form and adds it to the applications chosen form container
     * <p>
     * If the form passed is <code>null</code> or not {@link EJTMTFormContainer}
     * has been implemented then this method will do nothing
     * 
     * @param form
     *            The form to be opened and added to the {@link EJForm}
     */
    public void add(EJInternalForm form)
    {
        if (form == null)
        {
            return;
        }

        if (_formContainer != null)
        {
            EJInternalForm addForm = _formContainer.addForm(form);
            // Inform the listeners that the form was opened
            fireFormOpened(addForm);
        }
    }

    public boolean isFormOpened(String formName)
    {

        return getForm(formName) != null;
    }

    @Override
    public void formChosen(EJTMTFormChosenEvent event)
    {
        EJInternalForm form = _applicationManager.getFrameworkManager().createInternalForm(event.getChosenFormName(), null);
        if (form != null)
        {
            add(form);
        }
    }

    @Override
    public void fireFormClosed(EJInternalForm closedForm)
    {

    }

    @Override
    public void fireFormOpened(EJInternalForm openedForm)
    {

    }

    @Override
    public void fireFormSelected(EJInternalForm selectedForm)
    {

    }

    public EJInternalForm getForm(String formName)
    {

        for (EJInternalForm form : getFormContainer().getAllForms())
        {
            if (formName.equals(form.getProperties().getName()))
            {
                return form;
            }
        }

        return null;
    }

    public EJInternalForm switchToForm(String key)
    {
        EJTMTFormContainer formContainer = getFormContainer();
        if (formContainer != null)
        {
            EJInternalForm switchToForm = formContainer.switchToForm(key);
            if (switchToForm != null)
            {
                if (formContainer instanceof EJApplicationComponentRenderer)
                {
                    switchTabs((Control) ((EJApplicationComponentRenderer) formContainer).getGuiComponent());
                }

                return switchToForm;
            }
        }

        return null;
    }

    private void switchTabs(Control control)
    {
        if (control == null || control.isDisposed())
        {
            return;
        }

        Control parent = control.getParent();
        while (parent != null && !parent.isDisposed())
        {
            if (parent.getData("TAB_ITEM") != null)
            {
                CTabItem data = (CTabItem) parent.getData("TAB_ITEM");
                data.getParent().setSelection(data);
                parent = null;
                switchTabs(data.getParent());
            }
            else
            {
                parent = parent.getParent();
            }
        }
    }
}
