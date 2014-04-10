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

import java.util.HashMap;

import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.resource.JFaceResources;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.FormData;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Shell;
import org.entirej.applicationframework.tmt.application.launcher.EJTMTContext;
import org.entirej.framework.core.enumerations.EJScreenType;

import com.eclipsesource.tabris.ui.AbstractPage;
import com.eclipsesource.tabris.ui.PageData;

public class EJTMTScreenPage extends AbstractPage
{

    public static final String CONTEXT_ID_KEY = "CIK";

    protected Context          context;

    public void createBody(Composite parent)
    {
        if (context != null)
        {
            context.createBody(parent);
        }
    }

    public static PageData createPageData(Context context)
    {
        PageData data = new PageData();
        data.set(CONTEXT_ID_KEY, context);
        return data;
    }

    protected void createButtonsForButtonBar(final Composite parent)
    {
        if (context != null)
        {
            context.createButtonsForButtonBar(parent);
        }
    }

    public void setButtonEnable(final int buttonId, boolean enabled)
    {
        getButton(buttonId).setEnabled(enabled);
    }
    
    
    public static String toPageID(String formName,String name,EJScreenType type)
    {
        switch (type)
        {
            case INSERT:
                return String.format("EJF_%s_I_%s",formName, name);
            case QUERY:
                return String.format("EJF_%s_Q_%s",formName, name);
            case UPDATE:
                return String.format("EJF_%s_U_%s",formName, name);
                

            default:
                break;
        }
        return String.format("EJF_%s__%s",formName, name);
    }

    public void validate()
    {
        if (context != null)
        {
            context.validate();
        }
    }

    public void canceled()
    {
        if (context != null)
        {
            context.canceled();
        }
    }

    @Override
    public void createContent(Composite parent, PageData data)
    {
        EJTMTContext.updateTabrisUIRef(getUI());
        context = data.get(CONTEXT_ID_KEY, Context.class);
        if (context != null)
        {
            context.page = this;
        }

        createContents(parent);
    }

    // clone dialog impl

    public static final int          OK      = 0;

    public static final int          CANCEL  = 1;

    protected Control                dialogArea;

    public Control                   buttonBar;

    private HashMap<Integer, Button> buttons = new HashMap<Integer, Button>();

    protected void cancelPressed()
    {
        close();
    }

    protected void buttonPressed(int buttonId)
    {
        if (context != null)
        {
            context.buttonPressed(buttonId);
        }
    }

    public Button createButton(Composite parent, int id, String label, boolean defaultButton)
    {
        // increment the number of columns in the button bar
        ((GridLayout) parent.getLayout()).numColumns++;
        Button button = new Button(parent, SWT.PUSH);
        button.setText(label);
        button.setFont(JFaceResources.getDialogFont());
        button.setData(new Integer(id));
        button.addSelectionListener(new SelectionAdapter()
        {
            public void widgetSelected(SelectionEvent event)
            {
                buttonPressed(((Integer) event.widget.getData()).intValue());
            }
        });
        if (defaultButton)
        {
            Shell shell = parent.getShell();
            if (shell != null)
            {
                shell.setDefaultButton(button);
            }
        }
        buttons.put(new Integer(id), button);
        setButtonLayoutData(button);
        return button;
    }

    protected Control createButtonBar(Composite parent)
    {
        Composite composite = new Composite(parent, SWT.NONE);
        // create a layout with spacing and margins appropriate for the font
        // size.
        GridLayout layout = new GridLayout();
        layout.numColumns = 0; // this is incremented by createButton
        layout.makeColumnsEqualWidth = true;
        layout.marginWidth = (IDialogConstants.HORIZONTAL_MARGIN);
        layout.marginHeight = (IDialogConstants.VERTICAL_MARGIN);
        layout.horizontalSpacing = (IDialogConstants.HORIZONTAL_SPACING);
        layout.verticalSpacing = (IDialogConstants.VERTICAL_SPACING);
        composite.setLayout(layout);
        GridData data = new GridData(GridData.HORIZONTAL_ALIGN_END | GridData.VERTICAL_ALIGN_CENTER);
        composite.setLayoutData(data);
        composite.setFont(parent.getFont());

        // Add the buttons to the button bar.
        createButtonsForButtonBar(composite);
        return composite;
    }

    protected Control createContents(Composite parent)
    {
        // create the top level composite for the dialog
        Composite composite = new Composite(parent, 0);
        GridLayout layout = new GridLayout();
        layout.marginHeight = 5;
        layout.marginWidth = 5;
        layout.verticalSpacing = 0;
        composite.setLayout(layout);
        // composite.setLayoutData(new GridData(GridData.FILL_BOTH));
        // initialize the dialog units
        // create the dialog area and button bar
        dialogArea = createDialogArea(composite);
        buttonBar = createButtonBar(composite);

        return composite;
    }

    protected Control createDialogArea(Composite parent)
    {
        // create a composite with standard margins and spacing
        Composite composite = new Composite(parent, SWT.NONE);
        GridLayout layout = new GridLayout();
        layout.marginHeight = (IDialogConstants.VERTICAL_MARGIN);
        layout.marginWidth = (IDialogConstants.HORIZONTAL_MARGIN);
        layout.verticalSpacing = (IDialogConstants.VERTICAL_SPACING);
        layout.horizontalSpacing = (IDialogConstants.HORIZONTAL_SPACING);
        composite.setLayout(layout);
        composite.setLayoutData(new GridData(GridData.FILL_BOTH));
        createBody(composite);
        return composite;
    }

    public Button getButton(int id)
    {
        return (Button) buttons.get(new Integer(id));
    }

    protected Control getButtonBar()
    {
        return buttonBar;
    }

    protected Control getDialogArea()
    {
        return dialogArea;
    }

    protected void okPressed()
    {
        close();
    }

    protected void setButtonLayoutData(Button button)
    {
        GridData data = new GridData(GridData.HORIZONTAL_ALIGN_FILL);
        int widthHint = (IDialogConstants.BUTTON_WIDTH);
        Point minSize = button.computeSize(SWT.DEFAULT, SWT.DEFAULT, true);
        data.widthHint = Math.max(widthHint, minSize.x);
        button.setLayoutData(data);
    }

    protected void setButtonLayoutFormData(Button button)
    {
        FormData data = new FormData();
        int widthHint = (IDialogConstants.BUTTON_WIDTH);
        Point minSize = button.computeSize(SWT.DEFAULT, SWT.DEFAULT, true);
        data.width = Math.max(widthHint, minSize.x);
        button.setLayoutData(data);
    }

    @Override
    public void activate()
    {
        super.activate();
        if (context != null)
        {
            context.open();
        }
    }

    @Override
    public void destroy()
    {
        // IGNORE for NOW
    }

    // context From

    public static abstract class Context
    {
        protected EJTMTScreenPage page;

        protected void createButtonsForButtonBar(final Composite parent)
        {

        }

        public void validate()
        {

        }

        public void canceled()
        {

        }

        public abstract void createBody(Composite parent);

        public void open()
        {

        }

        public void close()
        {

        }

        public void buttonPressed(int buttonId)
        {

        }

        public EJTMTScreenPage getPage()
        {
            return page;
        }
    }

}
