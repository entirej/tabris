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

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Text;
import org.entirej.applicationframework.tmt.renderers.item.definition.interfaces.EJTMTTextItemRendererDefinitionProperties;

public class EJTMTTextAreaRenderer extends EJTMTTextItemRenderer
{
    @Override
    protected Text newTextField(Composite composite, int style)
    {
        if (_rendererProps.getBooleanProperty(EJTMTTextItemRendererDefinitionProperties.PROPERTY_WRAP, false))
        {
            style = style | SWT.MULTI | SWT.WRAP;
        }
        else
        {
            style = style | SWT.MULTI | SWT.H_SCROLL | SWT.V_SCROLL;
        }
        return super.newTextField(composite, style);
    }
}
