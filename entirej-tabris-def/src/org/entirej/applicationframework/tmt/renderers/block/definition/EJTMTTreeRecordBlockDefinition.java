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
package org.entirej.applicationframework.tmt.renderers.block.definition;

import java.util.Collections;

import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.entirej.applicationframework.tmt.renderers.block.definition.interfaces.EJTMTTreeBlockDefinitionProperties;
import org.entirej.applicationframework.tmt.renderers.screen.definition.EJTMTInsertScreenRendererDefinition;
import org.entirej.applicationframework.tmt.renderers.screen.definition.EJTMTQueryScreenRendererDefinition;
import org.entirej.applicationframework.tmt.renderers.screen.definition.EJTMTUpdateScreenRendererDefinition;
import org.entirej.framework.core.properties.definitions.EJPropertyDefinitionType;
import org.entirej.framework.core.properties.definitions.interfaces.EJFrameworkExtensionProperties;
import org.entirej.framework.core.properties.definitions.interfaces.EJPropertyDefinition;
import org.entirej.framework.core.properties.definitions.interfaces.EJPropertyDefinitionGroup;
import org.entirej.framework.core.properties.definitions.interfaces.EJPropertyDefinitionListener;
import org.entirej.framework.core.properties.interfaces.EJMainScreenProperties;
import org.entirej.framework.dev.properties.EJDevPropertyDefinition;
import org.entirej.framework.dev.properties.EJDevPropertyDefinitionGroup;
import org.entirej.framework.dev.properties.interfaces.EJDevBlockDisplayProperties;
import org.entirej.framework.dev.properties.interfaces.EJDevScreenItemDisplayProperties;
import org.entirej.framework.dev.renderer.definition.EJDevBlockRendererDefinitionControl;
import org.entirej.framework.dev.renderer.definition.EJDevItemRendererDefinitionControl;
import org.entirej.framework.dev.renderer.definition.interfaces.EJDevBlockRendererDefinition;
import org.entirej.framework.dev.renderer.definition.interfaces.EJDevInsertScreenRendererDefinition;
import org.entirej.framework.dev.renderer.definition.interfaces.EJDevQueryScreenRendererDefinition;
import org.entirej.framework.dev.renderer.definition.interfaces.EJDevUpdateScreenRendererDefinition;

public class EJTMTTreeRecordBlockDefinition implements EJDevBlockRendererDefinition
{
    public EJTMTTreeRecordBlockDefinition()
    {
    }

    @Override
    public String getRendererClassName()
    {
        return "org.entirej.applicationframework.tmt.renderers.blocks.EJTMTTreeRecordBlockRenderer";
    }

    @Override
    public boolean allowSpacerItems()
    {
        return false;
    }

    public void loadValidValuesForProperty(EJFrameworkExtensionProperties frameworkExtensionProperties, EJPropertyDefinition propertyDefinition)
    {
        // no impl
    }

    public void propertyChanged(EJPropertyDefinitionListener propertyDefListener, EJFrameworkExtensionProperties properties, String propertyName)
    {
        // no impl
    }

    public boolean useInsertScreen()
    {
        return true;
    }

    public boolean useQueryScreen()
    {
        return true;
    }

    public boolean useUpdateScreen()
    {
        return true;
    }

    @Override
    public EJDevInsertScreenRendererDefinition getInsertScreenRendererDefinition()
    {
        return new EJTMTInsertScreenRendererDefinition();
    }

    @Override
    public EJDevQueryScreenRendererDefinition getQueryScreenRendererDefinition()
    {
        return new EJTMTQueryScreenRendererDefinition();
    }

    @Override
    public EJDevUpdateScreenRendererDefinition getUpdateScreenRendererDefinition()
    {
        return new EJTMTUpdateScreenRendererDefinition();
    }

    public boolean allowMultipleItemGroupsOnMainScreen()
    {
        return false;
    }

