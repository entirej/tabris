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
package org.entirej.applicationframework.tmt.renderers.form.definition;

import org.entirej.framework.core.properties.definitions.EJPropertyDefinitionType;
import org.entirej.framework.core.properties.definitions.interfaces.EJFrameworkExtensionProperties;
import org.entirej.framework.core.properties.definitions.interfaces.EJPropertyDefinition;
import org.entirej.framework.core.properties.definitions.interfaces.EJPropertyDefinitionGroup;
import org.entirej.framework.core.properties.definitions.interfaces.EJPropertyDefinitionListener;
import org.entirej.framework.dev.properties.EJDevPropertyDefinition;
import org.entirej.framework.dev.properties.EJDevPropertyDefinitionGroup;
import org.entirej.framework.dev.properties.EJDevPropertyDefinitionList;
import org.entirej.framework.dev.renderer.definition.interfaces.EJDevFormRendererDefinition;

public class EJTMTFormRendererDefinition implements EJDevFormRendererDefinition
{

    public final static String PAGE_ACTIONS         = "PAGE_ACTIONS";
    public final static String PAGE_ACTION_ID       = "ACTION_ID";
    public final static String PAGE_ACTION_IMAGE    = "ACTION_IMAGE";
    public final static String PAGE_ACTION_PRIORITY = "ACTION_PRIORITY";
    public final static String PAGE_ACTION_NAME     = "ACTION_NAME";

    public String getRendererClassName()
    {
        return "org.entirej.applicationframework.tmt.renderers.form.EJTMTFormRenderer";
    }

    public void propertyChanged(EJPropertyDefinitionListener propertyDefListener, EJFrameworkExtensionProperties properties, String propertyName)
    {
        // no impl
    }

    public void loadValidValuesForProperty(EJFrameworkExtensionProperties frameworkExtensionProperties, EJPropertyDefinition propertyDefinition)
    {
        // no impl
    }

    public EJPropertyDefinitionGroup getFormPropertyDefinitionGroup()
    {
        EJDevPropertyDefinitionGroup sectionGroup = new EJDevPropertyDefinitionGroup("Form Renderer");

        EJDevPropertyDefinitionList list = new EJDevPropertyDefinitionList(PAGE_ACTIONS, "Page Actions");
        EJDevPropertyDefinition actionID = new EJDevPropertyDefinition(PAGE_ACTION_ID,
                EJPropertyDefinitionType.ACTION_COMMAND);
        actionID.setLabel("Action Command");
        actionID.setDescription("The action command to be used when this action is selected. The action command will be sent to your forms <b>executeActionCommand</a> action processor method for execution");
        actionID.setMandatory(true);

        EJDevPropertyDefinition actionImage = new EJDevPropertyDefinition(PAGE_ACTION_IMAGE,
                EJPropertyDefinitionType.PROJECT_FILE);
        actionImage.setLabel("Action Image");
        actionImage.setDescription("The image that is displayed to represent this action");
        actionImage.setMandatory(true);

        EJDevPropertyDefinition actionName = new EJDevPropertyDefinition(PAGE_ACTION_NAME,
                EJPropertyDefinitionType.STRING);
        actionName.setLabel("Action Name");
        actionName.setDescription("The unique name to identify this action");

        EJDevPropertyDefinition priority = new EJDevPropertyDefinition(PAGE_ACTION_PRIORITY,
                EJPropertyDefinitionType.BOOLEAN);
        priority.setLabel("Prioritised Action");
        priority.setDescription("Prioritised actions will be placed in a significant position within the UI. The position is difference depending on the device being used. Read <a href=\"http://docs.entirej.com/display/EJ1/Mobile+Actions\">here</a> for more information on actions");

        list.addPropertyDefinition(actionID);
        list.addPropertyDefinition(actionImage);
        list.addPropertyDefinition(actionName);
        list.addPropertyDefinition(priority);
        
        sectionGroup.addPropertyDefinitionList(list);
        return sectionGroup;
    }

}
