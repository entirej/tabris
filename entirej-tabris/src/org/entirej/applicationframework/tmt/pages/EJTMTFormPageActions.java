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
package org.entirej.applicationframework.tmt.pages;

import org.entirej.applicationframework.tmt.application.launcher.EJTMTContext;
import org.entirej.applicationframework.tmt.pages.EJTMTFormPage.FormActionConfiguration;
import org.entirej.framework.core.enumerations.EJScreenType;

public class EJTMTFormPageActions
{

    private EJTMTFormPageActions()
    {
        // private
    }

    public static void setActionEnableByPageId(String pageid, String actionCommand, boolean enable)
    {
        EJTMTContext.getTabrisUI().getActionOperator().setActionEnabled(FormActionConfiguration.toActionId(pageid, actionCommand), enable);
    }

    public static void setActionEnableByFormId(String formid, String actionCommand, boolean enable)
    {
        EJTMTContext.getTabrisUI().getActionOperator()
                .setActionEnabled(FormActionConfiguration.toActionId(EJTMTFormPage.toFormPageID(formid), actionCommand), enable);
    }

    public static void setActionEnableByScreen(String formid, String block, String actionCommand, EJScreenType type, boolean enable)
    {
        EJTMTContext.getTabrisUI().getActionOperator()
                .setActionEnabled(FormActionConfiguration.toActionId(EJTMTScreenPage.toPageID(formid, block, type), actionCommand), enable);
    }

    public static boolean isActionEnableByPageId(String pageid, String actionCommand)
    {

        return EJTMTContext.getTabrisUI().getActionOperator().isActionEnabled(FormActionConfiguration.toActionId(pageid, actionCommand));
    }

    public static boolean isActionEnableByFormId(String formid, String actionCommand)
    {
        return EJTMTContext.getTabrisUI().getActionOperator()
                .isActionEnabled(FormActionConfiguration.toActionId(EJTMTFormPage.toFormPageID(formid), actionCommand));
    }

    public static boolean isActionEnableByScreen(String formid, String block, String actionCommand, EJScreenType type, boolean enable)
    {
        return EJTMTContext.getTabrisUI().getActionOperator()
                .isActionEnabled(FormActionConfiguration.toActionId(EJTMTScreenPage.toPageID(formid, block, type), actionCommand));
    }

    public static void setActionVisibleByPageId(String pageid, String actionCommand, boolean visible)
    {
        EJTMTContext.getTabrisUI().getActionOperator().setActionVisible(FormActionConfiguration.toActionId(pageid, actionCommand), visible);
    }

    public static void setActionVisibleByFormId(String formid, String actionCommand, boolean enable)
    {
        EJTMTContext.getTabrisUI().getActionOperator()
                .setActionVisible(FormActionConfiguration.toActionId(EJTMTFormPage.toFormPageID(formid), actionCommand), enable);
    }

    public static void setActionVisibleByScreen(String formid, String block, String actionCommand, EJScreenType type, boolean visible)
    {
        EJTMTContext.getTabrisUI().getActionOperator()
                .setActionVisible(FormActionConfiguration.toActionId(EJTMTScreenPage.toPageID(formid, block, type), actionCommand), visible);
    }

    public static boolean isActionVisibleByPageId(String pageid, String actionCommand)
    {

        return EJTMTContext.getTabrisUI().getActionOperator().isActionVisible(FormActionConfiguration.toActionId(pageid, actionCommand));
    }

    public static boolean isActionVisibleByFormId(String formid, String actionCommand)
    {
        return EJTMTContext.getTabrisUI().getActionOperator()
                .isActionVisible(FormActionConfiguration.toActionId(EJTMTFormPage.toFormPageID(formid), actionCommand));
    }

    public static boolean isActionVisibleByScreen(String formid, String block, String actionCommand, EJScreenType type, boolean enable)
    {
        return EJTMTContext.getTabrisUI().getActionOperator()
                .isActionVisible(FormActionConfiguration.toActionId(EJTMTScreenPage.toPageID(formid, block, type), actionCommand));
    }

}
