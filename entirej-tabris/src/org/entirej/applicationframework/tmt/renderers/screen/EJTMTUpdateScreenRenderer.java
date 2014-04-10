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
import org.entirej.framework.core.EJRecord;
import org.entirej.framework.core.data.EJDataRecord;
import org.entirej.framework.core.data.controllers.EJEditableBlockController;
import org.entirej.framework.core.enumerations.EJManagedScreenProperty;
import org.entirej.framework.core.enumerations.EJScreenType;
import org.entirej.framework.core.extensions.properties.EJCoreFrameworkExtensionPropertyList;
import org.entirej.framework.core.interfaces.EJScreenItemController;
import org.entirej.framework.core.internal.EJInternalEditableBlock;
import org.entirej.framework.core.properties.EJCoreUpdateScreenItemProperties;
import org.entirej.framework.core.properties.definitions.interfaces.EJFrameworkExtensionProperties;
import org.entirej.framework.core.properties.definitions.interfaces.EJFrameworkExtensionPropertyListEntry;
import org.entirej.framework.core.properties.interfaces.EJBlockProperties;
import org.entirej.framework.core.properties.interfaces.EJScreenItemProperties;
import org.entirej.framework.core.renderers.EJManagedItemRendererWrapper;
import org.entirej.framework.core.renderers.eventhandlers.EJItemValueChangedListener;
import org.entirej.framework.core.renderers.interfaces.EJItemRenderer;
import org.entirej.framework.core.renderers.interfaces.EJUpdateScreenRenderer;
import org.entirej.framework.core.renderers.registry.EJBlockItemRendererRegister;
import org.entirej.framework.core.renderers.registry.EJUpdateScreenItemRendererRegister;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.eclipsesource.tabris.ui.Action;
import com.eclipsesource.tabris.ui.PageConfiguration;
import com.eclipsesource.tabris.ui.PageData;
import com.eclipsesource.tabris.ui.PageStyle;
import com.eclipsesource.tabris.ui.PlacementPriority;
import com.eclipsesource.tabris.ui.UI;
import com.eclipsesource.tabris.ui.UIConfiguration;

public class EJTMTUpdateScreenRenderer extends EJTMTAbstractScreenRenderer implements EJUpdateScreenRenderer, EJItemValueChangedListener
{
    private final int                          UPDATE_OK_ACTION_COMMAND     = 0;
    private final int                          UPDATE_CANCEL_ACTION_COMMAND = -1;

    private EJEditableBlockController          _block;
    private EJTMTScreenPage.Context            _updateDialog;
    private EJUpdateScreenItemRendererRegister _itemRegister;
    private EJFrameworkManager                 _frameworkManager;

    final Logger                               _logger                      = LoggerFactory.getLogger(EJTMTUpdateScreenRenderer.class);
    private String                             _title;

    @Override
    public void refreshUpdateScreenRendererProperty(String propertyName)
    {
    }

    @Override
    public EJBlockItemRendererRegister getItemRegister()
    {
        return _itemRegister;
    }

    EJTMTApplicationManager getTMTManager()
    {
        return (EJTMTApplicationManager) _frameworkManager.getApplicationManager();
    }

    @Override
    public EJScreenItemController getItem(String itemName)
    {
        return _block.getScreenItem(EJScreenType.UPDATE, itemName);
    }

    @Override
    public void refreshItemProperty(EJCoreUpdateScreenItemProperties itemProperties, EJManagedScreenProperty managedItemProperty)
    {
        EJManagedItemRendererWrapper rendererWrapper = _itemRegister.getManagedItemRendererForItem(itemProperties.getReferencedItemName());
        if (rendererWrapper == null)
        {
            return;
        }
        switch (managedItemProperty)
        {
            case VISIBLE:
                rendererWrapper.setVisible(itemProperties.isVisible());
                break;
            case EDIT_ALLOWED:
                rendererWrapper.setEditAllowed(itemProperties.isEditAllowed());
                break;
            case MANDATORY:
                rendererWrapper.setMandatory(itemProperties.isMandatory());
                break;
            case LABEL:
                rendererWrapper.setLabel(itemProperties.getLabel());
                break;
            case HINT:
                rendererWrapper.setHint(itemProperties.getHint());
                break;
        }
    }

    @Override
    public Object getGuiComponent()
    {
        return _updateDialog;
    }

    @Override
    public void initialiseRenderer(EJEditableBlockController block)
    {
        _block = block;
        _itemRegister = new EJUpdateScreenItemRendererRegister(block);
        _frameworkManager = block.getFrameworkManager();
    }

