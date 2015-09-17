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
import org.eclipse.swt.custom.ScrolledComposite;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.entirej.applicationframework.tmt.application.EJTMTApplicationManager;
import org.entirej.applicationframework.tmt.application.EJTMTImageRetriever;
import org.entirej.applicationframework.tmt.application.launcher.EJTMTContext;
import org.entirej.applicationframework.tmt.layout.EJTMTEntireJGridPane;
import org.entirej.applicationframework.tmt.pages.EJTMTFormPage;
import org.entirej.applicationframework.tmt.pages.EJTMTScreenPage;
import org.entirej.applicationframework.tmt.pages.EJTMTFormPage.FormActionConfiguration;
import org.entirej.applicationframework.tmt.renderers.item.EJTMTItemTextChangeNotifier;
import org.entirej.applicationframework.tmt.renderers.item.EJTMTItemTextChangeNotifier.ChangeListener;
import org.entirej.applicationframework.tmt.renderers.screen.definition.interfaces.EJTMTScreenRendererDefinitionProperties;
import org.entirej.framework.core.EJApplicationException;
import org.entirej.framework.core.EJFrameworkManager;
import org.entirej.framework.core.EJLovBlock;
import org.entirej.framework.core.EJQueryBlock;
import org.entirej.framework.core.EJRecord;
import org.entirej.framework.core.EJScreenItem;
import org.entirej.framework.core.data.EJDataItem;
import org.entirej.framework.core.data.EJDataRecord;
import org.entirej.framework.core.data.controllers.EJBlockController;
import org.entirej.framework.core.data.controllers.EJLovController;
import org.entirej.framework.core.enumerations.EJManagedScreenProperty;
import org.entirej.framework.core.enumerations.EJRecordType;
import org.entirej.framework.core.enumerations.EJScreenType;
import org.entirej.framework.core.extensions.properties.EJCoreFrameworkExtensionPropertyList;
import org.entirej.framework.core.interfaces.EJScreenItemController;
import org.entirej.framework.core.internal.EJInternalBlock;
import org.entirej.framework.core.properties.EJCoreQueryScreenItemProperties;
import org.entirej.framework.core.properties.definitions.interfaces.EJFrameworkExtensionProperties;
import org.entirej.framework.core.properties.definitions.interfaces.EJFrameworkExtensionPropertyListEntry;
import org.entirej.framework.core.properties.interfaces.EJBlockProperties;
import org.entirej.framework.core.properties.interfaces.EJScreenItemProperties;
import org.entirej.framework.core.renderers.EJManagedItemRendererWrapper;
import org.entirej.framework.core.renderers.eventhandlers.EJItemValueChangedListener;
import org.entirej.framework.core.renderers.interfaces.EJItemRenderer;
import org.entirej.framework.core.renderers.interfaces.EJQueryScreenRenderer;
import org.entirej.framework.core.renderers.registry.EJBlockItemRendererRegister;
import org.entirej.framework.core.renderers.registry.EJQueryScreenItemRendererRegister;
import org.entirej.framework.core.service.EJQueryCriteria;
import org.entirej.framework.core.service.EJRestrictions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.eclipsesource.tabris.ui.Action;
import com.eclipsesource.tabris.ui.PageConfiguration;
import com.eclipsesource.tabris.ui.PageData;
import com.eclipsesource.tabris.ui.PageStyle;
import com.eclipsesource.tabris.ui.PlacementPriority;
import com.eclipsesource.tabris.ui.UI;
import com.eclipsesource.tabris.ui.UIConfiguration;

public class EJTMTQueryScreenRenderer extends EJTMTAbstractScreenRenderer implements EJQueryScreenRenderer, EJItemValueChangedListener
{
    private final int                         QUERY_OK_ACTION_COMMAND     = 0;
    private final int                         QUERY_CANCEL_ACTION_COMMAND = 2;
    private final int                         QUERY_CLEAR_ACTION_COMMAND  = 4;

    private EJBlockController                 _block;
    private EJTMTScreenPage.Context           _queryDialog;
    private EJQueryScreenItemRendererRegister _itemRegister;
    private EJFrameworkManager                _frameworkManager;

    final Logger                              _logger                     = LoggerFactory.getLogger(EJTMTQueryScreenRenderer.class);
    private String                            title;

    @Override
    public void refreshQueryScreenRendererProperty(String propertyName)
    {
    }

