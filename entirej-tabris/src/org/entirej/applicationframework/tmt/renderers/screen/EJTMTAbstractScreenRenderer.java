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
package org.entirej.applicationframework.tmt.renderers.screen;

import java.util.Collection;

import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.entirej.applicationframework.tmt.application.EJTMTImageRetriever;
import org.entirej.applicationframework.tmt.layout.EJTMTEntireJGridPane;
import org.entirej.applicationframework.tmt.renderer.interfaces.EJTMTAppItemRenderer;
import org.entirej.applicationframework.tmt.renderers.blocks.definition.interfaces.EJTMTSingleRecordBlockDefinitionProperties;
import org.entirej.framework.core.enumerations.EJScreenType;
import org.entirej.framework.core.interfaces.EJScreenItemController;
import org.entirej.framework.core.internal.EJInternalBlock;
import org.entirej.framework.core.properties.EJCoreProperties;
import org.entirej.framework.core.properties.EJCoreVisualAttributeProperties;
import org.entirej.framework.core.properties.containers.interfaces.EJItemGroupPropertiesContainer;
import org.entirej.framework.core.properties.definitions.interfaces.EJFrameworkExtensionProperties;
import org.entirej.framework.core.properties.interfaces.EJItemGroupProperties;
import org.entirej.framework.core.properties.interfaces.EJScreenItemProperties;
import org.entirej.framework.core.renderers.EJManagedItemRendererWrapper;
import org.entirej.framework.core.renderers.interfaces.EJItemRenderer;
import org.entirej.framework.core.renderers.interfaces.EJRenderer;
import org.entirej.framework.core.renderers.registry.EJBlockItemRendererRegister;

public abstract class EJTMTAbstractScreenRenderer implements EJRenderer
{
    private EJManagedItemRendererWrapper _firstNavigationalItem;

    protected abstract EJInternalBlock getBlock();

    public abstract EJBlockItemRendererRegister getItemRegister();

    protected abstract void registerRendererForItem(EJItemRenderer managedRenderer, EJScreenItemController item);

    protected abstract EJFrameworkExtensionProperties getItemRendererPropertiesForItem(EJScreenItemProperties item);

    protected void addAllItemGroups(EJItemGroupPropertiesContainer container, EJTMTEntireJGridPane containerPane, EJScreenType screenType)
    {
        Collection<EJItemGroupProperties> itemGroupProperties = container.getAllItemGroupProperties();
        for (EJItemGroupProperties ejItemGroupProperties : itemGroupProperties)
        {
            createItemGroup(containerPane, ejItemGroupProperties, screenType);
        }
    }

    protected void setFoucsItemRenderer()
    {
        if (_firstNavigationalItem != null)
        {
            _firstNavigationalItem.gainFocus();
        }
    }

    private void createItemGroup(Composite parent, EJItemGroupProperties groupProperties, EJScreenType screenType)
    {
        EJTMTEntireJGridPane groupPane;
        String frameTitle = groupProperties.getFrameTitle();
        boolean hasGroup = groupProperties.dispayGroupFrame() && frameTitle != null && frameTitle.length() > 0;
        if (hasGroup)
        {
            Group group = new Group(parent, SWT.NONE);
            group.setLayout(new FillLayout());
            group.setLayoutData(createItemGroupGridData(groupProperties));
            group.setText(frameTitle);

            parent = group;
            groupPane = new EJTMTEntireJGridPane(parent, groupProperties.getNumCols());
        }
        else
        {
            groupPane = new EJTMTEntireJGridPane(parent, groupProperties.getNumCols(), groupProperties.dispayGroupFrame() ? SWT.BORDER : SWT.NONE);
        }

        groupPane.getLayout().marginRight = 10;
        groupPane.getLayout().marginLeft = 10;
        groupPane.getLayout().verticalSpacing = 1;

        groupPane.setPaneName(groupProperties.getName());
        if (!hasGroup)
        {
            groupPane.setLayoutData(createItemGroupGridData(groupProperties));
        }
        Collection<EJScreenItemProperties> itemProperties = groupProperties.getAllItemProperties();

        for (EJScreenItemProperties screenItemProperties : itemProperties)
        {
            createScreenItem(groupPane, screenItemProperties, screenType);
        }

        // build sub groups
        EJItemGroupPropertiesContainer groupPropertiesContainer = groupProperties.getChildItemGroupContainer();
        Collection<EJItemGroupProperties> itemGroupProperties = groupPropertiesContainer.getAllItemGroupProperties();
        for (EJItemGroupProperties ejItemGroupProperties : itemGroupProperties)
        {
            createItemGroup(groupPane, ejItemGroupProperties, screenType);
        }
    }

