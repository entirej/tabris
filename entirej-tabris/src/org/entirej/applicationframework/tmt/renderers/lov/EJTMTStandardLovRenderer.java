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
package org.entirej.applicationframework.tmt.renderers.lov;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.eclipse.jface.viewers.ColumnLabelProvider;
import org.eclipse.jface.viewers.ColumnViewerToolTipSupport;
import org.eclipse.jface.viewers.DoubleClickEvent;
import org.eclipse.jface.viewers.IDoubleClickListener;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.TableViewerColumn;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.rap.rwt.RWT;
import org.eclipse.rap.rwt.template.Cell;
import org.eclipse.rap.rwt.template.ImageCell;
import org.eclipse.rap.rwt.template.Template;
import org.eclipse.rap.rwt.template.ImageCell.ScaleMode;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.entirej.applicationframework.tmt.application.EJTMTApplicationManager;
import org.entirej.applicationframework.tmt.application.launcher.EJTMTContext;
import org.entirej.applicationframework.tmt.pages.EJTMTScreenPage;
import org.entirej.applicationframework.tmt.renderer.interfaces.EJTMTAppItemRenderer;
import org.entirej.applicationframework.tmt.renderers.blocks.definition.interfaces.EJTMTMultiRecordBlockDefinitionProperties;
import org.entirej.applicationframework.tmt.table.EJTMTAbstractFilteredTable;
import org.entirej.applicationframework.tmt.table.EJTMTAbstractFilteredTable.FilteredContentProvider;
import org.entirej.applicationframework.tmt.table.EJTMTTableViewerColumnFactory;
import org.entirej.framework.core.EJFrameworkManager;
import org.entirej.framework.core.data.EJDataRecord;
import org.entirej.framework.core.data.controllers.EJBlockController;
import org.entirej.framework.core.data.controllers.EJItemLovController;
import org.entirej.framework.core.data.controllers.EJLovController;
import org.entirej.framework.core.enumerations.EJLovDisplayReason;
import org.entirej.framework.core.enumerations.EJManagedScreenProperty;
import org.entirej.framework.core.enumerations.EJScreenType;
import org.entirej.framework.core.interfaces.EJScreenItemController;
import org.entirej.framework.core.internal.EJInternalBlock;
import org.entirej.framework.core.properties.EJCoreMainScreenItemProperties;
import org.entirej.framework.core.properties.EJCoreProperties;
import org.entirej.framework.core.properties.EJCoreVisualAttributeProperties;
import org.entirej.framework.core.properties.definitions.interfaces.EJFrameworkExtensionProperties;
import org.entirej.framework.core.properties.interfaces.EJItemGroupProperties;
import org.entirej.framework.core.properties.interfaces.EJScreenItemProperties;
import org.entirej.framework.core.renderers.EJManagedItemRendererWrapper;
import org.entirej.framework.core.renderers.interfaces.EJLovRenderer;
import org.entirej.framework.core.renderers.interfaces.EJQueryScreenRenderer;

import com.eclipsesource.tabris.ui.PageConfiguration;
import com.eclipsesource.tabris.ui.PageData;
import com.eclipsesource.tabris.ui.PageStyle;
import com.eclipsesource.tabris.ui.UI;
import com.eclipsesource.tabris.ui.UIConfiguration;

public class EJTMTStandardLovRenderer implements EJLovRenderer
{
    final int                       OK_ACTION_COMMAND     = 1;
    final int                       CANCEL_ACTION_COMMAND = 2;

    private EJItemLovController     _itemToValidate;
    private EJLovDisplayReason      _displayReason;
    private EJLovController         _lovController;
    private TableViewer             _tableViewer;
    private boolean                 _validate             = true;

    private EJFrameworkManager      _frameworkManager;
    private EJTMTScreenPage.Context _dialog;

    private EJInternalBlock         _block;
    private FilteredContentProvider _filteredContentProvider;
    private List<EJDataRecord>      _tableBaseRecords     = new ArrayList<EJDataRecord>();

