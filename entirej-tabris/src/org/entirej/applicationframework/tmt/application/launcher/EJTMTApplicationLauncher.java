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
package org.entirej.applicationframework.tmt.application.launcher;

import static com.eclipsesource.tabris.internal.Clauses.whenNull;
import static org.eclipse.rap.rwt.internal.service.ContextProvider.getApplicationContext;
import static org.eclipse.rap.rwt.internal.service.ContextProvider.getContext;

import java.io.IOException;
import java.io.InputStream;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.rap.json.JsonValue;
import org.eclipse.rap.rwt.RWT;
import org.eclipse.rap.rwt.application.Application;
import org.eclipse.rap.rwt.application.Application.OperationMode;
import org.eclipse.rap.rwt.application.ApplicationConfiguration;
import org.eclipse.rap.rwt.application.EntryPoint;
import org.eclipse.rap.rwt.application.EntryPointFactory;
import org.eclipse.rap.rwt.client.WebClient;
import org.eclipse.rap.rwt.internal.lifecycle.RWTLifeCycle;
import org.eclipse.rap.rwt.service.ResourceLoader;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;
import org.entirej.applicationframework.tmt.application.EJTMTApplicationContainer;
import org.entirej.applicationframework.tmt.application.EJTMTApplicationManager;
import org.entirej.applicationframework.tmt.application.interfaces.EJTMTAppComponentRenderer;
import org.entirej.applicationframework.tmt.pages.EJTMTFormComponentPage;
import org.entirej.applicationframework.tmt.pages.EJTMTMenuComponentPage;
import org.entirej.framework.core.EJFrameworkHelper;
import org.entirej.framework.core.EJFrameworkInitialiser;
import org.entirej.framework.core.properties.EJCoreLayoutContainer;
import org.entirej.framework.core.properties.EJCoreMenuProperties;
import org.entirej.framework.core.properties.EJCoreProperties;

import com.eclipsesource.tabris.TabrisClientInstaller;
import com.eclipsesource.tabris.ui.Action;
import com.eclipsesource.tabris.ui.ActionConfiguration;
import com.eclipsesource.tabris.ui.Page;
import com.eclipsesource.tabris.ui.PageConfiguration;
import com.eclipsesource.tabris.ui.PlacementPriority;
import com.eclipsesource.tabris.ui.TabrisUI;
import com.eclipsesource.tabris.ui.UIConfiguration;

public abstract class EJTMTApplicationLauncher implements ApplicationConfiguration
{

    public void configure(Application configuration)
    {
        createEntryPoint(configuration);
    }

    protected String getFavicon()
    {
        return "icons/favicon.ico";
    }

    protected String getApplicationIcon()
    {
        return "icons/EJ.png";
    }

    protected String getLoadingImage()
    {
        return "icons/ej-default_loading.gif";
    }

    protected String getLoadingMessage()
    {
        return "EJ Loading...";
    }

    protected String getWebPathContext()
    {
        return "ej";
    }
    
    
    protected int getSessionTimeout()
    {
        return 60*10;
    }

    protected String getBodyHtml()
    {
        StringBuilder b = new StringBuilder();
        b.append("<div id=\"splash\" style=\"width:100%;  position: absolute;  top: 50%;   text-align: center;\">");
        b.append("<img src=\"./rwt-resources/");
        b.append(getLoadingImage());
        b.append("\"  style=\"margin: 10px 15px 0\" />");
        b.append("<div style=\"margin: 5px 15px 10px;  font: 12px Verdana, 'Lucida Sans', sans-serif\">");
        b.append(getLoadingMessage());
        b.append("</div></div>");

        return b.toString();
    }