    @Override
    public void open(EJDataRecord recordToUpdate)
    {
        _itemRegister.resetRegister();
        setupUpdateScreen();
        String pageID = EJTMTScreenPage.toPageID(_block.getForm().getProperties().getName(),_block.getProperties().getName(),EJScreenType.UPDATE);
        final UI ui = EJTMTContext.getTabrisUI();
        final UIConfiguration configuration = EJTMTContext.getUiConfiguration();

        if (configuration.getPageConfiguration(pageID) == null)
        {
            PageConfiguration pageConfiguration = new PageConfiguration(pageID, EJTMTScreenPage.class).setTitle(_title != null ? _title : "");
            pageConfiguration.setStyle(PageStyle.DEFAULT);
            EJFrameworkExtensionProperties formRendererProperties = _block.getProperties().getUpdateScreenRendererProperties();
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
                                    _block.executeActionCommand(action, EJScreenType.UPDATE);
                                    
                                    
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
        PageData pageData = EJTMTScreenPage.createPageData(_updateDialog);
        ui.getPageOperator().openPage(pageID, pageData);

        _itemRegister.register(recordToUpdate);
        _updateDialog.validate();
    }

    @Override
    public void close()
    {
        _updateDialog.close();
        _updateDialog = null;
    }

    @Override
    public EJDataRecord getUpdateRecord()
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

    @Override
    protected EJInternalEditableBlock getBlock()
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
        return ((EJCoreUpdateScreenItemProperties) item).getUpdateScreenRendererProperties();
    }