    /*
     * (non-Javadoc)
     * 
     * @seeorg.entirej.framework.renderers.IBlockRenderer#
     * getBlockPropertyDefinitionGroup()
     */
    public EJPropertyDefinitionGroup getBlockPropertyDefinitionGroup()
    {
        EJDevPropertyDefinitionGroup mainGroup = new EJDevPropertyDefinitionGroup("Tree-Record Block");

        EJDevPropertyDefinition doubleClickActionCommand = new EJDevPropertyDefinition(EJTMTTreeBlockDefinitionProperties.DOUBLE_CLICK_ACTION_COMMAND,
                EJPropertyDefinitionType.ACTION_COMMAND);
        doubleClickActionCommand.setLabel("Double Click Action Command");
        doubleClickActionCommand.setDescription("Add an action command that will be sent to the action processor when a user double clicks on this block");

        EJDevPropertyDefinition showTableBorder = new EJDevPropertyDefinition(EJTMTTreeBlockDefinitionProperties.HIDE_TREE_BORDER,
                EJPropertyDefinitionType.BOOLEAN);
        showTableBorder.setLabel("Hide Tree Border");
        showTableBorder.setDescription("If selected, the renderer will hide the tree standard border");
        showTableBorder.setDefaultValue("false");
        EJDevPropertyDefinition filter = new EJDevPropertyDefinition(EJTMTTreeBlockDefinitionProperties.FILTER, EJPropertyDefinitionType.BOOLEAN);
        filter.setLabel("Add Filter");
        filter.setDescription("If selected, the renderer will display a filter field above the table. This filter can then be used by users to filter table data");
        filter.setDefaultValue("false");

        EJDevPropertyDefinition parentItem = new EJDevPropertyDefinition(EJTMTTreeBlockDefinitionProperties.PARENT_ITEM, EJPropertyDefinitionType.BLOCK_ITEM);
        parentItem.setLabel("Parent Item");
        parentItem.setMandatory(true);
        parentItem.setDescription("A TreeRecord displays records in a tree hierarchy. The hierarchy is made by joining this item to a Relation Item. ");

        EJDevPropertyDefinition relationItem = new EJDevPropertyDefinition(EJTMTTreeBlockDefinitionProperties.RELATION_ITEM,
                EJPropertyDefinitionType.BLOCK_ITEM);
        relationItem.setLabel("Relation Item");
        relationItem.setMandatory(true);
        relationItem.setDescription("Use to join to the Parent Item to create the hierarchy for the data displayed within this block");

        EJDevPropertyDefinition imageItem = new EJDevPropertyDefinition(EJTMTTreeBlockDefinitionProperties.NODE_IMAGE_ITEM, EJPropertyDefinitionType.BLOCK_ITEM);
        imageItem.setLabel("Image Item");
        imageItem
                .setDescription("It is possible to dynamically add an image to the tree node by supplying the path of a picture within your project or by supplying a byteArray within the items value");

        EJDevPropertyDefinition expandLevel = new EJDevPropertyDefinition(EJTMTTreeBlockDefinitionProperties.NODE_EXPAND_LEVEL,
                EJPropertyDefinitionType.INTEGER);
        expandLevel.setLabel("Expand Level");
        expandLevel.setDescription("Indicates the level to which the tree will be opened by default when the form is opened");

        EJDevPropertyDefinition visualAttribute = new EJDevPropertyDefinition(EJTMTTreeBlockDefinitionProperties.VISUAL_ATTRIBUTE_PROPERTY,
                EJPropertyDefinitionType.VISUAL_ATTRIBUTE);
        visualAttribute.setLabel("Visual Attribute");
        visualAttribute.setDescription("The background, foreground and font attributes applied for screen item");
        visualAttribute.setMandatory(false);

        mainGroup.addPropertyDefinition(visualAttribute);

        mainGroup.addPropertyDefinition(parentItem);
        mainGroup.addPropertyDefinition(relationItem);
        mainGroup.addPropertyDefinition(imageItem);
        mainGroup.addPropertyDefinition(expandLevel);
        mainGroup.addPropertyDefinition(doubleClickActionCommand);
        mainGroup.addPropertyDefinition(showTableBorder);
        mainGroup.addPropertyDefinition(filter);

        return mainGroup;
    }

    public EJPropertyDefinitionGroup getItemPropertiesDefinitionGroup()
    {
        EJDevPropertyDefinitionGroup mainGroup = new EJDevPropertyDefinitionGroup("Tree-Record Block: Required Item Properties");

        EJDevPropertyDefinition prefix = new EJDevPropertyDefinition(EJTMTTreeBlockDefinitionProperties.ITEM_PREFIX, EJPropertyDefinitionType.STRING);
        prefix.setLabel("Prefix");
        prefix.setDescription("If you are displaying multiple items as part of the tree, then the Prefix and Suffix properties can be used to seperate the item labels");
        prefix.setMandatory(false);
        EJDevPropertyDefinition suffix = new EJDevPropertyDefinition(EJTMTTreeBlockDefinitionProperties.ITEM_SUFFIX, EJPropertyDefinitionType.STRING);
        suffix.setLabel("Suffix");
        prefix.setDescription("If you are displaying multiple items as part of the tree, then the Prefix and Suffix properties can be used to seperate the item labels");
        suffix.setMandatory(false);

        mainGroup.addPropertyDefinition(prefix);
        mainGroup.addPropertyDefinition(suffix);

        return mainGroup;
    }

    @Override
    public EJPropertyDefinitionGroup getSpacerItemPropertiesDefinitionGroup()
    {
        // No spacers are available for a multi record block
        return null;
    }

    @Override
    public EJDevBlockRendererDefinitionControl addBlockControlToCanvas(EJMainScreenProperties mainScreenProperties,
            EJDevBlockDisplayProperties blockDisplayProperties, Composite parent, FormToolkit toolkit)
    {
        EJFrameworkExtensionProperties rendererProperties = blockDisplayProperties.getBlockRendererProperties();

        Composite layoutBody;

        if (mainScreenProperties.getDisplayFrame())
        {

            layoutBody = new Group(parent, SWT.NONE);
            if (mainScreenProperties.getFrameTitle() != null)
                ((Group) layoutBody).setText(mainScreenProperties.getFrameTitle());

        }
        else
        {
            layoutBody = new Composite(parent, SWT.NONE);
        }

        layoutBody.setLayout(new GridLayout(mainScreenProperties.getNumCols(), false));

        Label browser = new Label(layoutBody, SWT.NONE);
        browser.setText("TREE RENDERER");
        return new EJDevBlockRendererDefinitionControl(blockDisplayProperties, Collections.<EJDevItemRendererDefinitionControl> emptyList());
    }

    @Override
    public EJDevItemRendererDefinitionControl getSpacerItemControl(EJDevScreenItemDisplayProperties itemProperties, Composite parent, FormToolkit toolkit)
    {
        return null;
    }

    @Override
    public EJPropertyDefinitionGroup getItemGroupPropertiesDefinitionGroup()
    {
        return null;
    }

}
