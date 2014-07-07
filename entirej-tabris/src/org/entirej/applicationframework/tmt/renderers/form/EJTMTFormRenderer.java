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
package org.entirej.applicationframework.tmt.renderers.form;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.eclipse.jface.layout.GridLayoutFactory;
import org.eclipse.rap.rwt.RWT;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.SashForm;
import org.eclipse.swt.custom.ScrolledComposite;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.TabFolder;
import org.eclipse.swt.widgets.TabItem;
import org.entirej.applicationframework.tmt.application.EJTMTApplicationContainer;
import org.entirej.applicationframework.tmt.application.EJTMTApplicationManager;
import org.entirej.applicationframework.tmt.application.launcher.EJTMTContext;
import org.entirej.applicationframework.tmt.layout.EJTMTEntireJGridPane;
import org.entirej.applicationframework.tmt.layout.EJTMTEntireJStackedPane;
import org.entirej.applicationframework.tmt.pages.EJTMTFormPage;
import org.entirej.applicationframework.tmt.pages.EJTMTScreenPage;
import org.entirej.applicationframework.tmt.renderer.interfaces.EJTMTAppBlockRenderer;
import org.entirej.applicationframework.tmt.renderer.interfaces.EJTMTAppFormRenderer;
import org.entirej.framework.core.EJApplicationException;
import org.entirej.framework.core.EJMessage;
import org.entirej.framework.core.common.utils.EJParameterChecker;
import org.entirej.framework.core.data.controllers.EJCanvasController;
import org.entirej.framework.core.enumerations.EJCanvasSplitOrientation;
import org.entirej.framework.core.enumerations.EJCanvasType;
import org.entirej.framework.core.enumerations.EJPopupButton;
import org.entirej.framework.core.internal.EJInternalBlock;
import org.entirej.framework.core.internal.EJInternalEditableBlock;
import org.entirej.framework.core.internal.EJInternalForm;
import org.entirej.framework.core.properties.EJCoreProperties;
import org.entirej.framework.core.properties.containers.interfaces.EJCanvasPropertiesContainer;
import org.entirej.framework.core.properties.definitions.interfaces.EJFrameworkExtensionProperties;
import org.entirej.framework.core.properties.interfaces.EJCanvasProperties;
import org.entirej.framework.core.properties.interfaces.EJFormProperties;
import org.entirej.framework.core.properties.interfaces.EJStackedPageProperties;
import org.entirej.framework.core.properties.interfaces.EJTabPageProperties;

import com.eclipsesource.tabris.device.ClientDevice;
import com.eclipsesource.tabris.device.ClientDevice.Platform;
import com.eclipsesource.tabris.ui.PageConfiguration;
import com.eclipsesource.tabris.ui.PageData;
import com.eclipsesource.tabris.ui.PageStyle;
import com.eclipsesource.tabris.ui.UI;
import com.eclipsesource.tabris.ui.UIConfiguration;
import com.eclipsesource.tabris.widgets.enhancement.Widgets;
import com.eclipsesource.tabris.widgets.swipe.Swipe;
import com.eclipsesource.tabris.widgets.swipe.SwipeContext;
import com.eclipsesource.tabris.widgets.swipe.SwipeItem;
import com.eclipsesource.tabris.widgets.swipe.SwipeItemProvider;

public class EJTMTFormRenderer implements EJTMTAppFormRenderer
{
    private EJInternalForm                       _form;
    private EJTMTEntireJGridPane                 _mainPane;
    private LinkedList<String>                   _canvasesIds     = new LinkedList<String>();
    private Map<String, CanvasHandler>           _canvases        = new HashMap<String, CanvasHandler>();
    private Map<String, Control>                 _canvassControls = new HashMap<String, Control>();
    private Map<String, EJInternalBlock>         _blocks          = new HashMap<String, EJInternalBlock>();
    private Map<String, EJTabFolder>             _tabFolders      = new HashMap<String, EJTabFolder>();
    private Map<String, EJTMTEntireJStackedPane> _stackedPanes    = new HashMap<String, EJTMTEntireJStackedPane>();

    @Override
    public void formCleared()
    {

    }

    @Override
    public void formClosed()
    {

    }

    @Override
    public void gainInitialFocus()
    {
        setFocus();

    }

