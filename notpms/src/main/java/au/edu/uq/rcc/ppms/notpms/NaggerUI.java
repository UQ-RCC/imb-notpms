/*
NotPMS PPMS Tracker
https://github.com/UQ-RCC/imb-notpms

SPDX-License-Identifier: Apache-2.0
Copyright (c) 2019 The University of Queensland

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

    http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
*/
package au.edu.uq.rcc.ppms.notpms;

import java.awt.event.ActionListener;
import javax.swing.JLabel;

public class NaggerUI extends javax.swing.JFrame {

	private final ActionListener listener;

	private final JLabel[] labels;

	public NaggerUI(ActionListener l) {
		this.listener = l;
		initComponents();
		this.labels = new JLabel[]{
			text1, text2, text3, text4, text5
		};
	}

	@SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        buttonPanel = new javax.swing.JPanel();
        quickBookBtn = new javax.swing.JButton();
        ppmsBtn = new javax.swing.JButton();
        incidentBtn = new javax.swing.JButton();
        pesterBtn = new javax.swing.JButton();
        logoffBtn = new javax.swing.JButton();
        text1 = new javax.swing.JLabel();
        text2 = new javax.swing.JLabel();
        text3 = new javax.swing.JLabel();
        text4 = new javax.swing.JLabel();
        text5 = new javax.swing.JLabel();

        setDefaultCloseOperation(javax.swing.WindowConstants.DO_NOTHING_ON_CLOSE);
        setTitle("PPMS Control Panel");
        setAlwaysOnTop(true);
        setResizable(false);
        setType(java.awt.Window.Type.UTILITY);

        buttonPanel.setLayout(new java.awt.GridLayout(1, 0));

        quickBookBtn.setText("Quick Book until XX:XX XX");
        quickBookBtn.setActionCommand("quickbook");
        quickBookBtn.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                quickBookBtnActionPerformed(evt);
            }
        });
        buttonPanel.add(quickBookBtn);

        ppmsBtn.setText("Open PPMS");
        ppmsBtn.setActionCommand("openppms");
        ppmsBtn.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                ppmsBtnActionPerformed(evt);
            }
        });
        buttonPanel.add(ppmsBtn);

        incidentBtn.setText("Report Incident");
        incidentBtn.setActionCommand("incident");
        incidentBtn.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                incidentBtnActionPerformed(evt);
            }
        });
        buttonPanel.add(incidentBtn);

        pesterBtn.setText("Stop Pestering Me");
        pesterBtn.setActionCommand("stoppester");
        pesterBtn.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                pesterBtnActionPerformed(evt);
            }
        });
        buttonPanel.add(pesterBtn);

        logoffBtn.setText("Logoff");
        logoffBtn.setActionCommand("logoff");
        logoffBtn.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                logoffBtnActionPerformed(evt);
            }
        });
        buttonPanel.add(logoffBtn);

        text1.setText("1 TEXT");

        text2.setText("2 TEXT");

        text3.setText("RED TEXT");

        text4.setText("BLUE TEXT");

        text5.setText("5 TEXT");

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(buttonPanel, javax.swing.GroupLayout.DEFAULT_SIZE, 1058, Short.MAX_VALUE)
                    .addComponent(text1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(text2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(text3, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(text4, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(text5, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(text1)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(text2)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(text3)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(text4)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(text5)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(buttonPanel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

	public void clearText() {
		for(int i = 0; i < labels.length; ++i) {
			labels[i].setText("");
		}
	}

	public void reflowButtons() {
		buttonPanel.removeAll();

		if(quickBookBtn.isVisible()) {
			buttonPanel.add(quickBookBtn);
		}

		if(ppmsBtn.isVisible()) {
			buttonPanel.add(ppmsBtn);
		}

		if(incidentBtn.isVisible()) {
			buttonPanel.add(incidentBtn);
		}

		if(pesterBtn.isVisible()) {
			buttonPanel.add(pesterBtn);
		}

		if(logoffBtn.isVisible()) {
			buttonPanel.add(logoffBtn);
		}

		this.pack();
	}

	public void setQuickBookVisible(boolean b) {
		//quickBookBtn.setVisible(b);
		/* For now */
		quickBookBtn.setVisible(false);
	}

	public void setPPMSVisible(boolean b) {
		ppmsBtn.setVisible(b);
	}

	public void setIncidentVisible(boolean b) {
		incidentBtn.setVisible(b);
	}

	public void setPesterVisible(boolean b) {
		pesterBtn.setVisible(b);
	}

	public void setLogoffVisible(boolean b) {
		logoffBtn.setVisible(b);
	}

	public void setText(int level, String fmt, Object... args) {
		if(level < 1 || level >= labels.length) {
			return;
		}

		labels[level - 1].setText(String.format(fmt, (Object[])args));
	}

    private void quickBookBtnActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_quickBookBtnActionPerformed
		listener.actionPerformed(evt);
    }//GEN-LAST:event_quickBookBtnActionPerformed

    private void ppmsBtnActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_ppmsBtnActionPerformed
		listener.actionPerformed(evt);
    }//GEN-LAST:event_ppmsBtnActionPerformed

    private void incidentBtnActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_incidentBtnActionPerformed
		listener.actionPerformed(evt);
    }//GEN-LAST:event_incidentBtnActionPerformed

    private void pesterBtnActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_pesterBtnActionPerformed
		listener.actionPerformed(evt);
    }//GEN-LAST:event_pesterBtnActionPerformed

    private void logoffBtnActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_logoffBtnActionPerformed
		listener.actionPerformed(evt);
    }//GEN-LAST:event_logoffBtnActionPerformed

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JPanel buttonPanel;
    private javax.swing.JButton incidentBtn;
    private javax.swing.JButton logoffBtn;
    private javax.swing.JButton pesterBtn;
    private javax.swing.JButton ppmsBtn;
    private javax.swing.JButton quickBookBtn;
    private javax.swing.JLabel text1;
    private javax.swing.JLabel text2;
    private javax.swing.JLabel text3;
    private javax.swing.JLabel text4;
    private javax.swing.JLabel text5;
    // End of variables declaration//GEN-END:variables
}
