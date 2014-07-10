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
package org.entirej.applicationframework.tmt.renderers.block.definition;

import java.util.List;

import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.entirej.applicationframework.tmt.renderers.block.definition.interfaces.EJTMTMultiRecordBlockDefinitionProperties;
import org.entirej.applicationframework.tmt.renderers.block.definition.interfaces.EJTMTSingleRecordBlockDefinitionProperties;
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


public class EJTMTSingleRecordBlockDefinition implements EJDevBlockRendererDefinition
{

    public EJTMTSingleRecordBlockDefinition()
    {

    }

    @Override
    public boolean allowMultipleItemGroupsOnMainScreen()
    {
        return true;
    }

    @Override
    public boolean allowSpacerItems()
    {
        return true;
    }

    @Override
    public EJPropertyDefinitionGroup getBlockPropertyDefinitionGroup()
    {
        EJDevPropertyDefinitionGroup mainGroup = new EJDevPropertyDefinitionGroup("Single-Record Block");
        
        EJDevPropertyDefinition pullRefreshmsg = new EJDevPropertyDefinition(EJTMTMultiRecordBlockDefinitionProperties.PULL_REFRESH_MESSAGE,
                EJPropertyDefinitionType.STRING);
        pullRefreshmsg.setLabel("Pull-Refresh Message");
        pullRefreshmsg.setDescription("Add a message that will be shown when a user Pull-Refresh in Block");
        
        EJDevPropertyDefinition pullRefreshActionCommand = new EJDevPropertyDefinition(EJTMTMultiRecordBlockDefinitionProperties.PULL_REFRESH_ACTION,
                EJPropertyDefinitionType.ACTION_COMMAND);
        pullRefreshActionCommand.setLabel("Pull-Refresh Action Command");
        pullRefreshActionCommand.setDescription("Add an action command that will be sent to the action processor when a user Pull-Refresh in Block");
      
        mainGroup.addPropertyDefinition(pullRefreshActionCommand);
        mainGroup.addPropertyDefinition(pullRefreshmsg);
        
        return mainGroup;
    }