    @Override
    public EJInternalForm getForm()
    {

        return _form;
    }

    @Override
    public void initialiseForm(EJInternalForm form)
    {
        EJParameterChecker.checkNotNull(form, "initialiseForm", "formController");
        _form = form;

    }

    @Override
    public void refreshFormRendererProperty(String arg0)
    {
    }

    @Override
    public void savePerformed()
    {
    }

    @Override
    public void showPopupCanvas(String canvasName)
    {
        CanvasHandler canvasHandler = _canvases.get(canvasName);
        if (canvasHandler instanceof PopupCanvasHandler)
        {
            PopupCanvasHandler handler = (PopupCanvasHandler) canvasHandler;
            handler.open();
        }
    }
    
    @Override
    public void openFormInCanvas(String arg0, EJInternalForm arg1)
    {
        throw new IllegalAccessError("Not support yet");
        
    }

    @Override
    public void closePopupCanvas(String canvasName)
    {
        CanvasHandler canvasHandler = _canvases.get(canvasName);
        if (canvasHandler instanceof PopupCanvasHandler)
        {
            PopupCanvasHandler handler = (PopupCanvasHandler) canvasHandler;
            handler.close();
        }

    }

    @Override
    public void showStackedPage(String canvasName, String pageName)
    {
        if (canvasName != null && pageName != null)
        {
            EJTMTEntireJStackedPane cardPane = _stackedPanes.get(canvasName);
            if (cardPane != null)
            {
                cardPane.showPane(pageName);
            }
        }
    }

    @Override
    public void showTabPage(String canvasName, String pageName)
    {
        if (canvasName != null && pageName != null)
        {
            EJTabFolder tabPane = _tabFolders.get(canvasName);
            if (tabPane != null)
            {
                tabPane.showPage(pageName);
            }
        }
    }

    @Override
    public void createControl(final Composite parent)
    {
        setupGui(parent);
        setFocus();
        _form.getFormController().formInitialised();
    }

    @Override
    public EJTMTEntireJGridPane getGuiComponent()
    {
        if (_mainPane == null)
        {
            throw new IllegalAccessError("Call createControl(Composite parent) before access getGuiComponent()");
        }

        return _mainPane;
    }

    private void setFocus()
    {
        for (String canvasName : _canvasesIds)
        {
            EJCanvasProperties canvasProperties = _form.getProperties().getCanvasProperties(canvasName);

            if (canvasProperties != null && setFocus(canvasProperties))
            {
                return;
            }
        }
    }

    private boolean setFocus(EJCanvasProperties canvasProperties)
    {
        if (canvasProperties.getType() == EJCanvasType.BLOCK)
        {
            if (canvasProperties.getBlockProperties() != null)
            {
                EJInternalEditableBlock block = _form.getBlock(canvasProperties.getBlockProperties().getName());

                if (block.getRendererController() != null)
                {
                    block.getManagedRenderer().gainFocus();
                    return true;
                }
            }

        }
        else if (canvasProperties.getType() == EJCanvasType.GROUP)
        {
            for (EJCanvasProperties groupCanvas : canvasProperties.getGroupCanvasContainer().getAllCanvasProperties())
            {
                if (setFocus(groupCanvas))
                {
                    return true;
                }
            }
        }
        else if (canvasProperties.getType() == EJCanvasType.SPLIT)
        {
            for (EJCanvasProperties groupCanvas : canvasProperties.getSplitCanvasContainer().getAllCanvasProperties())
            {
                if (setFocus(groupCanvas))
                {
                    return true;
                }
            }
        }
        else if (canvasProperties.getType() == EJCanvasType.STACKED)
        {
            for (EJStackedPageProperties pageProps : canvasProperties.getStackedPageContainer().getAllStackedPageProperties())
            {
                if (pageProps.getName().equals(canvasProperties.getInitialStackedPageName() == null ? "" : canvasProperties.getInitialStackedPageName()))
                {
                    for (EJCanvasProperties stackedCanvas : pageProps.getContainedCanvases().getAllCanvasProperties())
                    {
                        if (setFocus(stackedCanvas))
                        {
                            return true;
                        }
                    }
                }
            }
        }
        else if (canvasProperties.getType() == EJCanvasType.TAB)
        {
            for (EJTabPageProperties tabPage : canvasProperties.getTabPageContainer().getAllTabPageProperties())
            {
                if (tabPage.isVisible())
                {
                    _form.getCanvasController().tabPageChanged(canvasProperties.getName(), tabPage.getName());
                    return true;
                }
            }
        }
        else if (canvasProperties.getType() == EJCanvasType.POPUP)
        {
            for (EJCanvasProperties popupCanvas : canvasProperties.getPopupCanvasContainer().getAllCanvasProperties())
            {

                if (setFocus(popupCanvas))
                {
                    return true;
                }

            }
        }

        return false;
    }