    static GridData createItemGroupGridData(EJItemGroupProperties groupProperties)
    {
        GridData gridData = new GridData(GridData.FILL_BOTH);
        gridData.widthHint = groupProperties.getWidth();
        gridData.heightHint = groupProperties.getHeight();

        gridData.horizontalSpan = groupProperties.getXspan();
        gridData.verticalSpan = groupProperties.getYspan();
        gridData.grabExcessHorizontalSpace = groupProperties.canExpandHorizontally();
        gridData.grabExcessVerticalSpace = groupProperties.canExpandVertically();

        if (gridData.grabExcessHorizontalSpace)
        {
            gridData.minimumWidth = groupProperties.getWidth();
        }
        if (gridData.grabExcessVerticalSpace)
        {
            gridData.minimumHeight = groupProperties.getHeight();
        }
        
        if(groupProperties.getHorizontalAlignment()!=null)
        {
            switch (groupProperties.getHorizontalAlignment())
            {
                case CENTER:
                    gridData.horizontalAlignment = SWT.CENTER;
                    gridData.grabExcessHorizontalSpace = true;
                    break;
                case BEGINNING:
                    gridData.horizontalAlignment = SWT.BEGINNING;
                    break;
                case END:
                    gridData.horizontalAlignment = SWT.END;
                    gridData.grabExcessHorizontalSpace = true;
                    break;

                default:
                    break;
            }
        }
        if(groupProperties.getVerticalAlignment()!=null)
        {
            switch (groupProperties.getVerticalAlignment())
            {
                case CENTER:
                    gridData.verticalAlignment = SWT.CENTER;
                    gridData.grabExcessVerticalSpace = true;
                    break;
                case BEGINNING:
                    gridData.verticalAlignment = SWT.BEGINNING;
                    break;
                case END:
                    gridData.verticalAlignment = SWT.END;
                    gridData.grabExcessVerticalSpace = true;
                    break;
                    
                default:
                    break;
            }
        }

        return gridData;
    }

    private GridData createBlockItemGridData(EJTMTAppItemRenderer itemRenderer, EJFrameworkExtensionProperties blockRequiredItemProperties, Control control)
    {
        boolean grabExcessVerticalSpace = blockRequiredItemProperties.getBooleanProperty(EJTMTSingleRecordBlockDefinitionProperties.MAIN_EXPAND_Y_PROPERTY,
                false);
        boolean grabExcessHorizontalSpace = blockRequiredItemProperties.getBooleanProperty(EJTMTSingleRecordBlockDefinitionProperties.MAIN_EXPAND_X_PROPERTY,
                false);
        GridData gridData;
        if (grabExcessVerticalSpace && grabExcessHorizontalSpace)
        {
            gridData = new GridData(GridData.FILL_BOTH);
        }
        else if (!grabExcessVerticalSpace && grabExcessHorizontalSpace)
        {
            gridData = new GridData(GridData.FILL_BOTH);
        }
        else if (grabExcessVerticalSpace && !grabExcessHorizontalSpace)
        {
            gridData = new GridData(GridData.FILL_VERTICAL);
        }
        else
        {
            gridData = new GridData(GridData.FILL_VERTICAL);
        }
        gridData.horizontalSpan = blockRequiredItemProperties.getIntProperty(EJTMTSingleRecordBlockDefinitionProperties.MAIN_XSPAN_PROPERTY, 1);
        gridData.verticalSpan = blockRequiredItemProperties.getIntProperty(EJTMTSingleRecordBlockDefinitionProperties.MAIN_YSPAN_PROPERTY, 1);
        gridData.grabExcessHorizontalSpace = grabExcessHorizontalSpace;
        gridData.grabExcessVerticalSpace = grabExcessVerticalSpace;

        int displayedWidth = blockRequiredItemProperties.getIntProperty(EJTMTSingleRecordBlockDefinitionProperties.DISPLAYED_WIDTH_PROPERTY, 0);
        int displayedHeight = blockRequiredItemProperties.getIntProperty(EJTMTSingleRecordBlockDefinitionProperties.DISPLAYED_HEIGHT_PROPERTY, 0);

        if (displayedWidth > 0)
        {
            float avgCharWidth = control == null ? 1 : EJTMTImageRetriever.getAvgCharWidth(control.getFont());
            if (itemRenderer != null && itemRenderer.useFontDimensions() && avgCharWidth > 0)
            {
                // add padding
                gridData.widthHint = (int) ((displayedWidth + 1) * avgCharWidth);
            }
            else
            {
                gridData.widthHint = displayedWidth;
            }
        }
        if (displayedHeight > 0)
        {

            float avgCharHeight = control == null ? 1 : EJTMTImageRetriever.getCharHeight(control.getFont());
            if (itemRenderer != null && itemRenderer.useFontDimensions() && avgCharHeight > 0)
            {
                // add padding
                gridData.heightHint = (int) ((displayedHeight + 1) * avgCharHeight);
            }
            else
            {
                gridData.heightHint = displayedHeight;
            }
        }
        return gridData;
    }

