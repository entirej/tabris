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
package org.entirej.applicationframework.tmt.renderers.blocks.definition.interfaces;

public interface EJTMTMultiRecordBlockDefinitionProperties
{


    public static final String ROW_HEIGHT                  = "ROW_HEIGHT";
    public static final String ROW_SELECTION               = "ROW_SELECTION";

    public static final String ROW_SELECTION_ACTION      = "ROW_SELECTION_ACTION";
    
    public static final String CELL_ACTION_COMMAND         = "ACTION_COMMAND";

    public static final String CELL_V_ALIGNMENT            = "V_ALLIGN";
    public static final String CELL_H_ALIGNMENT            = "H_ALLIGN";
    public static final String CELL_TOP                    = "C_TOP";
    public static final String CELL_BOTTOM                 = "C_BOTTOM";
    public static final String CELL_RIGHT                  = "C_RIGHT";
    public static final String CELL_LEFT                   = "C_LEFT";

    public static final String WIDTH_PROPERTY              = "WIDTH";
    public static final String HEIGHT_PROPERTY             = "HEIGHT";
    
    public static final String COLUMN_ALLIGN_NONE          = "NONE";
    public static final String COLUMN_ALLIGN_LEFT          = "LEFT";
    public static final String COLUMN_ALLIGN_CENTER        = "CENTER";
    public static final String COLUMN_ALLIGN_RIGHT         = "RIGHT";
    public static final String COLUMN_ALLIGN_TOP           = "TOP";
    public static final String COLUMN_ALLIGN_BOTTOM        = "BOTTOM";
    
    public static final String VISUAL_ATTRIBUTE_PROPERTY   = "VISUAL_ATTRIBUTE";

    public static final String FILTER                      = "FILTER";
}