    private void setupGui(final Composite parent)
    {
        EJFormProperties formProperties = _form.getProperties();
        EJCanvasController canvasController = _form.getCanvasController();

        // Now loop through all the forms blocks and create controllers for them
        for (EJInternalBlock block : _form.getAllBlocks())
        {
            String canvasName = block.getProperties().getCanvasName();
            // If the block has not had a canvas defined for it, it cannot be
            // displayed.
            if (canvasName == null || canvasName.trim().length() == 0)
            {
                continue;
            }

            _blocks.put(canvasName, block);
        }
        _mainPane = new EJTMTEntireJGridPane(parent, formProperties.getNumCols());
        _mainPane.cleanLayout();
        for (EJCanvasProperties canvasProperties : formProperties.getCanvasContainer().getAllCanvasProperties())
        {
            createCanvas(_mainPane, canvasProperties, canvasController);
        }

    }

    private void createCanvas(Composite parent, EJCanvasProperties canvasProperties, EJCanvasController canvasController)
    {
        switch (canvasProperties.getType())
        {
            case BLOCK:
            case GROUP:
                createGroupCanvas(parent, canvasProperties, canvasController);
                break;
            case SPLIT:
                createSplitCanvas(parent, canvasProperties, canvasController);
                break;
            case STACKED:
                createStackedCanvas(parent, canvasProperties, canvasController);
                break;
            case TAB:
                createTabCanvas(parent, canvasProperties, canvasController);
                break;
            case POPUP:
                buildPopupCanvas(canvasProperties, canvasController);
        }
    }

    private void createStackedCanvas(Composite parent, EJCanvasProperties canvasProperties, EJCanvasController canvasController)
    {
        final String name = canvasProperties.getName();
        EJTMTEntireJStackedPane stackedPane = new EJTMTEntireJStackedPane(parent);
        stackedPane.setLayoutData(createCanvasGridData(canvasProperties));
        _stackedPanes.put(name, stackedPane);

        _canvassControls.put(canvasProperties.getName(), stackedPane);

        for (EJStackedPageProperties page : canvasProperties.getStackedPageContainer().getAllStackedPageProperties())
        {
            EJTMTEntireJGridPane pagePane = new EJTMTEntireJGridPane(stackedPane, page.getNumCols());
            pagePane.cleanLayout();
            stackedPane.add(page.getName(), pagePane);
            for (EJCanvasProperties properties : page.getContainedCanvases().getAllCanvasProperties())
            {
                createCanvas(pagePane, properties, canvasController);
            }
        }

        if (canvasProperties.getInitialStackedPageName() != null)
        {
            stackedPane.showPane(canvasProperties.getInitialStackedPageName());
        }

        _canvasesIds.add(name);
    }

