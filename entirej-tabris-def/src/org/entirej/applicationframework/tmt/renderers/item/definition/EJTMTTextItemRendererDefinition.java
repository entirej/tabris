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

public class EJTMTTextItemRendererDefinition implements EJDevItemRendererDefinition
{
    public static final String PROPERTY_MAXLENGTH                        = "MAXLENGTH";
    public static final String PROPERTY_CASE                             = "CASE";
    public static final String PROPERTY_CASE_UPPER                       = "UPPER";
    public static final String PROPERTY_CASE_LOWER                       = "LOWER";
    public static final String PROPERTY_CASE_MIXED                       = "MIXED";
    public static final String PROPERTY_ALIGNMENT                        = "ALIGNMENT";
    public static final String PROPERTY_ALIGNMENT_LEFT                   = "LEFT";
    public static final String PROPERTY_ALIGNMENT_RIGHT                  = "RIGHT";
    public static final String PROPERTY_ALIGNMENT_CENTER                 = "CENTER";
    public static final String PROPERTY_WRAP                             = "WRAP";
    public static final String PROPERTY_DISPLAY_VAUE_AS_LABEL            = "DISPLAY_VALUE_AS_LABEL";
    public static final String PROPERTY_SELECT_ON_FOCUS                  = "SELECT_ON_FOCUS";
    public static final String PROPERTY_DISPLAY_VAUE_AS_PROTECTED        = "PROTECTED";

    public static final String PROPERTY_KEYBOARD                         = "KEYBOARD";
    public static final String PROPERTY_KEYBOARD_ASCII                   = "ASCII";
    public static final String PROPERTY_KEYBOARD_NUMBERS                 = "NUMBERS";
    public static final String PROPERTY_KEYBOARD_DECIMAL                 = "DECIMAL";
    public static final String PROPERTY_KEYBOARD_NUMBERS_AND_PUNCTUATION = "NUMBERS_AND_PUNCTUATION";
    public static final String PROPERTY_KEYBOARD_URL                     = "URL";
    public static final String PROPERTY_KEYBOARD_PHONE                   = "PHONE";
    public static final String PROPERTY_KEYBOARD_EMAIL                   = "EMAIL";

    public EJTMTTextItemRendererDefinition()
    {
    }

