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

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.MouseAdapter;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.entirej.applicationframework.tmt.renderers.block.definition.interfaces.EJTMTMultiRecordBlockDefinitionProperties;
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
import org.entirej.framework.dev.properties.interfaces.EJDevBlockItemDisplayProperties;
import org.entirej.framework.dev.properties.interfaces.EJDevItemGroupDisplayProperties;
import org.entirej.framework.dev.properties.interfaces.EJDevMainScreenItemDisplayProperties;
import org.entirej.framework.dev.properties.interfaces.EJDevScreenItemDisplayProperties;
import org.entirej.framework.dev.renderer.definition.EJDevBlockRendererDefinitionControl;
import org.entirej.framework.dev.renderer.definition.EJDevItemRendererDefinitionControl;
import org.entirej.framework.dev.renderer.definition.interfaces.EJDevBlockRendererDefinition;
import org.entirej.framework.dev.renderer.definition.interfaces.EJDevBlockWidgetChosenListener;
import org.entirej.framework.dev.renderer.definition.interfaces.EJDevInsertScreenRendererDefinition;
import org.entirej.framework.dev.renderer.definition.interfaces.EJDevItemWidgetChosenListener;
import org.entirej.framework.dev.renderer.definition.interfaces.EJDevQueryScreenRendererDefinition;
import org.entirej.framework.dev.renderer.definition.interfaces.EJDevUpdateScreenRendererDefinition;

public class EJTMTMultiRecordBlockDefinition implements EJDevBlockRendererDefinition
{
    public EJTMTMultiRecordBlockDefinition()
    {
    }

