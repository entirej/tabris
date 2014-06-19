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
package org.entirej.applicationframework.tmt.renderers.item;

import java.io.Serializable;
import java.text.Collator;
import java.util.ArrayList;
import java.util.List;

import org.eclipse.jface.fieldassist.ControlDecoration;
import org.eclipse.jface.fieldassist.FieldDecorationRegistry;
import org.eclipse.jface.viewers.ColumnLabelProvider;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.rap.rwt.template.Cell;
import org.eclipse.rap.rwt.template.Template;
import org.eclipse.rap.rwt.template.TextCell;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.FocusEvent;
import org.eclipse.swt.events.FocusListener;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.MouseListener;
import org.eclipse.swt.events.VerifyEvent;
import org.eclipse.swt.events.VerifyListener;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;
import org.entirej.applicationframework.tmt.application.EJTMTImageRetriever;
import org.entirej.applicationframework.tmt.application.components.EJTMTAbstractActionText;
import org.entirej.applicationframework.tmt.renderer.interfaces.EJTMTAppItemRenderer;
import org.entirej.applicationframework.tmt.renderers.item.definition.interfaces.EJTMTTextItemRendererDefinitionProperties;
import org.entirej.applicationframework.tmt.table.EJTMTAbstractTableSorter;
import org.entirej.applicationframework.tmt.utils.EJTMTItemRendererVisualContext;
import org.entirej.applicationframework.tmt.utils.EJTMTVisualAttributeUtils;
import org.entirej.framework.core.EJApplicationException;
import org.entirej.framework.core.EJMessage;
import org.entirej.framework.core.data.EJDataRecord;
import org.entirej.framework.core.enumerations.EJLovDisplayReason;
import org.entirej.framework.core.interfaces.EJScreenItemController;
import org.entirej.framework.core.properties.EJCoreProperties;
import org.entirej.framework.core.properties.EJCoreVisualAttributeProperties;
import org.entirej.framework.core.properties.definitions.interfaces.EJFrameworkExtensionProperties;
import org.entirej.framework.core.properties.interfaces.EJItemProperties;
import org.entirej.framework.core.properties.interfaces.EJScreenItemProperties;

import com.eclipsesource.tabris.widgets.enhancement.TextDecorator;
import com.eclipsesource.tabris.widgets.enhancement.Widgets;

public class EJTMTTextItemRenderer implements EJTMTAppItemRenderer, FocusListener, Serializable, EJTMTItemTextChangeNotifier
{
    private List<ChangeListener>              _changeListeners = new ArrayList<EJTMTItemTextChangeNotifier.ChangeListener>(1);
    protected EJFrameworkExtensionProperties  _rendererProps;
    protected EJScreenItemController          _item;
    protected EJScreenItemProperties          _screenItemProperties;
    protected EJItemProperties                _itemProperties;
    protected String                          _registeredItemName;
    protected Text                            _textField;
    protected EJTMTAbstractActionText         _actionControl;
    protected Label                           _valueLabel;
    protected Label                           _label;
    protected boolean                         _isValid         = true;
    protected boolean                         _mandatory;
    protected int                             _maxLength;
    protected boolean                         _displayValueAsLabel;
    protected boolean                         _displayValueAsProtected;
    protected boolean                         _valueChanged;
    protected final TextModifyListener        _modifyListener  = new TextModifyListener();
    protected VALUE_CASE                      _valueCase       = VALUE_CASE.DEFAULT;

    protected boolean                         _lovActivated;

    protected EJCoreVisualAttributeProperties _visualAttributeProperties;
    protected EJCoreVisualAttributeProperties _initialVAProperties;
    protected ControlDecoration               _errorDecoration;
    protected ControlDecoration               _mandatoryDecoration;
    private EJTMTItemRendererVisualContext    _visualContext;

    protected Object                          _baseValue;

    protected boolean controlState(Control control)
    {
        return control != null && !control.isDisposed();

    }

    @Override
    public boolean useFontDimensions()
    {
        return true;
    }

    @Override
    public void refreshItemRendererProperty(String propertyName)
    {
    }

    @Override
    public void setInitialVisualAttribute(EJCoreVisualAttributeProperties va)
    {
        this._initialVAProperties = va;
        setVisualAttribute(va);

    }

