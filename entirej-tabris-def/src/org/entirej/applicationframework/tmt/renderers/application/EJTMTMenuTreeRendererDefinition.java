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
package org.entirej.applicationframework.tmt.renderers.application;

import org.entirej.framework.core.properties.definitions.EJPropertyDefinitionType;
import org.entirej.framework.core.properties.definitions.interfaces.EJFrameworkExtensionProperties;
import org.entirej.framework.core.properties.definitions.interfaces.EJPropertyDefinition;
import org.entirej.framework.core.properties.definitions.interfaces.EJPropertyDefinitionGroup;
import org.entirej.framework.core.properties.definitions.interfaces.EJPropertyDefinitionListener;
import org.entirej.framework.dev.properties.EJDevPropertyDefinition;
import org.entirej.framework.dev.properties.EJDevPropertyDefinitionGroup;
import org.entirej.framework.dev.renderer.definition.interfaces.EJDevAppComponentRendererDefinition;

public class EJTMTMenuTreeRendererDefinition implements EJDevAppComponentRendererDefinition
{

    public final static String PAGE_ID    = "PAGE_ID";
    public final static String PAGE_IMAGE = "PAGE_IMAGE";
    public final static String PAGE_TITLE = "PAGE_TITLE";
    public static final String MENU_GROUP = "MENU_GROUP";

    @Override
    public EJPropertyDefinitionGroup getComponentPropertyDefinitionGroup()
    {
        EJDevPropertyDefinitionGroup mainGroup = new EJDevPropertyDefinitionGroup("MENUCONFIG");
        mainGroup.setLabel("Menu Configuration");

        EJDevPropertyDefinition menuId = new EJDevPropertyDefinition(MENU_GROUP, EJPropertyDefinitionType.MENU_GROUP);
        menuId.setLabel("Application Menu");
        menuId.setDescription("The menu that will be displayed within this component");
        
        EJDevPropertyDefinition pageID = new EJDevPropertyDefinition(PAGE_ID,
                EJPropertyDefinitionType.STRING);
        pageID.setLabel("Page ID");
        pageID.setDescription("The page id to be used when this componet display as a Tabris Page");
        pageID.setMandatory(true);

        EJDevPropertyDefinition pageImage = new EJDevPropertyDefinition(PAGE_IMAGE,
                EJPropertyDefinitionType.PROJECT_FILE);
        pageImage.setLabel("Image");
        pageImage.setDescription("The image to display in the title bar for this page");
        pageImage.setMandatory(true);

        EJDevPropertyDefinition title = new EJDevPropertyDefinition(PAGE_TITLE,
                EJPropertyDefinitionType.STRING);
        title.setLabel("Title");
        title.setMandatory(true);
        
        
        mainGroup.addPropertyDefinition(pageID);
        mainGroup.addPropertyDefinition(title);
        mainGroup.addPropertyDefinition(pageImage);
        mainGroup.addPropertyDefinition(menuId);
        return mainGroup;
    }
    
    

    @Override
    public String getRendererClassName()
    {
        return "org.entirej.applicationframework.tmt.application.components.EJTMTMenuTreeComponent";
    }

    @Override
    public void loadValidValuesForProperty(EJFrameworkExtensionProperties arg0, EJPropertyDefinition arg1)
    {

    }

    @Override
    public void propertyChanged(EJPropertyDefinitionListener arg0, EJFrameworkExtensionProperties arg1, String arg2)
    {

    }

}