    public void createEntryPoint(final Application configuration)
    {
        TabrisClientInstaller.install(configuration);
        Map<String, String> properties = new HashMap<String, String>();

        configuration.setOperationMode(OperationMode.SWT_COMPATIBILITY);
        if (this.getClass().getClassLoader().getResource("application.ejprop") != null)
        {
            EJFrameworkInitialiser.initialiseFramework("application.ejprop");
        }
        else if (this.getClass().getClassLoader().getResource("EntireJApplication.properties") != null)
        {

            EJFrameworkInitialiser.initialiseFramework("EntireJApplication.properties");
        }
        else
        {
            throw new RuntimeException("application.ejprop not found");
        }
        EJCoreLayoutContainer layoutContainer = EJCoreProperties.getInstance().getLayoutContainer();
        properties.put(WebClient.PAGE_TITLE, layoutContainer.getTitle());
        properties.put(WebClient.FAVICON, getFavicon());
        properties.put(WebClient.BODY_HTML, getBodyHtml());
        configuration.addResource(getFavicon(), new FileResource());
        configuration.addResource(getLoadingImage(), new FileResource());

        configuration.addEntryPoint(String.format("/%s", getWebPathContext()), new EntryPointFactory()
        {

            public EntryPoint create()
            {
                try
                {
                    registerServiceHandlers();
                }
                catch (java.lang.IllegalArgumentException e)
                {
                    // ignore if already registered
                }
                return new EntryPoint()
                {

                    public int createUI()
                    {

                        EJTMTContext.initContext();

                        EJTMTApplicationManager applicationManager = null;

                        if (this.getClass().getClassLoader().getResource("application.ejprop") != null)
                        {
                            applicationManager = (EJTMTApplicationManager) EJFrameworkInitialiser.initialiseFramework("application.ejprop");
                        }
                        else if (this.getClass().getClassLoader().getResource("EntireJApplication.properties") != null)
                        {

                            applicationManager = (EJTMTApplicationManager) EJFrameworkInitialiser.initialiseFramework("EntireJApplication.properties");
                        }
                        else
                        {
                            throw new RuntimeException("application.ejprop not found");
                        }

                        //set timeout
                        int sessionTimeout = getSessionTimeout();
                        RWT.getUISession().getHttpSession().setMaxInactiveInterval(sessionTimeout);
                        getContext().getProtocolWriter().appendHead( "tabris.UI", JsonValue.valueOf( true ) );
                        getContext().getUISession().setAttribute("ej.applicationManager", applicationManager);
                        EJTMTAuthenticateProvider authenticateProvider = getAuthenticateProvider(applicationManager);
                        if (authenticateProvider != null)
                        {
                            authenticateProvider.authenticate(applicationManager);
                        }
                        Display display = Display.getDefault();
                        if (display.isDisposed())
                            display = new Display();
                        preApplicationBuild(applicationManager);

                        // build tabris ui
                        UIConfiguration uiConfiguration = new UIConfiguration();
                        uiConfiguration.setImage(EJTMTApplicationLauncher.class.getClassLoader().getResourceAsStream(getApplicationIcon()));
                        getContext().getUISession().setAttribute("ej.tabrisUIConfiguration", uiConfiguration);

                        

                        Shell shell = new Shell(display, SWT.NO_TRIM);
                        // Now build the application container
                        EJTMTApplicationContainer appContainer = new EJTMTApplicationContainer();
                        applicationManager.setApplicationContainer(appContainer);
                        appContainer.buildComponents();
                        List<EJTMTAppComponentRenderer> components = appContainer.getComponents();
                        for (EJTMTAppComponentRenderer renderer : components)
                        {
                            String pageId = renderer.getPageId();
                            if(pageId!=null && pageId.length()>0)
                            {
                                uiConfiguration.addPageConfiguration(renderer.createPageConfiguration());
                            }
                        }
                        
                        initRootPageConfiguration(uiConfiguration);
                        create(shell, uiConfiguration);
                        applicationManager.buildApplication( shell);
                        postApplicationBuild(applicationManager);
                        shell.layout();
                        shell.setMaximized(true);

                        return openShell(display, shell);
                    }

                    public void create(Shell shell, UIConfiguration uiConfiguration)
                    {
                        whenNull(shell).throwIllegalArgument("Shell must not be null");
                        prepareShell(shell);
                        
                        TabrisUI tabrisUI = new TabrisUI(uiConfiguration);
                        tabrisUI.create(shell);
                    }

                    private void prepareShell(Shell shell)
                    {
                        shell.setMaximized(true);
                    }

                };
            }
        }, properties);
    }

    protected void initRootPageConfiguration(UIConfiguration configuration)
    {
           
    }