    @Override
    public EJBlockItemRendererRegister getItemRegister()
    {
        return _itemRegister;
    }

    @Override
    public EJScreenItemController getItem(String itemName)
    {
        return _block.getScreenItem(EJScreenType.QUERY, itemName);
    }

    @Override
    public void refreshItemProperty(EJCoreQueryScreenItemProperties itemProperties, EJManagedScreenProperty managedItemProperty)
    {
        EJManagedItemRendererWrapper rendererForItem = _itemRegister.getManagedItemRendererForItem(itemProperties.getReferencedItemName());
        if (rendererForItem == null)
        {
            return;
        }
        switch (managedItemProperty)
        {
            case VISIBLE:
                rendererForItem.setVisible(itemProperties.isVisible());
                break;
            case EDIT_ALLOWED:
                rendererForItem.setEditAllowed(itemProperties.isEditAllowed());
                break;
            case MANDATORY:
                rendererForItem.setMandatory(itemProperties.isMandatory());
                break;
            case LABEL:
                rendererForItem.setLabel(itemProperties.getLabel());
                break;
            case HINT:
                rendererForItem.setHint(itemProperties.getHint());
                break;
        }
    }

    @Override
    public Object getGuiComponent()
    {
        return _queryDialog;
    }

    @Override
    public void initialiseRenderer(EJBlockController block)
    {
        _block = block;

        _frameworkManager = block.getFrameworkManager();
        _itemRegister = new EJQueryScreenItemRendererRegister(block);
    }

    @Override
    public void initialiseRenderer(EJLovController controller)
    {
        _block = controller.getBlock().getBlockController();
        _itemRegister = new EJQueryScreenItemRendererRegister(controller);
        _frameworkManager = controller.getFrameworkManager();
        setupQueryScreen();
    }

    @Override
    public void open(EJDataRecord queryRecord)
    {
        _itemRegister.resetRegister();
        setupQueryScreen();

        String pageID = EJTMTScreenPage.toPageID(_block.getForm().getProperties().getName(),_block.getProperties().getName(),EJScreenType.QUERY);
        final UI ui = EJTMTContext.getTabrisUI();
        final UIConfiguration configuration = EJTMTContext.getUiConfiguration();

        if (configuration.getPageConfiguration(pageID) == null)
        {
            PageConfiguration pageConfiguration = new PageConfiguration(pageID, EJTMTScreenPage.class).setTitle(title != null ? title : "");
            pageConfiguration.setStyle(PageStyle.DEFAULT);
            
            EJFrameworkExtensionProperties formRendererProperties = _block.getProperties().getQueryScreenRendererProperties();
            if(formRendererProperties!=null)
            {
                EJCoreFrameworkExtensionPropertyList actions = formRendererProperties.getPropertyList(EJTMTFormPage.PAGE_ACTIONS);
                if(actions != null)
                {
                    for (EJFrameworkExtensionPropertyListEntry entry : actions.getAllListEntries())
                    {
                        final String action = entry.getProperty(EJTMTFormPage.PAGE_ACTION_ID);
                        if(action!=null && action.length() >0)
                        {
                            FormActionConfiguration actionConfiguration = new FormActionConfiguration(FormActionConfiguration.toActionId(pageID, action), new Action()
                            {
                                
                                @Override
                                public void execute(UI ui)
                                {
                                    
                                    EJRecord record = null;
                                    if( _block.getFocusedRecord()!=null)
                                    {
                                        record = new EJRecord(_block.getFocusedRecord());
                                    }
                                    _block.executeActionCommand(action, EJScreenType.QUERY);
                                    
                                    
                                }
                            });
                            
                            String image = entry.getProperty(EJTMTFormPage.PAGE_ACTION_IMAGE);
                            if(image!=null && image.length()>0)
                            {
                                try
                                {
                                    actionConfiguration.setImage(EJTMTImageRetriever.class.getClassLoader().getResourceAsStream(image));
                                }
                                catch(Exception ex)
                                {
                                    _block.getForm().getMessenger().handleException(ex);
                                }
                            }
                            actionConfiguration.setTitle(entry.getProperty(EJTMTFormPage.PAGE_ACTION_NAME));
                            
                            if(Boolean.valueOf(entry.getProperty(EJTMTFormPage.PAGE_ACTION_PRIORITY)))
                            {
                                actionConfiguration.setPlacementPriority(PlacementPriority.HIGH);
                            }
                            pageConfiguration.addActionConfiguration(actionConfiguration);
                        }
                    }
                }
                
            }
            configuration.addPageConfiguration(pageConfiguration);

        }
        PageData pageData = EJTMTScreenPage.createPageData(_queryDialog);
        ui.getPageOperator().openPage(pageID, pageData);
        _itemRegister.register(queryRecord);
        _itemRegister.initialiseRegisteredRenderers();
    }