    private void createTabCanvas(Composite parent, EJCanvasProperties canvasProperties, final EJCanvasController canvasController)
    {
        int style = SWT.FLAT;

        EJFrameworkExtensionProperties rendererProp = EJCoreProperties.getInstance().getApplicationDefinedProperties();
        if (rendererProp != null)
        {
            boolean displayBorder = rendererProp.getBooleanProperty("DISPLAY_TAB_BORDER", true);
            if (displayBorder)
            {
                style = SWT.FLAT | SWT.BORDER;
            }
        }

        switch (canvasProperties.getTabPosition())
        {
            case BOTTOM:
                style = style | SWT.BOTTOM;
                break;
            case LEFT:
                style = style | SWT.LEFT;
                break;
            case RIGHT:
                style = style | SWT.RIGHT;
                break;
            default:
                style = style | SWT.TOP;
                break;
        }
        final String name = canvasProperties.getName();
        final TabFolder folder = new TabFolder(parent, style);

        _canvassControls.put(canvasProperties.getName(), folder);
        EJTabFolder tabFolder = new EJTabFolder(folder);
        folder.addSelectionListener(new SelectionAdapter()
        {
            @Override
            public void widgetSelected(SelectionEvent e)
            {
                canvasController.tabPageChanged(name, (String) folder.getSelection()[0].getData("TAB_KEY"));
            }
        });
        Widgets.onTabFolder(tabFolder.folder).usePaging();
        _tabFolders.put(name, tabFolder);
        folder.setLayoutData(createCanvasGridData(canvasProperties));

        Collection<EJTabPageProperties> allTabPageProperties = canvasProperties.getTabPageContainer().getAllTabPageProperties();
        for (EJTabPageProperties page : allTabPageProperties)
        {
            if (page.isVisible())
            {
                TabItem tabItem = new TabItem(folder, SWT.NONE);
                tabItem.setData("TAB_KEY", page.getName());
                EJTMTEntireJGridPane pageCanvas = new EJTMTEntireJGridPane(folder, page.getNumCols());
                tabItem.setText(page.getPageTitle() != null && page.getPageTitle().length() > 0 ? page.getPageTitle() : page.getName());
                tabItem.setControl(pageCanvas);
                EJCanvasPropertiesContainer containedCanvases = page.getContainedCanvases();
                for (EJCanvasProperties pageProperties : containedCanvases.getAllCanvasProperties())
                {
                    createCanvas(pageCanvas, pageProperties, canvasController);
                }
                if (folder.getSelection() == null)
                {
                    folder.setSelection(tabItem);
                }

                tabFolder.put(page.getName(), tabItem);
                tabItem.getControl().setEnabled(page.isEnabled());
            }
        }

        _canvasesIds.add(name);
    }

    private void buildPopupCanvas(EJCanvasProperties canvasProperties, final EJCanvasController canvasController)
    {

        String name = canvasProperties.getName();

        _canvases.put(name, new PopupCanvasHandler(canvasProperties, canvasController));
    }

    private GridData createCanvasGridData(EJCanvasProperties canvasProperties)
    {
        GridData gridData = new GridData(GridData.FILL_BOTH);
        gridData.widthHint = canvasProperties.getWidth();
        gridData.heightHint = canvasProperties.getHeight();

        gridData.horizontalSpan = canvasProperties.getHorizontalSpan();
        gridData.verticalSpan = canvasProperties.getVerticalSpan();
        gridData.grabExcessHorizontalSpace = canvasProperties.canExpandHorizontally();
        gridData.grabExcessVerticalSpace = canvasProperties.canExpandVertically();

        if (gridData.grabExcessHorizontalSpace)
        {
            gridData.minimumWidth = canvasProperties.getHeight();
        }
        if (gridData.grabExcessVerticalSpace)
        {
            gridData.minimumHeight = canvasProperties.getWidth();
        }

        return gridData;
    }