    @Override
    public void refreshItemRenderer()
    {

    }

    @Override
    public void initialise(EJScreenItemController item, EJScreenItemProperties screenItemProperties)
    {

        _item = item;
        _itemProperties = _item.getReferencedItemProperties();
        _screenItemProperties = screenItemProperties;
        _rendererProps = _itemProperties.getItemRendererProperties();
        _maxLength = _rendererProps.getIntProperty(EJTMTTextItemRendererDefinitionProperties.PROPERTY_MAXLENGTH, 0);
        _displayValueAsLabel = _rendererProps.getBooleanProperty(EJTMTTextItemRendererDefinitionProperties.PROPERTY_DISPLAY_VAUE_AS_LABEL, false);
        _displayValueAsProtected = _rendererProps.getBooleanProperty(EJTMTTextItemRendererDefinitionProperties.PROPERTY_DISPLAY_VAUE_AS_PROTECTED, false);
        final String caseProperty = _rendererProps.getStringProperty(EJTMTTextItemRendererDefinitionProperties.PROPERTY_CASE);
        if (caseProperty != null && caseProperty.trim().length() > 0)
        {
            if (caseProperty.equals(EJTMTTextItemRendererDefinitionProperties.PROPERTY_CASE_LOWER))
            {
                _valueCase = VALUE_CASE.LOWER;
            }
            else if (caseProperty.equals(EJTMTTextItemRendererDefinitionProperties.PROPERTY_CASE_UPPER))
            {
                _valueCase = VALUE_CASE.UPPER;
            }
        }

        if (controlState(_actionControl))
        {
            _actionControl.setActionVisible(isLovActivated() && isEditAllowed());
        }
    }

    @Override
    public void setLabel(String label)
    {
        if (_label != null)
        {
            _label.setText(label == null ? "" : label);
        }
    }

    @Override
    public void setHint(String hint)
    {
        if (controlState(_textField))
        {
            _textField.setToolTipText(hint == null ? "" : hint);
        }
        if (controlState(_valueLabel))
        {
            _valueLabel.setToolTipText(hint == null ? "" : hint);
        }
    }

    @Override
    public void enableLovActivation(boolean activate)
    {
        _lovActivated = activate;
        if (_displayValueAsLabel)
        {
            return;
        }
        if (controlState(_actionControl))
        {
            _actionControl.setActionVisible(activate && isEditAllowed());
        }
    }

    @Override
    public EJScreenItemController getItem()
    {
        return _item;
    }

    public EJItemProperties getItemProperties()
    {
        return _itemProperties;
    }

    @Override
    public Control getGuiComponent()
    {
        if (_displayValueAsLabel)
        {
            return _valueLabel;
        }
        return _actionControl;
    }

    @Override
    public Label getGuiComponentLabel()
    {
        return _label;
    }

    @Override
    public void clearValue()
    {
        _baseValue = null;
        if (_displayValueAsLabel)
        {
            if (controlState(_valueLabel))
            {
                _valueLabel.setText("");
            }
        }

        try
        {
            _modifyListener.enable = false;
            if (controlState(_textField))
            {
                _textField.setText("");
            }
        }
        finally
        {
            _modifyListener.enable = true;
        }
    }

    @Override
    public String getRegisteredItemName()
    {
        return _registeredItemName;
    }

    @Override
    public Object getValue()
    {
        if (_displayValueAsLabel)
        {
            return _baseValue;
        }

        if (!controlState(_textField))
        {
            return _baseValue;
        }

        String value = _textField.getText();

        if (value == null || value.length() == 0)
        {
            value = null;
        }

        return _baseValue = value;
    }

    @Override
    public boolean isEditAllowed()
    {
        if (controlState(_textField))
        {
            return _textField.getEditable();
        }

        return false;
    }

    public boolean isLovActivated()
    {
        return _lovActivated;
    }

    @Override
    public boolean isVisible()
    {
        if (_displayValueAsLabel)
        {
            if (controlState(_valueLabel))
            {
                return _valueLabel.isVisible();
            }
        }
        else
        {
            if (controlState(_textField))
            {
                return _textField.isVisible();
            }
        }

        return false;
    }

