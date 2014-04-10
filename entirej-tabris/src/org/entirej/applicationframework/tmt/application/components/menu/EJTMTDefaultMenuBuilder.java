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
package org.entirej.applicationframework.tmt.application.components.menu;

import java.io.Serializable;

import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.entirej.applicationframework.tmt.application.components.menu.EJTMTMenuTreeElement.Type;
import org.entirej.framework.core.EJActionProcessorException;
import org.entirej.framework.core.EJApplicationException;
import org.entirej.framework.core.EJMessageFactory;
import org.entirej.framework.core.actionprocessor.interfaces.EJMenuActionProcessor;
import org.entirej.framework.core.enumerations.EJFrameworkMessage;
import org.entirej.framework.core.interfaces.EJApplicationManager;

import com.eclipsesource.tabris.widgets.enhancement.TreeDecorator;
import com.eclipsesource.tabris.widgets.enhancement.Widgets;

public class EJTMTDefaultMenuBuilder implements Serializable
{
    private TreeViewer           _menuTree;
    private EJApplicationManager _applicationManager;
    private Composite            _parent;

    public EJTMTDefaultMenuBuilder(EJApplicationManager appManager, Composite parent)
    {
        this._applicationManager = appManager;
        this._parent = parent;
    }

    private TreeViewer createMenuTree(EJTMTMenuTreeRoot root)
    {
        _menuTree = new TreeViewer(_parent);
        //enable tabris options
        TreeDecorator onTree = Widgets.onTree(_menuTree.getTree());
        onTree.enableBackButtonNavigation();
        //onTree.enableAlternativeSelection(TreePart.LEAF);

        _menuTree.setContentProvider(new EJTMTMenuTreeContentProvider());
        _menuTree.setLabelProvider(new LabelProvider()
        {
            @Override
            public String getText(Object element)
            {
                if (element instanceof EJTMTMenuTreeElement)
                {
                    return ((EJTMTMenuTreeElement) element).getText();
                }
                return "<EMPTY>";
            }

            @Override
            public Image getImage(Object element)
            {
                if (element instanceof EJTMTMenuTreeElement)
                {
                    return ((EJTMTMenuTreeElement) element).getImage();
                }
                return super.getImage(element);
            }

        });
        _menuTree.setInput(root);

        EJMenuActionProcessor actionProcessor = null;
        if (root.getActionProcessorClassName() != null && root.getActionProcessorClassName().length() > 0)
        {
            try
            {
                Class<?> processorClass = Class.forName(root.getActionProcessorClassName());
                try
                {
                    Object processorObject = processorClass.newInstance();
                    if (processorObject instanceof EJMenuActionProcessor)
                    {
                        actionProcessor = (EJMenuActionProcessor) processorObject;
                    }
                    else
                    {
                        throw new EJApplicationException(EJMessageFactory.getInstance().createMessage(EJFrameworkMessage.INVALID_ACTION_PROCESSOR_NAME,
                                processorClass.getName(), "EJMenuActionProcessor"));
                    }
                }
                catch (InstantiationException e)
                {
                    throw new EJApplicationException(EJMessageFactory.getInstance().createMessage(EJFrameworkMessage.UNABLE_TO_CREATE_ACTION_PROCESSOR,
                            processorClass.getName()), e);
                }
                catch (IllegalAccessException e)
                {
                    throw new EJApplicationException(EJMessageFactory.getInstance().createMessage(EJFrameworkMessage.UNABLE_TO_CREATE_ACTION_PROCESSOR,
                            processorClass.getName()), e);
                }
            }
            catch (ClassNotFoundException e)
            {
                throw new EJApplicationException(EJMessageFactory.getInstance().createMessage(EJFrameworkMessage.INVALID_ACTION_PROCESSOR_FOR_MENU,
                        root.getActionProcessorClassName()));
            }
        }
        final EJMenuActionProcessor menuActionProcessor = actionProcessor;

        
        _menuTree.addSelectionChangedListener( new ISelectionChangedListener() {

            public void selectionChanged( SelectionChangedEvent event ) {
                IStructuredSelection selection = ( IStructuredSelection )event.getSelection();
                if (selection instanceof IStructuredSelection)
                {
                    IStructuredSelection structuredSelection = (IStructuredSelection) selection;
                    if (structuredSelection.getFirstElement() instanceof EJTMTMenuTreeElement)
                    {
                        EJTMTMenuTreeElement element = (EJTMTMenuTreeElement) structuredSelection.getFirstElement();
                        if (element.getType() == Type.FORM)
                            _applicationManager.getFrameworkManager().openForm(element.getActionCommand(), null, false);
                        else if (element.getType() == Type.ACTION && menuActionProcessor!=null)
                        {
                            try
                            {
                                menuActionProcessor.executeActionCommand(element.getActionCommand());
                            }
                            catch (EJActionProcessorException e)
                            {
                                _applicationManager.getApplicationMessenger().handleException(e, true);
                            }
                        }
                        
                    }
                }
            }
            
        });
        return _menuTree;
    }

    public Control createTreeComponent(String menuId)
    {
        EJTMTMenuTreeRoot root = EJTMTDefaultMenuPropertiesBuilder.buildMenuProperties(_applicationManager, menuId);

        _menuTree = createMenuTree(root);
        return _menuTree.getControl();
    }

    

}