    @Override
    public String getRendererClassName()
    {
        return "org.entirej.applicationframework.tmt.renderers.blocks.EJTMTMultiRecordBlockRenderer";
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

    public EJPropertyDefinitionGroup getBlockPropertyDefinitionGroup()
    {
        EJDevPropertyDefinitionGroup mainGroup = new EJDevPropertyDefinitionGroup("Multi-Record Block");

        EJDevPropertyDefinition rowSelaction = new EJDevPropertyDefinition(EJTMTMultiRecordBlockDefinitionProperties.ROW_SELECTION,
                EJPropertyDefinitionType.BOOLEAN);
        rowSelaction.setLabel("Row Selection");
        rowSelaction.setDescription("If selected, the renderer will support row selection");
        rowSelaction.setDefaultValue("true");

        
        EJDevPropertyDefinition selectionActionCommand = new EJDevPropertyDefinition(EJTMTMultiRecordBlockDefinitionProperties.ROW_SELECTION_ACTION,
                EJPropertyDefinitionType.ACTION_COMMAND);
        selectionActionCommand.setLabel("Selection Action Command");
        selectionActionCommand.setDescription("Add an action command that will be sent to the action processor when a user select a row");

 
        EJDevPropertyDefinition rowHeight = new EJDevPropertyDefinition(EJTMTMultiRecordBlockDefinitionProperties.ROW_HEIGHT, EJPropertyDefinitionType.INTEGER);
        rowHeight.setLabel("Custom Row Height (pixels)");
        rowHeight.setDescription("If provided, the renderer will display each row in the table to the height specified. The height is specified in units of <b>pixels</b>");

        EJDevPropertyDefinition filter = new EJDevPropertyDefinition(EJTMTMultiRecordBlockDefinitionProperties.FILTER, EJPropertyDefinitionType.BOOLEAN);
        filter.setLabel("Add Filter");
        filter.setDescription("If selected, the renderer will display a filter field above the blocks data. This filter can then be used by users to filter the blocks displayed data");
        filter.setDefaultValue("false");

        mainGroup.addPropertyDefinition(filter);
        mainGroup.addPropertyDefinition(rowHeight);
        mainGroup.addPropertyDefinition(rowSelaction);
        mainGroup.addPropertyDefinition(selectionActionCommand);

        return mainGroup;
    }

    public EJPropertyDefinitionGroup getItemPropertiesDefinitionGroup()
    {
        EJDevPropertyDefinitionGroup mainGroup = new EJDevPropertyDefinitionGroup("Multi-Record Block: Required Item Properties");

        // cell action to support selection
        EJDevPropertyDefinition cellActionCommand = new EJDevPropertyDefinition(EJTMTMultiRecordBlockDefinitionProperties.CELL_ACTION_COMMAND,
                EJPropertyDefinitionType.ACTION_COMMAND);
        cellActionCommand.setLabel("Cell Action Command");
        cellActionCommand.setDescription("An event is fired each time the user selects a cell with this command set. The command is then sent to the forms action processors <b>executeActionCommand</b> method for execution.");

        // cell width & height
        EJDevPropertyDefinition height = new EJDevPropertyDefinition(EJTMTMultiRecordBlockDefinitionProperties.HEIGHT_PROPERTY,
                EJPropertyDefinitionType.INTEGER);
        height.setLabel("Height");
        height.setDescription("The height  of this items column within the cell.");

        EJDevPropertyDefinition width = new EJDevPropertyDefinition(EJTMTMultiRecordBlockDefinitionProperties.WIDTH_PROPERTY, EJPropertyDefinitionType.INTEGER);
        width.setLabel("Width");
        width.setDescription("The width of this items column within the cell.");

        EJDevPropertyDefinition top = new EJDevPropertyDefinition(EJTMTMultiRecordBlockDefinitionProperties.CELL_TOP, EJPropertyDefinitionType.INTEGER);
        top.setLabel("Top");
        top.setDescription("Sets the top offset of the cell, i.e. the distance from the top edge of the template.");
        EJDevPropertyDefinition bottom = new EJDevPropertyDefinition(EJTMTMultiRecordBlockDefinitionProperties.CELL_BOTTOM, EJPropertyDefinitionType.INTEGER);
        bottom.setLabel("Bottom");
        bottom.setDescription("Sets the bottom offset of the cell, i.e. the distance from the bottom edge of the template.");
        EJDevPropertyDefinition left = new EJDevPropertyDefinition(EJTMTMultiRecordBlockDefinitionProperties.CELL_LEFT, EJPropertyDefinitionType.INTEGER);
        left.setLabel("Left");
        left.setDescription("Sets the left offset of the cell, i.e. the distance from the left edge of the template.");
        EJDevPropertyDefinition right = new EJDevPropertyDefinition(EJTMTMultiRecordBlockDefinitionProperties.CELL_RIGHT, EJPropertyDefinitionType.INTEGER);
        right.setLabel("Right");
        right.setDescription("Sets the right offset of the cell, i.e. the distance from the right edge of the template.");

        EJDevPropertyDefinition vAllignment = new EJDevPropertyDefinition(EJTMTMultiRecordBlockDefinitionProperties.CELL_V_ALIGNMENT,
                EJPropertyDefinitionType.STRING);
        vAllignment.setLabel("Vertical Alignment");
        vAllignment.setDescription("Indicates the alignment of the contents within this cell.");
        vAllignment.setDefaultValue(EJTMTMultiRecordBlockDefinitionProperties.COLUMN_ALLIGN_NONE);

        vAllignment.addValidValue(EJTMTMultiRecordBlockDefinitionProperties.COLUMN_ALLIGN_NONE, "None");
        vAllignment.addValidValue(EJTMTMultiRecordBlockDefinitionProperties.COLUMN_ALLIGN_TOP, "Top");
        vAllignment.addValidValue(EJTMTMultiRecordBlockDefinitionProperties.COLUMN_ALLIGN_CENTER, "Center");
        vAllignment.addValidValue(EJTMTMultiRecordBlockDefinitionProperties.COLUMN_ALLIGN_BOTTOM, "Bottom");

        EJDevPropertyDefinition hAllignment = new EJDevPropertyDefinition(EJTMTMultiRecordBlockDefinitionProperties.CELL_H_ALIGNMENT,
                EJPropertyDefinitionType.STRING);
        hAllignment.setLabel("Horizontal Alignment");
        hAllignment.setDescription("Indicates the alignment of the contents within this cell.");
        hAllignment.setDefaultValue(EJTMTMultiRecordBlockDefinitionProperties.COLUMN_ALLIGN_NONE);

        hAllignment.addValidValue(EJTMTMultiRecordBlockDefinitionProperties.COLUMN_ALLIGN_NONE, "None");
        hAllignment.addValidValue(EJTMTMultiRecordBlockDefinitionProperties.COLUMN_ALLIGN_LEFT, "Left");
        hAllignment.addValidValue(EJTMTMultiRecordBlockDefinitionProperties.COLUMN_ALLIGN_CENTER, "Center");
        hAllignment.addValidValue(EJTMTMultiRecordBlockDefinitionProperties.COLUMN_ALLIGN_RIGHT, "Right");

        EJDevPropertyDefinition visualAttribute = new EJDevPropertyDefinition(EJTMTMultiRecordBlockDefinitionProperties.VISUAL_ATTRIBUTE_PROPERTY,
                EJPropertyDefinitionType.VISUAL_ATTRIBUTE);
        visualAttribute.setLabel("Visual Attribute");
        visualAttribute.setDescription("The column will be displayed using the properties from the chosen visual attribute");
        visualAttribute.setMandatory(false);

        mainGroup.addPropertyDefinition(width);
        mainGroup.addPropertyDefinition(height);
        mainGroup.addPropertyDefinition(hAllignment);
        mainGroup.addPropertyDefinition(vAllignment);
        mainGroup.addPropertyDefinition(top);
        mainGroup.addPropertyDefinition(left);
        mainGroup.addPropertyDefinition(right);
        mainGroup.addPropertyDefinition(bottom);

        mainGroup.addPropertyDefinition(cellActionCommand);
        mainGroup.addPropertyDefinition(visualAttribute);

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

        
        Composite preview = new Composite(layoutBody, SWT.BORDER);
        
        GridData gridData = new GridData(GridData.FILL_HORIZONTAL);
        gridData.grabExcessHorizontalSpace = true;
        gridData.minimumHeight = rendererProperties.getIntProperty(EJTMTMultiRecordBlockDefinitionProperties.ROW_HEIGHT, 60);
        gridData.heightHint = rendererProperties.getIntProperty(EJTMTMultiRecordBlockDefinitionProperties.ROW_HEIGHT, 60);
        preview.setLayoutData(gridData);
        preview.setBackground(Display.getDefault().getSystemColor(SWT.COLOR_WHITE));
        preview.setLayout(new EJTMTRowTemplateLayout());
        

        Color systemColor = Display.getDefault().getSystemColor(SWT.COLOR_GRAY);
        
        final List<EJDevItemWidgetChosenListener> _itemWidgetListenerList = new ArrayList<EJDevItemWidgetChosenListener>();
        
        for (EJDevItemGroupDisplayProperties itemGroupProperties : blockDisplayProperties.getMainScreenItemGroupDisplayContainer()
                .getAllItemGroupDisplayProperties())
        {
            Collection<EJDevScreenItemDisplayProperties> allItemDisplayProperties = itemGroupProperties.getAllItemDisplayProperties();
            for (final EJDevScreenItemDisplayProperties item : allItemDisplayProperties)
            {
                Label label = new Label(preview, SWT.BORDER);
                if(item.getLabel()!=null && item.getLabel().trim().length()>0)
                {

                    label.setText(item.getLabel());
                }
                else
                {

                    label.setText(item.getReferencedItemName());
                }
                label.addMouseListener(new MouseAdapter()
                {
                    @Override
                    public void mouseUp(MouseEvent e)
                    {
                        for (EJDevItemWidgetChosenListener listener : _itemWidgetListenerList)
                        {
                            listener.fireRendererChosen(item);
                        }
                    }
                });
                
                EJFrameworkExtensionProperties blockRequiredItemProperties = ((EJDevMainScreenItemDisplayProperties)item).getBlockRendererRequiredProperties();

                EJTMTRowTemplateLayout.RowTemplateData data = new EJTMTRowTemplateLayout.RowTemplateData();
                
                data.top = blockRequiredItemProperties.getIntProperty(EJTMTMultiRecordBlockDefinitionProperties.CELL_TOP, -1);
                data.bottom = blockRequiredItemProperties.getIntProperty(EJTMTMultiRecordBlockDefinitionProperties.CELL_BOTTOM, -1);
                data.left = blockRequiredItemProperties.getIntProperty(EJTMTMultiRecordBlockDefinitionProperties.CELL_LEFT, -1);
                data.right = blockRequiredItemProperties.getIntProperty(EJTMTMultiRecordBlockDefinitionProperties.CELL_RIGHT, -1);
                data.width = blockRequiredItemProperties.getIntProperty(EJTMTMultiRecordBlockDefinitionProperties.WIDTH_PROPERTY, 0);
                data.height = blockRequiredItemProperties.getIntProperty(EJTMTMultiRecordBlockDefinitionProperties.HEIGHT_PROPERTY, 0);
                data.verticalAlignment = getComponentStyle(blockRequiredItemProperties.getStringProperty(EJTMTMultiRecordBlockDefinitionProperties.CELL_V_ALIGNMENT));
                data.horizontalAlignment = getComponentStyle(blockRequiredItemProperties.getStringProperty(EJTMTMultiRecordBlockDefinitionProperties.CELL_H_ALIGNMENT));
                label.setLayoutData(data);
                label.setBackground(systemColor);
            }
        }
        
        
        
        return new EJDevBlockRendererDefinitionControl(blockDisplayProperties, Collections.<EJDevItemRendererDefinitionControl> emptyList())
        {
            
            @Override
            public void addItemWidgetChosenListener(EJDevItemWidgetChosenListener listener)
            {
                if (listener != null)
                {
                    _itemWidgetListenerList.add(listener);
                }
            }

            @Override
            public void removeItemWidgetChosenListener(EJDevItemWidgetChosenListener listener)
            {
                if (listener != null)
                {
                    _itemWidgetListenerList.remove(listener);
                }
            }
        };
    }

    
    protected int getComponentStyle(String alignmentProperty)
    {
        if (alignmentProperty != null && alignmentProperty.trim().length() > 0)
        {

            if (alignmentProperty.equals(EJTMTMultiRecordBlockDefinitionProperties.COLUMN_ALLIGN_LEFT))
            {
                return SWT.LEFT;
            }
            if (alignmentProperty.equals(EJTMTMultiRecordBlockDefinitionProperties.COLUMN_ALLIGN_RIGHT))
            {
                return SWT.RIGHT;
            }
            if (alignmentProperty.equals(EJTMTMultiRecordBlockDefinitionProperties.COLUMN_ALLIGN_CENTER))
            {
                return SWT.CENTER;
            }
            if (alignmentProperty.equals(EJTMTMultiRecordBlockDefinitionProperties.COLUMN_ALLIGN_TOP))
            {
                return SWT.TOP;
            }
            if (alignmentProperty.equals(EJTMTMultiRecordBlockDefinitionProperties.COLUMN_ALLIGN_BOTTOM))
            {
                return SWT.BOTTOM;
            }
        }
        return SWT.NONE;
    }
   
    
    @Override
    public EJDevItemRendererDefinitionControl getSpacerItemControl(EJDevScreenItemDisplayProperties itemProperties, Composite parent, FormToolkit toolkit)
    {
        return null;
    }

    @Override
    public EJPropertyDefinitionGroup getItemGroupPropertiesDefinitionGroup()
    {
        // TODO Auto-generated method stub
        return null;
    }

}