    @Override
    public boolean isValid()
    {
        if (_displayValueAsLabel)
        {
            return true;
        }

        if (_isValid)
        {
            if (_mandatory && getValue() == null)
            {
                return false;
            }
            else
            {
                return true;
            }
        }
        else
        {
            return false;
        }
    }

    @Override
    public void gainFocus()
    {
        if (_displayValueAsLabel)
        {
            if (_valueLabel != null)
            {
                _valueLabel.forceFocus();
            }
        }
        else
        {
            if (controlState(_textField))
            {
                _textField.forceFocus();
            }
        }
    }

    @Override
    public void setEditAllowed(boolean editAllowed)
    {
        if (_displayValueAsLabel)
        {
            return;
        }
        if (controlState(_textField))
        {
            _textField.setEditable(editAllowed);
        }
        setMandatoryBorder(editAllowed && _mandatory);

        if (controlState(_actionControl))
        {
            _actionControl.setActionVisible(isLovActivated() && editAllowed);
        }
    }

    @Override
    public void setInitialValue(Object value)
    {
        setValue(value);
    }

    @Override
    public void setRegisteredItemName(String name)
    {
        _registeredItemName = name;
    }

    private String toCaseValue(String string)
    {
        switch (_valueCase)
        {
            case LOWER:
                string = string.toLowerCase();
                break;
            case UPPER:
                string = string.toUpperCase();
                break;
        }

        return string;
    }

    @Override
    public void setValue(Object value)
    {
        _baseValue = value;
        _valueChanged = false;
        try
        {
            _modifyListener.enable = false;
            if (_displayValueAsLabel)
            {
                if (controlState(_valueLabel))
                {
                    if (value == null)
                    {
                        _valueLabel.setText("");
                        _valueLabel.setToolTipText("");
                    }
                    else
                    {
                        _valueLabel.setText(toCaseValue(value.toString()));
                        _valueLabel.setToolTipText(value.toString());
                    }
                }
            }
            else
            {
                if (controlState(_textField))
                {
                    if (value != null)
                    {
                        if (_maxLength > 0 && value.toString().length() > _maxLength)
                        {
                            EJMessage message = new EJMessage("The value for item, " + _item.getReferencedItemProperties().getBlockName() + "."
                                    + _item.getReferencedItemProperties().getName() + " is too long for its field definition.");
                            throw new EJApplicationException(message);
                        }
                    }

                    _textField.setText(value == null ? "" : toCaseValue(value.toString()));
                    setMandatoryBorder(_mandatory);
                }
            }
        }
        finally
        {
            _modifyListener.enable = true;
        }
    }

    @Override
    public void setVisible(boolean visible)
    {
        if (_displayValueAsLabel)
        {
            if (controlState(_valueLabel))
            {
                _valueLabel.setVisible(visible);
            }
        }
        else
        {
            if (controlState(_textField))
            {
                _textField.setVisible(visible);
            }
        }

        if (controlState(_label))
        {
            _label.setVisible(visible);
        }
    }

    @Override
    public void setMandatory(boolean mandatory)
    {
        _mandatory = mandatory;
        
        if(controlState(_textField))
        {
            if(_mandatory)
            {
                _textField.setMessage("Required*");
            }
            else
            {
                _textField.setMessage("");
            }
        }
        
        
        setMandatoryBorder(mandatory);
    }

    @Override
    public boolean isMandatory()
    {
        return _mandatory;
    }

    @Override
    public boolean valueEqualsTo(Object value)
    {
        return value.equals(this.getValue());
    }

    @Override
    public void validationErrorOccurred(boolean error)
    {
        if (_displayValueAsLabel)
        {
            return;
        }
        if (_errorDecoration != null && !_errorDecoration.getControl().isDisposed())
        {
            _errorDecoration.setDescriptionText("");
            if (error)
            {
                _errorDecoration.show();
            }
            else
            {
                _errorDecoration.hide();
            }
        }

        fireTextChange();
    }

    public void valueChanged()
    {
        if (!_textField.isFocusControl())
        {
            _valueChanged = false;
            _item.itemValueChaged();
        }
        else
        {
            _valueChanged = true;
        }
        setMandatoryBorder(_mandatory);
        fireTextChange();
    }

