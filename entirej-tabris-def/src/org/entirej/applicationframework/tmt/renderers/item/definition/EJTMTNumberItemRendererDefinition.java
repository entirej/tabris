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
package org.entirej.applicationframework.tmt.renderers.item.definition;

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.entirej.framework.core.properties.definitions.EJPropertyDefinitionType;
import org.entirej.framework.core.properties.definitions.interfaces.EJFrameworkExtensionProperties;
import org.entirej.framework.core.properties.definitions.interfaces.EJPropertyDefinition;
import org.entirej.framework.core.properties.definitions.interfaces.EJPropertyDefinitionGroup;
import org.entirej.framework.core.properties.definitions.interfaces.EJPropertyDefinitionListener;
import org.entirej.framework.dev.properties.EJDevPropertyDefinition;
import org.entirej.framework.dev.properties.EJDevPropertyDefinitionGroup;
import org.entirej.framework.dev.properties.interfaces.EJDevScreenItemDisplayProperties;
import org.entirej.framework.dev.renderer.definition.EJDevItemRendererDefinitionControl;
import org.entirej.framework.dev.renderer.definition.interfaces.EJDevItemRendererDefinition;


public class EJTMTNumberItemRendererDefinition implements EJDevItemRendererDefinition
{
    public static final String PROPERTY_MAXVALUE              = "MAX_VALUE";
    public static final String PROPERTY_MINVALUE              = "MIN_VALUE";
    public static final String PROPERTY_FORMAT                = "FORMAT";
    public static final String PROPERTY_DISPLAY_VAUE_AS_LABEL = "DISPLAY_VALUE_AS_LABEL";

    public EJTMTNumberItemRendererDefinition()
    {
    }

    public String getRendererClassName()
    {
        return "org.entirej.applicationframework.tmt.renderers.item.EJTMTNumberItemRenderer";
    }

    public boolean canExecuteActionCommand()
    {
        return false;
    }

    public void propertyChanged(EJPropertyDefinitionListener propertyDefListener, EJFrameworkExtensionProperties properties, String propertyName)
    {
        // no impl
    }

    public void loadValidValuesForProperty(EJFrameworkExtensionProperties frameworkExtensionProperties, EJPropertyDefinition propertyDefinition)
    {
        // no impl
    }

    public EJPropertyDefinitionGroup getItemPropertyDefinitionGroup()
    {
        EJDevPropertyDefinitionGroup mainGroup = new EJDevPropertyDefinitionGroup("Number Item Renderer");

        EJDevPropertyDefinition format = new EJDevPropertyDefinition(PROPERTY_FORMAT, EJPropertyDefinitionType.STRING);
        format.setLabel("Format");
        format.setDescription("The format of this item as defined by formats specified within java.text.DecimalFormat. e.g. ###,###,##0.00 where a # represents any number and a 0 indicates that a value will be displayed, even if it is 0");
        format.setDefaultValue("###,###,###,###.##");

        EJDevPropertyDefinition displayValueAsLabel = new EJDevPropertyDefinition(PROPERTY_DISPLAY_VAUE_AS_LABEL, EJPropertyDefinitionType.BOOLEAN);
        displayValueAsLabel.setLabel("Display value as label");
        displayValueAsLabel.setDefaultValue("false");
        displayValueAsLabel.setDescription("Indicates if this item should be displayed as a label. Items displayed as labels cannot be modified by the user.");

        EJDevPropertyDefinition textAlignment = new EJDevPropertyDefinition(EJTMTTextItemRendererDefinition.PROPERTY_ALIGNMENT, EJPropertyDefinitionType.STRING);
        textAlignment.setLabel("Alignment");
        textAlignment.setDescription("The alignment of the text displayed within this item");
        textAlignment.addValidValue(EJTMTTextItemRendererDefinition.PROPERTY_ALIGNMENT_LEFT, "Left");
        textAlignment.addValidValue(EJTMTTextItemRendererDefinition.PROPERTY_ALIGNMENT_RIGHT, "Right");
        textAlignment.addValidValue(EJTMTTextItemRendererDefinition.PROPERTY_ALIGNMENT_CENTER, "Center");

        EJDevPropertyDefinition selectOnFocus = new EJDevPropertyDefinition(EJTMTTextItemRendererDefinition.PROPERTY_SELECT_ON_FOCUS,
                EJPropertyDefinitionType.BOOLEAN);
        selectOnFocus.setLabel("Select on focus");
        selectOnFocus.setDescription("Indicates if this item should select text on focus");
        selectOnFocus.setDefaultValue("true");
        
      //tabris keyboard layout options 
        EJDevPropertyDefinition keyborad = new EJDevPropertyDefinition(EJTMTTextItemRendererDefinition.PROPERTY_KEYBOARD, EJPropertyDefinitionType.STRING);
        keyborad.setLabel("Keyboard");
        keyborad.setDescription("The keybord layout when enter values.");
        keyborad.setDefaultValue(EJTMTTextItemRendererDefinition.PROPERTY_KEYBOARD_NUMBERS);
        keyborad.addValidValue(EJTMTTextItemRendererDefinition.PROPERTY_KEYBOARD_NUMBERS, "Numbers");
        keyborad.addValidValue(EJTMTTextItemRendererDefinition.PROPERTY_KEYBOARD_NUMBERS_AND_PUNCTUATION, "Numbers and Punctuation");
        keyborad.addValidValue(EJTMTTextItemRendererDefinition.PROPERTY_KEYBOARD_DECIMAL, "Decimal");

        mainGroup.addPropertyDefinition(format);
        mainGroup.addPropertyDefinition(textAlignment);
        mainGroup.addPropertyDefinition(keyborad);
        mainGroup.addPropertyDefinition(selectOnFocus);
        mainGroup.addPropertyDefinition(displayValueAsLabel);

        return mainGroup;
    }

    @Override
    public EJDevItemRendererDefinitionControl getItemControl(EJDevScreenItemDisplayProperties itemProperties, Composite parent, FormToolkit toolkit)
    {
        Text text = toolkit.createText(parent, "");
        return new EJDevItemRendererDefinitionControl(itemProperties, text);
    }

    @Override
    public Control getLabelControl(EJDevScreenItemDisplayProperties itemProperties, Composite parent, FormToolkit toolkit)
    {
        String labelText = itemProperties.getLabel();
        Label label = new Label(parent, SWT.NULL);
        label.setText(labelText == null ? "" : labelText);
        return label;
    }

    public boolean isReadOnly()
    {
        return false;
    }
}