    static String getDefaultMenuID()
    {
        Collection<EJCoreMenuProperties> allMenuProperties = EJCoreProperties.getInstance().getMenuContainer().getAllMenuProperties();
        String defaultMenu = null;
        for (EJCoreMenuProperties ejCoreMenuProperties : allMenuProperties)
        {
            if (ejCoreMenuProperties.isDefault())
            {
                defaultMenu = ejCoreMenuProperties.getName();
                break;
            }
        }
        if (defaultMenu == null)
        {
            throw new RuntimeException("application.ejprop default menu not defined");
        }
        return defaultMenu;
    }

    public static class DefaultMenuPage extends EJTMTMenuComponentPage
    {
        public static final String ID = "EJTMTDMP";

        public DefaultMenuPage()
        {
            super(getDefaultMenuID());
        }

    }

    protected PageConfiguration addRootPageConfiguration(UIConfiguration configuration, String id, Class<? extends Page> page, String title, InputStream image)
    {
        PageConfiguration pageConfig = new PageConfiguration(id, page);
        pageConfig.setTitle(title);
        if (image != null)
            pageConfig.setImage(image);
        pageConfig.setTopLevel(true);

        configuration.addPageConfiguration(pageConfig);
        return pageConfig;
    }

    protected PageConfiguration addRootPageConfiguration(UIConfiguration configuration, String id, Class<? extends Page> page, String title)
    {
        return addRootPageConfiguration(configuration, id, page, title, null);
    }

    protected PageConfiguration addRootFormComponentPage(UIConfiguration configuration, String id, String formId, Class<? extends EJTMTFormComponentPage> page,
            String title, InputStream image)
    {
        PageConfiguration pageConfig = new PageConfiguration(id, page);
        pageConfig.setTitle(title);
        if (image != null)
            pageConfig.setImage(image);
        pageConfig.setTopLevel(true);

        EJTMTFormComponentPage.addFormRendererActions(id, pageConfig, formId);

        configuration.addPageConfiguration(pageConfig);
        return pageConfig;
    }

    protected PageConfiguration addRootFormComponentPage(UIConfiguration configuration, String id, String formId, Class<? extends EJTMTFormComponentPage> page,
            String title)
    {
        return addRootFormComponentPage(configuration, id, formId, page, title, null);
    }

    protected ActionConfiguration addRootActionConfiguration(UIConfiguration configuration, String id, Class<? extends Action> action,
            PlacementPriority priority, String title, InputStream image)
    {
        ActionConfiguration sctionConfig = new ActionConfiguration(id, action);
        sctionConfig.setTitle(title);
        if (image != null)
            sctionConfig.setImage(image);
        configuration.addActionConfiguration(sctionConfig);
        return sctionConfig;
    }

    protected ActionConfiguration addPageActionConfiguration(PageConfiguration configuration, String id, Class<? extends Action> action,
            PlacementPriority priority, String title, InputStream image)
    {
        ActionConfiguration sctionConfig = new ActionConfiguration(id, action);
        sctionConfig.setTitle(title);
        if (image != null)
            sctionConfig.setImage(image);
        configuration.addActionConfiguration(sctionConfig);
        return sctionConfig;
    }

    protected ActionConfiguration addRootActionConfiguration(UIConfiguration configuration, String id, Class<? extends Action> action,
            PlacementPriority priority, String title)
    {
        return addRootActionConfiguration(configuration, id, action, priority, title, null);
    }

    protected ActionConfiguration addPageActionConfiguration(PageConfiguration configuration, String id, Class<? extends Action> action,
            PlacementPriority priority, String title)
    {
        return addPageActionConfiguration(configuration, id, action, priority, title, null);
    }

    public static int openShell(Display display, Shell shell)
    {
        shell.open();
        if (getApplicationContext().getLifeCycleFactory().getLifeCycle() instanceof RWTLifeCycle)
        {
            while (!shell.isDisposed())
            {
                if (!display.readAndDispatch())
                {
                    display.sleep();
                }
            }
        }
        return 0;
    }

    public void preApplicationBuild(EJFrameworkHelper frameworkHelper)
    {
    }

    public void postApplicationBuild(EJFrameworkHelper frameworkHelper)
    {
    }

    public EJTMTAuthenticateProvider getAuthenticateProvider(EJFrameworkHelper frameworkHelper)
    {
        return null;
    }

    public void registerServiceHandlers()
    {

    }

