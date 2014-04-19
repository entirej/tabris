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
/**
 * 
 */
package org.entirej.applicationframework.tmt.application.components;

import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.ScrolledComposite;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.entirej.applicationframework.tmt.application.EJTMTApplicationManager;
import org.entirej.applicationframework.tmt.application.interfaces.EJTMTAppComponentRenderer;
import org.entirej.applicationframework.tmt.application.interfaces.EJTMTApplicationComponent;
import org.entirej.applicationframework.tmt.application.interfaces.EJTMTFormChosenListener;
import org.entirej.applicationframework.tmt.layout.EJTMTEntireJGridPane;
import org.entirej.applicationframework.tmt.pages.EJTMTFormAwarePage;
import org.entirej.applicationframework.tmt.pages.EJTMTFormComponentPage;
import org.entirej.applicationframework.tmt.renderers.form.EJTMTFormRenderer;
import org.entirej.framework.core.internal.EJInternalForm;
import org.entirej.framework.core.properties.definitions.interfaces.EJFrameworkExtensionProperties;

import com.eclipsesource.tabris.ui.Page;
import com.eclipsesource.tabris.ui.PageConfiguration;

public class EJTMTFormComponent implements EJTMTApplicationComponent, EJTMTAppComponentRenderer
{
    private EJTMTApplicationManager _appManager;
    private Control                 formControl;
    private String                  formId     = null;
    private String                  pageId     = null;
    private String                  pageImage  = null;
    private String                  pageTitle  = null;
    EJInternalForm                  form;

    public static final String      FORM_ID    = "FORM_ID";

    public final static String      PAGE_ID    = "PAGE_ID";
    public final static String      PAGE_IMAGE = "PAGE_IMAGE";
    public final static String      PAGE_TITLE = "PAGE_TITLE";

    @Override
    public void fireFormOpened(EJInternalForm openedForm)
    {
        // no impl
    }

    @Override
    public void fireFormClosed(EJInternalForm closedForm)
    {
        // no impl
    }

    @Override
    public void fireFormSelected(EJInternalForm selectedForm)
    {
        // no impl
    }

    @Override
    public void addFormChosenListener(EJTMTFormChosenListener formChosenListener)
    {
        // no impl
    }

    @Override
    public void removeFormChosenListener(EJTMTFormChosenListener formChosenListener)
    {
        // no impl
    }

    @Override
    public Control createComponent(Composite parent)
    {

        if (form != null)
        {

            Composite composite = new Composite(parent, SWT.BORDER);
            FillLayout fillLayout = new FillLayout();
            fillLayout.marginHeight = 0;
            fillLayout.marginWidth = 0;
            composite.setLayout(fillLayout);
            
            
            final int height = form.getProperties().getFormHeight();
            //final int width = form.getProperties().getFormWidth();
            EJTMTFormRenderer renderer = ((EJTMTFormRenderer) form.getRenderer());

            
            final ScrolledComposite scrollComposite = new ScrolledComposite(composite, SWT.V_SCROLL );
            renderer.createControl(scrollComposite);
            scrollComposite.setContent(renderer.getGuiComponent());
            
            scrollComposite.setExpandVertical(true);
            scrollComposite.setExpandHorizontal(true);
            scrollComposite.setMinHeight(height);
            
            
            EJTMTEntireJGridPane gridPane = renderer.getGuiComponent();
            gridPane.cleanLayout();

            formControl = composite;

        }
        return formControl;
    }

    @Override
    public Control getGuiComponent()
    {
        return formControl;
    }

    @Override
    public void init(EJFrameworkExtensionProperties rendererprop)
    {
        if (rendererprop != null)
        {
            formId = rendererprop.getStringProperty(FORM_ID);
            pageId = rendererprop.getStringProperty(PAGE_ID);
            pageImage = rendererprop.getStringProperty(PAGE_IMAGE);
            pageTitle = rendererprop.getStringProperty(PAGE_TITLE);

        }
    }

    @Override
    public void createContainer(EJTMTApplicationManager manager, Page page, Composite parent)
    {
        _appManager = manager;

        form = _appManager.getFrameworkManager().createInternalForm(formId, null);
        if (page instanceof EJTMTDynamicFormPage)
        {
            ((EJTMTDynamicFormPage) page).form = form;
        }

        createComponent(parent);
    }

    @Override
    public String getPageId()
    {
        return pageId;
    }

    @Override
    public PageConfiguration createPageConfiguration()
    {
        PageConfiguration pageConfig = new PageConfiguration(pageId, EJTMTDynamicFormPage.class);
        pageConfig.setTitle(pageTitle);
        if (pageImage != null && pageImage.length() > 0)
            pageConfig.setImage(this.getClass().getClassLoader().getResourceAsStream(pageImage));
        pageConfig.setTopLevel(true);

        EJTMTFormComponentPage.addFormRendererActions(pageId, pageConfig, formId);

        return pageConfig;
    }

    public static class EJTMTDynamicFormPage extends EJTMTDynamicComponentPage implements EJTMTFormAwarePage
    {
        EJInternalForm form;

        @Override
        public EJInternalForm getForm()
        {
            // TODO Auto-generated method stub
            return form;
        }
        
        @Override
        public void activate()
        {
            super.activate();
            form.getFormController().getRenderer().gainInitialFocus();
            form.focusGained();
        }

    }
}