    protected void setMandatoryBorder(boolean req)
    {
        if (_displayValueAsLabel || _mandatoryDecoration == null || _mandatoryDecoration.getControl().isDisposed())
        {
            return;
        }

        if (req && getValue() == null)
        {
            _mandatoryDecoration.show();
            
        }
        else
        {
            _mandatoryDecoration.hide();
        }
    }

    @Override
    public void focusGained(FocusEvent event)
    {
        _item.itemFocusGained();
        if (controlState(_textField))
        {
            _textField.forceFocus();
        }
    }

    @Override
    public void focusLost(FocusEvent event)
    {
        Display.getCurrent().asyncExec(new Runnable()
        {
            
            @Override
            public void run()
            {
                if (_valueChanged)
                {
                    _valueChanged = false;
                    _item.itemValueChaged();
                    setMandatoryBorder(_mandatory);
                    
                }
                _item.itemFocusLost();
                
            }
        });
    }

    @Override
    public void setVisualAttribute(EJCoreVisualAttributeProperties visualAttributeProperties)
    {
        _visualAttributeProperties = visualAttributeProperties != null ? visualAttributeProperties : _initialVAProperties;

        refreshBackground();
        refreshForeground();
        refreshFont();
    }

    @Override
    public EJCoreVisualAttributeProperties getVisualAttributeProperties()
    {
        return _visualAttributeProperties;
    }

    private void refreshBackground()
    {

        Color background = EJTMTVisualAttributeUtils.INSTANCE.getBackground(_visualAttributeProperties);
        Color color = background != null ? background : (_visualContext!=null ? _visualContext.getBackgroundColor() :null);
        if (_displayValueAsLabel)
        {
            if (controlState(_valueLabel))
            {
                if(color==null || !color.equals(_valueLabel.getBackground()))
                {
                    _valueLabel.setBackground(color);
                }
            }
        }
        else
        {
            if (controlState(_textField))
            {
                if(color==null || !color.equals(_textField.getBackground()))
                {
                    _textField.setBackground(color);
                }
            }
        }
    }

    private void refreshForeground()
    {

        Color foreground = EJTMTVisualAttributeUtils.INSTANCE.getForeground(_visualAttributeProperties);
        if (_displayValueAsLabel)
        {

            if (controlState(_valueLabel))
            {
                _valueLabel.setForeground(foreground != null ? foreground : _visualContext.getForegroundColor());
            }
        }
        else
        {
            if (controlState(_textField))
            {
                _textField.setForeground(foreground != null ? foreground : _visualContext.getForegroundColor());
            }
        }
    }

    private void refreshFont()
    {
        if (_displayValueAsLabel)
        {
            if (controlState(_valueLabel))
            {
                _valueLabel.setFont(EJTMTVisualAttributeUtils.INSTANCE.getFont(_visualAttributeProperties, _visualContext.getItemFont()));
            }

        }
        else
        {
            if (controlState(_textField))
            {
                _textField.setFont(EJTMTVisualAttributeUtils.INSTANCE.getFont(_visualAttributeProperties, _visualContext.getItemFont()));
            }
        }
    }

    @Override
    public String toString()
    {
        StringBuffer buffer = new StringBuffer();
        buffer.append("TextItem:\n");
        buffer.append("  RegisteredItemName: ");
        buffer.append(_registeredItemName);
        buffer.append("\n");
        buffer.append("  TextField: ");
        buffer.append(_textField);
        buffer.append("  Label: ");
        buffer.append(_label);
        buffer.append("  GUI Component: ");
        buffer.append(_textField);

        return buffer.toString();
    }

    protected Label newVlaueLabel(Composite composite)
    {
        return new Label(composite, SWT.NONE);
    }