    private void setupUpdateScreen()
    {
        // Setup pane for query window
        EJFrameworkExtensionProperties rendererProperties = _block.getProperties().getUpdateScreenRendererProperties();

        _title = rendererProperties.getStringProperty(EJTMTScreenRendererDefinitionProperties.TITLE);
        final int width = rendererProperties.getIntProperty(EJTMTScreenRendererDefinitionProperties.WIDTH, 300);
        final int height = rendererProperties.getIntProperty(EJTMTScreenRendererDefinitionProperties.HEIGHT, 500);
        final int numCols = rendererProperties.getIntProperty(EJTMTScreenRendererDefinitionProperties.NUM_COLS, 1);
        final String updateButtonLabel = rendererProperties.getStringProperty(EJTMTScreenRendererDefinitionProperties.EXECUTE_BUTTON_TEXT);
        final String cancelButtonLabel = rendererProperties.getStringProperty(EJTMTScreenRendererDefinitionProperties.CANCEL_BUTTON_TEXT);

        EJFrameworkExtensionProperties extraButtonsGroup = rendererProperties.getPropertyGroup(EJTMTScreenRendererDefinitionProperties.EXTRA_BUTTONS_GROUP);

        final String button1Label = extraButtonsGroup.getStringProperty(EJTMTScreenRendererDefinitionProperties.EXTRA_BUTTON_ONE_LABEL);
        final String button1Command = extraButtonsGroup.getStringProperty(EJTMTScreenRendererDefinitionProperties.EXTRA_BUTTON_ONE_COMMAND);
        final String button2Label = extraButtonsGroup.getStringProperty(EJTMTScreenRendererDefinitionProperties.EXTRA_BUTTON_TWO_LABEL);
        final String button2Command = extraButtonsGroup.getStringProperty(EJTMTScreenRendererDefinitionProperties.EXTRA_BUTTON_TWO_COMMAND);
        final String button3Label = extraButtonsGroup.getStringProperty(EJTMTScreenRendererDefinitionProperties.EXTRA_BUTTON_THREE_LABEL);
        final String button3Command = extraButtonsGroup.getStringProperty(EJTMTScreenRendererDefinitionProperties.EXTRA_BUTTON_THREE_COMMAND);
        final String button4Label = extraButtonsGroup.getStringProperty(EJTMTScreenRendererDefinitionProperties.EXTRA_BUTTON_FOUR_LABEL);
        final String button4Command = extraButtonsGroup.getStringProperty(EJTMTScreenRendererDefinitionProperties.EXTRA_BUTTON_FOUR_COMMAND);
        final String button5Label = extraButtonsGroup.getStringProperty(EJTMTScreenRendererDefinitionProperties.EXTRA_BUTTON_FIVE_LABEL);
        final String button5Command = extraButtonsGroup.getStringProperty(EJTMTScreenRendererDefinitionProperties.EXTRA_BUTTON_FIVE_COMMAND);

        final int ID_BUTTON_1 = 1;
        final int ID_BUTTON_2 = 2;
        final int ID_BUTTON_3 = 3;
        final int ID_BUTTON_4 = 4;
        final int ID_BUTTON_5 = 5;

        _updateDialog = new EJTMTScreenPage.Context()
        {
            @Override
            public void createBody(Composite parent)
            {
                parent.setLayout(new FillLayout());
                final ScrolledComposite scrollComposite = new ScrolledComposite(parent, SWT.V_SCROLL );

                EJTMTEntireJGridPane _mainPane = new EJTMTEntireJGridPane(scrollComposite, numCols);
                _mainPane.cleanLayout();
                EJBlockProperties blockProperties = _block.getProperties();
                addAllItemGroups(blockProperties.getScreenItemGroupContainer(EJScreenType.UPDATE), _mainPane, EJScreenType.UPDATE);

                scrollComposite.setContent(_mainPane);
                scrollComposite.setExpandHorizontal(true);
                scrollComposite.setExpandVertical(true);
                // remove the offset
                scrollComposite.setMinHeight( height - 10);

                _block.addItemValueChangedListener(EJTMTUpdateScreenRenderer.this);
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

            public void open()
            {
                validate();
                setFoucsItemRenderer();
            }

            @Override
            protected void createButtonsForButtonBar(Composite parent)
            {
                // Add the buttons in reverse order, as they will be added from
                // left to right
                addExtraButton(parent, button5Label, ID_BUTTON_5);
                addExtraButton(parent, button4Label, ID_BUTTON_4);
                addExtraButton(parent, button3Label, ID_BUTTON_3);
                addExtraButton(parent, button2Label, ID_BUTTON_2);
                addExtraButton(parent, button1Label, ID_BUTTON_1);
                page.createButton(parent, UPDATE_OK_ACTION_COMMAND, updateButtonLabel == null ? "Update" : updateButtonLabel, true);
                page.createButton(parent, UPDATE_CANCEL_ACTION_COMMAND, cancelButtonLabel == null ? "Cancel" : cancelButtonLabel, false);
            }

            @Override
            public void validate()
            {
                Button button = page.getButton(UPDATE_OK_ACTION_COMMAND);
                if (button == null || button.isDisposed())
                {
                    return;
                }
                Collection<EJScreenItemController> allScreenItems = _block.getAllScreenItems(EJScreenType.UPDATE);
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

            private void addExtraButton(Composite parent, String label, int id)
            {
                if (label == null)
                {
                    return;
                }
                page.createButton(parent, id, label, false);
            }

            @Override
            public void close()
            {
                _block.removeItemValueChangedListener(EJTMTUpdateScreenRenderer.this);
                _block.setRendererFocus(true);
                final UIConfiguration configuration = EJTMTContext.getUiConfiguration();
                final UI ui = EJTMTContext.getTabrisUI();
                String pageID = EJTMTScreenPage.toPageID(_block.getForm().getProperties().getName(),_block.getProperties().getName(),EJScreenType.UPDATE);
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
            public void canceled()
            {
                _block.updateCancelled();

            }

            @Override
            public void buttonPressed(int buttonId)
            {
                try
                {
                    switch (buttonId)
                    {
                        case UPDATE_OK_ACTION_COMMAND:
                        {
                            try
                            {
                                _block.getBlock().updateRecord(_itemRegister.getRegisteredRecord());
                                if (_block.getUpdateScreenDisplayProperties().getBooleanProperty(
                                        EJTMTScreenRendererDefinitionProperties.SAVE_FORM_AFTER_EXECUTE, false))
                                {
                                    _block.getBlock().getForm().saveChanges();
                                }
                            }
                            catch (EJApplicationException e)
                            {
                                page.setButtonEnable(buttonId, false);
                                throw e;
                            }
                            close();
                            break;
                        }
                        case UPDATE_CANCEL_ACTION_COMMAND:
                        {
                            _block.updateCancelled();
                            close();
                            break;
                        }
                        case ID_BUTTON_1:
                        {
                            _block.executeActionCommand(button1Command, EJScreenType.UPDATE);
                            break;
                        }
                        case ID_BUTTON_2:
                        {
                            _block.executeActionCommand(button2Command, EJScreenType.UPDATE);
                            break;
                        }
                        case ID_BUTTON_3:
                        {
                            _block.executeActionCommand(button3Command, EJScreenType.UPDATE);
                            break;
                        }
                        case ID_BUTTON_4:
                        {
                            _block.executeActionCommand(button4Command, EJScreenType.UPDATE);
                            break;
                        }
                        case ID_BUTTON_5:
                        {
                            _block.executeActionCommand(button5Command, EJScreenType.UPDATE);
                            break;
                        }

                        default:
                            _block.updateCancelled();
                            break;
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
    public void valueChanged(EJScreenItemController arg0, EJItemRenderer arg1)
    {
        if (_updateDialog != null)
        {
            _updateDialog.validate();
        }
    }
}