    @Override
    public Object getGuiComponent()
    {
        return _dialog;
    }

    protected EJLovController getLovController()
    {
        return _lovController;
    }

    @Override
    public EJQueryScreenRenderer getQueryScreenRenderer()
    {
        return null;
    }

    @Override
    public void refreshLovRendererProperty(String propertyName)
    {
    }

    @Override
    public void refreshItemProperty(String itemName, EJManagedScreenProperty managedItemPropertyType, EJDataRecord record)
    {
    }

    @Override
    public void refreshItemRendererProperty(String itemName, String propertyName)
    {
    }

    @Override
    public void synchronize()
    {
    }

    @Override
    public void initialiseRenderer(EJLovController lovController)
    {
        this._lovController = lovController;
        _frameworkManager = _lovController.getFrameworkManager();
        _block = _lovController.getBlock();

    }

    protected Control createToolbar(Composite parent)
    {
        return null;
    }

    private String toPageID(String name)
    {
        return String.format("EJFLOV_%s", name);
    }

    protected void buildGui()
    {
        int width = _lovController.getDefinitionProperties().getWidth();
        int height = _lovController.getDefinitionProperties().getHeight();

        _dialog = new EJTMTScreenPage.Context()
        {
            private static final long serialVersionUID = -4685316941898120169L;

            @Override
            public void close()
            {
                _tableViewer = null;
                _dialog = null;
                final UIConfiguration configuration = EJTMTContext.getUiConfiguration();
                final UI ui = EJTMTContext.getTabrisUI();
                String pageID = toPageID(_block.getProperties().getName());
                if (pageID.equals(ui.getPageOperator().getCurrentPageId()))
                {
                    ui.getPageOperator().closeCurrentPage();
                }
                // switch to page page and close;
                PageConfiguration pageConfiguration = configuration.getPageConfiguration(pageID);
                if (pageConfiguration != null)
                {
                    // configuration.removePageConfiguration(pageID);
                }
            }

            @Override
            public void createBody(Composite parent)
            {
                GridLayout layout = new GridLayout();
                layout.marginWidth = 0;
                // layout.horizontalSpacing = 0;
                layout.marginLeft = 0;
                layout.marginRight = 0;
                layout.marginHeight = 0;
                // layout.verticalSpacing = 0;
                layout.marginBottom = 0;
                layout.marginTop = 0;
                parent.setLayout(layout);
                EJFrameworkExtensionProperties rendererProp = _lovController.getDefinitionProperties().getLovRendererProperties();
                int style = SWT.VIRTUAL | SWT.H_SCROLL | SWT.V_SCROLL;

               
                    style = style | SWT.FULL_SELECTION;
                
              

                final EJTMTAbstractFilteredTable filterTree;
                Table table;

                filterTree = new EJTMTAbstractFilteredTable(parent, style)
                {
                    @Override
                    public void filter(String filter)
                    {
                        if (_filteredContentProvider != null
                                && (filter == null && _filteredContentProvider.getFilter() != null || !filter.equals(_filteredContentProvider.getFilter())))
                        {
                            _filteredContentProvider.setFilter(filter);
                            getViewer().setInput(filter);
                        }
                    }

                    @Override
                    protected boolean doCreateCustomComponents(Composite parent)
                    {
                        return createToolbar(parent) != null;
                    }

                    @Override
                    protected TableViewer doCreateTableViewer(Composite parent, int style)
                    {

                        _tableViewer = new TableViewer(new Table(parent, style));
                        return _tableViewer;
                    }
                };
                table = (_tableViewer = filterTree.getViewer()).getTable();

                table.setLayoutData(new GridData(GridData.FILL_BOTH | GridData.GRAB_HORIZONTAL | GridData.GRAB_VERTICAL));
                table.setLinesVisible(false);
                table.setHeaderVisible(false);

                EJTMTTableViewerColumnFactory factory = new EJTMTTableViewerColumnFactory(_tableViewer);
                ColumnViewerToolTipSupport.enableFor(_tableViewer);
                Collection<EJItemGroupProperties> allItemGroupProperties = _block.getProperties().getScreenItemGroupContainer(EJScreenType.MAIN)
                        .getAllItemGroupProperties();
                final List<ColumnLabelProvider> nodeTextProviders = new ArrayList<ColumnLabelProvider>();

                Template template = new Template();

                int colIndex = 0;
                for (EJItemGroupProperties groupProperties : allItemGroupProperties)
                {
                    Collection<EJScreenItemProperties> itemProperties = groupProperties.getAllItemProperties();
                    for (EJScreenItemProperties screenItemProperties : itemProperties)
                    {
                        EJCoreMainScreenItemProperties mainScreenItemProperties = (EJCoreMainScreenItemProperties) screenItemProperties;
                        ColumnLabelProvider screenItem = createScreenItem(factory, mainScreenItemProperties, template, colIndex);
                        if (screenItem != null)
                        {
                            nodeTextProviders.add(screenItem);
                            colIndex++;
                        }
                    }
                }

                int rowheight = rendererProp.getIntProperty(EJTMTMultiRecordBlockDefinitionProperties.ROW_HEIGHT, Math.max((colIndex + 1) * 20, 60));
                if (rowheight > 0)
                {
                    table.setData(RWT.CUSTOM_ITEM_HEIGHT, rowheight);
                }
                table.setData(RWT.ROW_TEMPLATE, template);

                final EJBlockController blockController = _block.getBlockController();

                _tableViewer.setContentProvider(_filteredContentProvider = new FilteredContentProvider()
                {

                    private static final long serialVersionUID = 7262009393527533868L;

                    boolean matchItem(EJDataRecord rec)
                    {
                        if (filter != null && filter.trim().length() > 0)
                        {
                            for (ColumnLabelProvider filterTextProvider : nodeTextProviders)
                            {
                                String text = filterTextProvider.getText(rec);
                                if (text != null && text.toLowerCase().contains(filter.toLowerCase()))
                                {
                                    return true;
                                }
                            }
                        }

                        return false;
                    }

                    @Override
                    public void inputChanged(Viewer arg0, Object arg1, Object arg2)
                    {
                        _tableBaseRecords.clear();

                        if (arg2 != null && arg2.equals(filter) && filter.trim().length() > 0)
                        {
                            // filter

                            for (EJDataRecord record : blockController.getBlock().getRecords())
                            {
                                if (matchItem(record))
                                {
                                    _tableBaseRecords.add(record);
                                }
                            }
                        }
                        else
                        {
                            filter = null;
                            if (filterTree != null)
                            {
                                filterTree.clearText();
                            }
                            _tableBaseRecords.addAll(blockController.getBlock().getRecords());
                        }
                    }

                    @Override
                    public void dispose()
                    {
                    }

                    @Override
                    public Object[] getElements(Object arg0)
                    {
                        return _tableBaseRecords.toArray();
                    }
                });
                _tableViewer.addDoubleClickListener(new IDoubleClickListener()
                {

                    @Override
                    public void doubleClick(DoubleClickEvent arg0)
                    {
                        buttonPressed(OK_ACTION_COMMAND);
                    }
                });
                _tableViewer.addSelectionChangedListener(new ISelectionChangedListener()
                {

                    @Override
                    public void selectionChanged(SelectionChangedEvent arg0)
                    {
                        if (!_validate)
                        {
                            return;
                        }

                        _validate = false;

                        try
                        {
                            EJDataRecord record = getFocusedRecord();
                            if (_lovController.getFocusedRecord() == null || _lovController.getFocusedRecord() != record)
                            {
                                _lovController.newRecordInstance(record);
                            }
                        }
                        finally
                        {
                            _validate = true;
                        }

                    }
                });

            }

            @Override
            protected void createButtonsForButtonBar(Composite parent)
            {

                page.createButton(parent, OK_ACTION_COMMAND, "OK", true);
                page.createButton(parent, CANCEL_ACTION_COMMAND, "Cancel", false);
            }

            @Override
            public void canceled()
            {
                _lovController.lovCompleted(_itemToValidate, null);
            }

            @Override
            public void buttonPressed(int buttonId)
            {
                switch (buttonId)
                {
                    case OK_ACTION_COMMAND:
                    {
                        _lovController.lovCompleted(_itemToValidate, _lovController.getFocusedRecord());
                        if (_dialog != null)
                        {
                            _dialog.close();
                        }
                        break;
                    }
                    case CANCEL_ACTION_COMMAND:
                    {
                        _lovController.lovCompleted(_itemToValidate, null);
                        if (_dialog != null)
                        {
                            _dialog.close();
                        }
                        break;
                    }

                    default:
                        _lovController.lovCompleted(_itemToValidate, null);

                        break;
                }
                super.buttonPressed(buttonId);

            }
        };

    }