    private void createGroupCanvas(Composite parent, EJCanvasProperties canvasProperties, EJCanvasController canvasController)
    {
        if (canvasProperties.getDisplayGroupFrame())
        {
            Group group = new Group(parent, SWT.NONE);
            group.setLayout(new FillLayout());
            group.setLayoutData(createCanvasGridData(canvasProperties));
            String frameTitle = canvasProperties.getGroupFrameTitle();
            if (frameTitle != null && frameTitle.length() > 0)
            {
                group.setText(frameTitle);
            }
            parent = group;
            _canvassControls.put(canvasProperties.getName(), group);
        }
        final EJTMTEntireJGridPane groupPane = new EJTMTEntireJGridPane(parent, canvasProperties.getNumCols());
        if (canvasProperties.getDisplayGroupFrame())
        {
            groupPane.cleanLayoutVertical();
        }
        else
        {
            groupPane.cleanLayout();

            _canvassControls.put(canvasProperties.getName(), groupPane);
        }

        groupPane.setPaneName(canvasProperties.getName());
        if (!canvasProperties.getDisplayGroupFrame())
        {
            groupPane.setLayoutData(createCanvasGridData(canvasProperties));
        }

        CanvasHandler canvasHandler = new CanvasHandler()
        {

            @Override
            public void add(EJInternalBlock block)
            {

                EJTMTAppBlockRenderer blockRenderer = (EJTMTAppBlockRenderer) block.getRendererController().getRenderer();
                if (blockRenderer == null)
                {
                    throw new EJApplicationException(new EJMessage("Block " + block.getProperties().getName()
                            + " has a canvas defined but no renderer. A block cannot be rendererd if no canvas has been defined."));
                }
                blockRenderer.buildGuiComponent(groupPane);
            }

            @Override
            public EJCanvasType getType()
            {
                return EJCanvasType.BLOCK;
            }
        };
        _canvases.put(groupPane.getPaneName(), canvasHandler);
        _canvasesIds.add(groupPane.getPaneName());
        EJInternalBlock block = _blocks.get(groupPane.getPaneName());
        if (block != null)
        {
            canvasHandler.add(block);
        }
        if (canvasProperties.getType() == EJCanvasType.GROUP)
        {
            for (EJCanvasProperties containedCanvas : canvasProperties.getGroupCanvasContainer().getAllCanvasProperties())
            {
                switch (containedCanvas.getType())
                {
                    case BLOCK:
                    case GROUP:
                        createGroupCanvas(groupPane, containedCanvas, canvasController);
                        break;
                    case SPLIT:
                        createSplitCanvas(groupPane, containedCanvas, canvasController);
                        break;
                    case STACKED:
                        createStackedCanvas(groupPane, containedCanvas, canvasController);
                        break;
                    case TAB:
                        createTabCanvas(groupPane, containedCanvas, canvasController);
                        break;
                    case POPUP:
                        throw new AssertionError();
                }
            }
        }
    }

    private void createSplitCanvas(Composite parent, EJCanvasProperties canvasProperties, final EJCanvasController canvasController)
    {

        if (canvasProperties.getType() == EJCanvasType.SPLIT)
        {
            ClientDevice service = RWT.getClient().getService(ClientDevice.class);
            if (service == null || service.getPlatform() == Platform.WEB)
            {
                SashForm layoutBody = new SashForm(parent, canvasProperties.getSplitOrientation() == EJCanvasSplitOrientation.HORIZONTAL ? SWT.HORIZONTAL
                        : SWT.VERTICAL);
                layoutBody.setLayoutData(createCanvasGridData(canvasProperties));

                List<EJCanvasProperties> items = new ArrayList<EJCanvasProperties>(canvasProperties.getSplitCanvasContainer().getAllCanvasProperties());
                int[] weights = new int[items.size()];

                for (EJCanvasProperties containedCanvas : items)
                {
                    if (containedCanvas.getType() == EJCanvasType.BLOCK && containedCanvas.getBlockProperties() != null
                            && containedCanvas.getBlockProperties().getMainScreenProperties() != null)
                    {
                        weights[items.indexOf(containedCanvas)] = containedCanvas.getBlockProperties().getMainScreenProperties().getWidth() + 1;
                    }
                    else
                    {
                        weights[items.indexOf(containedCanvas)] = containedCanvas.getWidth() + 1;
                    }

                    switch (containedCanvas.getType())
                    {
                        case BLOCK:
                        case GROUP:
                            createGroupCanvas(layoutBody, containedCanvas, canvasController);
                            break;
                        case SPLIT:
                            createSplitCanvas(layoutBody, containedCanvas, canvasController);
                            break;
                        case STACKED:
                            createStackedCanvas(layoutBody, containedCanvas, canvasController);
                            break;
                        case TAB:
                            createTabCanvas(layoutBody, containedCanvas, canvasController);
                            break;
                        case POPUP:
                            throw new AssertionError();

                    }
                }
                layoutBody.setWeights(weights);

                return;
            }

            List<EJCanvasProperties> items = new ArrayList<EJCanvasProperties>(canvasProperties.getSplitCanvasContainer().getAllCanvasProperties());
            int[] weights = new int[items.size()];

            final List<SwipeItem> swipeItems = new ArrayList<SwipeItem>();
            for (final EJCanvasProperties containedCanvas : items)
            {
                if (containedCanvas.getType() == EJCanvasType.BLOCK && containedCanvas.getBlockProperties() != null
                        && containedCanvas.getBlockProperties().getMainScreenProperties() != null)
                {
                    weights[items.indexOf(containedCanvas)] = containedCanvas.getBlockProperties().getMainScreenProperties().getWidth() + 1;
                }
                else
                {
                    weights[items.indexOf(containedCanvas)] = containedCanvas.getWidth() + 1;
                }
                SwipeItem item = new SwipeItem()
                {

                    @Override
                    public Control load(Composite parent)
                    {
                        Composite body = new Composite(parent, SWT.NONE);

                        GridLayoutFactory.fillDefaults().margins(0, 0).applyTo(body);

                        switch (containedCanvas.getType())
                        {
                            case BLOCK:
                            case GROUP:
                                createGroupCanvas(body, containedCanvas, canvasController);
                                break;
                            case SPLIT:
                                createSplitCanvas(body, containedCanvas, canvasController);
                                break;
                            case STACKED:
                                createStackedCanvas(body, containedCanvas, canvasController);
                                break;
                            case TAB:
                                createTabCanvas(body, containedCanvas, canvasController);
                                break;
                            case POPUP:
                                throw new AssertionError();

                        }
                        parent.layout(true, true);
                        return body;
                    }

                    @Override
                    public boolean isPreloadable()
                    {
                        return true;
                    }

                    @Override
                    public void deactivate(SwipeContext context)
                    {
                        // ignore

                    }

                    @Override
                    public void activate(SwipeContext context)
                    {
                        setFocus(containedCanvas);

                    }
                };
                swipeItems.add(item);
            }
            Swipe swipe = new Swipe(parent, new SwipeItemProvider()
            {

                @Override
                public int getItemCount()
                {
                    return swipeItems.size();
                }

                @Override
                public SwipeItem getItem(int index)
                {
                    return swipeItems.get(index);
                }
            });
            swipe.setCacheSize(swipeItems.size());
            swipe.getControl().setLayoutData(createCanvasGridData(canvasProperties));

            _canvassControls.put(canvasProperties.getName(), swipe.getControl());
        }
    }

