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
/**
 * 
 */
package org.entirej.applicationframework.tmt.renderers.blocks.definition.interfaces;

public interface EJTMTSingleRecordBlockDefinitionProperties
{
    public final String INITIALLY_DISPLAYED_PROPERTY        = "INITIALLY_DISPLAYED";

    public final String DISPLAY_FOCUSED_BORDER              = "DISPLAY_FOCUS_BORDER";
    public final String ITEM_POSITION_PROPERTY              = "ITEM_POSITION";
    public final String LABEL_POSITION_PROPERTY             = "LABEL_POSITION";
    public final String LABEL_ORIENTATION_PROPERTY          = "LABEL_ORIENTATION";
    public final String LABEL_POSITION_LEFT_PROPERTY        = "LEFT";
    public final String LABEL_POSITION_RIGHT_PROPERTY       = "RIGHT";

    public final String LABEL_ORIENTATION_LEFT_PROPERTY     = "LEFT";
    public final String LABEL_ORIENTATION_RIGHT_PROPERTY    = "RIGHT";
    public final String LABEL_ORIENTATION_CENTER_PROPERTY   = "CENTER";

    public final String MAIN_DISPLAY_COORDINATES_GROUP      = "MAIN_DISPLAY_COORDINATES";
    public final String MAIN_XSPAN_PROPERTY                 = "XSPAN";
    public final String MAIN_YSPAN_PROPERTY                 = "YSPAN";
    public final String MAIN_EXPAND_X_PROPERTY              = "EXPAND_X";
    public final String MAIN_EXPAND_Y_PROPERTY              = "EXPAND_Y";

    
    public final String VISUAL_ATTRIBUTE_PROPERTY           = "VISUAL_ATTRIBUTE";
    public final String DISPLAYED_WIDTH_PROPERTY            = "DISPLAYED_WIDTH";
    public final String DISPLAYED_HEIGHT_PROPERTY           = "DISPLAYED_HEIGHT";

    public final String ITEM_GROUP_TITLE_BAR                = "TITLE_BAR";
    public final String ITEM_GROUP_TITLE_BAR_TITLE          = "TITLE";
    public final String ITEM_GROUP_TITLE_BAR_MODE           = "TITLE_BAR_MODE";
    public final String ITEM_GROUP_TITLE_BAR_MODE_GROUP     = "GROUP";
    public final String ITEM_GROUP_TITLE_BAR_MODE_NO_EXPAND = "NO_EXPAND";
    public final String ITEM_GROUP_TITLE_BAR_MODE_TWISTIE   = "TWISTIE";
    public final String ITEM_GROUP_TITLE_BAR_MODE_TREE_NODE = "TREE_NODE";
    public final String ITEM_GROUP_TITLE_BAR_EXPANDED       = "TITLE_BAR_EXPANDED";

    public final String ITEM_GROUP_TITLE_BAR_ACTIONS        = "TITLE_BAR_ACTIONS";
    public final String ITEM_GROUP_TITLE_BAR_ACTION_ID      = "ACTION_ID";
    public final String ITEM_GROUP_TITLE_BAR_ACTION_IMAGE   = "ACTION_IMAGE";
    public final String ITEM_GROUP_TITLE_BAR_ACTION_TOOLTIP = "ACTION_TOOLTIP";
    public final String ITEM_GROUP_TITLE_BAR_ACTION_NAME    = "ACTION_NAME";

}