    @Override
    public EJLovDisplayReason getDisplayReason()
    {
        return _displayReason;
    }

    @Override
    public EJDataRecord getFocusedRecord()
    {
        EJDataRecord _focusedRecord = null;

        if (_tableViewer != null && !_tableViewer.getTable().isDisposed())
        {
            ISelection selection = _tableViewer.getSelection();
            if (selection instanceof IStructuredSelection)
            {
                IStructuredSelection structuredSelection = (IStructuredSelection) selection;
                Object firstElement = structuredSelection.getFirstElement();
                if (firstElement instanceof EJDataRecord)
                {
                    _focusedRecord = (EJDataRecord) firstElement;
                }
            }
        }
        return _focusedRecord;
    }

    @Override
    public void enterQuery(EJDataRecord record)
    {
        // No user query is permitted on this standard lov
    }

    @Override
    public void blockCleared()
    {
        if (_tableViewer != null && !_tableViewer.getTable().isDisposed())
        {
            _tableViewer.setInput(new Object());
        }

    }

    EJTMTApplicationManager getTMTManager()
    {
        return (EJTMTApplicationManager) _frameworkManager.getApplicationManager();
    }

    @Override
    public void displayLov(EJItemLovController itemToValidate, EJLovDisplayReason displayReason)
    {
        _itemToValidate = itemToValidate;
        _displayReason = displayReason;
        buildGui();
        String title = null;
        if (_itemToValidate.getLovMappingProperties().getLovDisplayName() != null)
        {
            title = _itemToValidate.getLovMappingProperties().getLovDisplayName();
        }
        String pageID = toPageID(_block.getProperties().getName());
        final UI ui = EJTMTContext.getTabrisUI();
        final UIConfiguration configuration = EJTMTContext.getUiConfiguration();

        if (configuration.getPageConfiguration(pageID) == null)
        {
            PageConfiguration pageConfiguration = new PageConfiguration(pageID, EJTMTScreenPage.class).setTitle(title != null ? title : "");
            pageConfiguration.setStyle(PageStyle.DEFAULT);
            configuration.addPageConfiguration(pageConfiguration);

        }
        PageData pageData = EJTMTScreenPage.createPageData(_dialog);
        ui.getPageOperator().openPage(pageID, pageData);

        _dialog.getPage().setButtonEnable(OK_ACTION_COMMAND, _itemToValidate.getManagedLovItemRenderer().isEditAllowed());

        if (_tableViewer != null && !_tableViewer.getTable().isDisposed())
        {
            _tableViewer.getTable().forceFocus();
        }

        _tableViewer.setInput(new Object());
        // selectRow(0);
    }

