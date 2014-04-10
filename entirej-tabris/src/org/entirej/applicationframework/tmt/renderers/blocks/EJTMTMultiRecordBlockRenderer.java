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

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.eclipse.jface.viewers.ColumnLabelProvider;
import org.eclipse.jface.viewers.ColumnViewerToolTipSupport;
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
import org.eclipse.rap.rwt.template.ImageCell.ScaleMode;
import org.eclipse.rap.rwt.template.Template;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.FocusEvent;
import org.eclipse.swt.events.FocusListener;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.events.KeyListener;
import org.eclipse.swt.events.MouseAdapter;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.entirej.applicationframework.tmt.layout.EJTMTEntireJGridPane;
import org.entirej.applicationframework.tmt.renderer.interfaces.EJTMTAppBlockRenderer;
import org.entirej.applicationframework.tmt.renderer.interfaces.EJTMTAppItemRenderer;
import org.entirej.applicationframework.tmt.renderers.blocks.definition.interfaces.EJTMTMultiRecordBlockDefinitionProperties;
import org.entirej.applicationframework.tmt.renderers.blocks.definition.interfaces.EJTMTSingleRecordBlockDefinitionProperties;
import org.entirej.applicationframework.tmt.renderers.blocks.definition.interfaces.EJTMTTreeBlockDefinitionProperties;
import org.entirej.applicationframework.tmt.renderers.screen.EJTMTInsertScreenRenderer;
import org.entirej.applicationframework.tmt.renderers.screen.EJTMTQueryScreenRenderer;
import org.entirej.applicationframework.tmt.renderers.screen.EJTMTUpdateScreenRenderer;
import org.entirej.applicationframework.tmt.table.EJTMTAbstractFilteredTable;
import org.entirej.applicationframework.tmt.table.EJTMTAbstractFilteredTable.FilteredContentProvider;
import org.entirej.applicationframework.tmt.table.EJTMTTableViewerColumnFactory;
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
import org.entirej.framework.core.properties.definitions.interfaces.EJFrameworkExtensionProperties;
import org.entirej.framework.core.properties.interfaces.EJBlockProperties;
import org.entirej.framework.core.properties.interfaces.EJItemGroupProperties;
import org.entirej.framework.core.properties.interfaces.EJMainScreenProperties;
import org.entirej.framework.core.properties.interfaces.EJScreenItemProperties;
import org.entirej.framework.core.renderers.EJManagedItemRendererWrapper;
import org.entirej.framework.core.renderers.interfaces.EJInsertScreenRenderer;
import org.entirej.framework.core.renderers.interfaces.EJQueryScreenRenderer;
import org.entirej.framework.core.renderers.interfaces.EJUpdateScreenRenderer;

public class EJTMTMultiRecordBlockRenderer implements EJTMTAppBlockRenderer, KeyListener
{
    private boolean                   _isFocused        = false;
    private EJEditableBlockController _block;
    private EJTMTEntireJGridPane      _mainPane;
    private TableViewer               _tableViewer;
    private EJTMTQueryScreenRenderer  _queryScreenRenderer;
    private EJTMTInsertScreenRenderer _insertScreenRenderer;
    private EJTMTUpdateScreenRenderer _updateScreenRenderer;

    private FilteredContentProvider   _filteredContentProvider;
    private List<EJDataRecord>        _tableBaseRecords = new ArrayList<EJDataRecord>();

    protected void clearFilter()
    {
        if (_filteredContentProvider != null)
        {
            _filteredContentProvider.setFilter(null);
        }
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
        if (EJManagedScreenProperty.ITEM_INSTANCE_VISUAL_ATTRIBUTE.equals(managedItemPropertyType))
        {
            EJScreenItemController item = _block.getScreenItem(EJScreenType.MAIN, itemName);
            if (item != null)
            {
                if (record == null || _tableViewer == null)
                {
                    return;
                }
                if (_tableViewer != null && !_tableViewer.getTable().isDisposed())
                {
                    _tableViewer.refresh(record);
                }
            }
        }
        else if (EJManagedScreenProperty.ITEM_INSTANCE_HINT_TEXT.equals(managedItemPropertyType))
        {
            EJScreenItemController item = _block.getScreenItem(EJScreenType.MAIN, itemName);
            if (item != null)
            {
                if (record == null)
                {
                    return;
                }
            }
        }
        else if (EJManagedScreenProperty.SCREEN_ITEM_VISUAL_ATTRIBUTE.equals(managedItemPropertyType))
        {
            EJScreenItemController item = _block.getScreenItem(EJScreenType.MAIN, itemName);
            if (item != null)
            {
                item.getManagedItemRenderer().getUnmanagedRenderer().setVisualAttribute(item.getProperties().getVisualAttributeProperties());
                if (_tableViewer != null && !_tableViewer.getTable().isDisposed())
                {
                    _tableViewer.setInput(new Object());
                }
            }
        }
    }

