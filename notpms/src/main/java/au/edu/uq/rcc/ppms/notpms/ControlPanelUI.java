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

public class ControlPanelUI extends javax.swing.JFrame {

	private final ActionListener listener;
	public ControlPanelUI(ActionListener l) {
		this.listener = l;
		initComponents();
	}

	public void setTopText(String fmt, Object... args) {
		topText.setText(String.format(fmt, (Object[]) args));
	}

	public void setBottomText(String fmt, Object... args) {
		bottomText.setText(String.format(fmt, (Object[]) args));
	}

	public void setBookingEnabled(boolean b) {
		bookBtn.setEnabled(b);
	}

	public void setIncidentEnabled(boolean b) {
		incidentBtn.setEnabled(b);
	}

	public void setEmailEnabled(boolean b) {
		emailBtn.setEnabled(b);
	}

	public void setLogoffEnabled(boolean b) {
		logoffBtn.setEnabled(b);
	}

	@SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        javax.swing.JPanel jPanel1 = new javax.swing.JPanel();
        topText = new javax.swing.JLabel();
        bottomText = new javax.swing.JLabel();
        javax.swing.JPanel jPanel2 = new javax.swing.JPanel();
        bookBtn = new javax.swing.JButton();
        incidentBtn = new javax.swing.JButton();
        emailBtn = new javax.swing.JButton();
        logoffBtn = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.DO_NOTHING_ON_CLOSE);
        setTitle("PPMS Control Panel");
        setAlwaysOnTop(true);
        setResizable(false);

        jPanel1.setLayout(new java.awt.GridLayout(0, 1));

        topText.setText("TOP TEXT");
        jPanel1.add(topText);

        bottomText.setText("BOTTOM TEXT");
        jPanel1.add(bottomText);

        jPanel2.setLayout(new java.awt.GridLayout(1, 0));

        bookBtn.setText("Book");
        bookBtn.setActionCommand("book");
        bookBtn.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                bookBtnActionPerformed(evt);
            }
        });
        jPanel2.add(bookBtn);

        incidentBtn.setText("Incident");
        incidentBtn.setActionCommand("incident");
        incidentBtn.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                incidentBtnActionPerformed(evt);
            }
        });
        jPanel2.add(incidentBtn);

        emailBtn.setText("Email Prefs");
        emailBtn.setActionCommand("email");
        emailBtn.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                emailBtnActionPerformed(evt);
            }
        });
        jPanel2.add(emailBtn);

        logoffBtn.setText("Logoff");
        logoffBtn.setActionCommand("logoff");
        logoffBtn.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                logoffBtnActionPerformed(evt);
            }
        });
        jPanel2.add(logoffBtn);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(jPanel2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, 43, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanel2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void bookBtnActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_bookBtnActionPerformed
        listener.actionPerformed(evt);
    }//GEN-LAST:event_bookBtnActionPerformed

    private void emailBtnActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_emailBtnActionPerformed
        listener.actionPerformed(evt);
    }//GEN-LAST:event_emailBtnActionPerformed

    private void incidentBtnActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_incidentBtnActionPerformed
        listener.actionPerformed(evt);
    }//GEN-LAST:event_incidentBtnActionPerformed

    private void logoffBtnActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_logoffBtnActionPerformed
        listener.actionPerformed(evt);
    }//GEN-LAST:event_logoffBtnActionPerformed

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton bookBtn;
    private javax.swing.JLabel bottomText;
    private javax.swing.JButton emailBtn;
    private javax.swing.JButton incidentBtn;
    private javax.swing.JButton logoffBtn;
    private javax.swing.JLabel topText;
    // End of variables declaration//GEN-END:variables
}