    @Override
    public void refreshAfterChange(EJDataRecord record)
    {
        if (_tableViewer != null && !_tableViewer.getTable().isDisposed())
        {
            _tableViewer.refresh(record);
        }
    }

    @Override
    public void recordSelected(EJDataRecord record)
    {
        if (_tableViewer != null && !_tableViewer.getTable().isDisposed())
        {
            _tableViewer.setSelection(record != null ? new StructuredSelection(record) : new StructuredSelection(), true);
        }
    }

    @Override
    public int getDisplayedRecordCount()
    {
        return _tableBaseRecords.size();
    }

    @Override
    public int getDisplayedRecordNumber(EJDataRecord record)
    {
        return _tableBaseRecords.indexOf(record);
    }

    @Override
    public EJDataRecord getFirstRecord()
    {
        return getRecordAt(0);
    }

    @Override
    public EJDataRecord getLastRecord()
    {
        return getRecordAt(getDisplayedRecordCount() - 1);
    }

    @Override
    public EJDataRecord getRecordAt(int displayedRecordNumber)
    {

        if (displayedRecordNumber > -1 && displayedRecordNumber < getDisplayedRecordCount())
        {

            return _tableBaseRecords.get(displayedRecordNumber);
        }

        return null;
    }

    @Override
    public EJDataRecord getRecordAfter(EJDataRecord record)
    {
        int viewIndex = getDisplayedRecordNumber(record);
        if (-1 < viewIndex)
        {
            return getRecordAt(viewIndex + 1);
        }
        return null;
    }

