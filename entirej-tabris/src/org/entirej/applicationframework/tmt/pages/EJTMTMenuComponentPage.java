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

import java.util.concurrent.atomic.AtomicBoolean;

import org.eclipse.swt.widgets.Composite;
import org.entirej.applicationframework.tmt.application.components.menu.EJTMTDefaultMenuBuilder;
import org.entirej.applicationframework.tmt.application.launcher.EJTMTContext;

import com.eclipsesource.tabris.ui.AbstractPage;
import com.eclipsesource.tabris.ui.PageData;

public abstract class EJTMTMenuComponentPage extends AbstractPage
{
    private AtomicBoolean init = new AtomicBoolean(true);
    private String menuId = null;

    private Composite parent;
    
    public EJTMTMenuComponentPage(String menuId)
    {
        this.menuId = menuId;
    }

    @Override
    public void createContent(Composite parent, PageData data)
    {
        EJTMTContext.updateTabrisUIRef(getUI());
        this.parent = parent;
        init.set(true);
    }
    
    @Override
    public void activate()
    {
        if(init.getAndSet(false))
        {
           System.err.println( getUI().getPageOperator().getCurrentPageId());
            EJTMTDefaultMenuBuilder _menuBuilder = new EJTMTDefaultMenuBuilder(EJTMTContext.getEJTMTApplicationManager(), parent);
            _menuBuilder.createTreeComponent(menuId);

        }
    }

}