    EJTMTApplicationManager getTMTManager()
    {
        return (EJTMTApplicationManager) _form.getFormController().getFrameworkManager().getApplicationManager();
    }

    private final class PopupCanvasHandler implements CanvasHandler
    {
        EJTMTScreenPage.Context  _popupDialog;

        final EJCanvasProperties canvasProperties;
        final EJCanvasController canvasController;

        public PopupCanvasHandler(EJCanvasProperties canvasProperties, EJCanvasController canvasController)
        {
            this.canvasController = canvasController;
            this.canvasProperties = canvasProperties;
        }

        @Override
        public void add(EJInternalBlock block)
        {
            // ignore
        }

        void open()
        {
            final String name = canvasProperties.getName();
            final String pageTitle = canvasProperties.getPopupPageTitle();
            final int width = canvasProperties.getWidth();
            final int height = canvasProperties.getHeight();
            final int numCols = canvasProperties.getNumCols();

            final String button1Label = canvasProperties.getButtonOneText();
            final String button2Label = canvasProperties.getButtonTwoText();
            final String button3Label = canvasProperties.getButtonThreeText();

            final int ID_BUTTON_1 = 1;
            final int ID_BUTTON_2 = 2;
            final int ID_BUTTON_3 = 3;

            _popupDialog = new EJTMTScreenPage.Context()
            {
                private static final long serialVersionUID = -4685316941898120169L;

                @Override
                public void createBody(Composite parent)
                {
                    parent.setLayout(new FillLayout());
                    final ScrolledComposite scrollComposite = new ScrolledComposite(parent, SWT.V_SCROLL);

                    EJTMTEntireJGridPane _mainPane = new EJTMTEntireJGridPane(scrollComposite, numCols);
                    _mainPane.cleanLayout();
                    EJCanvasPropertiesContainer popupCanvasContainer = canvasProperties.getPopupCanvasContainer();
                    Collection<EJCanvasProperties> allCanvasProperties = popupCanvasContainer.getAllCanvasProperties();
                    for (EJCanvasProperties canvasProperties : allCanvasProperties)
                    {
                        createCanvas(_mainPane, canvasProperties, canvasController);
                    }
                    scrollComposite.setContent(_mainPane);
                    scrollComposite.setExpandHorizontal(true);
                    scrollComposite.setExpandVertical(true);
                    scrollComposite.setMinHeight(_mainPane.computeSize(SWT.DEFAULT, SWT.DEFAULT, true).y);
                }

                @Override
                protected void createButtonsForButtonBar(Composite parent)
                {
                    // Add the buttons in reverse order, as they will be added
                    // from left to right
                    addExtraButton(parent, button3Label, ID_BUTTON_3);
                    addExtraButton(parent, button2Label, ID_BUTTON_2);
                    addExtraButton(parent, button1Label, ID_BUTTON_1);
                }

                @Override
                public void open()
                {
                    setFocus(canvasProperties);
                }

                private void addExtraButton(Composite parent, String label, int id)
                {
                    if (label == null || label.length() == 0)
                    {
                        return;
                    }
                    page.createButton(parent, id, label, false);

                }

                @Override
                public void close()
                {
                    final UIConfiguration configuration = EJTMTContext.getUiConfiguration();
                    final UI ui = EJTMTContext.getTabrisUI();
                    String pageID = toPageID(name);
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
                    switch (buttonId)
                    {

                        case ID_BUTTON_1:
                        {
                            canvasController.closePopupCanvas(name, EJPopupButton.ONE);
                            break;
                        }
                        case ID_BUTTON_2:
                        {
                            canvasController.closePopupCanvas(name, EJPopupButton.TWO);
                            break;
                        }
                        case ID_BUTTON_3:
                        {
                            canvasController.closePopupCanvas(name, EJPopupButton.THREE);
                            close();
                            break;
                        }

                        default:
                            super.buttonPressed(buttonId);
                            break;
                    }

                }
            };

            String pageID = toPageID(name);
            final UI ui = EJTMTContext.getTabrisUI();
            final UIConfiguration configuration = EJTMTContext.getUiConfiguration();

            if (configuration.getPageConfiguration(pageID) == null)
            {
                PageConfiguration pageConfiguration = new PageConfiguration(pageID, EJTMTScreenPage.class).setTitle(pageTitle != null ? pageTitle : "");
                pageConfiguration.setStyle(PageStyle.DEFAULT);

                EJTMTApplicationContainer.addFormActions(_form, _form.getProperties(), pageID, pageConfiguration);
                configuration.addPageConfiguration(pageConfiguration);

            }
            PageData pageData = EJTMTScreenPage.createPageData(_popupDialog);
            pageData.set(EJTMTFormPage.FORM_ID_KEY, _form);
            ui.getPageOperator().openPage(pageID, pageData);
            setFocus(canvasProperties);
        }