    @Override
    public EJDataRecord getRecordBefore(EJDataRecord record)
    {
        int viewIndex = getDisplayedRecordNumber(record);
        if (-1 < viewIndex)
        {
            return getRecordAt(viewIndex - 1);
        }
        return null;
    }

    public void selectRow(int selectedRow)
    {

        if (_tableViewer != null && !_tableViewer.getTable().isDisposed() && getDisplayedRecordCount() > selectedRow)
        {
            _tableViewer.setSelection(new StructuredSelection(getRecordAt(selectedRow)), true);
        }
    }

    @Override
    public void executingQuery()
    {
        // TODO Auto-generated method stub
    }

    protected void clearFilter()
    {
        if (_filteredContentProvider != null)
        {
            _filteredContentProvider.setFilter(null);
        }
    }

    @Override
    public void queryExecuted()
    {
        try
        {
            _validate = false;
            if (_tableViewer != null && !_tableViewer.getTable().isDisposed())
            {
                clearFilter();
                _tableViewer.setInput(new Object());

            }
            // selectRow(0);

        }
        finally
        {
            _validate = true;
        }
    }

    public ColumnLabelProvider createScreenItem(EJTMTTableViewerColumnFactory factory, EJCoreMainScreenItemProperties itemProps, Template template, int col)
    {
        if (itemProps.isSpacerItem())
        {

            return null;
        }
        EJScreenItemController item = _block.getScreenItem(EJScreenType.MAIN, itemProps.getReferencedItemName());
        EJManagedItemRendererWrapper renderer = item.getManagedItemRenderer();
        if (renderer != null)
        {
            EJFrameworkExtensionProperties blockProperties = itemProps.getLovRendererRequiredProperties();

            EJTMTAppItemRenderer itemRenderer = (EJTMTAppItemRenderer) renderer.getUnmanagedRenderer();
            ColumnLabelProvider labelProvider = itemRenderer.createColumnLabelProvider(itemProps, item);
            if (labelProvider != null)
            {

                Cell<? extends Cell<?>> columnCell = itemRenderer.createColumnCell(itemProps, item, template);
                if (columnCell == null)
                {
                    return null;
                }
                columnCell.setBindingIndex(col);

                int width = blockProperties.getIntProperty(EJTMTMultiRecordBlockDefinitionProperties.WIDTH_PROPERTY, 0);
                int height = blockProperties.getIntProperty(EJTMTMultiRecordBlockDefinitionProperties.HEIGHT_PROPERTY, 0);
                int top = blockProperties.getIntProperty(EJTMTMultiRecordBlockDefinitionProperties.CELL_TOP, -1);
                int left = blockProperties.getIntProperty(EJTMTMultiRecordBlockDefinitionProperties.CELL_LEFT, -1);
                int right = blockProperties.getIntProperty(EJTMTMultiRecordBlockDefinitionProperties.CELL_RIGHT, -1);
                int bottom = blockProperties.getIntProperty(EJTMTMultiRecordBlockDefinitionProperties.CELL_BOTTOM, -1);

                if(columnCell instanceof ImageCell)
                {
                    if(width>0 & height>0)
                    {
                        ((ImageCell)columnCell).setScaleMode(ScaleMode.FILL);
                    }
                }
               
                
                String visualAttribute = blockProperties.getStringProperty(EJTMTMultiRecordBlockDefinitionProperties.VISUAL_ATTRIBUTE_PROPERTY);

                if (visualAttribute != null)
                {
                    EJCoreVisualAttributeProperties va = EJCoreProperties.getInstance().getVisualAttributesContainer()
                            .getVisualAttributeProperties(visualAttribute);
                    if (va != null)
                    {
                        itemRenderer.setInitialVisualAttribute(va);
                    }
                }
                // create dummy column
                TableViewerColumn viewerColumn = factory.createColumn(itemProps.getLabel(), 5, labelProvider, SWT.LEFT);
                TableColumn column = viewerColumn.getColumn();
                column.setData("KEY", itemProps.getReferencedItemName());
                column.setToolTipText(itemProps.getHint());

                if (top > -1)
                {
                    columnCell.setTop(top);
                }
                if (left > -1)
                {
                    columnCell.setLeft(left);
                }
                if (right > -1)
                {
                    columnCell.setRight(right);
                }
                if (bottom > -1)
                {
                    columnCell.setBottom(bottom);
                }

                if (left <= -1 && right <= -1)
                {
                    if(EJTMTMultiRecordBlockDefinitionProperties.COLUMN_ALLIGN_BOTTOM.equals(blockProperties
                            .getStringProperty(EJTMTMultiRecordBlockDefinitionProperties.CELL_V_ALIGNMENT)))
                    {
                        columnCell.setBottom(0);
                    }
                    else
                    {
                        columnCell.setTop(0);
                    }
                    
                }
                
                if (top <= -1 && bottom <= -1)
                {
                    if(EJTMTMultiRecordBlockDefinitionProperties.COLUMN_ALLIGN_RIGHT.equals(blockProperties
                            .getStringProperty(EJTMTMultiRecordBlockDefinitionProperties.CELL_H_ALIGNMENT)))
                    {
                        columnCell.setRight(0);
                    }
                    else
                    {
                        columnCell.setLeft(0);
                    }
                   
                }
                
                if (left <= -1 || right <= -1)
                {
                    if (width != 0)
                    {
                        columnCell.setWidth(width);
                    }
                    else
                    {
                        columnCell.setWidth(SWT.DEFAULT);
                    }
                }

                if (top <= -1 || bottom <= -1)
                {
                    if (height != 0)
                    {
                        columnCell.setHeight(height);
                    }
                    else
                    {
                        columnCell.setHeight(SWT.DEFAULT);
                    }
                }
                columnCell.setHorizontalAlignment(getComponentStyle(blockProperties
                        .getStringProperty(EJTMTMultiRecordBlockDefinitionProperties.CELL_H_ALIGNMENT)));
                columnCell
                        .setVerticalAlignment(getComponentStyle(blockProperties.getStringProperty(EJTMTMultiRecordBlockDefinitionProperties.CELL_V_ALIGNMENT)));

                final String action = blockProperties.getStringProperty(EJTMTMultiRecordBlockDefinitionProperties.CELL_ACTION_COMMAND);
                if (action != null && action.length() > 0)
                {
                    columnCell.setName(action);
                    columnCell.setSelectable(true);
                }

            }
            return labelProvider;
        }
        return null;
    }

    protected int getComponentStyle(String alignmentProperty)
    {
        if (alignmentProperty != null && alignmentProperty.trim().length() > 0)
        {
            if (alignmentProperty.equals(EJTMTMultiRecordBlockDefinitionProperties.COLUMN_ALLIGN_RIGHT))
            {
                return SWT.RIGHT;
            }
            else if (alignmentProperty.equals(EJTMTMultiRecordBlockDefinitionProperties.COLUMN_ALLIGN_CENTER))
            {
                return SWT.CENTER;
            }
        }
        return SWT.LEFT;
    }

}