    @Override
    public EJPropertyDefinitionGroup getItemPropertiesDefinitionGroup()
    {
        EJDevPropertyDefinition itemPosition = new EJDevPropertyDefinition(EJTMTSingleRecordBlockDefinitionProperties.ITEM_POSITION_PROPERTY,
                EJPropertyDefinitionType.STRING);
        itemPosition.setLabel("Item Orientation");
        itemPosition.setDescription("If the item is fixed in size and smaller than other items within its displayed column, then you can indicate how the item is displayed");
        itemPosition.addValidValue(EJTMTSingleRecordBlockDefinitionProperties.LABEL_ORIENTATION_LEFT_PROPERTY, "Left");
        itemPosition.addValidValue(EJTMTSingleRecordBlockDefinitionProperties.LABEL_ORIENTATION_RIGHT_PROPERTY, "Right");
        itemPosition.addValidValue(EJTMTSingleRecordBlockDefinitionProperties.LABEL_ORIENTATION_CENTER_PROPERTY, "Center");
        itemPosition.setDefaultValue(EJTMTSingleRecordBlockDefinitionProperties.LABEL_POSITION_LEFT_PROPERTY);
        itemPosition.setMandatory(true);

        EJDevPropertyDefinition labelPosition = new EJDevPropertyDefinition(EJTMTSingleRecordBlockDefinitionProperties.LABEL_POSITION_PROPERTY,
                EJPropertyDefinitionType.STRING);
        labelPosition.setLabel("Label Position");
        labelPosition.setDescription("The position the items label should be displayed i.e. Before or after the item");
        labelPosition.addValidValue(EJTMTSingleRecordBlockDefinitionProperties.LABEL_POSITION_LEFT_PROPERTY, "Left");
        labelPosition.addValidValue(EJTMTSingleRecordBlockDefinitionProperties.LABEL_POSITION_RIGHT_PROPERTY, "Right");
        labelPosition.setDefaultValue(EJTMTSingleRecordBlockDefinitionProperties.LABEL_POSITION_LEFT_PROPERTY);

        EJDevPropertyDefinition labelOrientation = new EJDevPropertyDefinition(EJTMTSingleRecordBlockDefinitionProperties.LABEL_ORIENTATION_PROPERTY,
                EJPropertyDefinitionType.STRING);
        labelOrientation.setLabel("Label Orientation");
        labelOrientation.setDescription("The orientation of the labels text");
        labelOrientation.addValidValue(EJTMTSingleRecordBlockDefinitionProperties.LABEL_ORIENTATION_LEFT_PROPERTY, "Left");
        labelOrientation.addValidValue(EJTMTSingleRecordBlockDefinitionProperties.LABEL_ORIENTATION_RIGHT_PROPERTY, "Right");
        labelOrientation.addValidValue(EJTMTSingleRecordBlockDefinitionProperties.LABEL_ORIENTATION_CENTER_PROPERTY, "Center");
        labelOrientation.setDefaultValue(EJTMTSingleRecordBlockDefinitionProperties.LABEL_ORIENTATION_RIGHT_PROPERTY);
        labelOrientation.setMandatory(true);

        EJDevPropertyDefinition initiallyDisplayed = new EJDevPropertyDefinition(EJTMTSingleRecordBlockDefinitionProperties.INITIALLY_DISPLAYED_PROPERTY,
                EJPropertyDefinitionType.BOOLEAN);
        initiallyDisplayed.setLabel("Initially Displayed");
        initiallyDisplayed
        .setDescription("Indicates if this item should be displayed to the user when the form starts. This property is effective if the Displayed property has been set true");
        initiallyDisplayed.setDefaultValue("true");
        initiallyDisplayed.setMandatory(true);

        EJDevPropertyDefinition horizontalSpan = new EJDevPropertyDefinition(EJTMTSingleRecordBlockDefinitionProperties.MAIN_XSPAN_PROPERTY,
                EJPropertyDefinitionType.INTEGER);
        horizontalSpan.setLabel("Horizontal Span");
        horizontalSpan.setDescription("Indicates how many columns this item should span");
        horizontalSpan.setDefaultValue("1");

        EJDevPropertyDefinition verticalSpan = new EJDevPropertyDefinition(EJTMTSingleRecordBlockDefinitionProperties.MAIN_YSPAN_PROPERTY,
                EJPropertyDefinitionType.INTEGER);
        verticalSpan.setLabel("Vertical Span");
        verticalSpan.setDescription("Indicates how many rows this item should span");
        verticalSpan.setDefaultValue("1");

        EJDevPropertyDefinition expandHorizontally = new EJDevPropertyDefinition(EJTMTSingleRecordBlockDefinitionProperties.MAIN_EXPAND_X_PROPERTY,
                EJPropertyDefinitionType.BOOLEAN);
        expandHorizontally.setLabel("Expand Horizontally");
        expandHorizontally.setDescription("Indicates if this item should expand horizontally when the canvas is stretched.");
        expandHorizontally.setDefaultValue("true");

        EJDevPropertyDefinition expandVertically = new EJDevPropertyDefinition(EJTMTSingleRecordBlockDefinitionProperties.MAIN_EXPAND_Y_PROPERTY,
                EJPropertyDefinitionType.BOOLEAN);
        expandVertically.setLabel("Expand Vertically");
        expandVertically.setDescription("Indicates if this item should expand vertically when the canvas is stretched.");
        expandVertically.setDefaultValue("false");

        EJDevPropertyDefinition visualAttribute = new EJDevPropertyDefinition(EJTMTSingleRecordBlockDefinitionProperties.VISUAL_ATTRIBUTE_PROPERTY,
                EJPropertyDefinitionType.VISUAL_ATTRIBUTE);
        visualAttribute.setLabel("Visual Attribute");
        visualAttribute.setDescription("The visual attribute that should be applied to this item");
        visualAttribute.setMandatory(false);

        EJDevPropertyDefinition displayedWidth = new EJDevPropertyDefinition(EJTMTSingleRecordBlockDefinitionProperties.DISPLAYED_WIDTH_PROPERTY,
                EJPropertyDefinitionType.INTEGER);
        displayedWidth.setLabel("Displayed Width");
        displayedWidth.setDescription("Indicates width (in characters) of this item. If no value or zero has been entered, the width of the item will depend upon its contents");
        displayedWidth.setNotifyWhenChanged(true);

        EJDevPropertyDefinition displayedHeight = new EJDevPropertyDefinition(EJTMTSingleRecordBlockDefinitionProperties.DISPLAYED_HEIGHT_PROPERTY,
                EJPropertyDefinitionType.INTEGER);
        displayedHeight.setLabel("Displayed Height");
        displayedWidth.setDescription("Indicates the height (in characters) of this item. If no value or zero has been entered, the height of the item will be relevent to its contents");
        displayedHeight.setNotifyWhenChanged(true);

        EJDevPropertyDefinitionGroup mainScreenGroup = new EJDevPropertyDefinitionGroup(
                EJTMTSingleRecordBlockDefinitionProperties.MAIN_DISPLAY_COORDINATES_GROUP);
        mainScreenGroup.addPropertyDefinition(itemPosition);
        mainScreenGroup.addPropertyDefinition(labelPosition);
        mainScreenGroup.addPropertyDefinition(labelOrientation);
        mainScreenGroup.addPropertyDefinition(initiallyDisplayed);
        mainScreenGroup.addPropertyDefinition(horizontalSpan);
        mainScreenGroup.addPropertyDefinition(verticalSpan);
        mainScreenGroup.addPropertyDefinition(expandHorizontally);
        mainScreenGroup.addPropertyDefinition(expandVertically);
        mainScreenGroup.addPropertyDefinition(displayedWidth);
        mainScreenGroup.addPropertyDefinition(displayedHeight);
        mainScreenGroup.addPropertyDefinition(visualAttribute);

        return mainScreenGroup;
    }