    protected Text newTextField(Composite composite, int style)
    {
        final Text text = new Text(composite, style);
        if (_rendererProps != null && _rendererProps.getBooleanProperty(EJTMTTextItemRendererDefinitionProperties.PROPERTY_SELECT_ON_FOCUS, false))
        {
            text.addFocusListener(new FocusListener()
            {
                @Override
                public void focusLost(FocusEvent arg0)
                {
                    // ignore
                }

                @Override
                public void focusGained(FocusEvent arg0)
                {
                    text.selectAll();
                }
            });
        }

        String keyboard = _rendererProps.getStringProperty(EJTMTTextItemRendererDefinitionProperties.PROPERTY_KEYBOARD);
        setupKeyboard(text, keyboard == null ? EJTMTTextItemRendererDefinitionProperties.PROPERTY_KEYBOARD_ASCII : keyboard);

        return text;
    }

    public Control createCustomButtonControl(Composite parent)
    {
        return null;
    }

    @Override
    public void createComponent(Composite composite)
    {

        String alignmentProperty = _rendererProps.getStringProperty(EJTMTTextItemRendererDefinitionProperties.PROPERTY_ALIGNMENT);
        if (alignmentProperty == null)
        {
            alignmentProperty = _rendererProps.getStringProperty("ALLIGNMENT");
        }
        String hint = _screenItemProperties.getHint();

        if (_displayValueAsLabel)
        {
            _valueLabel = newVlaueLabel(composite);
            _valueLabel.setData(_itemProperties.getName());
            if (hint != null && hint.trim().length() > 0)
            {
                _valueLabel.setToolTipText(hint);
            }
            setValueLabelAlign(alignmentProperty);
            _visualContext = new EJTMTItemRendererVisualContext(_valueLabel.getBackground(), _valueLabel.getForeground(), _valueLabel.getFont());
            setInitialValue(_baseValue);
        }
        else
        {
            final String alignmentProp = alignmentProperty;
            _actionControl = new EJTMTAbstractActionText(composite)
            {
                @Override
                public Text createText(Composite parent)
                {
                    int style = SWT.BORDER;
                    if (_displayValueAsProtected)
                    {
                        style = style | SWT.PASSWORD;
                    }
                    _textField = newTextField(parent, getComponentStyle(alignmentProp, style));
                    return _textField;
                }

                @Override
                public Control createCustomActionLabel(Composite parent)
                {
                    return createCustomButtonControl(parent);
                }

                @Override
                public Control createActionLabel(Composite parent)
                {
                    Button label = new Button(parent, SWT.NONE);
                    label.setImage(EJTMTImageRetriever.get(EJTMTImageRetriever.IMG_FIND_LOV));
                    label.addFocusListener(EJTMTTextItemRenderer.this);
                    label.addMouseListener(new MouseListener()
                    {
                        private static final long serialVersionUID = 529634857284996692L;

                        @Override
                        public void mouseUp(MouseEvent arg0)
                        {
                            _item.getItemLovController().displayLov(EJLovDisplayReason.LOV);
                        }

                        @Override
                        public void mouseDown(MouseEvent arg0)
                        {

                        }

                        @Override
                        public void mouseDoubleClick(MouseEvent arg0)
                        {

                        }
                    });

                    final EJFrameworkExtensionProperties rendererProp = EJCoreProperties.getInstance().getApplicationDefinedProperties();

                    
                    
                    return label;
                }
            };

            if (_maxLength > 0)
            {
                _textField.setTextLimit(_maxLength);
            }
            if (hint != null && hint.trim().length() > 0)
            {
                _textField.setToolTipText(hint);
            }

            _visualContext = new EJTMTItemRendererVisualContext(_textField.getBackground(), _textField.getForeground(), _textField.getFont());

            _textField.setData(_item.getReferencedItemProperties().getName());
            _textField.addFocusListener(this);
            

            _mandatoryDecoration = new ControlDecoration(_actionControl, SWT.TOP | SWT.LEFT);
            _errorDecoration = new ControlDecoration(_actionControl, SWT.TOP | SWT.LEFT);
            _errorDecoration.setImage(EJTMTImageRetriever.get(EJTMTImageRetriever.IMG_ERROR_OVR));
            _mandatoryDecoration.setImage(EJTMTImageRetriever.get(EJTMTImageRetriever.IMG_REQUIRED_OVR));
            _mandatoryDecoration.setShowHover(true);
            _mandatoryDecoration.setDescriptionText(_screenItemProperties.getLabel() == null || _screenItemProperties.getLabel().isEmpty() ? "Required Item"
                    : String.format("%s is required", _screenItemProperties.getLabel()));
            if (_isValid)
            {
                _errorDecoration.hide();
            }
            _mandatoryDecoration.hide();
            _textField.addModifyListener(_modifyListener);
            if(_valueCase!=null && _valueCase!= VALUE_CASE.DEFAULT)
            {
                _textField.addVerifyListener(new VerifyListener()
                {
                    @Override
                    public void verifyText(VerifyEvent event)
                    {
                        event.text = toCaseValue(event.text);
                    }
                });
            }
            setInitialValue(_baseValue);
        }
    }

