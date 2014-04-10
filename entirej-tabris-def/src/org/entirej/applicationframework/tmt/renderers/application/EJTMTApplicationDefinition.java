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
package org.entirej.applicationframework.tmt.renderers.application;

import org.entirej.framework.core.application.definition.interfaces.EJApplicationDefinition;
import org.entirej.framework.core.properties.EJCoreLayoutItem.TYPE;
import org.entirej.framework.core.properties.definitions.EJPropertyDefinitionType;
import org.entirej.framework.core.properties.definitions.interfaces.EJFrameworkExtensionProperties;
import org.entirej.framework.core.properties.definitions.interfaces.EJPropertyDefinition;
import org.entirej.framework.core.properties.definitions.interfaces.EJPropertyDefinitionGroup;
import org.entirej.framework.core.properties.definitions.interfaces.EJPropertyDefinitionListener;
import org.entirej.framework.dev.properties.EJDevPropertyDefinition;
import org.entirej.framework.dev.properties.EJDevPropertyDefinitionGroup;


public class EJTMTApplicationDefinition implements EJApplicationDefinition
{

    public static final String DISPLAY_TAB_BORDER            = "DISPLAY_TAB_BORDER";

 

    @Override
    public String getApplicationManagerClassName()
    {
        return "org.entirej.applicationframework.tmt.application.EJTMTApplicationManager";
    }

    @Override
    public EJPropertyDefinitionGroup getApplicationPropertyDefinitionGroup()
    {
        EJDevPropertyDefinitionGroup mainGroup = new EJDevPropertyDefinitionGroup("TMTAPP");
        mainGroup.setLabel("Tabris Application");

        EJDevPropertyDefinition displayTabBorder = new EJDevPropertyDefinition(DISPLAY_TAB_BORDER, EJPropertyDefinitionType.BOOLEAN);
        displayTabBorder.setLabel("Display border on tabs");
        displayTabBorder.setDescription("Indicates if borders should be used to surround tab canvases. Displaying borders on tabs can lead to many unwanted frames displayed on your application.");

        mainGroup.addPropertyDefinition(displayTabBorder);

       

      
        return mainGroup;
    }

    @Override
    public void loadValidValuesForProperty(EJFrameworkExtensionProperties arg0, EJPropertyDefinition arg1)
    {
        // no impl

    }

    @Override
    public void propertyChanged(EJPropertyDefinitionListener arg0, EJFrameworkExtensionProperties arg1, String arg2)
    {
        // no impl

    }

    @Override
    public TYPE[] getSupportedLayoutTypes()
    {
        return new TYPE[]{TYPE.COMPONENT};
    }
    
}
