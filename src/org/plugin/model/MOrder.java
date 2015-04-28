/******************************************************************************
 * Product: iDempiere Free ERP Project based on Compiere (2006)               *
 * Copyright (C) 2014 Redhuan D. Oon All Rights Reserved.                     *
 * This program is free software; you can redistribute it and/or modify it    *
 * under the terms version 2 of the GNU General Public License as published   *
 * by the Free Software Foundation. This program is distributed in the hope   *
 * that it will be useful, but WITHOUT ANY WARRANTY; without even the implied *
 * warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.           *
 * See the GNU General Public License for more details.                       *
 * You should have received a copy of the GNU General Public License along    *
 * with this program; if not, write to the Free Software Foundation, Inc.,    *
 * 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA.                     *
 *  FOR NON-COMMERCIAL DEVELOPER USE ONLY                                     *
 *  @author Redhuan D. Oon  - red1@red1.org  www.red1.org                     *
 *****************************************************************************/

package org.plugin.model;

import java.sql.ResultSet;
import java.util.Properties;

import org.adempiere.model.POWrapper;

public class MOrder extends org.compiere.model.MOrder {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public MOrder(Properties ctx, int C_Order_ID, String trxName) {
		super(ctx, C_Order_ID, trxName); 
	}

	public MOrder(Properties ctx, ResultSet rs, String trxName) {
		super(ctx, rs, trxName);
	}
	
	//TODO Using POWrapper to extend model functionality
	I_C_Order newfield = POWrapper.create(this, I_C_Order.class);
	
	//getter
	int field = newfield.getNew_ID();
	
	//setter
	public void setNewfield(I_C_Order newfield) {
		this.newfield = newfield;
	}

}