    protected void setValueLabelAlign(String alignmentProperty)
    {
        if (alignmentProperty != null && alignmentProperty.trim().length() > 0)
        {
            if (alignmentProperty.equals(EJTMTTextItemRendererDefinitionProperties.PROPERTY_ALIGNMENT_LEFT))
            {
                _valueLabel.setAlignment(SWT.LEFT);
            }
            else if (alignmentProperty.equals(EJTMTTextItemRendererDefinitionProperties.PROPERTY_ALIGNMENT_RIGHT))
            {
                _valueLabel.setAlignment(SWT.RIGHT);
            }
            else if (alignmentProperty.equals(EJTMTTextItemRendererDefinitionProperties.PROPERTY_ALIGNMENT_CENTER))
            {
                _valueLabel.setAlignment(SWT.CENTER);
            }
        }
    }

    protected int getComponentStyle(String alignmentProperty, int style)
    {
        if (alignmentProperty != null && alignmentProperty.trim().length() > 0)
        {
            if (alignmentProperty.equals(EJTMTTextItemRendererDefinitionProperties.PROPERTY_ALIGNMENT_LEFT))
            {
                style = style | SWT.LEFT;
            }
            else if (alignmentProperty.equals(EJTMTTextItemRendererDefinitionProperties.PROPERTY_ALIGNMENT_RIGHT))
            {
                style = style | SWT.RIGHT;
            }
            else if (alignmentProperty.equals(EJTMTTextItemRendererDefinitionProperties.PROPERTY_ALIGNMENT_CENTER))
            {
                style = style | SWT.CENTER;
            }
        }
        return style;
    }

    private Image getDecorationImage(String image)
    {
        FieldDecorationRegistry registry = FieldDecorationRegistry.getDefault();
        return registry.getFieldDecoration(image).getImage();
    }

    @Override
    public void createLable(Composite composite)
    {
        _label = new Label(composite, SWT.NONE);
        _label.setText(_screenItemProperties.getLabel() == null ? "" : _screenItemProperties.getLabel());
    }

    class TextModifyListener implements ModifyListener
    {
        protected boolean enable = true;

        @Override
        public void modifyText(ModifyEvent event)
        {
            if (enable)
            {
                valueChanged();
            }
        }

    }

    @Override
    public ColumnLabelProvider createColumnLabelProvider(final EJScreenItemProperties item, EJScreenItemController controller)
    {
        ColumnLabelProvider provider = new ColumnLabelProvider()
        {
            @Override
            public Color getBackground(Object element)
            {
                EJCoreVisualAttributeProperties properties = getAttributes(item, element);
                if (properties != null)
                {
                    Color background = EJTMTVisualAttributeUtils.INSTANCE.getBackground(properties);
                    if (background != null)
                    {
                        return background;
                    }
                }
                return super.getBackground(element);
            }

            @Override
            public Color getForeground(Object element)
            {
                EJCoreVisualAttributeProperties properties = getAttributes(item, element);
                if (properties != null)
                {
                    Color foreground = EJTMTVisualAttributeUtils.INSTANCE.getForeground(properties);
                    if (foreground != null)
                    {
                        return foreground;
                    }
                }
                return super.getForeground(element);
            }

            private EJCoreVisualAttributeProperties getAttributes(final EJScreenItemProperties item, Object element)
            {
                EJCoreVisualAttributeProperties properties = null;
                if (element instanceof EJDataRecord)
                {
                    EJDataRecord record = (EJDataRecord) element;
                    properties = record.getItem(item.getReferencedItemName()).getVisualAttribute();
                }
                if (properties == null)
                {
                    properties = _visualAttributeProperties;
                }
                return properties;
            }

            @Override
            public Font getFont(Object element)
            {
                EJCoreVisualAttributeProperties properties = getAttributes(item, element);
                if (properties != null)
                {
                    return EJTMTVisualAttributeUtils.INSTANCE.getFont(properties, super.getFont(element));

                }
                return super.getFont(element);
            }

            @Override
            public String getText(Object element)
            {
                if (element instanceof EJDataRecord)
                {
                    EJDataRecord record = (EJDataRecord) element;
                    Object value = record.getValue(item.getReferencedItemName());
                    if (value instanceof String)
                    {
                        return value.toString();
                    }
                }
                return "";
            }

        };
        return provider;
    }