    public abstract class EJTMTDefaultAuthenticateProvider implements EJTMTAuthenticateProvider
    {
        public final void authenticate(EJFrameworkHelper frameworkHelper)
        {
            create(frameworkHelper);

        }

        public void create(final EJFrameworkHelper frameworkHelper)
        {

            Display display = Display.getDefault();
            Shell _shell = new Shell(display, SWT.NO_TRIM | SWT.APPLICATION_MODAL);

            _shell.setLayout(new FillLayout());

            createLoginBody(frameworkHelper, display, _shell);
            _shell.layout();
            _shell.setMaximized(true);
            openShell(_shell.getDisplay(), _shell);
        }

        protected void createLoginBody(final EJFrameworkHelper frameworkHelper, Display display, Composite parentBody)
        {
            final Group _body = new Group(parentBody, SWT.NONE);

            EJCoreLayoutContainer layoutContainer = EJCoreProperties.getInstance().getLayoutContainer();
            _body.setText(layoutContainer.getTitle());

            GridLayout loginGroupLayout = new GridLayout(1, false);
            _body.setLayout(loginGroupLayout);

            final GridData gdError = new GridData(SWT.FILL, SWT.TOP, true, true);

            final Text txtUsername = new Text(_body, SWT.BORDER);
            txtUsername.setToolTipText("Username");
            GridData gdUsername = new GridData(SWT.FILL, SWT.CENTER, true, false);
            txtUsername.setMessage("Username");
            txtUsername.setLayoutData(gdUsername);

            final Text txtPwd = new Text(_body, SWT.BORDER | SWT.PASSWORD);
            txtPwd.setToolTipText("Password");
            txtPwd.setMessage("Password");
            txtPwd.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));

            new Label(_body, SWT.NONE);
            final Button btnLogin = new Button(_body, SWT.PUSH);
            parentBody.getShell().setDefaultButton(btnLogin);
            btnLogin.setText("Sign in");

            final Label lblError = new Label(_body, SWT.WRAP);
            ;
            lblError.setLayoutData(gdError);
            txtUsername.setFocus();
            btnLogin.addSelectionListener(new SelectionAdapter()
            {

                private static final long serialVersionUID = 1L;

                @Override
                public void widgetSelected(SelectionEvent e)
                {

                    lblError.setText("");
                    try
                    {
                        txtUsername.setEnabled(false);
                        txtPwd.setEnabled(false);
                        btnLogin.setEnabled(false);
                        String usernameStr = txtUsername.getText();
                        String passwordStr = txtPwd.getText();

                        performLogin(frameworkHelper, lblError, usernameStr, passwordStr);
                    }
                    finally
                    {
                        if (!_body.isDisposed() && (!txtUsername.isDisposed() && !txtPwd.isDisposed() && !btnLogin.isDisposed()))
                        {
                            txtUsername.setEnabled(true);
                            txtPwd.setEnabled(true);
                            btnLogin.setEnabled(true);
                        }
                    }
                }
            });

        }

        protected void performLogin(final EJFrameworkHelper frameworkHelper, final Label lblError, String usernameStr, String passwordStr)
        {
            if (usernameStr == null || usernameStr.length() == 0)
            {
                lblError.setText("Enter your username.");
                return;
            }

            if (passwordStr == null || passwordStr.length() == 0)
            {
                lblError.setText("Enter your password.");
                return;
            }
            try
            {
                String authenticateError = authenticate(frameworkHelper, usernameStr, passwordStr);
                if (authenticateError != null)
                {
                    lblError.setText(authenticateError);
                    return;
                }
            }
            catch (Exception e)
            {
                frameworkHelper.handleException(e);
                lblError.setText("Internal Error occured on Authentication.");
                return;
            }
            lblError.getShell().dispose();
        }

        public abstract String authenticate(EJFrameworkHelper frameworkHelper, String user, String password);
    }

    public static interface EJTMTAuthenticateProvider
    {

        void authenticate(EJFrameworkHelper frameworkHelper);

    }

    public static class FileResource implements ResourceLoader
    {

        public InputStream getResourceAsStream(String arg0) throws IOException
        {
            return getLoader().getResourceAsStream(arg0);
        }

        public ClassLoader getLoader()
        {
            return EJTMTApplicationLauncher.class.getClassLoader();
        }

    }

}