    @Override
    public void close()
    {
        _queryDialog.close();
        _queryDialog = null;
    }

  
    @Override
    public EJDataRecord getQueryRecord()
    {
        return _itemRegister.getRegisteredRecord();
    }

    @Override
    public void refreshAfterChange(EJDataRecord record)
    {
        _itemRegister.refreshAfterChange(record);
    }

    @Override
    public void synchronize()
    {
    }

    EJTMTApplicationManager getTMTManager()
    {
        return (EJTMTApplicationManager) _frameworkManager.getApplicationManager();
    }

    private void setupQueryScreen()
    {
        // Setup pane for query window
        EJFrameworkExtensionProperties rendererProperties = _block.getProperties().getQueryScreenRendererProperties();

        title = rendererProperties.getStringProperty(EJTMTScreenRendererDefinitionProperties.TITLE);
        final int width = rendererProperties.getIntProperty(EJTMTScreenRendererDefinitionProperties.WIDTH, 300);
        final int height = rendererProperties.getIntProperty(EJTMTScreenRendererDefinitionProperties.HEIGHT, 500);
        final int numCols = rendererProperties.getIntProperty(EJTMTScreenRendererDefinitionProperties.NUM_COLS, 1);
        final String queryButtonLabel = rendererProperties.getStringProperty(EJTMTScreenRendererDefinitionProperties.EXECUTE_BUTTON_TEXT);
        final String cancelButtonLabel = rendererProperties.getStringProperty(EJTMTScreenRendererDefinitionProperties.CANCEL_BUTTON_TEXT);
        final String clearButtonLabel = rendererProperties.getStringProperty(EJTMTScreenRendererDefinitionProperties.CLEAR_BUTTON_TEXT);

        _queryDialog = new EJTMTScreenPage.Context()
        {
            private static final long serialVersionUID = -4685316941898120169L;

            public void setButtonEnable(int id, boolean state)
            {
               if(page!=null)
               {
                   Button button = page.getButton(id);
                   if(button!=null && !button.isDisposed())
                   {
                       button.setEnabled(state);
                   }
               }
                
            }
            
            @Override
            public void createBody(Composite parent)
            {
                parent.setLayout(new FillLayout());
                final ScrolledComposite scrollComposite = new ScrolledComposite(parent, SWT.V_SCROLL );

                EJTMTEntireJGridPane _mainPane = new EJTMTEntireJGridPane(scrollComposite, numCols);
                _mainPane.cleanLayout();
                EJBlockProperties blockProperties = _block.getProperties();
                addAllItemGroups(blockProperties.getScreenItemGroupContainer(EJScreenType.QUERY), _mainPane, EJScreenType.QUERY);

                scrollComposite.setContent(_mainPane);
                scrollComposite.setExpandHorizontal(true);
                scrollComposite.setExpandVertical(true);
                // remove the oddset
                scrollComposite.setMinHeight(height - 10);

                _block.addItemValueChangedListener(EJTMTQueryScreenRenderer.this);
                EJTMTItemTextChangeNotifier.ChangeListener changeListener = new ChangeListener()
                {
                    @Override
                    public void changed()
                    {
                        validate();
                    }
                };
                Collection<EJManagedItemRendererWrapper> registeredRenderers = _itemRegister.getRegisteredRenderers();
                for (EJManagedItemRendererWrapper ejManagedItemRendererWrapper : registeredRenderers)
                {
                    if (ejManagedItemRendererWrapper.getUnmanagedRenderer() instanceof EJTMTItemTextChangeNotifier)
                    {
                        ((EJTMTItemTextChangeNotifier) ejManagedItemRendererWrapper.getUnmanagedRenderer()).addListener(changeListener);
                    }
                }
            }

            @Override
            public void validate()
            {
                Button button = page.getButton(QUERY_OK_ACTION_COMMAND);
                if (button == null || button.isDisposed())
                {
                    return;
                }
                Collection<EJScreenItemController> allScreenItems = _block.getAllScreenItems(EJScreenType.QUERY);
                for (EJScreenItemController ejScreenItemController : allScreenItems)
                {
                    if (!ejScreenItemController.getManagedItemRenderer().isValid())
                    {
                        button.setEnabled(false);
                        return;
                    }
                }
                button.setEnabled(true);
            }

            @Override
            public void open()
            {
                validate();
                setFoucsItemRenderer();
            }

            @Override
            protected void createButtonsForButtonBar(Composite parent)
            {
                page.createButton(parent, QUERY_OK_ACTION_COMMAND, queryButtonLabel == null ? "Query" : queryButtonLabel, true);
                page.createButton(parent, QUERY_CLEAR_ACTION_COMMAND, clearButtonLabel == null ? "Clear" : clearButtonLabel, false);
                page.createButton(parent, QUERY_CANCEL_ACTION_COMMAND, cancelButtonLabel == null ? "Cancel" : cancelButtonLabel, false);
            }

            @Override
            public void close()
            {
                _block.removeItemValueChangedListener(EJTMTQueryScreenRenderer.this);
                _block.setRendererFocus(true);
                final UIConfiguration configuration = EJTMTContext.getUiConfiguration();
                final UI ui = EJTMTContext.getTabrisUI();
                String pageID = EJTMTScreenPage.toPageID(_block.getForm().getProperties().getName(),_block.getProperties().getName(),EJScreenType.QUERY);
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
            public void buttonPressed(int buttonId)
            {
                try
                {
                    
                    switch (buttonId)
                    {
                        case QUERY_OK_ACTION_COMMAND:
                        {
                            EJQueryBlock b = new EJLovBlock(_block.getBlock());
                            EJQueryCriteria queryCriteria = new EJQueryCriteria(b);

                            EJDataRecord record = getQueryRecord();
                            for (EJDataItem item : record.getAllItems())
                            {
                                boolean serviceItem = item.isBlockServiceItem();
                                if (item.getValue() != null)
                                {
                                    if (item.getProperties().getDataTypeClass().isAssignableFrom(String.class))
                                    {
                                        String value = (String) item.getValue();
                                        if (value.contains("%"))
                                        {
                                            queryCriteria.add(EJRestrictions.like(item.getName(),serviceItem, item.getValue()));
                                        }
                                        else
                                        {
                                            queryCriteria.add(EJRestrictions.equals(item.getName(),serviceItem, item.getValue()));
                                        }
                                    }
                                    else
                                    {
                                        queryCriteria.add(EJRestrictions.equals(item.getName(),serviceItem, item.getValue()));
                                    }
                                }
                            }
                            try
                            {
                                _block.executeQuery(queryCriteria);
                            }
                            catch (EJApplicationException e)
                            {
                                page.setButtonEnable(buttonId, false);
                                throw e;
                            }
                            close();
                            break;
                        }
                        case QUERY_CLEAR_ACTION_COMMAND:
                        {
                            _itemRegister.clearRegisteredValues();
                            _itemRegister.register(_block.createRecord(EJRecordType.QUERY));
                            break;
                        }
                        case QUERY_CANCEL_ACTION_COMMAND:
                        {
                            close();
                            break;
                        }
                    }
                }
                catch (EJApplicationException e)
                {
                    _logger.trace(e.getMessage());
                    _frameworkManager.handleException(e);
                    return;
                }
                super.buttonPressed(buttonId);
            }
        };
    }

    @Override
    protected EJInternalBlock getBlock()
    {
        return _block.getBlock();
    }

    @Override
    protected void registerRendererForItem(EJItemRenderer renderer, EJScreenItemController item)
    {
        _itemRegister.registerRendererForItem(renderer, item);
    }

    @Override
    protected EJFrameworkExtensionProperties getItemRendererPropertiesForItem(EJScreenItemProperties item)
    {
        return ((EJCoreQueryScreenItemProperties) item).getQueryScreenRendererProperties();
    }

    public void setFocusToItem(EJScreenItem item)
    {
        EJManagedItemRendererWrapper renderer = _itemRegister.getManagedItemRendererForItem(item.getName());
        if (renderer != null)
        {
            renderer.gainFocus();
        }
    }

    @Override
    public void valueChanged(EJScreenItemController arg0, EJItemRenderer arg1)
    {
        if (_queryDialog != null)
        {
            _queryDialog.validate();
        }
    }
}