    @Override
    public EJTMTAbstractTableSorter getColumnSorter(final EJScreenItemProperties item, EJScreenItemController controller)
    {
        final Collator compareCollator = Collator.getInstance();
        return new EJTMTAbstractTableSorter()
        {
            @Override
            public int compare(Viewer viewer, Object e1, Object e2)
            {

                if (e1 instanceof EJDataRecord && e2 instanceof EJDataRecord)
                {
                    EJDataRecord d1 = (EJDataRecord) e1;
                    EJDataRecord d2 = (EJDataRecord) e2;
                    if (d1 != null && d2 != null)
                    {

                        Object value1 = d1.getValue(item.getReferencedItemName());
                        Object value2 = d2.getValue(item.getReferencedItemName());
                        if (value1 == null && value2 == null)
                        {
                            return 0;
                        }
                        if (value1 == null && value2 != null)
                        {
                            return -1;
                        }
                        if (value1 != null && value2 == null)
                        {
                            return 1;
                        }
                        return compareCollator.compare(value1, value2);
                    }
                }
                return 0;
            }
        };
    }

    @Override
    public boolean isReadOnly()
    {
        return false;
    }

    @Override
    public void addListener(ChangeListener listener)
    {
        _changeListeners.add(listener);
    }

    @Override
    public void removeListener(ChangeListener listener)
    {
        _changeListeners.remove(listener);
    }

    protected void fireTextChange()
    {
        for (ChangeListener listener : new ArrayList<ChangeListener>(_changeListeners))
        {
            listener.changed();
        }
    }

    protected void setupKeyboard(Text text, String keyboard)
    {

        TextDecorator textDecorator = Widgets.onText(text);
        if (keyboard.equals(EJTMTTextItemRendererDefinitionProperties.PROPERTY_KEYBOARD_ASCII))
        {
            textDecorator.useAsciiKeyboard();
        }
        else if (keyboard.equals(EJTMTTextItemRendererDefinitionProperties.PROPERTY_KEYBOARD_DECIMAL))
        {
            textDecorator.useDecimalKeyboard();
        }
        else if (keyboard.equals(EJTMTTextItemRendererDefinitionProperties.PROPERTY_KEYBOARD_EMAIL))
        {
            textDecorator.useEmailKeyboard();
        }
        else if (keyboard.equals(EJTMTTextItemRendererDefinitionProperties.PROPERTY_KEYBOARD_NUMBERS))
        {
            textDecorator.useNumberKeyboard();
        }
        else if (keyboard.equals(EJTMTTextItemRendererDefinitionProperties.PROPERTY_KEYBOARD_NUMBERS_AND_PUNCTUATION))
        {
            textDecorator.useNumbersAndPunctuationKeyboard();
        }
        else if (keyboard.equals(EJTMTTextItemRendererDefinitionProperties.PROPERTY_KEYBOARD_PHONE))
        {
            textDecorator.usePhoneKeyboard();
        }
        else if (keyboard.equals(EJTMTTextItemRendererDefinitionProperties.PROPERTY_KEYBOARD_URL))
        {
            textDecorator.useUrlKeyboard();
        }
    }

    @Override
    public Cell<? extends Cell<?>> createColumnCell(EJScreenItemProperties item, EJScreenItemController controller, Template template)
    {
        TextCell textCell = new TextCell(template);
        textCell.setWrap(_rendererProps.getBooleanProperty(EJTMTTextItemRendererDefinitionProperties.PROPERTY_WRAP, false));
        return textCell;
    }
}
