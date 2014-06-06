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
package org.entirej.applicationframework.tmt.renderers.blocks;

import java.util.Collection;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.FocusEvent;
import org.eclipse.swt.events.FocusListener;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.events.KeyListener;
import org.eclipse.swt.events.MouseAdapter;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.entirej.applicationframework.tmt.application.EJTMTImageRetriever;
import org.entirej.applicationframework.tmt.layout.EJTMTEntireJGridPane;
import org.entirej.applicationframework.tmt.renderer.interfaces.EJTMTAppBlockRenderer;
import org.entirej.applicationframework.tmt.renderer.interfaces.EJTMTAppItemRenderer;
import org.entirej.applicationframework.tmt.renderers.blocks.definition.interfaces.EJTMTSingleRecordBlockDefinitionProperties;
import org.entirej.applicationframework.tmt.renderers.screen.EJTMTInsertScreenRenderer;
import org.entirej.applicationframework.tmt.renderers.screen.EJTMTQueryScreenRenderer;
import org.entirej.applicationframework.tmt.renderers.screen.EJTMTUpdateScreenRenderer;
import org.entirej.framework.core.EJForm;
import org.entirej.framework.core.EJMessage;
import org.entirej.framework.core.data.EJDataRecord;
import org.entirej.framework.core.data.controllers.EJEditableBlockController;
import org.entirej.framework.core.data.controllers.EJQuestion;
import org.entirej.framework.core.enumerations.EJManagedBlockProperty;
import org.entirej.framework.core.enumerations.EJManagedScreenProperty;
import org.entirej.framework.core.enumerations.EJQuestionButton;
import org.entirej.framework.core.enumerations.EJScreenType;
import org.entirej.framework.core.interfaces.EJScreenItemController;
import org.entirej.framework.core.internal.EJInternalEditableBlock;
import org.entirej.framework.core.properties.EJCoreMainScreenItemProperties;
import org.entirej.framework.core.properties.EJCoreProperties;
import org.entirej.framework.core.properties.EJCoreVisualAttributeProperties;
import org.entirej.framework.core.properties.containers.interfaces.EJItemGroupPropertiesContainer;
import org.entirej.framework.core.properties.definitions.interfaces.EJFrameworkExtensionProperties;
import org.entirej.framework.core.properties.interfaces.EJBlockProperties;
import org.entirej.framework.core.properties.interfaces.EJItemGroupProperties;
import org.entirej.framework.core.properties.interfaces.EJMainScreenProperties;
import org.entirej.framework.core.properties.interfaces.EJScreenItemProperties;
import org.entirej.framework.core.renderers.EJManagedItemRendererWrapper;
import org.entirej.framework.core.renderers.interfaces.EJInsertScreenRenderer;
import org.entirej.framework.core.renderers.interfaces.EJQueryScreenRenderer;
import org.entirej.framework.core.renderers.interfaces.EJUpdateScreenRenderer;
import org.entirej.framework.core.renderers.registry.EJMainScreenItemRendererRegister;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class EJTMTSingleRecordBlockRenderer implements EJTMTAppBlockRenderer, KeyListener
{
    final Logger                             logger             = LoggerFactory.getLogger(EJTMTSingleRecordBlockRenderer.class);

    private boolean                          _showFocusedBorder = false;
    private EJManagedItemRendererWrapper     _firstNavigationalItem;
    private EJEditableBlockController        _block;
    private EJMainScreenItemRendererRegister _mainItemRegister;
    private EJTMTEntireJGridPane             _mainPane;
    private boolean                          _isFocused         = false;

    private EJTMTQueryScreenRenderer         _queryScreenRenderer;
    private EJTMTInsertScreenRenderer        _insertScreenRenderer;
    private EJTMTUpdateScreenRenderer        _updateScreenRenderer;

    protected EJInternalEditableBlock getBlock()
    {
        return _block.getBlock();
    }

    protected void notifyStatus()
    {
        //TODO
    }

    @Override
    public void refreshBlockProperty(EJManagedBlockProperty managedBlockPropertyType)
    {
    }

    @Override
    public void refreshBlockRendererProperty(String propertyName)
    {
    }

    @Override
    public void executingQuery()
    {
        // no impl
    }

    @Override
    public void refreshItemProperty(String itemName, EJManagedScreenProperty managedItemPropertyType, EJDataRecord record)
    {
        EJManagedItemRendererWrapper itemRenderer = _mainItemRegister.getManagedItemRendererForItem(itemName);

        if (itemRenderer == null)
        {
            return;
        }

        switch (managedItemPropertyType)
        {
            case EDIT_ALLOWED:
                itemRenderer.setEditAllowed((itemRenderer.isReadOnly() || _block.getBlock().getProperties().isControlBlock())
                        && itemRenderer.getItem().getProperties().isEditAllowed());
                break;
            case MANDATORY:
                itemRenderer.setMandatory(itemRenderer.getItem().getProperties().isMandatory());
                break;
            case VISIBLE:
                itemRenderer.setVisible(itemRenderer.getItem().getProperties().isVisible());
                break;
            case HINT:
                itemRenderer.setHint(itemRenderer.getItem().getProperties().getHint());
                break;
            case LABEL:
                itemRenderer.setLabel(itemRenderer.getItem().getProperties().getLabel());
                break;
            case SCREEN_ITEM_VISUAL_ATTRIBUTE:
                itemRenderer.setVisualAttribute(itemRenderer.getItem().getProperties().getVisualAttributeProperties());
                break;
            case ITEM_INSTANCE_VISUAL_ATTRIBUTE:
                if (record == getFocusedRecord())
                {
                    refreshRecordInstanceVA(record);
                }
                break;
            case ITEM_INSTANCE_HINT_TEXT:
                if (record == getFocusedRecord())
                {
                    refreshRecordInstanceHintText(record);
                }
                break;
        }
    }

    @Override
    public void refreshItemRendererProperty(String itemName, String propertyName)
    {
        _mainItemRegister.getManagedItemRendererForItem(itemName).refreshItemRendererProperty(propertyName);
    }

    @Override
    public Object getGuiComponent()
    {
        return _mainPane;
    }

    @Override
    public EJQueryScreenRenderer getQueryScreenRenderer()
    {
        return _queryScreenRenderer;
    }

    @Override
    public EJInsertScreenRenderer getInsertScreenRenderer()
    {
        return _insertScreenRenderer;
    }

    @Override
    public EJUpdateScreenRenderer getUpdateScreenRenderer()
    {
        return _updateScreenRenderer;
    }

    @Override
    public void initialiseRenderer(EJEditableBlockController block)
    {
        _block = block;
        _mainItemRegister = new EJMainScreenItemRendererRegister(_block);
        _queryScreenRenderer = new EJTMTQueryScreenRenderer();
        _insertScreenRenderer = new EJTMTInsertScreenRenderer();
        _updateScreenRenderer = new EJTMTUpdateScreenRenderer();
    }

    @Override
    public boolean isCurrentRecordDirty()
    {
        return _mainItemRegister.changesMade();
    }

    @Override
    public void synchronize()
    {
        // implementing this method caused modified values to be overridden by
        // the screen values
    }

    @Override
    public void blockCleared()
    {
        logger.trace("START blockCleared");
        _mainItemRegister.clearRegisteredValues();
        logger.trace("END blockCleared");
        notifyStatus();
    }

    public void savePerformed()
    {
    }

    @Override
    public void detailBlocksCleared()
    {
    }

    @Override
    public boolean hasFocus()
    {
        return _isFocused;
    }

    @Override
    public void askToDeleteRecord(EJDataRecord recordToDelete, String msg)
    {
        if (msg == null)
        {
            msg = "Are you sure you want to delete the current record?";
        }
        EJMessage message = new EJMessage(msg);
        EJQuestion question = new EJQuestion(new EJForm(_block.getForm()), "DELETE_RECORD", "Delete", message, "Yes", "No", recordToDelete);
        _block.getForm().getMessenger().askQuestion(question);
        if (EJQuestionButton.ONE == question.getAnswer())
        {
            _block.getBlock().deleteRecord(recordToDelete);
        }
        _block.setRendererFocus(true);

    }

    @Override
    public void enterInsert(EJDataRecord record)
    {
        if (getBlock().getInsertScreenRenderer() == null)
        {
            EJMessage message = new EJMessage("Please define an Insert Screen Renderer for this form before an insert operation can be performed.");
            getBlock().getForm().getMessenger().handleMessage(message);
        }
        else
        {
            getBlock().getInsertScreenRenderer().open(record);
        }
    }

    @Override
    public void enterQuery(EJDataRecord record)
    {
        if (getBlock().getQueryScreenRenderer() == null)
        {
            EJMessage message = new EJMessage("Please define a Query Screen Renderer for this form before a query operation can be performed.");
            getBlock().getForm().getMessenger().handleMessage(message);
        }
        else
        {
            getBlock().getQueryScreenRenderer().open(record);
        }
    }

    @Override
    public void enterUpdate(EJDataRecord recordToUpdate)
    {
        if (getBlock().getUpdateScreenRenderer() == null)
        {
            EJMessage message = new EJMessage("Please define an Update Screen Renderer for this form before an update operation can be performed.");
            getBlock().getForm().getMessenger().handleMessage(message);
        }
        else
        {
            getBlock().getUpdateScreenRenderer().open(recordToUpdate);
        }
    }

    @Override
    public int getDisplayedRecordNumber(EJDataRecord record)
    {
        return _block.getDataBlock().getRecordNumber(record);
    }

    @Override
    public int getDisplayedRecordCount()
    {
        return _block.getDataBlock().getBlockRecordCount();
    }

    @Override
    public EJDataRecord getRecordAt(int displayedRecordNumber)
    {
        if (displayedRecordNumber > -1 && displayedRecordNumber < getDisplayedRecordCount())
        {

            return _block.getRecord(displayedRecordNumber);
        }
        return null;
    }

    @Override
    public EJDataRecord getRecordAfter(EJDataRecord record)
    {
        return _block.getDataBlock().getRecordAfter(record);
    }

    @Override
    public EJDataRecord getRecordBefore(EJDataRecord record)
    {
        return _block.getDataBlock().getRecordBefore(record);
    }

    @Override
    public EJDataRecord getFirstRecord()
    {
        return _block.getDataBlock().getRecord(0);
    }

    @Override
    public EJDataRecord getLastRecord()
    {
        return _block.getDataBlock().getRecord(_block.getBlockRecordCount() - 1);
    }

    @Override
    public EJDataRecord getFocusedRecord()
    {
        return _mainItemRegister.getRegisteredRecord();
    }

    @Override
    public void queryExecuted()
    {
        if (getFocusedRecord() == null)
        {
            _mainItemRegister.register(getFirstRecord());
        }
        notifyStatus();
    }

    @Override
    public void recordDeleted(int dataBlockRecordNumber)
    {
        EJDataRecord recordAt = getRecordAt(dataBlockRecordNumber > 1 ? dataBlockRecordNumber - 2 : 0);

        if (recordAt == null)

        {
            recordAt = getLastRecord();
        }
        recordSelected(recordAt);
    }

    @Override
    public void recordInserted(EJDataRecord record)
    {
        if (record != null)
        {
            logger.trace("START recordInserted");
            _mainItemRegister.register(record);
            logger.trace("END recordInserted");
        }
        notifyStatus();
    }

    @Override
    public void refreshAfterChange(EJDataRecord record)
    {
        logger.trace("START refreshAfterChange");
        _mainItemRegister.refreshAfterChange(record);
        logger.trace("END recordUpdated");
    }

    @Override
    public void recordSelected(EJDataRecord record)
    {
        if (record != null)
        {
            logger.trace("START recordSelected");
            synchronize();

            _mainItemRegister.register(record);
            logger.trace("END recordSelected");
        }
        notifyStatus();
    }

    private void refreshRecordInstanceVA(EJDataRecord record)
    {
        for (EJManagedItemRendererWrapper wrapper : _mainItemRegister.getRegisteredRenderers())
        {
            // The screen item visual attribute has priority over the record
            // instance va
            if (record.containsItem(wrapper.getRegisteredItemName()) && wrapper.getItem().getProperties().getVisualAttributeProperties() == null)
            {
                if (record.getItem(wrapper.getRegisteredItemName()).getVisualAttribute() != null)
                {
                    wrapper.setVisualAttribute(record.getItem(wrapper.getRegisteredItemName()).getVisualAttribute());
                }
                else
                {
                    if (wrapper.getVisualAttributeProperties() != null)
                    {
                        wrapper.setVisualAttribute(null);
                    }
                }
            }
        }
    }

    private void refreshRecordInstanceHintText(EJDataRecord record)
    {
        for (EJManagedItemRendererWrapper wrapper : _mainItemRegister.getRegisteredRenderers())
        {
            if (record.containsItem(wrapper.getRegisteredItemName()))
            {
                if (record.getItem(wrapper.getRegisteredItemName()).getHint() != null)
                {
                    wrapper.setHint(record.getItem(wrapper.getRegisteredItemName()).getHint());
                }
                else
                {
                    EJScreenItemController screenItem = record.getBlock().getScreenItem(EJScreenType.MAIN, wrapper.getRegisteredItemName());
                    if (screenItem != null)
                    {
                        wrapper.setHint(screenItem.getProperties().getHint());
                    }
                }
            }
        }
    }

    @Override
    public void gainFocus()
    {
        logger.trace("START gainFocus");
        if (_firstNavigationalItem != null)
        {
            _firstNavigationalItem.gainFocus();
        }
        else
        {
            _mainPane.forceFocus();
        }
        setHasFocus(true);
        logger.trace("END gainFocus");

    }

    @Override
    public void setHasFocus(boolean focus)
    {
        logger.trace("START setHasFocus. Focus: {}", focus);
        _isFocused = focus;
        if (_isFocused)
        {
            showFocusedBorder(true);
            _block.focusGained();
        }
        else
        {
            showFocusedBorder(false);
            _block.focusLost();
        }
        logger.trace("END hasFocus");
        notifyStatus();
    }

    /**
     * Enables a red border around this controller. This will indicate that the
     * container held by this controller has cursor focus.
     * 
     * @param focused
     *            If <code>true</code> is passed then the border will be
     *            displayed, if <code>false</code> is passed then no border will
     *            be shown.
     */
    private void showFocusedBorder(boolean focused)
    {
    }

    @Override
    public void setFocusToItem(EJScreenItemController item)
    {
        if (item == null)
        {
            return;
        }

        logger.trace("START setFocusToItem. Item: {}", item.getName());

        EJManagedItemRendererWrapper renderer = _mainItemRegister.getManagedItemRendererForItem(item.getProperties().getReferencedItemName());
        if (renderer != null)
        {
            renderer.gainFocus();
        }
        logger.trace("END setFocusToItem");
    }

    protected void setShowFocusedBorder(boolean show)
    {
        _showFocusedBorder = show;
    }

    protected EJMainScreenItemRendererRegister getMainItemRegister()
    {
        return _mainItemRegister;
    }

    protected void setFirstNavigationalItem(EJManagedItemRendererWrapper firstNavigationalItem)
    {
        _firstNavigationalItem = firstNavigationalItem;
    }

    @Override
    public void buildGuiComponent(EJTMTEntireJGridPane blockCanvas)
    {

        EJBlockProperties blockProperties = _block.getProperties();
        EJMainScreenProperties mainScreenProperties = blockProperties.getMainScreenProperties();

        EJFrameworkExtensionProperties brendererProperties = blockProperties.getBlockRendererProperties();

        GridData gridData = new GridData(GridData.FILL_BOTH);
        gridData.widthHint = mainScreenProperties.getWidth();
        gridData.heightHint = mainScreenProperties.getHeight();

        gridData.horizontalSpan = mainScreenProperties.getHorizontalSpan();
        gridData.verticalSpan = mainScreenProperties.getVerticalSpan();
        gridData.grabExcessHorizontalSpace = mainScreenProperties.canExpandHorizontally();
        gridData.grabExcessVerticalSpace = mainScreenProperties.canExpandVertically();

        if (gridData.grabExcessHorizontalSpace)
        {
            gridData.minimumWidth = mainScreenProperties.getWidth();
        }
        if (gridData.grabExcessVerticalSpace)
        {
            gridData.minimumHeight = mainScreenProperties.getHeight();
        }
        blockCanvas.setLayoutData(gridData);

        EJFrameworkExtensionProperties sectionProperties = null;
        if (brendererProperties != null)
        {
            sectionProperties = brendererProperties.getPropertyGroup(EJTMTSingleRecordBlockDefinitionProperties.ITEM_GROUP_TITLE_BAR);
        }
 
        
            if (mainScreenProperties.getDisplayFrame())
            {
                Group group = new Group(blockCanvas, SWT.NONE);
                group.setLayout(new FillLayout());
                group.setLayoutData(gridData);
                String frameTitle = mainScreenProperties.getFrameTitle();
                if (frameTitle != null && frameTitle.length() > 0)
                {
                    group.setText(frameTitle);
                }
                _mainPane = new EJTMTEntireJGridPane(group, mainScreenProperties.getNumCols());
            }
            else
            {
                _mainPane = new EJTMTEntireJGridPane(blockCanvas, mainScreenProperties.getNumCols());
                _mainPane.setLayoutData(gridData);
            }
        


        hookFocusListener(_mainPane);
        _mainPane.cleanLayout();
        EJDataRecord registeredRecord = _mainItemRegister.getRegisteredRecord();
        _mainItemRegister.resetRegister();

        EJItemGroupPropertiesContainer container = blockProperties.getScreenItemGroupContainer(EJScreenType.MAIN);
        Collection<EJItemGroupProperties> itemGroupProperties = container.getAllItemGroupProperties();
        for (EJItemGroupProperties ejItemGroupProperties : itemGroupProperties)
        {
            createItemGroup(_mainPane, ejItemGroupProperties);
        }
        
        _mainItemRegister.clearRegisteredValues();
        if(registeredRecord ==null)
        {
            registeredRecord = getFirstRecord();
        }
        if(registeredRecord!=null)
        {

            _mainItemRegister.register(registeredRecord);
        }
        _mainPane.addMouseListener(new MouseAdapter()
        {
            @Override
            public void mouseDown(MouseEvent arg0)
            {
                setHasFocus(true);
            }
        });
    }

    

    private void createItemGroup(Composite parent, EJItemGroupProperties groupProperties)
    {
        EJTMTEntireJGridPane groupPane;
        String frameTitle = groupProperties.getFrameTitle();
        boolean hasGroup = groupProperties.dispayGroupFrame() && frameTitle != null && frameTitle.length() > 0;
        if (hasGroup)
        {

            EJFrameworkExtensionProperties rendererProperties = groupProperties.getRendererProperties();

                Group group = new Group(parent, SWT.NONE);
                group.setLayout(new FillLayout());
                group.setLayoutData(createItemGroupGridData(groupProperties));
                group.setText(frameTitle);
                parent = group;
                group.addMouseListener(new MouseAdapter()
                {
                    @Override
                    public void mouseDown(MouseEvent arg0)
                    {
                        setHasFocus(true);
                    }
                });
                groupPane = new EJTMTEntireJGridPane(parent, groupProperties.getNumCols());
                groupPane.getLayout().marginRight = 5;
                groupPane.getLayout().marginLeft = 5;
            
        }
        else
        {
            groupPane = new EJTMTEntireJGridPane(parent, groupProperties.getNumCols(), groupProperties.dispayGroupFrame() ? SWT.BORDER : SWT.NONE);
        }

        groupPane.getLayout().verticalSpacing = 1;
        groupPane.addMouseListener(new MouseAdapter()
        {
            @Override
            public void mouseDown(MouseEvent arg0)
            {
                setHasFocus(true);
            }
        });

        groupPane.setPaneName(groupProperties.getName());
        if (!hasGroup)
        {
            groupPane.setLayoutData(createItemGroupGridData(groupProperties));
        }
        // items adding
        Collection<EJScreenItemProperties> itemProperties = groupProperties.getAllItemProperties();
        for (EJScreenItemProperties screenItemProperties : itemProperties)
        {
            createScreenItem(groupPane, (EJCoreMainScreenItemProperties) screenItemProperties);
        }

        // build sub groups
        EJItemGroupPropertiesContainer groupPropertiesContainer = groupProperties.getChildItemGroupContainer();
        Collection<EJItemGroupProperties> itemGroupProperties = groupPropertiesContainer.getAllItemGroupProperties();
        for (EJItemGroupProperties ejItemGroupProperties : itemGroupProperties)
        {
            createItemGroup(groupPane, ejItemGroupProperties);
        }
    }

    static GridData createItemGroupGridData(EJItemGroupProperties groupProperties)
    {
        GridData gridData = new GridData(GridData.FILL_BOTH);
        if (groupProperties.getWidth() > 0)
        {
            gridData.widthHint = groupProperties.getWidth();
        }
        if (groupProperties.getHeight() > 0)
        {
            gridData.heightHint = groupProperties.getHeight();
        }
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

    public void createScreenItem(Composite parent, EJCoreMainScreenItemProperties itemProps)
    {
        if (itemProps.isSpacerItem())
        {
            Label label = new Label(parent, SWT.NONE);
            label.setLayoutData(createBlockItemGridData(null, itemProps.getBlockRendererRequiredProperties(), label));
            return;
        }
        EJScreenItemController item = _block.getScreenItem(EJScreenType.MAIN, itemProps.getReferencedItemName());
        EJManagedItemRendererWrapper renderer = item.getManagedItemRenderer();
        if (renderer != null)
        {
            _mainItemRegister.registerRendererForItem(renderer.getUnmanagedRenderer(), item);
            EJFrameworkExtensionProperties blockRequiredItemProperties = itemProps.getBlockRendererRequiredProperties();

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

            hookFocusListener(itemRenderer.getGuiComponent());

            EJScreenItemProperties itemProperties = item.getProperties();

            renderer.setVisible(itemProperties.isVisible());
            renderer.setEditAllowed((itemRenderer.isReadOnly() || _block.getBlock().getProperties().isControlBlock()) && itemProperties.isEditAllowed());

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

    @Override
    public void keyPressed(KeyEvent arg0)
    {
        // ignore
    }

    @Override
    public void keyReleased(KeyEvent arg0)
    {
        
       
        
    }

   

    private void hookFocusListener(final Control control)
    {
        control.addFocusListener(new FocusListener()
        {
            @Override
            public void focusLost(FocusEvent arg0)
            {
                logger.trace("START focusLost");
                setHasFocus(false);
                logger.trace("END focusLost");
            }

            @Override
            public void focusGained(FocusEvent arg0)
            {
                logger.trace("START focusGained");
                setHasFocus(true);
                logger.trace("END focusGained");
            }
        });
    }
}