    @Override
    public void refreshItemRendererProperty(String itemName, String propertyName)
    {
    }

    @Override
    public Composite getGuiComponent()
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
        _queryScreenRenderer = new EJTMTQueryScreenRenderer();
        _insertScreenRenderer = new EJTMTInsertScreenRenderer();
        _updateScreenRenderer = new EJTMTUpdateScreenRenderer();
    }

    @Override
    public void blockCleared()
    {
        if (_tableViewer != null && !_tableViewer.getTable().isDisposed())
        {
            clearFilter();
            _tableViewer.setInput(new Object());
        }
        notifyStatus();
    }

    @Override
    public void synchronize()
    {
    }

    protected void notifyStatus()
    {
        // TODO
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

    public boolean isInsertMode()
    {
        return false;
    }

    public boolean isUpdateMode()
    {
        return false;
    }

    @Override
    public boolean isCurrentRecordDirty()
    {
        return false;
    }

    @Override
    public void enterInsert(EJDataRecord record)
    {
        if (_block.getInsertScreenRenderer() == null)
        {
            EJMessage message = new EJMessage("Please define an Insert Screen Renderer for this form before an insert operation can be performed.");
            _block.getForm().getMessenger().handleMessage(message);
        }
        else
        {
            _block.getInsertScreenRenderer().open(record);
        }
    }

    @Override
    public void enterQuery(EJDataRecord queryRecord)
    {
        if (_block.getQueryScreenRenderer() == null)
        {
            EJMessage message = new EJMessage("Please define a Query Screen Renderer for this form before a query operation can be performed.");
            _block.getForm().getMessenger().handleMessage(message);
        }
        else
        {
            _block.getQueryScreenRenderer().open(queryRecord);
        }
    }

    @Override
    public void enterUpdate(EJDataRecord recordToUpdate)
    {
        if (_block.getUpdateScreenRenderer() == null)
        {
            EJMessage message = new EJMessage("Please define an Update Screen Renderer for this form before an update operation can be performed.");
            _block.getForm().getMessenger().handleMessage(message);
        }
        else
        {
            _block.getUpdateScreenRenderer().open(recordToUpdate);
        }
    }

    @Override
    public void queryExecuted()
    {
        if (_tableViewer != null && !_tableViewer.getTable().isDisposed())
        {
            clearFilter();
            _tableViewer.setInput(new Object());
        }
         selectRow(0);

    }

    public void pageRetrieved()
    {
        if (_tableViewer != null && !_tableViewer.getTable().isDisposed())
        {
            clearFilter();
            _tableViewer.setInput(new Object());
        }
         selectRow(0);
    }

    @Override
    public void recordDeleted(int dataBlockRecordNumber)
    {
        EJDataRecord recordAt = getRecordAt(dataBlockRecordNumber > 1 ? dataBlockRecordNumber - 2 : 0);

        if (recordAt == null)
        {
            recordAt = getLastRecord();
        }
        if (_tableViewer != null && !_tableViewer.getTable().isDisposed())
        {
            clearFilter();
            _tableViewer.setInput(new Object());
        }
        if (recordAt != null)
        {
            recordSelected(recordAt);
        }
    }

    @Override
    public void recordInserted(EJDataRecord record)
    {
        if (_tableViewer != null && !_tableViewer.getTable().isDisposed())
        {
            clearFilter();
            _tableViewer.setInput(new Object());
        }
        recordSelected(record);
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
            if(record != null )
            {
                _tableViewer.setSelection( new StructuredSelection(record), true);
            }
            else
            {
                selectRow(0);//select default row
            }
           
        }
        notifyStatus();
    }

    @Override
    public void setHasFocus(boolean focus)
    {
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
        
        notifyStatus();
    }

    /**
     * Enables a blue border around this controller. This will indicate that the
     * container held by this controller has cursor focus.
     * 
     * @param focused
     *            If <code>true</code> is passed then the border will be
     *            displayed, if <code>false</code> is passed then no border will
     *            be shown.
     */
    protected void showFocusedBorder(boolean focused)
    {
    }

    @Override
    public void setFocusToItem(EJScreenItemController item)
    {
        if (_tableViewer != null && !_tableViewer.getTable().isDisposed())
        {
            _tableViewer.getTable().forceFocus();
        }
    }

    @Override
    public void gainFocus()
    {
        if (_tableViewer != null && !_tableViewer.getTable().isDisposed())
        {
            _tableViewer.getTable().forceFocus();
        }
        setHasFocus(true);

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
                if(firstElement==null )
                {
                    selectRow(0);
                    structuredSelection = (IStructuredSelection)_tableViewer.getSelection();
                    firstElement = structuredSelection.getFirstElement();
                }
                if (firstElement instanceof EJDataRecord)
                {
                    _focusedRecord = (EJDataRecord) firstElement;
                }
            }
        }
        return _focusedRecord;
    }

    @Override
    public int getDisplayedRecordNumber(EJDataRecord record)
    {
        if (record == null)
        {
            return -1;
        }

        return _tableBaseRecords.indexOf(record);
    }

    @Override
    public int getDisplayedRecordCount()
    {
        // Indicates the number of records that are available within the View.
        // the number depends on the filters set on the table!
        return _tableBaseRecords.size();
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
        notifyStatus();
    }

    public final EJInternalEditableBlock getBlock()
    {
        return _block.getBlock();
    }

    @Override
    public void buildGuiComponent(EJTMTEntireJGridPane blockCanvas)
    {

        EJBlockProperties blockProperties = _block.getProperties();
        EJMainScreenProperties mainScreenProperties = blockProperties.getMainScreenProperties();

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
        EJFrameworkExtensionProperties rendererProp = blockProperties.getBlockRendererProperties();
        blockCanvas.setLayoutData(gridData);

        EJFrameworkExtensionProperties sectionProperties = null;
        if (rendererProp != null)
        {
            sectionProperties = rendererProp.getPropertyGroup(EJTMTSingleRecordBlockDefinitionProperties.ITEM_GROUP_TITLE_BAR);
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
            _mainPane = new EJTMTEntireJGridPane(group, 1);
        }
        else
        {
            _mainPane = new EJTMTEntireJGridPane(blockCanvas, 1);
            _mainPane.setLayoutData(gridData);
            
        }
        _mainPane.cleanLayout();

        int style = SWT.VIRTUAL | SWT.H_SCROLL | SWT.V_SCROLL;

        if (rendererProp.getBooleanProperty(EJTMTMultiRecordBlockDefinitionProperties.ROW_SELECTION, true))
        {
            style = style | SWT.FULL_SELECTION;
        }
        

        Collection<EJItemGroupProperties> allItemGroupProperties = _block.getProperties().getScreenItemGroupContainer(EJScreenType.MAIN)
                .getAllItemGroupProperties();
        final Table table;
        final EJTMTAbstractFilteredTable filterTree;
        if (rendererProp.getBooleanProperty(EJTMTTreeBlockDefinitionProperties.FILTER, false))
        {
            if (allItemGroupProperties.size() > 0)
            {
                EJItemGroupProperties displayProperties = allItemGroupProperties.iterator().next();
                if (displayProperties.dispayGroupFrame())
                {
                    Group group = new Group(_mainPane, SWT.NONE);
                    group.setLayout(new FillLayout());
                    if (displayProperties.getFrameTitle() != null && displayProperties.getFrameTitle().length() > 0)
                    {
                        group.setText(displayProperties.getFrameTitle());
                    }
                    group.setLayoutData(new GridData(GridData.FILL_BOTH | GridData.GRAB_HORIZONTAL | GridData.GRAB_VERTICAL));

                    filterTree = new EJTMTAbstractFilteredTable(group, style)
                    {
                        @Override
                        public void filter(String filter)
                        {
                            if (_filteredContentProvider != null
                                    && (filter == null && _filteredContentProvider.getFilter() != null || !filter.equals(_filteredContentProvider.getFilter())))
                            {
                                _filteredContentProvider.setFilter(filter);
                                getViewer().setInput(filter);

                                notifyStatus();
                            }
                        }

                        @Override
                        protected TableViewer doCreateTableViewer(Composite parent, int style)
                        {
                            return _tableViewer = new TableViewer(parent, style);
                        }
                    };
                }
                else
                {

                    filterTree = new EJTMTAbstractFilteredTable(_mainPane, style)
                    {
                        @Override
                        public void filter(String filter)
                        {
                            if (_filteredContentProvider != null
                                    && (filter == null && _filteredContentProvider.getFilter() != null || !filter.equals(_filteredContentProvider.getFilter())))
                            {
                                _filteredContentProvider.setFilter(filter);
                                getViewer().setInput(filter);

                                notifyStatus();
                            }
                        }

                        @Override
                        protected TableViewer doCreateTableViewer(Composite parent, int style)
                        {
                            return _tableViewer = new TableViewer(parent, style);
                        }
                    };
                    filterTree.setLayoutData(new GridData(GridData.FILL_BOTH | GridData.GRAB_HORIZONTAL | GridData.GRAB_VERTICAL));
                }
            }
            else
            {
                filterTree = new EJTMTAbstractFilteredTable(_mainPane, style)
                {
                    @Override
                    public void filter(String filter)
                    {
                        if (_filteredContentProvider != null
                                && (filter == null && _filteredContentProvider.getFilter() != null || !filter.equals(_filteredContentProvider.getFilter())))
                        {
                            _filteredContentProvider.setFilter(filter);
                            getViewer().setInput(filter);

                            notifyStatus();
                        }
                    }

                    @Override
                    protected TableViewer doCreateTableViewer(Composite parent, int style)
                    {
                        return _tableViewer = new TableViewer(parent, style);
                    }
                };

                filterTree.setLayoutData(new GridData(GridData.FILL_BOTH | GridData.GRAB_HORIZONTAL | GridData.GRAB_VERTICAL));
            }
            table = (_tableViewer = filterTree.getViewer()).getTable();
        }
        else
        {
            filterTree = null;
            if (allItemGroupProperties.size() > 0)
            {
                EJItemGroupProperties displayProperties = allItemGroupProperties.iterator().next();
                if (displayProperties.dispayGroupFrame())
                {
                    Group group = new Group(_mainPane, SWT.NONE);
                    group.setLayout(new FillLayout());
                    if (displayProperties.getFrameTitle() != null && displayProperties.getFrameTitle().length() > 0)
                    {
                        group.setText(displayProperties.getFrameTitle());
                    }
                    group.setLayoutData(new GridData(GridData.FILL_BOTH | GridData.GRAB_HORIZONTAL | GridData.GRAB_VERTICAL));
                    table = new Table(group, style);
                }
                else
                {
                    table = new Table(_mainPane, style);
                    table.setLayoutData(new GridData(GridData.FILL_BOTH | GridData.GRAB_HORIZONTAL | GridData.GRAB_VERTICAL));
                }
            }
            else
            {
                table = new Table(_mainPane, style);
                table.setLayoutData(new GridData(GridData.FILL_BOTH | GridData.GRAB_HORIZONTAL | GridData.GRAB_VERTICAL));
            }

            _tableViewer = new TableViewer(table);
        }

        table.setLinesVisible(true);

        EJTMTTableViewerColumnFactory factory = new EJTMTTableViewerColumnFactory(_tableViewer);
        ColumnViewerToolTipSupport.enableFor(_tableViewer);

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

       
        
        int rowheight = rendererProp.getIntProperty(EJTMTMultiRecordBlockDefinitionProperties.ROW_HEIGHT,  60);
        if (rowheight > 0)
        {
            table.setData(RWT.CUSTOM_ITEM_HEIGHT, rowheight);
        }
        table.setData(RWT.ROW_TEMPLATE, template);

        table.addSelectionListener(new SelectionAdapter()
        {
            @Override
            public void widgetSelected(SelectionEvent e)
            {
                
                if (e.text != null)
                {
                    _block.executeActionCommand(e.text, EJScreenType.MAIN);
                }
            }
        });

        table.addFocusListener(new FocusListener()
        {
            @Override
            public void focusLost(FocusEvent arg0)
            {
                setHasFocus(false);
            }

            @Override
            public void focusGained(FocusEvent arg0)
            {
                setHasFocus(true);
            }
        });
        _mainPane.addMouseListener(new MouseAdapter()
        {
            @Override
            public void mouseDown(MouseEvent arg0)
            {
                
                if (!_block.hasFocus())
                {
                    setHasFocus(true);
                }
            }
        });
        table.addMouseListener(new MouseAdapter()
        {
            @Override
            public void mouseDown(MouseEvent arg0)
            {
                if (!_block.hasFocus())
                {
                    setHasFocus(true);
                }
            }
        });

        _tableViewer.setContentProvider(_filteredContentProvider = new FilteredContentProvider()
        {
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
                    for (EJDataRecord record : _block.getBlock().getRecords())
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
                    _tableBaseRecords.addAll(_block.getBlock().getRecords());
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
        _tableViewer.setInput(new Object());

        _tableViewer.addSelectionChangedListener(new ISelectionChangedListener()
        {

            @Override
            public void selectionChanged(SelectionChangedEvent arg0)
            {

                EJDataRecord focusedRecord = getFocusedRecord();
                if (focusedRecord != null)
                {
                    _block.newRecordInstance(focusedRecord);
                }
                
                
                notifyStatus();
            }
        });

        final String selectionAction = rendererProp.getStringProperty(EJTMTMultiRecordBlockDefinitionProperties.ROW_SELECTION_ACTION);
        if(selectionAction!=null && selectionAction.trim().length()>0)
        {
            _tableViewer.getTable().addMouseListener(new MouseAdapter()
            {
                @Override
                public void mouseUp(MouseEvent e)
                {
                    EJDataRecord rec = getFocusedRecord();
                    if(rec!=null)
                    {
                        _block.executeActionCommand(selectionAction, EJScreenType.MAIN);
                    }
                }
            });
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
            EJFrameworkExtensionProperties blockProperties = itemProps.getBlockRendererRequiredProperties();
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

                if(columnCell instanceof ImageCell)
                {
                    if(width>0 & height>0)
                    {
                        ((ImageCell)columnCell).setScaleMode(ScaleMode.FILL);
                    }
                }
                
                if(width>0)
                {
                    if((left==-1 && right!=-1) || EJTMTMultiRecordBlockDefinitionProperties.COLUMN_ALLIGN_RIGHT.equals(blockProperties
                            .getStringProperty(EJTMTMultiRecordBlockDefinitionProperties.CELL_H_ALIGNMENT)))
                    {
                        left = -1;
                        if(right==-1)
                        {
                            right =0;
                        }
                    }
                    else
                    {
                        right = -1;
                        if(left==-1)
                        {
                            left =0;
                        }
                    }
                }
                if(height>0)
                {
                    if((top==-1 && bottom!=-1) || EJTMTMultiRecordBlockDefinitionProperties.COLUMN_ALLIGN_BOTTOM.equals(blockProperties
                            .getStringProperty(EJTMTMultiRecordBlockDefinitionProperties.CELL_V_ALIGNMENT)))
                    {
                        top = -1;
                        if(bottom==-1)
                        {
                            bottom =0;
                        }
                    }
                    else
                    {
                        bottom = -1;
                        if(top==-1)
                        {
                            top =0;
                        }
                    }
                }
                
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
                
                if (top <= -1 && bottom <= -1)
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
                
                if (left <= -1 || right <= -1)
                {
                    if (width != 0)
                    {
                        columnCell.setWidth(width);
                    }
                    else
                    {
                        columnCell.setWidth(20);
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
                        columnCell.setHeight(20);
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

                return labelProvider;
            }
        }
        return null;
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
}