    private GridData createBlockLableGridData(EJFrameworkExtensionProperties blockRequiredItemProperties)
    {
        GridData gridData = new GridData(GridData.FILL_HORIZONTAL);
        gridData.grabExcessHorizontalSpace = false;
        gridData.verticalSpan = blockRequiredItemProperties.getIntProperty(EJTMTSingleRecordBlockDefinitionProperties.MAIN_YSPAN_PROPERTY, 1);
        if (gridData.verticalSpan > 1
                || blockRequiredItemProperties.getBooleanProperty(EJTMTSingleRecordBlockDefinitionProperties.MAIN_EXPAND_Y_PROPERTY, false))
        {
            gridData.verticalIndent = 2;
            gridData.verticalAlignment = SWT.TOP;
        }

        return gridData;
    }

    private void labletextAliment(Label label, String labelOrientation)
    {
        if (label == null)
        {
            return;
        }
        if (EJTMTSingleRecordBlockDefinitionProperties.LABEL_ORIENTATION_LEFT_PROPERTY.equals(labelOrientation))
        {
            label.setAlignment(SWT.LEFT);
        }
        else if (EJTMTSingleRecordBlockDefinitionProperties.LABEL_ORIENTATION_RIGHT_PROPERTY.equals(labelOrientation))
        {
            label.setAlignment(SWT.RIGHT);
        }
        else if (EJTMTSingleRecordBlockDefinitionProperties.LABEL_ORIENTATION_CENTER_PROPERTY.equals(labelOrientation))
        {
            label.setAlignment(SWT.CENTER);
        }
    }

    public void createScreenItem(Composite parent, EJScreenItemProperties itemProps, EJScreenType screenType)
    {
        EJFrameworkExtensionProperties itemRendererPropertiesForItem = getItemRendererPropertiesForItem(itemProps);
        if (itemProps.isSpacerItem())
        {
            Label label = new Label(parent, SWT.NONE);
            label.setLayoutData(createBlockItemGridData(null, itemRendererPropertiesForItem, label));
            return;
        }
        EJScreenItemController item = getBlock().getScreenItem(screenType, itemProps.getReferencedItemName());
        EJManagedItemRendererWrapper renderer = item.getManagedItemRenderer();
        if (renderer != null)
        {
            registerRendererForItem(renderer.getUnmanagedRenderer(), item);
            EJFrameworkExtensionProperties blockRequiredItemProperties = itemRendererPropertiesForItem;

            String labelPosition = blockRequiredItemProperties.getStringProperty(EJTMTSingleRecordBlockDefinitionProperties.LABEL_POSITION_PROPERTY);
            String labelOrientation = blockRequiredItemProperties.getStringProperty(EJTMTSingleRecordBlockDefinitionProperties.LABEL_ORIENTATION_PROPERTY);
            String visualAttribute = blockRequiredItemProperties.getStringProperty(EJTMTSingleRecordBlockDefinitionProperties.VISUAL_ATTRIBUTE_PROPERTY);

            EJTMTAppItemRenderer itemRenderer = (EJTMTAppItemRenderer) renderer.getUnmanagedRenderer();
            boolean hasLabel = itemProps.getLabel() != null && itemProps.getLabel().trim().length() > 0;

            if (hasLabel && EJTMTSingleRecordBlockDefinitionProperties.LABEL_POSITION_LEFT_PROPERTY.equals(labelPosition))
            {
                itemRenderer.createLable(parent);
                itemRenderer.createComponent(parent);
                labletextAliment(itemRenderer.getGuiComponentLabel(), labelOrientation);
            }
            else if (hasLabel && EJTMTSingleRecordBlockDefinitionProperties.LABEL_POSITION_RIGHT_PROPERTY.equals(labelPosition))
            {
                itemRenderer.createComponent(parent);
                itemRenderer.createLable(parent);
                labletextAliment(itemRenderer.getGuiComponentLabel(), labelOrientation);
            }
            else
            {
                itemRenderer.createComponent(parent);
            }
            itemRenderer.getGuiComponent().setLayoutData(createBlockItemGridData(itemRenderer, blockRequiredItemProperties, itemRenderer.getGuiComponent()));
            if (itemRenderer.getGuiComponentLabel() != null)
            {
                itemRenderer.getGuiComponentLabel().setLayoutData(createBlockLableGridData(blockRequiredItemProperties));
            }

            if (visualAttribute != null)
            {
                EJCoreVisualAttributeProperties va = EJCoreProperties.getInstance().getVisualAttributesContainer()
                        .getVisualAttributeProperties(visualAttribute);
                if (va != null)
                {
                    itemRenderer.setInitialVisualAttribute(va);
                }
            }
            EJScreenItemProperties itemProperties = item.getProperties();

            renderer.setVisible(itemProperties.isVisible());
            renderer.setEditAllowed(itemProperties.isEditAllowed());

            // Add the item to the pane according to its display coordinates.
            renderer.setMandatory(itemProperties.isMandatory());

            renderer.enableLovActivation(itemProperties.getLovMappingName() != null);

            if (_firstNavigationalItem == null)
            {
                if (itemProperties.isVisible() && itemProperties.isEditAllowed())
                {
                    _firstNavigationalItem = renderer;
                }
            }
        }
    }
}