    @Override
    public EJPropertyDefinitionGroup getSpacerItemPropertiesDefinitionGroup()
    {
        EJDevPropertyDefinition horizontalSpan = new EJDevPropertyDefinition(EJTMTSingleRecordBlockDefinitionProperties.MAIN_XSPAN_PROPERTY,
                EJPropertyDefinitionType.INTEGER);
        horizontalSpan.setLabel("Horizontal Span");
        horizontalSpan.setDescription("Indicates how many columns this spacer should span");

        EJDevPropertyDefinition verticalSpan = new EJDevPropertyDefinition(EJTMTSingleRecordBlockDefinitionProperties.MAIN_YSPAN_PROPERTY,
                EJPropertyDefinitionType.INTEGER);
        verticalSpan.setLabel("Vertical Span");
        verticalSpan.setDescription("Indicates how many rows this spacer should span");

        EJDevPropertyDefinition expandx = new EJDevPropertyDefinition(EJTMTSingleRecordBlockDefinitionProperties.MAIN_EXPAND_X_PROPERTY,
                EJPropertyDefinitionType.BOOLEAN);
        expandx.setLabel("Expand Horizontally");
        expandx.setDescription("Indicates if this spacer should expand horizontally to fill the gap between items before and after this spacer");

        EJDevPropertyDefinition expandy = new EJDevPropertyDefinition(EJTMTSingleRecordBlockDefinitionProperties.MAIN_EXPAND_Y_PROPERTY,
                EJPropertyDefinitionType.BOOLEAN);
        expandy.setLabel("Expand Vertically");
        expandy.setDescription("Indicates if this spacer should expand vertically to fill the gap between items above and below this spacer");

        EJDevPropertyDefinition displayedWidth = new EJDevPropertyDefinition(EJTMTSingleRecordBlockDefinitionProperties.DISPLAYED_WIDTH_PROPERTY,
                EJPropertyDefinitionType.INTEGER);
        displayedWidth.setLabel("Displayed Width");
        displayedWidth.setDescription("Indicates the width (in characters) of this spacer. If no value or zero has been entered, the width of the item will be relevent to its contents");
        displayedWidth.setNotifyWhenChanged(true);

        EJDevPropertyDefinition displayedHeight = new EJDevPropertyDefinition(EJTMTSingleRecordBlockDefinitionProperties.DISPLAYED_HEIGHT_PROPERTY,
                EJPropertyDefinitionType.INTEGER);
        displayedHeight.setLabel("Displayed Height");
        displayedWidth.setDescription("Indicates the height (in characters) of this spacer. If no value or zero has been entered, the height of the item will be relevent to its contents");
        displayedHeight.setNotifyWhenChanged(true);

        EJDevPropertyDefinitionGroup mainScreenGroup = new EJDevPropertyDefinitionGroup(
                EJTMTSingleRecordBlockDefinitionProperties.MAIN_DISPLAY_COORDINATES_GROUP);
        mainScreenGroup.addPropertyDefinition(horizontalSpan);
        mainScreenGroup.addPropertyDefinition(verticalSpan);
        mainScreenGroup.addPropertyDefinition(expandx);
        mainScreenGroup.addPropertyDefinition(expandy);
        mainScreenGroup.addPropertyDefinition(displayedWidth);
        mainScreenGroup.addPropertyDefinition(displayedHeight);

        return mainScreenGroup;
    }

    @Override
    public boolean useInsertScreen()
    {
        return true;
    }

    @Override
    public boolean useQueryScreen()
    {
        return true;
    }

    @Override
    public boolean useUpdateScreen()
    {
        return true;
    }

    @Override
    public String getRendererClassName()
    {
        return "org.entirej.applicationframework.tmt.renderers.blocks.EJTMTSingleRecordBlockRenderer";
    }

    @Override
    public void loadValidValuesForProperty(EJFrameworkExtensionProperties arg0, EJPropertyDefinition arg1)
    {
    }

    @Override
    public void propertyChanged(EJPropertyDefinitionListener arg0, EJFrameworkExtensionProperties arg1, String arg2)
    {
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

        EJTMTBlockPreviewerCreator creator = new EJTMTBlockPreviewerCreator();
        List<EJDevItemRendererDefinitionControl> itemControls = creator.addBlockPreviewControl(this, blockDisplayProperties, layoutBody, toolkit);

        return new EJDevBlockRendererDefinitionControl(blockDisplayProperties, itemControls);
    }

    @Override
    public EJDevItemRendererDefinitionControl getSpacerItemControl(EJDevScreenItemDisplayProperties itemProperties, Composite parent, FormToolkit toolkit)
    {
        Label text = new Label(parent, SWT.NULL);

        return new EJDevItemRendererDefinitionControl(itemProperties, text);
    }

    @Override
    public EJPropertyDefinitionGroup getItemGroupPropertiesDefinitionGroup()
    {
        EJDevPropertyDefinitionGroup sectionGroup = new EJDevPropertyDefinitionGroup("Single-Record Block");

        

        return sectionGroup;
    }

}