    public String getRendererClassName()
    {
        return "org.entirej.applicationframework.tmt.renderers.item.EJTMTTextItemRenderer";
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
        EJDevPropertyDefinitionGroup mainGroup = new EJDevPropertyDefinitionGroup("Text Item Renderer");

        EJDevPropertyDefinition maxLength = new EJDevPropertyDefinition(PROPERTY_MAXLENGTH, EJPropertyDefinitionType.INTEGER);
        maxLength.setLabel("Maximum Length");
        maxLength.setDescription("The maximum amount of characters allowed within this item");

        EJDevPropertyDefinition textCase = new EJDevPropertyDefinition(PROPERTY_CASE, EJPropertyDefinitionType.STRING);
        textCase.setLabel("Case");
        textCase.setDescription("The character case for the text displayed within this item");
        textCase.addValidValue(PROPERTY_CASE_UPPER, "Upper");
        textCase.addValidValue(PROPERTY_CASE_LOWER, "Lower");
        textCase.addValidValue(PROPERTY_CASE_MIXED, "Mixed");

        EJDevPropertyDefinition textAlignment = new EJDevPropertyDefinition(PROPERTY_ALIGNMENT, EJPropertyDefinitionType.STRING);
        textAlignment.setLabel("Alignment");
        textAlignment.setDescription("The alignment of the text displayed within this item");
        textAlignment.addValidValue(PROPERTY_ALIGNMENT_LEFT, "Left");
        textAlignment.addValidValue(PROPERTY_ALIGNMENT_RIGHT, "Right");
        textAlignment.addValidValue(PROPERTY_ALIGNMENT_CENTER, "Center");

        EJDevPropertyDefinition displayValueAsLabel = new EJDevPropertyDefinition(PROPERTY_DISPLAY_VAUE_AS_LABEL, EJPropertyDefinitionType.BOOLEAN);
        displayValueAsLabel.setLabel("Display value as label");
        displayValueAsLabel.setDefaultValue("false");
        displayValueAsLabel.setDescription("Indicates if this item should be displayed as a label. Items displayed as labels cannot be modified by the user.");

        EJDevPropertyDefinition protectedField = new EJDevPropertyDefinition(PROPERTY_DISPLAY_VAUE_AS_PROTECTED, EJPropertyDefinitionType.BOOLEAN);
        protectedField.setLabel("Display value as Protected");
        protectedField
                .setDescription("Indicates if this item should be displayed as a Protected. Items displayed as labels cannot be seen by the user eg'*****'.");

        EJDevPropertyDefinition selectOnFocus = new EJDevPropertyDefinition(PROPERTY_SELECT_ON_FOCUS, EJPropertyDefinitionType.BOOLEAN);
        selectOnFocus.setLabel("Select on focus");
        selectOnFocus.setDescription("Indicates if this item should select text on focus");
        selectOnFocus.setDefaultValue("false");

        // tabris keyboard layout options
        EJDevPropertyDefinition keyborad = new EJDevPropertyDefinition(EJTMTTextItemRendererDefinition.PROPERTY_KEYBOARD, EJPropertyDefinitionType.STRING);
        keyborad.setLabel("Keyboard");
        keyborad.setDescription("The keybord layout when enter values.");
        keyborad.setDefaultValue(EJTMTTextItemRendererDefinition.PROPERTY_KEYBOARD_ASCII);
        keyborad.addValidValue(EJTMTTextItemRendererDefinition.PROPERTY_KEYBOARD_ASCII, "ASCII");
        keyborad.addValidValue(EJTMTTextItemRendererDefinition.PROPERTY_KEYBOARD_NUMBERS, "Numbers");
        keyborad.addValidValue(EJTMTTextItemRendererDefinition.PROPERTY_KEYBOARD_NUMBERS_AND_PUNCTUATION, "Numbers and Punctuation");
        keyborad.addValidValue(EJTMTTextItemRendererDefinition.PROPERTY_KEYBOARD_DECIMAL, "Decimal");
        keyborad.addValidValue(EJTMTTextItemRendererDefinition.PROPERTY_KEYBOARD_URL, "URL");
        keyborad.addValidValue(EJTMTTextItemRendererDefinition.PROPERTY_KEYBOARD_EMAIL, "Email");
        keyborad.addValidValue(EJTMTTextItemRendererDefinition.PROPERTY_KEYBOARD_PHONE, "Phone");

        mainGroup.addPropertyDefinition(maxLength);
        mainGroup.addPropertyDefinition(textCase);
        mainGroup.addPropertyDefinition(textAlignment);
        mainGroup.addPropertyDefinition(keyborad);

        mainGroup.addPropertyDefinition(displayValueAsLabel);
        mainGroup.addPropertyDefinition(protectedField);
        mainGroup.addPropertyDefinition(selectOnFocus);

        return mainGroup;
    }

    @Override
    public EJDevItemRendererDefinitionControl getItemControl(EJDevScreenItemDisplayProperties itemProperties, Composite parent, FormToolkit toolkit)
    {

        Text text = toolkit.createText(parent, "");
        text.setEditable(false);
        return new EJDevItemRendererDefinitionControl(itemProperties, text);
    }

    /**
     * Used to return the label widget for this item
     * <p>
     * If the widget does not display a label, then this method should do
     * nothing and <code>null</code> should be returned
     * 
     * @param parent
     *            The <code>Composite</code> upon which this widgets label will
     *            be displayed
     * @param screemDisplayProperties
     *            The display properties of this item
     * @param formToolkit
     *            The toolkit to use for the creation of the label widget
     * @return The label widget or <code>null</code> if this item displays no
     *         label
     */
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
