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

import static com.eclipsesource.tabris.internal.Clauses.whenNull;

import java.io.InputStream;

import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.ScrolledComposite;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Composite;
import org.entirej.applicationframework.tmt.application.launcher.EJTMTContext;
import org.entirej.applicationframework.tmt.renderers.form.EJTMTFormRenderer;
import org.entirej.framework.core.internal.EJInternalForm;

import com.eclipsesource.tabris.internal.ui.ActionDescriptor;
import com.eclipsesource.tabris.internal.ui.ImageUtil;
import com.eclipsesource.tabris.internal.ui.InstanceCreator;
import com.eclipsesource.tabris.ui.AbstractPage;
import com.eclipsesource.tabris.ui.Action;
import com.eclipsesource.tabris.ui.ActionConfiguration;
import com.eclipsesource.tabris.ui.PageData;
import com.eclipsesource.tabris.ui.PlacementPriority;
import com.eclipsesource.tabris.ui.UI;

public class EJTMTFormPage extends AbstractPage
{
    public static final String FORM_ID_KEY          = "FPIK";

    // actions
    public final static String PAGE_ACTIONS         = "PAGE_ACTIONS";
    public final static String PAGE_ACTION_ID       = "ACTION_ID";
    public final static String PAGE_ACTION_IMAGE    = "ACTION_IMAGE";
    public final static String PAGE_ACTION_PRIORITY = "ACTION_PRIORITY";
    public final static String PAGE_ACTION_NAME     = "ACTION_NAME";

    private EJInternalForm     form;

    @Override
    public void createContent(Composite parent, PageData data)
    {
        EJTMTContext.updateTabrisUIRef(getUI());
        form = data.get(FORM_ID_KEY, EJInternalForm.class);
        if (form != null)
        {
            final int height = form.getProperties().getFormHeight();
            //final int width = form.getProperties().getFormWidth();
            EJTMTFormRenderer renderer = ((EJTMTFormRenderer) form.getRenderer());

            FillLayout fillLayout = new FillLayout();
            parent.setLayout(fillLayout);
            final ScrolledComposite scrollComposite = new ScrolledComposite(parent, SWT.V_SCROLL );
            renderer.createControl(scrollComposite);
            scrollComposite.setContent(renderer.getGuiComponent());
            
            scrollComposite.setExpandVertical(true);
            scrollComposite.setExpandHorizontal(true);
            scrollComposite.setMinHeight(height);
            
            
        }
    }
    @Override
    public void activate()
    {
        form.getFormController().getRenderer().gainInitialFocus();
    }
    
    public static String toFormPageID(String name)
    {
        return String.format("EJTF_%s", name);
    }

    public EJInternalForm getForm()
    {
        return form;
    }

    public static PageData createPageData(EJInternalForm form)
    {
        PageData data = new PageData();
        data.set(FORM_ID_KEY, form);
        return data;
    }
    public static EJInternalForm getFormByPageData(PageData data)
    {
        if(data==null)
        {
            return null;
        }
        return data.get(FORM_ID_KEY, EJInternalForm.class);
    }
    
    

    public static class FormActionConfiguration extends ActionConfiguration
    {
        private final Action      action;

        private final String      actionId;
        private String            title;
        private boolean           enabled;
        private boolean           visible;
        private byte[]            image;
        private PlacementPriority placementPriority;

        public FormActionConfiguration(String actionId, Action action)
        {
            super(actionId, Dummy.class);
            this.action = action;
            this.actionId = actionId;
            this.title = "";
            this.enabled = true;
            this.visible = true;
        }
        
        
        public static String toActionId(String pageid,String actionId)
        {
            return String.format("%s_%s", pageid,actionId);
        }

        @Override
        public ActionConfiguration setTitle(String title)
        {
            whenNull(title).throwIllegalArgument("Action Title must not be null");
            this.title = title;
            return this;
        }

        @Override
        public ActionConfiguration setVisible(boolean visible)
        {
            this.visible = visible;
            return this;
        }

        @Override
        public ActionConfiguration setEnabled(boolean enabled)
        {
            this.enabled = enabled;
            return this;
        }

        @Override
        public ActionConfiguration setImage(InputStream image)
        {
            whenNull(image).throwIllegalArgument("Action Image must not be null");
            this.image = ImageUtil.getBytes(image);
            return this;
        }

        @Override
        public ActionConfiguration setPlacementPriority(PlacementPriority placementPriority)
        {
            whenNull(placementPriority).throwIllegalArgument("PlacementPriority must not be null");
            this.placementPriority = placementPriority;
            return this;
        }

        @Override
        @SuppressWarnings("unchecked")
        public <T> T getAdapter(Class<T> adapter)
        {
            if (adapter == ActionDescriptor.class)
            {
                return (T) createDescriptor();
            }
            return null;
        }

        private ActionDescriptor createDescriptor()
        {

            return new ActionDescriptor(actionId, action).setTitle(title).setImage(image).setVisible(visible).setEnabled(enabled)
                    .setPlacementPrority(placementPriority);
        }

        private static class Dummy implements Action
        {

            @Override
            public void execute(UI ui)
            {
                throw new AssertionError();

            }

        }
    }

}
