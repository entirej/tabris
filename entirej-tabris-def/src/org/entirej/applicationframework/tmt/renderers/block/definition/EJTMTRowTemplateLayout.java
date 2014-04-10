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
package org.entirej.applicationframework.tmt.renderers.block.definition;

import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Layout;
import org.eclipse.swt.widgets.Shell;

public class EJTMTRowTemplateLayout extends Layout
{

    @Override
    protected Point computeSize(Composite composite, int wHint, int hHint, boolean flushCache)
    {

        int width = 0, height = 0;

        if (wHint != SWT.DEFAULT)
            width = wHint;
        if (hHint != SWT.DEFAULT)
            height = hHint;

        Control[] children = composite.getChildren();
        for (Control control : children)
        {
            Object layoutData = control.getLayoutData();
            if (layoutData instanceof RowTemplateData)
            {
                RowTemplateData row = (RowTemplateData) layoutData;
                width = Math.max(width, row.width);
                height = Math.max(height, row.height);
            }
        }
        return new Point(width, height);
    }

    @Override
    protected void layout(Composite composite, boolean flushCache)
    {
        Rectangle rect = composite.getClientArea();
        Control[] children = composite.getChildren();
        int count = children.length;
        if (count == 0)
            return;
        int width = rect.width;
        int height = rect.height;

        int zindex = 0;
        Control pre = null;
        for (Control control : children)
        {
            Object layoutData = control.getLayoutData();

            if (layoutData instanceof RowTemplateData)
            {
                RowTemplateData row = (RowTemplateData) layoutData;
                if (control instanceof Label)
                {
                    Label label = (Label) control;
                    label.setAlignment(row.horizontalAlignment);
                }
                int x = 0, y = 0;
                int w = 20, h = 20;
                w = Math.max(w, row.width);
                h = Math.max(h, row.height);
                
                
                
                int left = row.left;
                int top = row.top;
                int right = row.right;
                int bottom = row.bottom;
                
                if(row.width>0)
                {
                    if((left==-1 && right!=-1) || row.horizontalAlignment == SWT.RIGHT)
                    {
                        left = -1;
                        if(right==-1)
                        {
                            right =0;
                        }
                    }
                    else
                    {
                        right = -1;
                        if(left==-1)
                        {
                            left =0;
                        }
                    }
                }
                if(row.height>0)
                {
                    if((top==-1 && bottom!=-1) || row.verticalAlignment == SWT.BOTTOM)
                    {
                        top = -1;
                        if(bottom==-1)
                        {
                            bottom =0;
                        }
                    }
                    else
                    {
                        bottom = -1;
                        if(top==-1)
                        {
                            top =0;
                        }
                    }
                }
                
                if (left >= 0)
                {
                    x = left;
                }

                
                if (top >= 0)
                {
                    y = top;
                }

                
                if (left < 0 && right >= 0)
                {
                    x = Math.max(x, width - (right + w));
                }

                
                if (top < 0 && bottom >= 0)
                {
                    y = Math.max(y, height - (bottom + h));
                }

                if (left >= 0 && right >= 0)
                {
                    w = width - (left + right);
                }

                if (top >= 0 && bottom >= 0)
                {
                    h = height - (top + bottom);
                }

                
                control.setBounds(x, y, w, h);
                
            }
            else
            {
                // defaults
                control.setBounds(0, 0, 20, 20);
            }
            if(control!=null)
            {
                control.moveAbove(pre);
            }
            pre = control;
        }

    }

    public static class RowTemplateData
    {
        public int left                = -1;
        public int right               = -1;
        public int top                 = -1;
        public int bottom              = -1;
        public int width               = -1;
        public int height              = -1;
        public int horizontalAlignment = SWT.NONE;
        public int verticalAlignment   = SWT.NONE;

    }

    public static void main(String[] args)
    {
        Display display = new Display();
        Shell shell = new Shell();
        shell.setLayout(new EJTMTRowTemplateLayout());

        // Create a label

        Label l1 = new Label(shell, SWT.NONE);
        l1.setText("L1");
        l1.setBackground(display.getSystemColor(SWT.COLOR_DARK_YELLOW));
        RowTemplateData l1d = new RowTemplateData();
        l1d.top = 5;
        l1d.left = 5;
        l1d.width = 120;
        l1d.height = 25;
        l1.setLayoutData(l1d);

        Label l2 = new Label(shell, SWT.NONE);
        l2.setText("L2");
        l2.setBackground(display.getSystemColor(SWT.COLOR_DARK_YELLOW));
        l1d = new RowTemplateData();
        l1d.top = 35;
        l1d.left = 5;
        l1d.right = 5;
        l1d.height = 25;
        l2.setLayoutData(l1d);

        Label l3 = new Label(shell, SWT.NONE);
        l3.setText("L3");
        l3.setBackground(display.getSystemColor(SWT.COLOR_DARK_YELLOW));
        l1d = new RowTemplateData();
        l1d.left = 5;
        l1d.bottom = 5;
        l1d.height = 25;
        l3.setLayoutData(l1d);
        
        Label l4 = new Label(shell, SWT.NONE);
        l4.setText("L4");
        l4.setBackground(display.getSystemColor(SWT.COLOR_DARK_YELLOW));
        l1d = new RowTemplateData();
        l1d.right = 5;
        l1d.bottom = 5;
        l1d.height = 25;
        l4.setLayoutData(l1d);

        shell.open();
        while (!shell.isDisposed())
        {
            if (!display.readAndDispatch())
            {
                display.sleep();
            }
        }
        display.dispose();
    }

}
