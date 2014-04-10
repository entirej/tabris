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
package org.entirej.applicationframework.tmt.application.launcher;

import static org.eclipse.rap.rwt.internal.service.ContextProvider.getContext;

import org.entirej.applicationframework.tmt.application.EJTMTApplicationManager;

import com.eclipsesource.tabris.ui.UI;
import com.eclipsesource.tabris.ui.UIConfiguration;

public class EJTMTContext
{
    private static final String CONTEXT_ID = "ej.EJTMTContext";

    public static void initContext()
    {
        getContext().getUISession().setAttribute(CONTEXT_ID, new EJTMTContext());
    }

    public static EJTMTContext getPageContext()
    {
        return (EJTMTContext) getContext().getUISession().getAttribute(CONTEXT_ID);
    }

    public static EJTMTApplicationManager getEJTMTApplicationManager()
    {
        return (EJTMTApplicationManager) getContext().getUISession().getAttribute("ej.applicationManager");
    }
    public static UIConfiguration getUiConfiguration()
    {
        return (UIConfiguration) getContext().getUISession().getAttribute("ej.tabrisUIConfiguration");
    }
    public static UI getTabrisUI()
    {
        return (UI) getContext().getUISession().getAttribute("ej.tabrisUI");
    }
    
    public static void updateTabrisUIRef(UI ui)
    {
        if(getTabrisUI()==null)
        {
            getContext().getUISession().setAttribute("ej.tabrisUI", ui);
        }
    }
}
