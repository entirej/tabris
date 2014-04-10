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

import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.Viewer;

public class EJTMTMenuTreeContentProvider implements ITreeContentProvider
{
    @Override
    public void dispose()
    {

    }

    @Override
    public void inputChanged(Viewer arg0, Object arg1, Object arg2)
    {

    }

    @Override
    public Object[] getChildren(Object element)
    {
        return getElements(element);
    }

    @Override
    public Object[] getElements(Object element)
    {
        if (element instanceof EJTMTMenuTreeElement)
        {
            return ((EJTMTMenuTreeElement) element).getTreeElements().toArray();

        }
        return new Object[0];
    }

    @Override
    public Object getParent(Object element)
    {
        if (element instanceof EJTMTMenuTreeElement)
        {
            return ((EJTMTMenuTreeElement) element).getParentTreeElement();
        }
        return null;
    }

    @Override
    public boolean hasChildren(Object element)
    {
        if (element instanceof EJTMTMenuTreeElement)
        {
            return ((EJTMTMenuTreeElement) element).getTreeElementCount() > 0;
        }
        return false;
    }
}