        private String toPageID(String name)
        {
            return String.format("EJFP_%s", name);
        }

        void close()
        {
            if (_popupDialog != null)
            {
                _popupDialog.close();
            }
        }

        @Override
        public EJCanvasType getType()
        {
            return EJCanvasType.POPUP;
        }
    }

    class EJTabFolder
    {
        final TabFolder            folder;
        final Map<String, TabItem> tabPages = new HashMap<String, TabItem>();

        EJTabFolder(TabFolder folder)
        {
            super();
            this.folder = folder;
        }

        public void showPage(String pageName)
        {
            TabItem cTabItem = tabPages.get(pageName);
            if (cTabItem != null)
            {
                folder.setSelection(cTabItem);
            }

        }

        void clear()
        {
            tabPages.clear();
        }

        boolean containsKey(String key)
        {
            return tabPages.containsKey(key);
        }

        TabItem get(String key)
        {
            return tabPages.get(key);
        }

        TabItem put(String key, TabItem value)
        {
            return tabPages.put(key, value);
        }

        TabItem remove(String key)
        {
            return tabPages.remove(key);
        }

        public String getActiveKey()
        {
            TabItem[] selection = folder.getSelection();
            if (selection != null && selection.length > 0)
            {
                return (String) selection[0].getData("TAB_KEY");
            }
            return null;
        }

    }

    private interface CanvasHandler
    {
        EJCanvasType getType();

        void add(EJInternalBlock block);
    }

    @Override
    public String getDisplayedStackedPage(String key)
    {
        EJTMTEntireJStackedPane stackedPane = _stackedPanes.get(key);
        if (stackedPane != null)
        {
            return stackedPane.getActiveControlKey();
        }

        return null;
    }

    @Override
    public String getDisplayedTabPage(String key)
    {
        EJTabFolder tabFolder = _tabFolders.get(key);
        if (tabFolder != null)
        {
            return tabFolder.getActiveKey();
        }
        return null;
    }
}
